package org.crontalks.constants;

import com.google.common.collect.ImmutableMap;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

public class Params {

    @Getter
    @Component
    @NoArgsConstructor
    public static class GSheets {

        private static GSheets Instance;

        @PostConstruct
        public void init() {
            GSheets.Instance = this;
        }

        public static GSheets getGSheetsParam() {
            return Instance;
        }

        @Value("${google.sheet}")
        private String sheet;

        @Value("${google.speaker-sheet}")
        private String thisWeekSpeaker;
    }

    @Getter
    @Component
    @NoArgsConstructor
    public static class WhatsApp {

        @Value("${whatsapp.url}")
        private String whatsAppUrl;

        @Value("${whatsapp.template.first}")
        private String whatsAppTemplateNameFirst;

        @Value("${whatsapp.template.second}")
        private String whatsAppTemplateNameSecond;

        @Value("${whatsapp.token}")
        private String whatsAppToken;

        @Value("${whatsapp.phoneNumberId}")
        private String whatsAppPhoneNumberId;

        @Value("${whatsapp.testPhoneNumber}")
        private String whatsAppTestPhoneNumber;

        private static WhatsApp Instance;

        @PostConstruct
        public void init() {
            WhatsApp.Instance = this;
        }

        public static WhatsApp getWhatsAppParam() {
            return Instance;
        }

        private final String reminderSpeakerTemplateWhatsAppV2 = """
            Hola {{speaker_name}} 👋
            
            Soy {{overseer_name}} de la congregación Veredillas de Torrejón de Ardoz, encantado de saludarte 😀
            
            Según nuestro calendario de discursos, te esperamos el *{{talk_date}}* para escuchar el bosquejo *N° {{outline_number}}*, con el título:
            *{{outline_title}}*
            Tu congregación es: *{{speaker_congregation}}*
            
            La reunión de nuestra congregación comienza a las _{{congregation_time}}_ y la dirección es _{{congregation_address}}_.
            Puedes consultar la dirección en Google Maps: {{congregation_gmap}}
            
            Por favor, *confírmame* lo antes posible:
            ▶️ Si los *datos son correctos* 📝
            ➡️ La *canción* que usarás 🎵 (asegúrate que no coincide con ninguna de las de la Atalaya de esa semana 😉)
            ⏺️ {{speaker_images}}
            
            *¡Estamos deseando escucharte!*
            
            Un fuerte abrazo 🤗
            """;

        public static ImmutableMap<String, String> createImmutableMap(String param_name, String param_value) {
            return ImmutableMap.of("type", "text", "parameter_name", param_name, "text", param_value);
        }

        private final String speakerCustomImagesTemplateWhatsApp = "Si utilizarás *imágenes* 🏞️ envíamelas por favor a _%s_ o por WhatsApp al hermano de video (%s - %s), con alguna indicación de cuándo ponerlas y quitarlas.";

        private final String outlineImagesTemplateWhatsApp = "Qué *imágenes* 🏞️ del bosquejo utilizarás (el bosquejo trae alguna/s). Envíame cuáles elegiste a _%s_ o por WhatsApp al hermano de video (%s - %s), con alguna indicación de cuándo ponerlas y quitarlas.";

    }

    @Getter
    @Component
    @NoArgsConstructor
    public static class Scheduling {

        private static Scheduling Instance;

        @PostConstruct
        public void init() {
            Scheduling.Instance = this;
        }

        public static Scheduling getSchedulingParam() {
            return Instance;
        }

        @Value("${email.from}")
        private String emailFrom;

        @Value("${email.from}")
        private String overseerEmail;

        @Value("${email.cc}")
        private String[] emailCC;

        @Value("${video-dept.email}")
        private String videoDeptEmail;

        @Value("${video-dept.overseer-name}")
        private String videoDeptOverseerName;

        @Value("${video-dept.overseer-phone}")
        private String videoDeptOverseerPhone;

        @Value("${MEETING-TIME:12:30}")
        private String meetingTime;

        @Value("${TALK-OVERSEER}")
        private String talksOverseer;

        @Value("${CONGREGATION-ADDRESS}")
        private String congregationAddress;

