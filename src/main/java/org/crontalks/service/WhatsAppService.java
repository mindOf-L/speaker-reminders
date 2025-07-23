package org.crontalks.service;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crontalks.entity.ScheduledTalk;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.crontalks.constants.Messages.WHATSAPP_SENT_CORRECTLY;
import static org.crontalks.constants.Params.Scheduling.getSchedulingParam;
import static org.crontalks.constants.Params.WhatsApp.createImmutableMap;
import static org.crontalks.constants.Params.WhatsApp.getWhatsAppParam;
import static org.crontalks.util.DateFormat.formatLongDateTalk;
import static org.crontalks.util.StringSplitter.splitName;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhatsAppService {

    private final SpeakerService speakerService;

    public String sendWhatsAppTest(String speakerPhone) throws HttpClientErrorException {
        var scheduledTalk = speakerService.getCurrentScheduledTalk();

        String uri = getWhatsAppParam().getWhatsAppUrl().formatted(getWhatsAppParam().getWhatsAppPhoneNumberId());

        RestTemplate restTemplate = new RestTemplate();
        // first message
        var componentsFirstMessage = setComponentsForFirstTemplate(scheduledTalk);
        HttpEntity<?> requestFirstMessage = buildWhatsAppRequest(speakerPhone, getWhatsAppParam().getWhatsAppTemplateNameFirst(),componentsFirstMessage);
        ResponseEntity<String> responseFirst = restTemplate.postForEntity(uri, requestFirstMessage, String.class);
        log.info("Response: {}", responseFirst.getBody());

        // second message
        // first message
        var componentsSecondMessage = setComponentsForSecondTemplate(scheduledTalk);
        HttpEntity<?> requestSecondMessage = buildWhatsAppRequest(speakerPhone, getWhatsAppParam().getWhatsAppTemplateNameSecond(), componentsSecondMessage);
        ResponseEntity<String> responseSecond = restTemplate.postForEntity(uri, requestSecondMessage, String.class);
        log.info("Response: {}", responseSecond.getBody());

        return String.format(WHATSAPP_SENT_CORRECTLY, speakerPhone);
    }

    private HttpEntity<?> buildWhatsAppRequest(String speakerPhone, String templateName, ImmutableMap<?,?> components) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getWhatsAppParam().getWhatsAppToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> template = new LinkedHashMap<>();
        template.put("name", templateName);
        template.put("language", ImmutableMap.of("code", "es"));
        template.put("components", List.of(components));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("messaging_product", "whatsapp");
        body.put("recipient_type", "individual");
        body.put("to", speakerPhone);
        body.put("type", "template");
        body.put("template", template);

        return new HttpEntity<>(body, headers);
    }

    private ImmutableMap<String, Object> setComponentsForFirstTemplate(ScheduledTalk scheduledTalk) {
        return ImmutableMap.of(
            "type", "body",
            "parameters", List.of(
                createImmutableMap("speaker_name", splitName(scheduledTalk.name())),
                createImmutableMap("overseer_name", getSchedulingParam().getTalksOverseer()),
                createImmutableMap("talk_date", formatLongDateTalk(scheduledTalk.localDateTime())),
                createImmutableMap("outline_number", String.valueOf(scheduledTalk.outlineNumber())),
                createImmutableMap("outline_title", scheduledTalk.outlineTitle()),
                createImmutableMap("speaker_congregation", scheduledTalk.congregation()),
                createImmutableMap("congregation_time", getSchedulingParam().getMeetingTime()),
                createImmutableMap("congregation_address", getSchedulingParam().getCongregationAddress()),
                createImmutableMap("congregation_gmap", getSchedulingParam().getCongregationGMaps())
            )
        );
    }

    private ImmutableMap<String, Object> setComponentsForSecondTemplate(ScheduledTalk scheduledTalk) {
        String speakerImages = String.format(scheduledTalk.outlineHasImages()
                ? getWhatsAppParam().getOutlineImagesTemplateWhatsApp()
                : getWhatsAppParam().getSpeakerCustomImagesTemplateWhatsApp(),
            getSchedulingParam().getVideoDeptEmail(),
            getSchedulingParam().getVideoDeptOverseerName(),
            getSchedulingParam().getVideoDeptOverseerPhone());

        return ImmutableMap.of(
            "type", "body",
            "parameters", List.of(
                createImmutableMap("speaker_images", speakerImages)
            )
        );
    }
}
