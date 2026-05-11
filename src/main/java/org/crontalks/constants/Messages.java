package org.crontalks.constants;

public class Messages {

    public static final String
        EMAIL_SENDING = "Sending email to {}}",
        EMAIL_SENDING_TO_CURRENT = "Sending email to current speaker",
        EMAIL_DEFAULT_SUBJECT = "Torrejón de Ardoz - Veredillas: Recordatorio de discurso público para esta semana",
        EMAIL_WHATSAPP_REMINDER_SUBJECT = "⏰ WhatsApp: Recordatorio de discurso público para esta semana",
        EMAIL_NOT_INFORMED_SUBJECT = "Email de discursante no informado o no válido: %s",
        EMAIL_SENT_CORRECTLY = "Email sent correctly to %s with this content: %s",
        EMAIL_SENT_CORRECTLY_NO_CONTENT = "Email sent correctly to %s",
        EMAIL_SENT = "Email sent",
        EMAIL_TEST_SUBJECT = "Test email",

        WHATSAPP_SENT_CORRECTLY = "Whatsapp sent correctly to %s",

        NOT_IMPLEMENTED_YET = "Not implemented yet",

        IM_ALIVE = "I'm alive! 🫀",

        WARNING_SENDING_EMAIL_EMPTY_DATA = "⚠️ Error enviando email: no hay datos!!",
        WARNING_SENDING_EMAIL_SOME_EMPTY_DATA = "⚠️ Error enviando email: faltan algunos datos!!",

        ERROR_SENDING_EMAIL = "Error sending email: %s",
        ERROR_SENDING_EMAIL_TO = "Error sending email to %s",
        ERROR_SENDING_WHATSAPP = "Error sending whatsapp to %s",
        ERROR_GETTING_DATA_FROM_GSHEET = "Error getting data from Google Sheet",
        ERROR_EMAIL_RECIPIENT_NOT_INFORMED = "Email recipient not informed",

        MESSAGES_FIRST_ATTEMPT_OK = "🟢 Service successfully executed on the first attempt.",
        MESSAGES_FIRST_ATTEMPT_KO = "❌ Failure on initial attempt: {}",
        MESSAGES_ATTEMPT_NUM = "🔄 Attempt #{}...",
        MESSAGES_ATTEMPT_NUM_OK = "🟢 Attempt #{} successful.",
        MESSAGES_ATTEMPT_NUM_KO = "❌ Fail on attempt #{}: {}",
        MESSAGES_MAX_ATTEMPT_KO = "🔴 The maximum of 6 attempts was reached. Quitting retries."
    ;
}
