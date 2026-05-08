package org.crontalks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.crontalks.constants.Messages;
import org.crontalks.constants.SchedulingProperties;
import org.crontalks.constants.WhatsAppProperties;
import org.crontalks.entity.EmailTemplate;
import org.crontalks.entity.WhatsAppTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import static org.crontalks.constants.Messages.EMAIL_NOT_INFORMED_SUBJECT;
import static org.crontalks.constants.Messages.EMAIL_TEST_SUBJECT;
import static org.crontalks.constants.Messages.ERROR_SENDING_EMAIL;
import static org.crontalks.constants.Messages.ERROR_SENDING_EMAIL_TO;
import static org.crontalks.constants.Messages.ERROR_SENDING_WHATSAPP;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

    private final GmailSmtpService emailService;
    private final SpeakerService speakerService;
    private final EmailTemplate emailTemplate;
    private final SchedulingProperties schedulingProperties;
    private final WhatsAppService whatsAppService;
    private final WhatsAppTemplate whatsAppTemplate;
    private final WhatsAppProperties whatsAppProperties;

    public String getMailTest() {
        var scheduledTalk = speakerService.getCurrentScheduledTalk();
        return emailTemplate.emailSpeakerTemplate(scheduledTalk);
    }

    public String sendMailTest(String to, String subject, String[] cc) {
        var scheduledTalk = speakerService.getCurrentScheduledTalk();
        var body = emailTemplate.emailSpeakerTemplate(scheduledTalk);

        if (StringUtils.isBlank(to))
            to = schedulingProperties.getOverseerEmail();

        if (StringUtils.isBlank(subject))
            subject = EMAIL_TEST_SUBJECT;

        try {
            emailService.sendEmail(to, subject, cc, body);
            return String.format(Messages.EMAIL_SENT_CORRECTLY, to, body);
        } catch (Exception e) {
            throw new RuntimeException(String.format(ERROR_SENDING_EMAIL, e.getMessage()), e);
        }
    }

    public String sendWrongMailTest() {
        var scheduledTalk = speakerService.getCurrentScheduledTalk();

        try {
            emailService.sendEmail(
                schedulingProperties.getOverseerEmail(),
                String.format(ERROR_SENDING_EMAIL_TO, scheduledTalk.name()),
                null,
                String.format(schedulingProperties.getReminderSpeakerNotInformedTemplate(), scheduledTalk.name()));
            return String.format(EMAIL_NOT_INFORMED_SUBJECT, scheduledTalk.name());
        } catch (Exception e) {
            throw new RuntimeException(String.format(ERROR_SENDING_EMAIL, e.getMessage()), e);
        }
    }

    public String getWhatsAppTest() {
        var scheduledTalk = speakerService.getCurrentScheduledTalk();
        return whatsAppTemplate.whatsAppSpeakerTemplate(scheduledTalk);
    }

    public String sendWhatsAppTest() {
        try {
            var response = whatsAppService.sendWhatsAppTest(whatsAppProperties.getWhatsAppTestPhoneNumber());
            log.info("Response: {}", response);
            return response;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            var error = String.format(ERROR_SENDING_WHATSAPP, whatsAppProperties.getWhatsAppTestPhoneNumber());
            log.error(error);
            log.error(e.getMessage());
            throw e;
        } catch (RestClientException e) {
            var error = String.format(ERROR_SENDING_WHATSAPP, whatsAppProperties.getWhatsAppTestPhoneNumber());
            log.error(error);
            throw new RuntimeException(e);
        }
    }
}
