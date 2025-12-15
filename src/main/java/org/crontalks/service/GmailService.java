package org.crontalks.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crontalks.constants.Messages;
import org.crontalks.exception.EmailRecipientNotInformedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;

import static org.crontalks.constants.Messages.EMAIL_DEFAULT_SUBJECT;
import static org.crontalks.constants.Messages.EMAIL_NOT_INFORMED_SUBJECT;
import static org.crontalks.constants.Messages.EMAIL_SENDING;
import static org.crontalks.constants.Messages.EMAIL_SENDING_TO_CURRENT;
import static org.crontalks.constants.Params.Scheduling.getSchedulingParam;
import static org.crontalks.entity.EmailTemplate.emailSpeakerNotInformedTemplate;
import static org.crontalks.entity.EmailTemplate.emailSpeakerTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmailService {

    private final GmailSmtpService emailService;
    private final SpeakerService speakerService;

    public ResponseEntity<?> sendMail(@RequestParam String to, @RequestParam String subject, @RequestParam String body) {
        try {
            log.info(EMAIL_SENDING, to);
            emailService.sendEmail(to, subject, body);

            return new ResponseEntity<>(String.format(Messages.EMAIL_SENT_CORRECTLY, to, body), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(String.format(Messages.ERROR_SENDING_EMAIL, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> sendMailCurrent() throws MessagingException, UnsupportedEncodingException {
        var scheduledTalk = speakerService.getCurrentScheduledTalk();

        if (scheduledTalk == null)
            return new ResponseEntity<>(Messages.ERROR_GETTING_DATA_FROM_GSHEET, HttpStatus.INTERNAL_SERVER_ERROR);

        var body = emailSpeakerTemplate(scheduledTalk);

        try {
            log.info(EMAIL_SENDING_TO_CURRENT);
            emailService.sendEmail(scheduledTalk.email(), EMAIL_DEFAULT_SUBJECT, body);
            log.info(Messages.EMAIL_SENT);
            return new ResponseEntity<>(String.format(Messages.EMAIL_SENT_CORRECTLY, scheduledTalk.email(), body), HttpStatus.OK);

        } catch (EmailRecipientNotInformedException e) {
            body = emailSpeakerNotInformedTemplate(scheduledTalk);
            emailService.sendEmail(getSchedulingParam().getOverseerEmail(), EMAIL_NOT_INFORMED_SUBJECT, body);
            return new ResponseEntity<>(String.format(Messages.ERROR_SENDING_EMAIL, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            return new ResponseEntity<>(String.format(Messages.ERROR_SENDING_EMAIL, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
