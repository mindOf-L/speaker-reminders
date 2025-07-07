package org.crontalks.service;

import lombok.RequiredArgsConstructor;
import org.crontalks.constants.Params;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GmailSmtpService {

    private final JavaMailSender mailSender;

    public void sendSimpleEmail(String to, String subject, String text) {
        var message = new SimpleMailMessage();
        message.setFrom(Params.GSheets.getInstance().getEmailFrom());
        message.setTo(to);
        message.setSubject(subject);
        message.setCc(Params.GSheets.getInstance().getEmailCC());
        message.setText(text);
        // TODO
        //  -> mailSender.send(message);
    }
}
