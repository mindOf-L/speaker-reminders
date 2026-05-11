package org.crontalks.constants;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class EmailTemplates {

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

    private final String emailEmptyData = """
        <p style="font-size:1.25em">Hola!!! 👋</p>
        
        <p style="font-size:1.25em">Revisa la hoja, parece que NO hay datos para esta semana!</p>
        
        <p style="font-size:1.25em">Recuerda que intentaré hacer el envío de nuevo mañana, habrá 6 reintentos en total.</p>
        """;

    private final String emailSomeEmptyData = """
        <p style="font-size:1.25em">Hola!!! 👋</p>
        
        <p style="font-size:1.25em">Revisa la hoja, parece que algunos datos NO están puestos para esta semana!</p>
        
        <p style="font-size:1.25em">Recuerda que intentaré hacer el envío de nuevo mañana, habrá 6 reintentos en total.</p>
        """;

    private final String reminderSpeakerTemplateCustomImages = "Si utilizarás <strong>imágenes</strong> 🏞️ envíamelas por favor a <a href=\"mailto:%s?subject=Imágenes de discurso Nº%s en Veredillas %s\">%s</a>, con alguna indicación de cuándo ponerlas y quitarlas.";

    private final String reminderSpeakerTemplateOutlineImages = "Qué <strong>imágenes</strong> 🏞️ del bosquejo utilizarás (el bosquejo trae alguna/s). Envíanos cuáles elegiste a <a href=\"mailto:%s?subject=Imágenes de discurso Nº%s en Veredillas %s\">%s</a>, con alguna indicación de cuándo ponerlas y quitarlas.";

    private final String reminderSpeakerTemplateOutlineVideos = "Además, el bosquejo tiene al menos 1 vídeo. Por favor, indícanos en qué momento darás paso al vídeo.";

    private final String reminderSpeakerNotInformedTemplate = """
        <p style="font-size:1.25em">Hola.</p>
        
        <p style="font-size:1.25em">He intentado enviar un correo a <strong>%s</strong> pero no he podido.</p>
        
        <p style="font-size:1.25em">Por favor, echa un vistazo a la tabla de Google Sheets y asegúrate de que tengas los datos correctos. Parece que el campo de correo electrónico está vacío o no es válido.</p>
        
        <p style="font-size:1.25em">Corrige el dato y lo intentaré de nuevo en 24 horas.</p>
        
        <p style="font-size:1.25em">Saludos de tu app!</p>
        """;

    private final String remiderSpeakerTemplateWhatsApp = """
        <div>
            <style>
                .button-primary {
                     font-size: 1.25em;
                     color: white;
                     background: rgb(28, 184, 65);
                     border: 1px solid transparent;
                     border-color: #ccc;
                     border-radius: 4px;
                     text-shadow: 0 1px 1px rgba(0, 0, 0, 0.2);
                     padding: 6px 12px;
                     margin-bottom: 0;
                     display: inline-block;
                     text-decoration: none;
                     text-align: center;
                     white-space: nowrap;
                     vertical-align: middle;
                     -ms-touch-action: manipulation;
                     touch-action: manipulation;
                     -webkit-user-select: none;
                     -moz-user-select: none;
                     -ms-user-select: none;
                     user-select: none;
                 }
            </style>

            <p style="font-size:1.25em">Hola %s 👋</p>

            <p style="font-size:1.25em">Te mando este correo automatizado para que informes al discursante de esta semana por WhatsApp. Solo presiona el botón de abajo y se abrirá WhatsApp automáticamente con los detalles del recordatorio. Recuerda que una vez se abra WhatsApp <strong>TIENES QUE DARLE A ENVIAR!!!</strong></p>

            <p><a class="button-primary" href="https://api.whatsapp.com/send?phone=%s&text=%s">Enviar Mensaje a %s</a></p>
        </div>
        """;
}
