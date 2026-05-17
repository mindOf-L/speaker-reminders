package org.crontalks.constants;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Component
@ConfigurationProperties(prefix = "whatsapp")
public class WhatsAppProperties {

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

    @Getter
    private final String remiderSpeakerTemplateWhatsApp = """
            Hola %s 👋
            Soy %s de la congregación Veredillas de Torrejón de Ardoz, encantado de saludarte 😀
            Según nuestro calendario de discursos, te esperamos el *%s* para escuchar el bosquejo *N° %s*, con el título:
            *%s*
            *Tu congregación es: %s*
            La reunión de nuestra congregación comienza a las %s y la dirección es %s.
            Puedes consultar la dirección en Google Maps: "%s.
            Por favor, *confírmame* lo antes posible:
            ▶️ Si los *datos son correctos* 📝
            ➡️ La *canción* que usarás 🎵 (*asegúrate que no coincide* con ninguna de las de la Atalaya de esa semana 😉).
            ⏺️ %s %s
            ⏰ Si te quedarás hasta el final para hacer la oración final
            ¡Estamos deseando escucharte!
            Un fuerte abrazo 🤗
            """;

    @Getter
    private final String remiderSpeakerNext4WeekTemplateWhatsApp = """
            Hola %s 👋
            Soy %s de la congregación Veredillas de Torrejón de Ardoz, encantado de saludarte 😀
            Según nuestro calendario de discursos, te esperamos el *%s* para escuchar el bosquejo *N° %s*, con el título:
            *%s*
            Nada más queríamos confirmar contigo que por tu parte el discurso sigue adelante. Muchas gracias por confirmar!!
            Un fuerte abrazo 🤗
            """;

    @Getter
    private final String reminderSpeakerWhatsAppCustomImagesTemplate = "Si utilizarás *imágenes* 🏞️ envíamelas por favor a %s, con alguna indicación de cuándo ponerlas y quitarlas.";

    @Getter
    private final String reminderSpeakerWhatsAppOutlineImagesTemplate = "Qué *imágenes* 🏞️ del bosquejo utilizarás (el bosquejo trae alguna/s). Envíanos cuáles elegiste a %s con alguna indicación de cuándo ponerlas y quitarlas.";

    @Getter
    private final String reminderSpeakerWhatsAppOutlineVideosTemplate = "Además, el bosquejo tiene al menos 1 vídeo. Por favor, indícanos en qué momento darás paso al vídeo.";

    @Getter
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

    public Map<String, String> createMap(String param_name, String param_value) {
        return Map.of("type", "text", "parameter_name", param_name, "text", param_value);
    }

    @Getter
    private final String speakerCustomImagesTemplateWhatsApp = "Si utilizarás *imágenes* 🏞️ envíamelas por favor a _%s_ o por WhatsApp al hermano de video (%s - %s), con alguna indicación de cuándo ponerlas y quitarlas.";

    @Getter
    private final String outlineImagesTemplateWhatsApp = "Qué *imágenes* 🏞️ del bosquejo utilizarás (el bosquejo trae alguna/s). Envíame cuáles elegiste a _%s_ o por WhatsApp al hermano de video (%s - %s), con alguna indicación de cuándo ponerlas y quitarlas.";

}
