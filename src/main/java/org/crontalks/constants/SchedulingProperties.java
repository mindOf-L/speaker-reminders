package org.crontalks.constants;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "schedule")
public class SchedulingProperties {

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
    private final String reminderSpeakerTemplateEmail = """
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
    private final String emailEmptyData = """
        <p style="font-size:1.25em">Hola!!! 👋</p>
        
        <p style="font-size:1.25em">Revisa la hoja, parece que NO hay datos para esta semana!</p>
        
        <p style="font-size:1.25em">Recuerda que intentaré hacer el envío de nuevo mañana, habrá 6 reintentos en total.</p>
        """;

    @Getter
    private final String emailSomeEmptyData = """
        <p style="font-size:1.25em">Hola!!! 👋</p>
        
        <p style="font-size:1.25em">Revisa la hoja, parece que algunos datos NO están puestos para esta semana!</p>
        
        <p style="font-size:1.25em">Recuerda que intentaré hacer el envío de nuevo mañana, habrá 6 reintentos en total.</p>
        """;

    @Getter
    private final String reminderSpeakerTemplateCustomImages = "Si utilizarás <strong>imágenes</strong> 🏞️ envíamelas por favor a <a href=\"mailto:%s?subject=Imágenes de discurso Nº%s en Veredillas %s\">%s</a>, con alguna indicación de cuándo ponerlas y quitarlas.";

    @Getter
    private final String reminderSpeakerTemplateOutlineImages = "Qué <strong>imágenes</strong> 🏞️ del bosquejo utilizarás (el bosquejo trae alguna/s). Envíanos cuáles elegiste a <a href=\"mailto:%s?subject=Imágenes de discurso Nº%s en Veredillas %s\">%s</a>, con alguna indicación de cuándo ponerlas y quitarlas.";

    @Getter
    private final String reminderSpeakerTemplateOutlineVideos = "Además, el bosquejo tiene al menos 1 vídeo. Por favor, indícanos en qué momento darás paso al vídeo.";

    @Getter
    private final String reminderSpeakerNotInformedTemplate = """
        <p style="font-size:1.25em">Hola.</p>
        
        <p style="font-size:1.25em">He intentado enviar un correo a <strong>%s</strong> pero no he podido.</p>
        
        <p style="font-size:1.25em">Por favor, echa un vistazo a la tabla de Google Sheets y asegúrate de que tengas los datos correctos. Parece que el campo de correo electrónico está vacío o no es válido.</p>
        
        <p style="font-size:1.25em">Corrige el dato y lo intentaré de nuevo en 24 horas.</p>
        
        <p style="font-size:1.25em">Saludos de tu app!</p>
        """;
}
