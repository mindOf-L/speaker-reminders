package org.crontalks.controller;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.crontalks.service.GmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class GmailController {

    private final GmailService gmailService;

    @PostMapping
    public ResponseEntity<?> sendMail(@RequestParam String to, @RequestParam String subject, @RequestParam String body) {
        return gmailService.sendMail(to, subject, body);
    }

    @PostMapping("/speaker/current")
    public ResponseEntity<?> sendMailCurrent() throws MessagingException, UnsupportedEncodingException {
        return gmailService.sendMailCurrent();
    }

}
