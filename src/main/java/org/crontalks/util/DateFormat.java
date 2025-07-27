package org.crontalks.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateFormat {

    public static String formatLongDateTalk(LocalDateTime localDateTime) {
        return localDateTime
            .format(DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM (dd/MM)")
            .withLocale(Locale.of("es", "ES")));
    }

    public static String formatShortDateTalk(LocalDateTime localDateTime) {
        return localDateTime
            .format(DateTimeFormatter.ofPattern("(dd/MMM)")
                .withLocale(Locale.of("es", "ES")));
    }

}
