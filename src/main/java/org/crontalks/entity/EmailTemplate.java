package org.crontalks.entity;

import org.crontalks.constants.Params;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class EmailTemplate {

    public static String emailSpeakerTemplate(ScheduledTalk scheduledTalk) {
        return Params.Scheduling.getInstance().getReminderSpeakerTemplate().formatted(
            scheduledTalk.name(), // speaker
            Params.Scheduling.getInstance().getTalkOverseer(), // talk overseer
            scheduledTalk.localDateTime().format(DateTimeFormatter.ofPattern("EEEE dd/MM").withLocale(Locale.of("es", "ES"))), // meeting day, format -> domingo 02/12
            scheduledTalk.outlineNumber(), // outline number
            scheduledTalk.outlineTitle(), // outline title
            scheduledTalk.congregation(), // speaker congregation
            scheduledTalk.localDateTime().format(DateTimeFormatter.ofPattern("HH:mm")), // meeting time
            Params.Scheduling.getInstance().getCongregationAddress(), // congregation address
            Params.Scheduling.getInstance().getCongregationGMaps(), // congregation google maps
            Params.Scheduling.getInstance().getEmailFrom() // publics talks overseer email
        );
    }

    public static String emailSpeakerNotInformedTemplate(ScheduledTalk scheduledTalk) {
        return Params.Scheduling.getInstance().getReminderSpeakerNotInformedTemplate().formatted(
          scheduledTalk.name()
        );
    }
}
