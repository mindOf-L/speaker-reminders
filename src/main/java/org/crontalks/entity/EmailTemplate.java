package org.crontalks.entity;

import org.crontalks.constants.Params;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.crontalks.util.StringSplitter.splitName;

public class EmailTemplate {

    public static String emailSpeakerTemplate(ScheduledTalk scheduledTalk) {
        return processTemplate(Params.Scheduling.getInstance().getReminderSpeakerTemplateEmail(), scheduledTalk);
    }

    public static String whatsAppSpeakerTemplate(ScheduledTalk scheduledTalk) {
        return processTemplate(Params.Scheduling.getInstance().getReminderSpeakerTemplateWhatsApp(), scheduledTalk);
    }

    private static String processTemplate(String template, ScheduledTalk scheduledTalk) {
        return template.formatted(
            splitName(scheduledTalk.name()), // speaker
            Params.Scheduling.getInstance().getTalksOverseer(), // talk overseer
            scheduledTalk.localDateTime().format(DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM (dd/MM)").withLocale(Locale.of("es", "ES"))), // meeting day, format -> domingo 02/12
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
