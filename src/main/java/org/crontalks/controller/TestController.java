package org.crontalks.controller;

import lombok.RequiredArgsConstructor;
import org.crontalks.constants.Messages;
import org.crontalks.service.GmailSmtpService;
import org.crontalks.service.SpeakerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.crontalks.constants.Messages.NOT_IMPLEMENTED_YET;
import static org.crontalks.entity.EmailTemplate.emailSpeakerTemplate;
import static org.crontalks.entity.EmailTemplate.whatsAppSpeakerTemplate;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final GmailSmtpService emailService;
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
        @RequestParam String to,
        @RequestParam String subject,
        @RequestParam(required = false) String[] cc) {

        var scheduledTalk = speakerService.getCurrentScheduledTalk();
        var body = emailSpeakerTemplate(scheduledTalk);

        try {
            emailService.sendEmail(to, "TEST! " + subject, cc, body);
            return new ResponseEntity<>(String.format(Messages.EMAIL_SENT_CORRECTLY, to, body), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(String.format(Messages.ERROR_SENDING_EMAIL, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/whatsapp")
    public ResponseEntity<?> sendMailTest(@RequestParam String to) {
        return new ResponseEntity<>(NOT_IMPLEMENTED_YET, HttpStatus.NOT_IMPLEMENTED);
    }

}
