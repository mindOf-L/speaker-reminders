package org.crontalks.controller;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.crontalks.constants.Messages;
import org.crontalks.constants.Params;
import org.crontalks.exception.EmailRecipientNotInformedException;
import org.crontalks.service.GmailSmtpService;
import org.crontalks.service.SpeakerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

import static org.crontalks.constants.Messages.EMAIL_DEFAULT_SUBJECT;
import static org.crontalks.constants.Messages.EMAIL_NOT_INFORMED_SUBJECT;
import static org.crontalks.entity.EmailTemplate.emailSpeakerNotInformedTemplate;
import static org.crontalks.entity.EmailTemplate.emailSpeakerTemplate;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GmailController {

    private final GmailSmtpService emailService;
    private final SpeakerService speakerService;

    @PostMapping("/email")
    public ResponseEntity<?> sendMail(@RequestParam String to, @RequestParam String subject, @RequestParam String body) {
        try {
            emailService.sendEmail(to, subject, body);

            return new ResponseEntity<>(String.format(Messages.EMAIL_SENT_CORRECTLY, to, body), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(String.format(Messages.ERROR_SENDING_EMAIL, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/email/speaker/current")
    public ResponseEntity<?> sendMail() throws MessagingException, UnsupportedEncodingException {
        var scheduledTalk = speakerService.getCurrentScheduledTalk();

        if (scheduledTalk == null)
            return new ResponseEntity<>(Messages.ERROR_GETTING_DATA_FROM_GSHEET, HttpStatus.INTERNAL_SERVER_ERROR);

        var body = emailSpeakerTemplate(scheduledTalk);

        try {
            emailService.sendEmail(scheduledTalk.email(), EMAIL_DEFAULT_SUBJECT, body);
            return new ResponseEntity<>(String.format(Messages.EMAIL_SENT_CORRECTLY, scheduledTalk.email(), body), HttpStatus.OK);

        } catch (EmailRecipientNotInformedException e) {
            body = emailSpeakerNotInformedTemplate(scheduledTalk);
            emailService.sendEmail(Params.Scheduling.getInstance().getOverseerEmail(), EMAIL_NOT_INFORMED_SUBJECT, body);
            return new ResponseEntity<>(String.format(Messages.ERROR_SENDING_EMAIL, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            return new ResponseEntity<>(String.format(Messages.ERROR_SENDING_EMAIL, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/speaker")
    public ResponseEntity<?> speaker() {
        var scheduledTalk = speakerService.getCurrentScheduledTalk();

        if (scheduledTalk == null)
            return new ResponseEntity<>(Messages.ERROR_GETTING_DATA_FROM_GSHEET, HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(scheduledTalk, HttpStatus.OK);
    }
}
