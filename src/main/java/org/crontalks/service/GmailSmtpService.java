package org.crontalks.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.crontalks.constants.Messages;
import org.crontalks.constants.SchedulingProperties;
import org.crontalks.exception.EmailRecipientNotInformedException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static org.crontalks.constants.Messages.ERROR_EMAIL_RECIPIENT_NOT_INFORMED;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmailSmtpService {

    private final JavaMailSender mailSender;
    private final SchedulingProperties schedulingProperties;

    public void sendEmail(String to, String subject, String body) {
        sendMail(to, subject, null, body);
    }

    public void sendEmail(String to, String subject, String[] cc, String body) {
        sendMail(to, subject, cc, body);
    }

    private void sendMail(String to, String subject, String[] cc, String body) {
        if(StringUtils.isBlank(to))
            throw new EmailRecipientNotInformedException(ERROR_EMAIL_RECIPIENT_NOT_INFORMED);

        log.info(Messages.EMAIL_SENDING, to);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());

        try {
            messageHelper.setFrom(schedulingProperties.getEmailFrom(), schedulingProperties.getTalksOverseer());
            messageHelper.setTo(StringUtils.isNotBlank(to) ? to : schedulingProperties.getEmailFrom());
            messageHelper.setSubject(subject);
            messageHelper.setCc(cc == null ? schedulingProperties.getEmailCC() : cc);
            messageHelper.setText(body, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(String.format(Messages.ERROR_SENDING_EMAIL, e.getMessage()), e);
        }
    }
}
