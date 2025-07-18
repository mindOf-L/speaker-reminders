package org.crontalks.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crontalks.constants.Params;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.crontalks.constants.Messages.ERROR_SENDING_WHATSAPP;

@Slf4j
@RestController
@RequestMapping("/whatsapp")
@RequiredArgsConstructor
public class WhatsAppController {

    // TODO
    //  private final GmailSmtpService emailService;
    //  private final SpeakerService speakerService;

    @PostMapping("/test")
    public ResponseEntity<?> sendWhatsAppTest() {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(Params.WhatsApp.getInstance().getWhatsAppToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> template = new HashMap<>();
        template.put("name", Params.WhatsApp.getInstance().getWhatsAppTemplate());
        template.put("language", Map.of("code", "en_US"));

        Map<String, Object> body = new HashMap<>();
        body.put("messaging_product", "whatsapp");
        body.put("to", Params.WhatsApp.getInstance().getWhatsAppToPhoneNumber());
        body.put("type", "template");
        body.put("template", template);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        String url = Params.WhatsApp.getInstance().getWhatsAppUrl()
            .formatted(Params.WhatsApp.getInstance().getWhatsAppPhoneNumberId());

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            log.info("Response: {}", response.getBody());
            return response;
        } catch (HttpClientErrorException e) {
            log.error(ERROR_SENDING_WHATSAPP.formatted(e.getResponseBodyAsString()));
        }

        return new ResponseEntity<>(ERROR_SENDING_WHATSAPP.formatted(Params.WhatsApp.getInstance().getWhatsAppToPhoneNumber()), HttpStatus.BAD_REQUEST);
    }

}
