package org.crontalks.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crontalks.constants.Messages;
import org.crontalks.constants.Params;
import org.crontalks.exception.EmailRecipientNotInformedException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

import static org.crontalks.constants.Messages.EMAIL_DEFAULT_SUBJECT;
import static org.crontalks.constants.Messages.EMAIL_NOT_INFORMED_SUBJECT;
import static org.crontalks.constants.Messages.EMAIL_SENDING;
import static org.crontalks.constants.Messages.EMAIL_SENDING_TO_CURRENT;
import static org.crontalks.constants.Messages.WARNING_SENDING_EMAIL_EMPTY_DATA;
import static org.crontalks.constants.Messages.WARNING_SENDING_EMAIL_SOME_EMPTY_DATA;
import static org.crontalks.entity.EmailTemplate.emailEmptyData;
import static org.crontalks.entity.EmailTemplate.emailSomeEmptyData;
import static org.crontalks.entity.EmailTemplate.emailSpeakerNotInformedTemplate;
import static org.crontalks.entity.EmailTemplate.emailSpeakerTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmailService {

    private final GmailSmtpService emailService;
    private final SpeakerService speakerService;
    private final Params.Scheduling schedulingParams;

    public String sendMail(String to, String subject, String body) {
        try {
            log.info(EMAIL_SENDING, to);
            emailService.sendEmail(to, subject, body);

            return String.format(Messages.EMAIL_SENT_CORRECTLY, to, body);
        } catch (Exception e) {
            throw new RuntimeException(String.format(Messages.ERROR_SENDING_EMAIL, e.getMessage()), e);
        }
    }

    public String sendMailCurrent() throws MessagingException, UnsupportedEncodingException {
        var scheduledTalk = speakerService.getCurrentScheduledTalk();

        if (scheduledTalk == null) {
            throw new RuntimeException(Messages.ERROR_GETTING_DATA_FROM_GSHEET);
        }

        var body = emailSpeakerTemplate(scheduledTalk);

        try {
            log.info(EMAIL_SENDING_TO_CURRENT);
            emailService.sendEmail(scheduledTalk.email(), EMAIL_DEFAULT_SUBJECT, body);
            log.info(Messages.EMAIL_SENT);
            return String.format(Messages.EMAIL_SENT_CORRECTLY, scheduledTalk.email(), body);

        } catch (EmailRecipientNotInformedException e) {
            body = emailSpeakerNotInformedTemplate(scheduledTalk);
            emailService.sendEmail(schedulingParams.getOverseerEmail(), EMAIL_NOT_INFORMED_SUBJECT, body);
            throw new RuntimeException(String.format(Messages.ERROR_SENDING_EMAIL, e.getMessage()), e);

        } catch (Exception e) {
            throw new RuntimeException(String.format(Messages.ERROR_SENDING_EMAIL, e.getMessage()), e);
        }
    }

    public void sendMailCurrentFails() {
        try {
            if (speakerService.getCurrentScheduledTalk() == null) {
                emailService.sendEmail(schedulingParams.getOverseerEmail(), WARNING_SENDING_EMAIL_EMPTY_DATA, emailEmptyData());
            } else {
                emailService.sendEmail(schedulingParams.getOverseerEmail(), WARNING_SENDING_EMAIL_SOME_EMPTY_DATA, emailSomeEmptyData());
            }
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send warning email about failures: {}", e.getMessage(), e);
        }
    }

}
