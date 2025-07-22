package org.crontalks.entity;

import static org.crontalks.constants.Params.Scheduling.getSchedulingParam;
import static org.crontalks.constants.Params.WhatsApp.getWhatsAppParam;
import static org.crontalks.util.DateFormat.formatLongDateTalk;
import static org.crontalks.util.StringSplitter.splitName;

public class WhatsAppTemplate {

    public static String whatsAppSpeakerTemplate(ScheduledTalk scheduledTalk) {
        return processWhatsAppTemplate(getWhatsAppParam().getReminderSpeakerTemplateWhatsAppV2(), scheduledTalk);
    }

    private static String processWhatsAppTemplate(String template, ScheduledTalk scheduledTalk) {
        String speakerImages = String.format(scheduledTalk.outlineHasImages()
                ? getWhatsAppParam().getOutlineImagesTemplateWhatsApp()
                : getWhatsAppParam().getSpeakerCustomImagesTemplateWhatsApp(),
            getSchedulingParam().getVideoDeptEmail(),
            getSchedulingParam().getVideoDeptOverseerName(),
            getSchedulingParam().getVideoDeptOverseerPhone());

        return template
                .replace("{{speaker_name}}", splitName(scheduledTalk.name()))
                .replace("{{overseer_name}}", getSchedulingParam().getTalksOverseer())
                .replace("{{talk_date}}", formatLongDateTalk(scheduledTalk.localDateTime()))
                .replace("{{outline_number}}", String.valueOf(scheduledTalk.outlineNumber()))
                .replace("{{outline_title}}", scheduledTalk.outlineTitle())
                .replace("{{speaker_congregation}}", scheduledTalk.congregation())
                .replace("{{congregation_time}}", getSchedulingParam().getMeetingTime())
                .replace("{{congregation_address}}", getSchedulingParam().getCongregationAddress())
                .replace("{{congregation_gmap}}", getSchedulingParam().getCongregationGMaps())
                .replace("{{speaker_images}}", speakerImages);

    }

}
