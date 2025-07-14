package org.crontalks.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.crontalks.constants.Params;
import org.crontalks.exception.EmailRecipientNotInformedException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static org.crontalks.constants.Messages.ERROR_EMAIL_RECIPIENT_NOT_INFORMED;

@Service
@RequiredArgsConstructor
public class GmailSmtpService {

    private final JavaMailSender mailSender;

    public void sendSimpleEmail(String to, String subject, String text) {

        if(StringUtils.isBlank(to))
            throw new EmailRecipientNotInformedException(ERROR_EMAIL_RECIPIENT_NOT_INFORMED);

        var message = new SimpleMailMessage();
        message.setFrom(Params.Scheduling.getInstance().getEmailFrom());
        message.setTo(StringUtils.isNotBlank(to) ? to : Params.Scheduling.getInstance().getEmailFrom());
        message.setSubject(subject);
        message.setCc(Params.Scheduling.getInstance().getEmailCC());
        message.setText(text);
        mailSender.send(message);
    }
}
