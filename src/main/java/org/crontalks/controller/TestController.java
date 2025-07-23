package org.crontalks.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.crontalks.constants.Messages;
import org.crontalks.service.GmailSmtpService;
import org.crontalks.service.SpeakerService;
import org.crontalks.service.WhatsAppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import static org.crontalks.constants.Messages.ERROR_SENDING_WHATSAPP;
import static org.crontalks.constants.Params.Scheduling.getSchedulingParam;
import static org.crontalks.constants.Params.WhatsApp.getWhatsAppParam;
import static org.crontalks.entity.EmailTemplate.emailSpeakerTemplate;
import static org.crontalks.entity.WhatsAppTemplate.whatsAppSpeakerTemplate;

@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final GmailSmtpService emailService;
    private final WhatsAppService whatsAppService;
    private final SpeakerService speakerService;

    @GetMapping("/email")
    public ResponseEntity<?> getMailTest() {

        var scheduledTalk = speakerService.getCurrentScheduledTalk();
        var body = emailSpeakerTemplate(scheduledTalk);
        return new ResponseEntity<>(body, HttpStatus.OK);

    }

    @GetMapping("/whatsapp")
    public ResponseEntity<?> getWhatsAppTest() {

        var scheduledTalk = speakerService.getCurrentScheduledTalk();
        var body = whatsAppSpeakerTemplate(scheduledTalk);
        return new ResponseEntity<>(body, HttpStatus.OK);

    }

    @PostMapping("/email")
    public ResponseEntity<?> sendMailTest(
        @RequestParam(required = false) String to,
        @RequestParam String subject,
        @RequestParam(required = false) String[] cc) {

        var scheduledTalk = speakerService.getCurrentScheduledTalk();
        var body = emailSpeakerTemplate(scheduledTalk);

        if (StringUtils.isBlank(to))
            to = getSchedulingParam().getOverseerEmail();

        try {
            emailService.sendEmail(to, "TEST! " + subject, cc, body);
            return new ResponseEntity<>(String.format(Messages.EMAIL_SENT_CORRECTLY, to, body), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(String.format(Messages.ERROR_SENDING_EMAIL, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/whatsapp")
    public ResponseEntity<?> sendWhatsAppTest() {
        try {
            var response = whatsAppService.sendWhatsAppTest(getWhatsAppParam().getWhatsAppTestPhoneNumber());
            log.info("Response: {}", response);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (HttpClientErrorException e) {
            var error = String.format(ERROR_SENDING_WHATSAPP, getWhatsAppParam().getWhatsAppTestPhoneNumber());
            log.error(error);
            log.error(e.getMessage());
        }

        return new ResponseEntity<>(ERROR_SENDING_WHATSAPP.formatted(getWhatsAppParam().getWhatsAppTestPhoneNumber()), HttpStatus.BAD_REQUEST);    }

}
