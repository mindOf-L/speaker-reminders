package org.crontalks.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.crontalks.exception.EmailRecipientNotInformedException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static org.crontalks.constants.Messages.ERROR_EMAIL_RECIPIENT_NOT_INFORMED;
import static org.crontalks.constants.Params.Scheduling.getSchedulingParam;

@Service
@RequiredArgsConstructor
public class GmailSmtpService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) throws MessagingException, UnsupportedEncodingException {
        sendEmail(to, subject, null, text);
    }

    public void sendEmail(String to, String subject, String[] cc, String text) throws MessagingException, UnsupportedEncodingException {
        if(StringUtils.isBlank(to))
            throw new EmailRecipientNotInformedException(ERROR_EMAIL_RECIPIENT_NOT_INFORMED);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());

        messageHelper.setFrom(getSchedulingParam().getEmailFrom(), getSchedulingParam().getTalksOverseer());
        messageHelper.setTo(StringUtils.isNotBlank(to) ? to : getSchedulingParam().getEmailFrom());
        messageHelper.setSubject(subject);
        messageHelper.setCc(cc == null ? getSchedulingParam().getEmailCC() : cc);
        messageHelper.setText(text, true);
        mailSender.send(message);
    }
}