        @Value("${CONGREGATION-GMAPS}")
        private String congregationGMaps;

        @Getter
        private static final String reminderSpeakerTemplateEmail = """
            <p style="font-size:1.25em">Hola %s 👋</p>
            
            <p style="font-size:1.25em">Soy %s de la congregación Veredillas de Torrejón de Ardoz, encantado de saludarte 😀</p>
            
            <p style="font-size:1.25em">Según nuestro calendario de discursos, te esperamos el <strong>%s</strong> para escuchar el bosquejo <b>N° %s</b>, con el título:</p>
            <p style="font-size:2em;font-weight:bold">%s</p>
            
            <p style="font-size:1.25em;font-weight:bold">Tu congregación es: %s</p>
            
            <p style="font-size:1.25em">La reunión de nuestra congregación comienza a las %s y la dirección es %s.<br />
            Puedes consultar la dirección en Google Maps <a href="%s">en este enlace</a>.</p>
            
            <p style="font-size:1.25em">Por favor, <strong>confírmame</strong> lo antes posible:</p>
            
            <p style="font-size:1.25em">▶️ Si los <strong>datos son correctos</strong> 📝</p>
            
            <p style="font-size:1.25em">➡️ La <strong>canción</strong> que usarás 🎵 (<strong>asegúrate que no coincide</strong> con ninguna de las de la Atalaya de esa semana 😉).</p>
            
            <p style="font-size:1.25em">⏺️ %s %s</p>
            
            <p style="font-size:1.25em">⏰ Si te quedarás hasta el final para hacer la oración final</p>
            
            <p style="font-size:1.25em;font-weight:bold"">¡Estamos deseando escucharte!</p>
            
            <p style="font-size:1.25em">Un fuerte abrazo 🤗</p>
            """;

        @Getter
        private static final String emailEmptyData = """
            <p style="font-size:1.25em">Hola!!! 👋</p>
            
            <p style="font-size:1.25em">Revisa la hoja, parece que NO hay datos para esta semana!</p>
            
            <p style="font-size:1.25em">Recuerda que intentaré hacer el envío de nuevo mañana, habrá 6 reintentos en total.</p>
            """;

        @Getter
        private static final String emailSomeEmptyData = """
            <p style="font-size:1.25em">Hola!!! 👋</p>
            
            <p style="font-size:1.25em">Revisa la hoja, parece que algunos datos NO están puestos para esta semana!</p>
            
            <p style="font-size:1.25em">Recuerda que intentaré hacer el envío de nuevo mañana, habrá 6 reintentos en total.</p>
            """;

        @Getter
        private static final String reminderSpeakerTemplateCustomImages = "Si utilizarás <strong>imágenes</strong> 🏞️ envíamelas por favor a <a href=\"mailto:%s?subject=Imágenes de discurso Nº%s en Veredillas %s\">%s</a>, con alguna indicación de cuándo ponerlas y quitarlas.";

        @Getter
        private static final String reminderSpeakerTemplateOutlineImages = "Qué <strong>imágenes</strong> 🏞️ del bosquejo utilizarás (el bosquejo trae alguna/s). Envíanos cuáles elegiste a <a href=\"mailto:%s?subject=Imágenes de discurso Nº%s en Veredillas %s\">%s</a>, con alguna indicación de cuándo ponerlas y quitarlas.";

        @Getter
        private static final String reminderSpeakerTemplateOutlineVideos = "Además, el bosquejo tiene al menos 1 vídeo. Por favor, indícanos en qué momento darás paso al vídeo.";

        @Getter
        private static final String reminderSpeakerNotInformedTemplate = """
            <p style="font-size:1.25em">Hola.</p>
            
            <p style="font-size:1.25em">He intentado enviar un correo a <strong>%s</strong> pero no he podido.</p>
            
            <p style="font-size:1.25em">Por favor, echa un vistazo a la tabla de Google Sheets y asegúrate de que tengas los datos correctos. Parece que el campo de correo electrónico está vacío o no es válido.</p>
            
            <p style="font-size:1.25em">Corrige el dato y lo intentaré de nuevo en 24 horas.</p>
            
            <p style="font-size:1.25em">Saludos de tu app!</p>
            """;
    }

}
