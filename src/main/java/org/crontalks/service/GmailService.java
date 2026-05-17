package org.crontalks.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crontalks.constants.Messages;
import org.crontalks.constants.SchedulingProperties;
import org.crontalks.entity.EmailTemplate;
import org.crontalks.entity.ScheduledTalk;
import org.crontalks.exception.EmailRecipientNotInformedException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmailService {

    private final EmailTemplate emailTemplate;
    private final SchedulingProperties schedulingProperties;
    private final GmailSmtpService emailService;
    private final SpeakerService speakerService;

    private final int outlineIndexOutsideOfPlanning = 900;

    public String sendMail(String to, String subject, String body) {
        try {
            log.info(Messages.EMAIL_SENDING, to);
            emailService.sendEmail(to, subject, body);

            return String.format(Messages.EMAIL_SENT_CORRECTLY_NO_CONTENT, to);
        } catch (Exception e) {
            throw new RuntimeException(String.format(Messages.ERROR_SENDING_EMAIL, e.getMessage()), e);
        }
    }

    public String sendMailCurrent() throws MessagingException, UnsupportedEncodingException {
        var scheduledTalk = speakerService.getCurrentScheduledTalk();

        if (scheduledTalk == null)
            throw new RuntimeException(Messages.ERROR_GETTING_DATA_FROM_GSHEET);

        if (scheduledTalk.outlineNumber() >= outlineIndexOutsideOfPlanning)
            return sendSpecialOutlineEmail(scheduledTalk);

        var bodyCurrentSpeaker = emailTemplate.processEmailSpeakerTemplate(scheduledTalk);
        var bodyCurrentSpeakerWhatsApp = emailTemplate.processEmailSpeakerWhatsAppTemplate(scheduledTalk);

        return sendMailForScheduledTalk(
            scheduledTalk,
            Messages.EMAIL_DEFAULT_SUBJECT, bodyCurrentSpeaker,
            Messages.EMAIL_WHATSAPP_REMINDER_SUBJECT, bodyCurrentSpeakerWhatsApp
        );
    }

    public String sendMailNext4Week() throws MessagingException, UnsupportedEncodingException {
        var scheduledTalk = speakerService.getNext4WeekScheduledTalk();

        if (scheduledTalk == null)
            throw new RuntimeException(Messages.ERROR_GETTING_DATA_FROM_GSHEET);

        if (scheduledTalk.outlineNumber() >= outlineIndexOutsideOfPlanning)
            return sendSpecialOutlineEmail(scheduledTalk);

        var bodyCurrentSpeaker = emailTemplate.processEmailSpeakerNext4WeekTemplate(scheduledTalk);
        var bodyCurrentSpeakerWhatsApp = emailTemplate.processEmailSpeakerNext4WeekWhatsAppTemplate(scheduledTalk);

        return sendMailForScheduledTalk(
            scheduledTalk,
            Messages.EMAIL_DEFAULT_NEXT_4_WEEK_SUBJECT, bodyCurrentSpeaker,
            Messages.EMAIL_WHATSAPP_NEXT_4_WEEK_REMINDER_SUBJECT, bodyCurrentSpeakerWhatsApp
        );
    }

    private String sendSpecialOutlineEmail(ScheduledTalk scheduledTalk) {
        log.info(Messages.EMAIL_SPECIAL_OUTLINE);
        var bodySpecialOutlineScheduled = emailTemplate.processEmailSpecialOutlineScheduledTemplate(scheduledTalk);
        return sendMail(schedulingProperties.getOverseerEmail(), Messages.EMAIL_SPECIAL_OUTLINE_SUBJECT, bodySpecialOutlineScheduled);
    }

    private String sendMailForScheduledTalk(
        final ScheduledTalk scheduledTalk,
        final String subject,
        final String body,
        final String subjectWathsApp,
        final String bodyEmailWhatsApp
    ) throws MessagingException, UnsupportedEncodingException {
        try {
            log.info(Messages.EMAIL_SENDING_TO_CURRENT);
            emailService.sendEmail(scheduledTalk.email(), subject, body);
            log.info(Messages.EMAIL_SENT_TO_CURRENT);

            if (scheduledTalk.hasWhatsApp()) {
                log.info(String.format(Messages.EMAIL_SENDING_WHATSAPP_ACTION, Messages.EMAIL_SENDING_TO_CURRENT));
                emailService.sendEmail(scheduledTalk.email(), subjectWathsApp, new String[]{schedulingProperties.getOverseerEmail()}, bodyEmailWhatsApp);
                log.info(Messages.EMAIL_SENT);
            }
            return String.format(Messages.EMAIL_SENT_CORRECTLY_NO_CONTENT, scheduledTalk.email());

        } catch (EmailRecipientNotInformedException e) {
            var bodyRecipientNotInformed = emailTemplate.emailSpeakerNotInformedTemplate(scheduledTalk);
            emailService.sendEmail(schedulingProperties.getOverseerEmail(), Messages.EMAIL_NOT_INFORMED_SUBJECT, bodyRecipientNotInformed);
            throw new RuntimeException(String.format(Messages.ERROR_SENDING_EMAIL, e.getMessage()), e);

        } catch (Exception e) {
            throw new RuntimeException(String.format(Messages.ERROR_SENDING_EMAIL, e.getMessage()), e);
        }
    }

    public void sendMailCurrentFails() {
        try {
            if (speakerService.getCurrentScheduledTalk() == null) {
                emailService.sendEmail(schedulingProperties.getOverseerEmail(), Messages.WARNING_SENDING_EMAIL_EMPTY_DATA, emailTemplate.emailEmptyData());
            } else {
                emailService.sendEmail(schedulingProperties.getOverseerEmail(), Messages.WARNING_SENDING_EMAIL_SOME_EMPTY_DATA, emailTemplate.emailSomeEmptyData());
            }
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send warning email about failures: {}", e.getMessage(), e);
        }
    }

}
