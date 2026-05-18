package org.crontalks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crontalks.constants.Messages;
import org.crontalks.constants.SchedulingProperties;
import org.crontalks.entity.EmailTemplate;
import org.crontalks.entity.ScheduledTalk;
import org.crontalks.exception.EmailRecipientNotInformedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmailService {

    private final EmailTemplate emailTemplate;
    private final SchedulingProperties schedulingProperties;
    private final GmailSmtpService gmailSmtpService;
    private final SpeakerService speakerService;

    private final int outlineIndexOutsideOfPlanning = 900;

    public String sendMailCurrent() {
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

    public String sendMailNext4Week() {
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
        gmailSmtpService.sendEmail(schedulingProperties.getOverseerEmail(), Messages.EMAIL_SPECIAL_OUTLINE_SUBJECT, bodySpecialOutlineScheduled);
        return String.format(Messages.EMAIL_SENT_CORRECTLY_NO_CONTENT, schedulingProperties.getOverseerEmail());
    }

    private String sendMailForScheduledTalk(
        final ScheduledTalk scheduledTalk,
        final String subject,
        final String body,
        final String subjectWathsApp,
        final String bodyEmailWhatsApp
    ) {
        try {
            log.info(Messages.EMAIL_SENDING_TO_CURRENT);
            gmailSmtpService.sendEmail(scheduledTalk.email(), subject, body);
            log.info(Messages.EMAIL_SENT_TO_CURRENT);

            if (scheduledTalk.hasWhatsApp()) {
                var weekFields = WeekFields.of(Locale.getDefault());
                var scheduledTalkWeek = scheduledTalk.localDateTime().get(weekFields.weekOfWeekBasedYear());
                var currentWeek = LocalDateTime.now().get(weekFields.weekOfWeekBasedYear());

                log.info(Messages.EMAIL_SENDING_WHATSAPP_ACTION, scheduledTalkWeek == currentWeek
                    ? Messages.EMAIL_CURRENT_SPEAKER_ACTION
                    : Messages.EMAIL_NEXT_4_WEEK_SPEAKER_ACTION);

                gmailSmtpService.sendEmail(schedulingProperties.getOverseerEmail(), subjectWathsApp, new String[]{schedulingProperties.getOverseerEmail()}, bodyEmailWhatsApp);
                log.info(Messages.EMAIL_SENT);
            }
            return String.format(Messages.EMAIL_SENT_CORRECTLY_NO_CONTENT, scheduledTalk.email());

        } catch (EmailRecipientNotInformedException e) {
            var bodyRecipientNotInformed = emailTemplate.emailSpeakerNotInformedTemplate(scheduledTalk);
            gmailSmtpService.sendEmail(schedulingProperties.getOverseerEmail(), Messages.EMAIL_NOT_INFORMED_SUBJECT, bodyRecipientNotInformed);
            throw new RuntimeException(String.format(Messages.ERROR_SENDING_EMAIL, e.getMessage()), e);

        } catch (Exception e) {
            throw new RuntimeException(String.format(Messages.ERROR_SENDING_EMAIL, e.getMessage()), e);
        }
    }

    public void sendMailCurrentFails() {
        try {
            if (speakerService.getCurrentScheduledTalk() == null) {
                gmailSmtpService.sendEmail(schedulingProperties.getOverseerEmail(), Messages.WARNING_SENDING_EMAIL_EMPTY_DATA, emailTemplate.emailEmptyData());
            } else {
                gmailSmtpService.sendEmail(schedulingProperties.getOverseerEmail(), Messages.WARNING_SENDING_EMAIL_SOME_EMPTY_DATA, emailTemplate.emailSomeEmptyData());
            }
        } catch (Exception e) {
            log.error("Failed to send warning email about failures: {}", e.getMessage(), e);
        }
    }

}
