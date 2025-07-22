package org.crontalks.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public String sendWhatsAppTest() throws JsonProcessingException, HttpClientErrorException {
        String uri = getWhatsAppParam().getWhatsAppUrl()
            .formatted(getWhatsAppParam().getWhatsAppPhoneNumberId());

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(uri, buildWhatsAppRequest(), String.class);
        log.info("Response: {}", response.getBody());

        return response.getBody();
    }

    private HttpEntity<?> buildWhatsAppRequest() throws JsonProcessingException {
        var scheduledTalk = speakerService.getCurrentScheduledTalk();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getWhatsAppParam().getWhatsAppToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        String speakerImages = String.format(scheduledTalk.outlineHasImages()
                ? getWhatsAppParam().getOutlineImagesTemplateWhatsApp()
                : getWhatsAppParam().getSpeakerCustomImagesTemplateWhatsApp(),
            getSchedulingParam().getVideoDeptEmail(),
            getSchedulingParam().getVideoDeptOverseerName(),
            getSchedulingParam().getVideoDeptOverseerPhone());

        Map<String, Object> template = new LinkedHashMap<>();
        template.put("name", getWhatsAppParam().getWhatsAppTemplate());
        template.put("language", ImmutableMap.of("code", "es"));
        template.put("components", List.of(
            ImmutableMap.of(
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
                    createImmutableMap("congregation_gmap", getSchedulingParam().getCongregationGMaps()),
                    createImmutableMap("speaker_images", speakerImages)
                )
            )
        ));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("messaging_product", "whatsapp");
        body.put("recipient_type", "individual");
        body.put("to", getWhatsAppParam().getWhatsAppTestPhoneNumber());
        body.put("type", "template");
        body.put("template", template);

        return new HttpEntity<>(new ObjectMapper().writeValueAsString(body), headers);
    }
}
