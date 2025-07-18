package org.crontalks.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crontalks.exception.EmailRecipientNotInformedException;
import org.crontalks.service.GmailSmtpService;
import org.crontalks.service.SpeakerService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.crontalks.constants.Messages.EMAIL_NOT_INFORMED_SUBJECT;
import static org.crontalks.entity.EmailTemplate.emailSpeakerNotInformedTemplate;
import static org.crontalks.entity.EmailTemplate.emailSpeakerTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final JobLauncher jobLauncher;
    private final Job reminderJob;
    private final SpeakerService speakerService;
    private final GmailSmtpService gmailSmtpService;
    private final CronProperties cronProperties;

    // Constantes para el sistema de reintentos
    private static final int MAX_RETRIES = 6;

    // Mapa para trackear los reintentos (en producción considerar usar Redis o base de datos)
    private final ConcurrentHashMap<String, AtomicInteger> retryAttempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LocalDateTime> lastAttemptTime = new ConcurrentHashMap<>();

    @Scheduled(cron = "#{@cronProperties.schedule}")
    public void runReminderJob() throws Exception {
        log.info(">>> Iniciando proceso de envío de recordatorios programado...");
        log.info("Cron expression is '{}'", cronProperties.getSchedule());

        JobParameters params = new JobParametersBuilder()
            .addLong("run.id", System.currentTimeMillis())
            .toJobParameters();

        // Ejecutar el batch job
        jobLauncher.run(reminderJob, params);

        // Intentar enviar el email
        sendReminderEmail();
    }

    /**
     * Método principal para enviar el email de recordatorio con lógica de reintentos
     */
    private void sendReminderEmail() {
        String currentWeek = getCurrentWeekKey();

        try {
            // Obtener los datos del discurso programado
            var scheduledTalk = speakerService.getCurrentScheduledTalk();

            if (scheduledTalk == null) {
                log.error("No se pudo obtener información del discurso programado desde Google Sheets");
                sendErrorNotification();
                return;
            }

            // Preparar el contenido del email
            String emailBody = emailSpeakerTemplate(scheduledTalk);

            throw new EmailRecipientNotInformedException("email no informado");

            /*
            // Intentar enviar el email
            gmailSmtpService.sendEmail(
                "eleaz.rs@gmail.com",
                EMAIL_DEFAULT_SUBJECT,
                new String[]{"jw.eleazar@gmail.com"},
                emailBody
            );

            log.info("Email enviado exitosamente a: {}", scheduledTalk.email());

            // Resetear contadores de reintentos en caso de éxito
            resetRetryCounters(currentWeek);
             */

        } catch (EmailRecipientNotInformedException e) {
            log.warn("Email del discursante no informado: {}", e.getMessage());
            handleEmailRecipientNotInformed(currentWeek);

        } catch (Exception e) {
            log.error("Error al enviar email: {}", e.getMessage(), e);
            handleEmailSendFailure(currentWeek, e);
        }
    }

    /**
     * Maneja el caso cuando el email del discursante no está informado
     */
    private void handleEmailRecipientNotInformed(String currentWeek) {
        AtomicInteger attempts = retryAttempts.computeIfAbsent(currentWeek, _ -> new AtomicInteger(0));
        attempts.incrementAndGet();

        lastAttemptTime.put(currentWeek, LocalDateTime.now());

        try {
            var scheduledTalk = speakerService.getCurrentScheduledTalk();
            if (scheduledTalk != null) {
                String notificationBody = emailSpeakerNotInformedTemplate(scheduledTalk);
                String notificationSubject = String.format(EMAIL_NOT_INFORMED_SUBJECT, scheduledTalk.name());

                gmailSmtpService.sendEmail(
                    "eleaz.rs@gmail.com",
                    notificationSubject,
                    new String[]{"jw.eleazar@gmail.com"},
                    notificationBody
                );

                log.info("Notificación enviada al supervisor sobre email no informado");
            }
        } catch (Exception notificationError) {
            log.error("Error al enviar notificación sobre email no informado: {}",
                notificationError.getMessage(), notificationError);
        }
    }

    /**
     * Maneja los fallos en el envío de email y programa reintentos
     */
    private void handleEmailSendFailure(String currentWeek, Exception error) {
        AtomicInteger attempts = retryAttempts.computeIfAbsent(currentWeek, _ -> new AtomicInteger(0));
        int currentAttempt = attempts.incrementAndGet();

        lastAttemptTime.put(currentWeek, LocalDateTime.now());

        log.warn("Intento {} de {} fallido para la semana {}: {}",
            currentAttempt, MAX_RETRIES, currentWeek, error.getMessage());

        if (currentAttempt >= MAX_RETRIES) {
            log.error("Máximo número de reintentos alcanzado para la semana {}", currentWeek);
            sendMaxRetriesReachedNotification(currentWeek, error);
            resetRetryCounters(currentWeek);
        } else {
            // Notificar sobre el fallo del intento actual
            sendRetryNotification(currentWeek, currentAttempt, error);
            log.info("Se programará un nuevo intento en 24 horas (intento {} de {})",
                currentAttempt + 1, MAX_RETRIES);
        }
    }

    /**
     * Método adicional para manejar reintentos programados cada 24 horas
     */
    //@Scheduled(cron = "*/30 * * * * *")
    public void handleScheduledRetries() {
        log.debug("Verificando reintentos programados...");

        for (String weekKey : retryAttempts.keySet()) {
            AtomicInteger attempts = retryAttempts.get(weekKey);
            LocalDateTime lastAttempt = lastAttemptTime.get(weekKey);

            if (attempts != null && lastAttempt != null &&
                attempts.get() > 0 && attempts.get() < MAX_RETRIES &&
                lastAttempt.plusHours(24).isBefore(LocalDateTime.now())) {

                log.info("Ejecutando reintento programado para la semana {}", weekKey);
                sendReminderEmail();
                break; // Solo procesar un reintento por ejecución
            }
        }
    }

    /**
     * Envía notificación sobre fallo en el intento de envío
     */
    private void sendRetryNotification(String weekKey, int attemptNumber, Exception error) {
        try {
            String subject = String.format("Fallo en envío de recordatorio - Intento %d de %d",
                attemptNumber, MAX_RETRIES);

            String body = String.format("""
                <h2>Fallo en envío de recordatorio</h2>
                <p><strong>Semana:</strong> %s</p>
                <p><strong>Intento:</strong> %d de %d</p>
                <p><strong>Error:</strong> %s</p>
                <p><strong>Próximo intento:</strong> En 24 horas</p>
                <p><strong>Hora del fallo:</strong> %s</p>
                
                <p>El sistema intentará enviar el recordatorio nuevamente de forma automática.</p>
                """,
                weekKey, attemptNumber, MAX_RETRIES,
                error.getMessage(), LocalDateTime.now());

            gmailSmtpService.sendEmail(
                "eleaz.rs@gmail.com",
                subject,
                new String[]{"jw.eleazar@gmail.com"},
                body
            );

        } catch (Exception e) {
            log.error("Error al enviar notificación de reintento: {}", e.getMessage(), e);
        }
    }

    /**
     * Envía notificación cuando se alcanza el máximo de reintentos
     */
    private void sendMaxRetriesReachedNotification(String weekKey, Exception lastError) {
        try {
            String subject = "CRÍTICO: Máximo de reintentos alcanzado - Recordatorio no enviado";

            String body = String.format("""
                <h2 style="color: red;">CRÍTICO: Fallo definitivo en envío de recordatorio</h2>
                <p><strong>Semana:</strong> %s</p>
                <p><strong>Intentos realizados:</strong> %d</p>
                <p><strong>Último error:</strong> %s</p>
                <p><strong>Hora del último intento:</strong> %s</p>
                
                <p style="color: red;"><strong>ACCIÓN REQUERIDA:</strong>
                Es necesario enviar el recordatorio manualmente y revisar la configuración del sistema.</p>
                
                <p>Posibles causas:</p>
                <ul>
                    <li>Problemas de conectividad con Google Sheets</li>
                    <li>Configuración incorrecta del email</li>
                    <li>Problemas con el servidor SMTP</li>
                    <li>Datos incorrectos en la hoja de cálculo</li>
                </ul>
                """,
                weekKey, MAX_RETRIES, lastError.getMessage(), LocalDateTime.now());

            gmailSmtpService.sendEmail(
                "eleaz.rs@gmail.com",
                subject,
                new String[]{"jw.eleazar@gmail.com"},
                body
            );

        } catch (Exception e) {
            log.error("Error crítico: No se pudo enviar notificación de máximo reintentos: {}",
                e.getMessage(), e);
        }
    }

    /**
     * Envía notificación de error general
     */
    private void sendErrorNotification() {
        try {
            String subject = "Error en sistema de recordatorios";
            String body = String.format("""
                <h2>Error en sistema de recordatorios</h2>
                <p><strong>Error:</strong> No se pudo obtener información del discurso programado desde Google Sheets</p>
                <p><strong>Hora:</strong> %s</p>
                
                <p>Por favor, revisa la configuración del sistema y los datos en Google Sheets.</p>
                """,
                LocalDateTime.now());

            gmailSmtpService.sendEmail(
                "eleaz.rs@gmail.com",
                subject,
                new String[]{"jw.eleazar@gmail.com"},
                body
            );

        } catch (Exception e) {
            log.error("Error al enviar notificación de error: {}", e.getMessage(), e);
        }
    }

    /**
     * Resetea los contadores de reintentos para una semana específica
     */
    private void resetRetryCounters(String weekKey) {
        retryAttempts.remove(weekKey);
        lastAttemptTime.remove(weekKey);
        log.debug("Contadores de reintentos reseteados para la semana {}", weekKey);
    }

    /**
     * Genera una clave única para la semana actual
     */
    private String getCurrentWeekKey() {
        LocalDateTime now = LocalDateTime.now();
        return String.format("%d-W%d", now.getYear(), now.getDayOfYear() / 7);
    }

    /**
     * Método para limpiar datos antiguos de reintentos (ejecutar semanalmente)
     */
    @Scheduled(cron = "#{@cronProperties.schedule}")
    public void cleanupOldRetryData() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusWeeks(2);

        retryAttempts.entrySet().removeIf(entry -> {
            LocalDateTime lastAttempt = lastAttemptTime.get(entry.getKey());
            return lastAttempt != null && lastAttempt.isBefore(cutoffTime);
        });

        lastAttemptTime.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoffTime));

        log.debug("Datos antiguos de reintentos limpiados");
    }
}
