package org.crontalks.entity;

import java.time.format.DateTimeFormatter;

import static org.crontalks.constants.Params.Scheduling.getSchedulingParam;
import static org.crontalks.util.DateFormat.formatLongDateTalk;
import static org.crontalks.util.StringSplitter.splitName;

public class EmailTemplate {

    public static String emailSpeakerTemplate(ScheduledTalk scheduledTalk) {
        return processTemplate(getSchedulingParam().getReminderSpeakerTemplateEmail(), scheduledTalk);
    }

    private static String processTemplate(String template, ScheduledTalk scheduledTalk) {
        return template.formatted(
            splitName(scheduledTalk.name()), // speaker
            getSchedulingParam().getTalksOverseer(), // talk overseer
            formatLongDateTalk(scheduledTalk.localDateTime()), // meeting day, format -> domingo 02/12
            scheduledTalk.outlineNumber(), // outline number
            scheduledTalk.outlineTitle(), // outline title
            scheduledTalk.congregation(), // speaker congregation
            scheduledTalk.localDateTime().format(DateTimeFormatter.ofPattern("HH:mm")), // meeting time
            getSchedulingParam().getCongregationAddress(), // congregation address
            getSchedulingParam().getCongregationGMaps(), // congregation google maps
            getSchedulingParam().getEmailFrom() // publics talks overseer email
        );
    }

    public static String emailSpeakerNotInformedTemplate(ScheduledTalk scheduledTalk) {
        return getSchedulingParam().getReminderSpeakerNotInformedTemplate().formatted(
          scheduledTalk.name()
        );
    }
}
