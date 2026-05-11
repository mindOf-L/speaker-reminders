package org.crontalks.entity;

import lombok.RequiredArgsConstructor;
import org.crontalks.constants.EmailTemplates;
import org.crontalks.constants.SchedulingProperties;
import org.crontalks.constants.WhatsAppProperties;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

import static org.crontalks.util.DateFormat.formatLongDateTalk;
import static org.crontalks.util.DateFormat.formatShortDateTalk;
import static org.crontalks.util.StringSplitter.splitName;

@Component
@RequiredArgsConstructor
public class EmailTemplate {

    private final SchedulingProperties schedulingProperties;
    private final WhatsAppProperties whatsAppProperties;
    private final EmailTemplates emailTemplates;

    public String processEmailSpeakerTemplate(ScheduledTalk scheduledTalk) {
        return emailTemplates.getReminderSpeakerTemplateEmail().formatted(
            splitName(scheduledTalk.name()), // speaker
            schedulingProperties.getTalksOverseer(), // talk overseer
            formatLongDateTalk(scheduledTalk.localDateTime()), // meeting day, format -> domingo 02/12
            scheduledTalk.outlineNumber(), // outline number
            scheduledTalk.outlineTitle(), // outline title
            scheduledTalk.congregation(), // speaker congregation
            scheduledTalk.localDateTime().format(DateTimeFormatter.ofPattern("HH:mm")), // meeting time
            schedulingProperties.getCongregationAddress(), // congregation address
            schedulingProperties.getCongregationGMaps(), // congregation google maps
            processTemplateImages(
                scheduledTalk,
                emailTemplates.getReminderSpeakerTemplateOutlineImages(),
                emailTemplates.getReminderSpeakerTemplateCustomImages()),
            processTemplateVideo(
                scheduledTalk,
                emailTemplates.getReminderSpeakerTemplateOutlineVideos())
        );
    }

    public String emailSpeakerNotInformedTemplate(ScheduledTalk scheduledTalk) {
        return emailTemplates.getReminderSpeakerNotInformedTemplate().formatted(
          scheduledTalk.name()
        );
    }

    private String processTemplateImages(ScheduledTalk scheduledTalk, String templateWithOutlineImages, String templateWithCustomImages) {
        return scheduledTalk.outlineHasImages()
            ? templateWithOutlineImages.formatted(
            schedulingProperties.getVideoDeptEmail(),
                scheduledTalk.outlineNumber(),
                formatShortDateTalk(scheduledTalk.localDateTime()),
                schedulingProperties.getVideoDeptEmail())
            : templateWithCustomImages.formatted(
            schedulingProperties.getVideoDeptEmail(),
                scheduledTalk.outlineNumber(),
                formatShortDateTalk(scheduledTalk.localDateTime()),
                schedulingProperties.getVideoDeptEmail());
    }

    private String processTemplateVideo(ScheduledTalk scheduledTalk, String template) {
        return scheduledTalk.outlineHasVideo()
            ? template
            : "";
    }

    public String emailEmptyData() {
        return emailTemplates.getEmailEmptyData();
    }

    public String emailSomeEmptyData() {
        return emailTemplates.getEmailSomeEmptyData();
    }

    public String processEmailSpeakerWhatsAppTemplate(ScheduledTalk scheduledTalk) {
        return emailTemplates.getRemiderSpeakerTemplateWhatsApp().formatted(
            schedulingProperties.getTalksOverseer(),
            String.format("34%s", scheduledTalk.phoneNumber()).replace(" ", ""),
            encodeSkippingEmojis(processWhatsAppMessageTemplate(scheduledTalk)),
            splitName(scheduledTalk.name())
        );
    }

    public String processWhatsAppMessageTemplate(ScheduledTalk scheduledTalk) {
        return whatsAppProperties.getRemiderSpeakerTemplateWhatsApp().formatted(
            splitName(scheduledTalk.name()), // speaker
            schedulingProperties.getTalksOverseer(), // talk overseer
            formatLongDateTalk(scheduledTalk.localDateTime()), // meeting day, format -> domingo 02/12
            scheduledTalk.outlineNumber(), // outline number
            scheduledTalk.outlineTitle(), // outline title
            scheduledTalk.congregation(), // speaker congregation
            scheduledTalk.localDateTime().format(DateTimeFormatter.ofPattern("HH:mm")), // meeting time
            schedulingProperties.getCongregationAddress(), // congregation address
            schedulingProperties.getCongregationGMaps(), // congregation google maps
            processTemplateImages(
                scheduledTalk,
                whatsAppProperties.getOutlineImagesTemplateWhatsApp(),
                whatsAppProperties.getSpeakerCustomImagesTemplateWhatsApp()),
            processTemplateVideo(
                scheduledTalk,
                whatsAppProperties.getReminderSpeakerWhatsAppOutlineVideosTemplate()
            )
        );
    }

    private String encodeSkippingEmojis(String input) {
        StringBuilder sb = new StringBuilder();

        input.codePoints().forEach(cp -> {
            String ch = new String(Character.toChars(cp));
            sb.append(isEmoji(cp) ? ch: URLEncoder.encode(ch, StandardCharsets.UTF_8));
        });

        return sb.toString();
    }

    private boolean isEmoji(int codePoint) {
        return
            (codePoint >= 0x1F600 && codePoint <= 0x1F64F) || // emoticons
            (codePoint >= 0x1F300 && codePoint <= 0x1F5FF) || // symbols & pictographs
            (codePoint >= 0x1F680 && codePoint <= 0x1F6FF) || // transport
            (codePoint >= 0x2600  && codePoint <= 0x26FF)  || // misc symbols
            (codePoint >= 0x2700  && codePoint <= 0x27BF)  || // dingbats
            (codePoint >= 0x1F900 && codePoint <= 0x1F9FF);
    }
}
