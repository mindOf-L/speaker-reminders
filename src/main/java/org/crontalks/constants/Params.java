package org.crontalks.constants;

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

        @Getter
        private static GSheets Instance;

        @PostConstruct
        public void init() {
            GSheets.Instance = this;
        }

        @Value("${google.sheet}")
        private String sheet;

        @Value("${google.speaker-sheet}")
        private String thisWeekSpeaker;
    }

    @Getter
    @Component
    @NoArgsConstructor
    public static class Scheduling {

        @Getter
        private static Scheduling Instance;

        @PostConstruct
        public void init() {
            Scheduling.Instance = this;
        }

        @Value("${email.from}")
        public String emailFrom;

        @Value("${email.from}")
        public String overseerEmail;

        @Value("${email.cc}")
        public String[] emailCC;

        @Value("${MEETING-TIME:12:30}")
        public String meetingTime;

        @Value("${TALK-OVERSEER}")
        public String talkOverseer;

        @Value("${CONGREGATION-ADDRESS}")
        public String congregationAddress;

        @Value("${CONGREGATION-GMAPS}")
        public String congregationGMaps;

        @Getter
        private final String reminderSpeakerTemplate = """
            Hola %s. 👋
            
            Soy %s de la congregación Veredillas de Torrejón de Ardoz, encantado de saludarte. 😀
            
            Según los planes de discursos, te esperamos este %s para escuchar el bosquejo con el tema *N° %s*, con el título *%s*.
            
            *Congregación: %s*
            
            La reunión comienza el domingo a las %s y la dirección es %s.
            Puedes consultar la dirección en Google Maps: %s
            
            Agradecería que si puedes lo antes posible me confirmaras:
            
            ▶️ Si los datos son correctos.
            
            ➡️ La canción que usarás.
            
            ⏺️ Si utilizarás imágenes. En ese caso envíamelas por favor a %s con alguna indicación de cuándo ponerlas y quitarlas. En cuanto las tenga, te confirmo que he recibido el correo.
            
            Un fuerte abrazo.
            """;

        @Getter
        private final String reminderSpeakerNotInformedTemplate = """
            Hola.
            
            He intentado enviar un correo a %s pero no he podido.
            
            Por favor, echa un vistazo a la tabla de Google Sheets y asegúrate de que tengas los datos correctos. Parece que el campo de correo electrónico está vacío o no es válido.
            
            Corrige el dato y lo intentaré de nuevo en 24 horas.
            
            Saludos de tu app!
            """;
    }

}
