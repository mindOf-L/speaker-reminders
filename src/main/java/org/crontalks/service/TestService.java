package org.crontalks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.crontalks.constants.Messages;
import org.crontalks.constants.Params;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import static org.crontalks.constants.Messages.EMAIL_NOT_INFORMED_SUBJECT;
import static org.crontalks.constants.Messages.EMAIL_TEST_SUBJECT;
import static org.crontalks.constants.Messages.ERROR_SENDING_EMAIL;
import static org.crontalks.constants.Messages.ERROR_SENDING_EMAIL_TO;
import static org.crontalks.constants.Messages.ERROR_SENDING_WHATSAPP;
import static org.crontalks.constants.Params.Scheduling.getSchedulingParam;
import static org.crontalks.constants.Params.WhatsApp.getWhatsAppParam;
import static org.crontalks.entity.EmailTemplate.emailSpeakerTemplate;
import static org.crontalks.entity.WhatsAppTemplate.whatsAppSpeakerTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

    private final GmailSmtpService emailService;
    private final SpeakerService speakerService;
    private final WhatsAppService whatsAppService;

    public ResponseEntity<?> getMailTest() {
        var scheduledTalk = speakerService.getCurrentScheduledTalk();
        var body = emailSpeakerTemplate(scheduledTalk);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    public ResponseEntity<?> sendMailTest(String to, String subject, String[] cc) {
        var scheduledTalk = speakerService.getCurrentScheduledTalk();
        var body = emailSpeakerTemplate(scheduledTalk);

        if (StringUtils.isBlank(to))
            to = getSchedulingParam().getOverseerEmail();

        if(StringUtils.isBlank(subject))
            subject = EMAIL_TEST_SUBJECT;

        try {
            emailService.sendEmail(to, subject, cc, body);
            return new ResponseEntity<>(String.format(Messages.EMAIL_SENT_CORRECTLY, to, body), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(String.format(ERROR_SENDING_EMAIL, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> sendWrongMailTest() {
        var scheduledTalk = speakerService.getCurrentScheduledTalk();

        try {
            emailService.sendEmail(
                getSchedulingParam().getOverseerEmail(),
                String.format(ERROR_SENDING_EMAIL_TO, scheduledTalk.email()),
                new String[] { "eleaz.rs@gmail.com" },
                String.format(Params.Scheduling.getReminderSpeakerNotInformedTemplate(), scheduledTalk.name()));
            return new ResponseEntity<>(String.format(EMAIL_NOT_INFORMED_SUBJECT, scheduledTalk.name()), HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            return new ResponseEntity<>(String.format(ERROR_SENDING_EMAIL, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getWhatsAppTest() {
        var scheduledTalk = speakerService.getCurrentScheduledTalk();
        var body = whatsAppSpeakerTemplate(scheduledTalk);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

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

        return new ResponseEntity<>(ERROR_SENDING_WHATSAPP.formatted(getWhatsAppParam().getWhatsAppTestPhoneNumber()), HttpStatus.BAD_REQUEST);
    }
}
