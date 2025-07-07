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

        @Value("${email.from}")
        public String emailFrom;

        @Value("${email.cc}")
        public String[] emailCC;

        @Getter
        private final String reminderTemplate = """
            Hola %s. 👋
            
            Soy %s de la congregación Veredillas de Torrejón de Ardoz, encantado de saludarte. 😀
            
            Según los planes de discursos, te esperamos este %s para escuchar el bosquejo con el tema *N° %s*, con el título *%s*.
            
            *Congregación: Veredillas- Torrejón de Ardoz*
            
            La reunión comienza el domingo a las 12:30 y la dirección es C. Álamo, 37, 28850 Torrejón de Ardoz.
            https://maps.app.goo.gl/1ds9mg7UQWU6XFjt5
            
            Agradecería que si puedes lo antes posible me confirmaras:
            
            ▶️ Si los datos son correctos.
            
            ➡️ La canción que usarás.
            
            ⏺️ Si utilizarás imágenes. En ese caso envíamelas por favor a %s con alguna indicación de cuándo ponerlas y quitarlas. En cuanto las tenga, te confirmo que he recibido el correo.
            
            Un fuerte abrazo.
            """;
    }

}
