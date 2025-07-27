package org.crontalks.entity;

import org.crontalks.constants.Params;

import java.time.format.DateTimeFormatter;

import static org.crontalks.constants.Params.Scheduling.getSchedulingParam;
import static org.crontalks.util.DateFormat.formatLongDateTalk;
import static org.crontalks.util.DateFormat.formatShortDateTalk;
import static org.crontalks.util.StringSplitter.splitName;

public class EmailTemplate {

    public static String emailSpeakerTemplate(ScheduledTalk scheduledTalk) {
        return processTemplate(Params.Scheduling.getReminderSpeakerTemplateEmail(), scheduledTalk);
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
            processTemplateImages(scheduledTalk)
        );
    }

    public static String emailSpeakerNotInformedTemplate(ScheduledTalk scheduledTalk) {
        return Params.Scheduling.getReminderSpeakerNotInformedTemplate().formatted(
          scheduledTalk.name()
        );
    }

    private static String processTemplateImages(ScheduledTalk scheduledTalk) {
        return scheduledTalk.outlineHasImages()
            ? Params.Scheduling.getReminderSpeakerTemplateOutlineImages().formatted(
            getSchedulingParam().getVideoDeptEmail(), // video dept email
                scheduledTalk.outlineNumber(), // outline number for email subject
                formatShortDateTalk(scheduledTalk.localDateTime()), // short date for email subject
                getSchedulingParam().getVideoDeptEmail()) // video dept email)
            : Params.Scheduling.getReminderSpeakerTemplateCustomImages().formatted(
            getSchedulingParam().getVideoDeptEmail(), // video dept email
                scheduledTalk.outlineNumber(), // outline number for email subject
                formatShortDateTalk(scheduledTalk.localDateTime()), // short date for email subject
                getSchedulingParam().getVideoDeptEmail()); // video dept email)
    }
}
