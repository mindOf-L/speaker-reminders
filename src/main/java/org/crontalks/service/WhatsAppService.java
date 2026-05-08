package org.crontalks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crontalks.constants.SchedulingProperties;
import org.crontalks.constants.WhatsAppProperties;
import org.crontalks.entity.ScheduledTalk;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.crontalks.constants.Messages.WHATSAPP_SENT_CORRECTLY;
import static org.crontalks.util.DateFormat.formatLongDateTalk;
import static org.crontalks.util.StringSplitter.splitName;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhatsAppService {

    private final SpeakerService speakerService;
    private final RestTemplate restTemplate;
    private final WhatsAppProperties whatsAppProperties;
    private final SchedulingProperties schedulingProperties;

    public String sendWhatsAppTest(String speakerPhone) throws RestClientException {
        var scheduledTalk = speakerService.getCurrentScheduledTalk();

        String uri = whatsAppProperties.getWhatsAppUrl().formatted(whatsAppProperties.getWhatsAppPhoneNumberId());

        try {
            // first message
            var componentsFirstMessage = setComponentsForFirstTemplate(scheduledTalk);
            HttpEntity<?> requestFirstMessage = buildWhatsAppRequest(speakerPhone, whatsAppProperties.getWhatsAppTemplateNameFirst(), componentsFirstMessage);
            ResponseEntity<String> responseFirst = restTemplate.postForEntity(uri, requestFirstMessage, String.class);
            log.info("Response First: {}", responseFirst.getBody());

            // second message
            var componentsSecondMessage = setComponentsForSecondTemplate(scheduledTalk);
            HttpEntity<?> requestSecondMessage = buildWhatsAppRequest(speakerPhone, whatsAppProperties.getWhatsAppTemplateNameSecond(), componentsSecondMessage);
            ResponseEntity<String> responseSecond = restTemplate.postForEntity(uri, requestSecondMessage, String.class);
            log.info("Response Second: {}", responseSecond.getBody());

            return String.format(WHATSAPP_SENT_CORRECTLY, speakerPhone);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error from WhatsApp API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (RestClientException e) {
            log.error("Network or timeout error calling WhatsApp API: {}", e.getMessage());
            throw e;
        }
    }

    private HttpEntity<?> buildWhatsAppRequest(String speakerPhone, String templateName, Map<?, ?> components) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(whatsAppProperties.getWhatsAppToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> template = new LinkedHashMap<>();
        template.put("name", templateName);
        template.put("language", Map.of("code", "es"));
        template.put("components", List.of(components));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("messaging_product", "whatsapp");
        body.put("recipient_type", "individual");
        body.put("to", speakerPhone);
        body.put("type", "template");
        body.put("template", template);

        return new HttpEntity<>(body, headers);
    }

    private Map<String, Object> setComponentsForFirstTemplate(ScheduledTalk scheduledTalk) {
        return Map.of(
            "type", "body",
            "parameters", List.of(
                whatsAppProperties.createMap("speaker_name", splitName(scheduledTalk.name())),
                whatsAppProperties.createMap("overseer_name", schedulingProperties.getTalksOverseer()),
                whatsAppProperties.createMap("talk_date", formatLongDateTalk(scheduledTalk.localDateTime())),
                whatsAppProperties.createMap("outline_number", String.valueOf(scheduledTalk.outlineNumber())),
                whatsAppProperties.createMap("outline_title", scheduledTalk.outlineTitle()),
                whatsAppProperties.createMap("speaker_congregation", scheduledTalk.congregation()),
                whatsAppProperties.createMap("congregation_time", schedulingProperties.getMeetingTime()),
                whatsAppProperties.createMap("congregation_address", schedulingProperties.getCongregationAddress()),
                whatsAppProperties.createMap("congregation_gmap", schedulingProperties.getCongregationGMaps())
            )
        );
    }

    private Map<String, Object> setComponentsForSecondTemplate(ScheduledTalk scheduledTalk) {
        String speakerImages = String.format(scheduledTalk.outlineHasImages()
                ? whatsAppProperties.getOutlineImagesTemplateWhatsApp()
                : whatsAppProperties.getSpeakerCustomImagesTemplateWhatsApp(),
            schedulingProperties.getVideoDeptEmail(),
            schedulingProperties.getVideoDeptOverseerName(),
            schedulingProperties.getVideoDeptOverseerPhone());

        return Map.of(
            "type", "body",
            "parameters", List.of(
                whatsAppProperties.createMap("speaker_images", speakerImages)
            )
        );
    }
}
