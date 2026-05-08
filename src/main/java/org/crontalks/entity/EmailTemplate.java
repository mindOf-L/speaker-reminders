package org.crontalks.entity;

import lombok.RequiredArgsConstructor;
import org.crontalks.constants.SchedulingProperties;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

import static org.crontalks.util.DateFormat.formatLongDateTalk;
import static org.crontalks.util.DateFormat.formatShortDateTalk;
import static org.crontalks.util.StringSplitter.splitName;

@Component
@RequiredArgsConstructor
public class EmailTemplate {

    private final SchedulingProperties schedulingProperties;

    public String emailSpeakerTemplate(ScheduledTalk scheduledTalk) {
        return processTemplate(schedulingProperties.getReminderSpeakerTemplateEmail(), scheduledTalk);
    }

    private String processTemplate(String template, ScheduledTalk scheduledTalk) {
        return template.formatted(
            splitName(scheduledTalk.name()), // speaker
            schedulingProperties.getTalksOverseer(), // talk overseer
            formatLongDateTalk(scheduledTalk.localDateTime()), // meeting day, format -> domingo 02/12
            scheduledTalk.outlineNumber(), // outline number
            scheduledTalk.outlineTitle(), // outline title
            scheduledTalk.congregation(), // speaker congregation
            scheduledTalk.localDateTime().format(DateTimeFormatter.ofPattern("HH:mm")), // meeting time
            schedulingProperties.getCongregationAddress(), // congregation address
            schedulingProperties.getCongregationGMaps(), // congregation google maps
            processTemplateImages(scheduledTalk),
            processTemplateVideo(scheduledTalk)
        );
    }

    public String emailSpeakerNotInformedTemplate(ScheduledTalk scheduledTalk) {
        return schedulingProperties.getReminderSpeakerNotInformedTemplate().formatted(
          scheduledTalk.name()
        );
    }

    private String processTemplateImages(ScheduledTalk scheduledTalk) {
        return scheduledTalk.outlineHasImages()
            ? schedulingProperties.getReminderSpeakerTemplateOutlineImages().formatted(
            schedulingProperties.getVideoDeptEmail(),
                scheduledTalk.outlineNumber(),
                formatShortDateTalk(scheduledTalk.localDateTime()),
                schedulingProperties.getVideoDeptEmail())
            : schedulingProperties.getReminderSpeakerTemplateCustomImages().formatted(
            schedulingProperties.getVideoDeptEmail(),
                scheduledTalk.outlineNumber(),
                formatShortDateTalk(scheduledTalk.localDateTime()),
                schedulingProperties.getVideoDeptEmail());
    }

    private String processTemplateVideo(ScheduledTalk scheduledTalk) {
        return scheduledTalk.outlineHasVideo()
            ? schedulingProperties.getReminderSpeakerTemplateOutlineVideos()
            : "";
    }

    public String emailEmptyData() {
        return schedulingProperties.getEmailEmptyData();
    }

    public String emailSomeEmptyData() {
        return schedulingProperties.getEmailSomeEmptyData();
    }
}
