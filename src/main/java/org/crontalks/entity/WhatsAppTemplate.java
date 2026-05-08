package org.crontalks.entity;

import lombok.RequiredArgsConstructor;
import org.crontalks.constants.SchedulingProperties;
import org.crontalks.constants.WhatsAppProperties;
import org.springframework.stereotype.Component;

import static org.crontalks.util.DateFormat.formatLongDateTalk;
import static org.crontalks.util.StringSplitter.splitName;

@Component
@RequiredArgsConstructor
public class WhatsAppTemplate {

    private final SchedulingProperties schedulingProperties;
    private final WhatsAppProperties whatsAppProperties;

    public String whatsAppSpeakerTemplate(ScheduledTalk scheduledTalk) {
        return processWhatsAppTemplate(whatsAppProperties.getReminderSpeakerTemplateWhatsAppV2(), scheduledTalk);
    }

    private String processWhatsAppTemplate(String template, ScheduledTalk scheduledTalk) {
        String speakerImages = String.format(scheduledTalk.outlineHasImages()
                ? whatsAppProperties.getOutlineImagesTemplateWhatsApp()
                : whatsAppProperties.getSpeakerCustomImagesTemplateWhatsApp(),
            schedulingProperties.getVideoDeptEmail(),
            schedulingProperties.getVideoDeptOverseerName(),
            schedulingProperties.getVideoDeptOverseerPhone());

        return template
                .replace("{{speaker_name}}", splitName(scheduledTalk.name()))
                .replace("{{overseer_name}}", schedulingProperties.getTalksOverseer())
                .replace("{{talk_date}}", formatLongDateTalk(scheduledTalk.localDateTime()))
                .replace("{{outline_number}}", String.valueOf(scheduledTalk.outlineNumber()))
                .replace("{{outline_title}}", scheduledTalk.outlineTitle())
                .replace("{{speaker_congregation}}", scheduledTalk.congregation())
                .replace("{{congregation_time}}", schedulingProperties.getMeetingTime())
                .replace("{{congregation_address}}", schedulingProperties.getCongregationAddress())
                .replace("{{congregation_gmap}}", schedulingProperties.getCongregationGMaps())
                .replace("{{speaker_images}}", speakerImages);

    }

}
