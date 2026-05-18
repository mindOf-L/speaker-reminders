package org.crontalks.controller;

import lombok.RequiredArgsConstructor;
import org.crontalks.service.GmailService;
import org.crontalks.service.GmailSmtpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class GmailController {

    private final GmailService gmailService;
    private final GmailSmtpService gmailSmtpService;

    @PostMapping
    public ResponseEntity<?> sendMail(@RequestParam String to, @RequestParam String subject, @RequestParam String body) {
        try {
            gmailSmtpService.sendEmail(to, subject, body);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/speaker/current")
    public ResponseEntity<?> sendMailCurrent() {
        try {
            return new ResponseEntity<>(gmailService.sendMailCurrent(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/speaker/planning")
    public ResponseEntity<?> sendMailPlanning() {
        try {
            var responseCurrent = gmailService.sendMailCurrent();
            var responseNext4Week = gmailService.sendMailNext4Week();
            return new ResponseEntity<>(String.format("%s\n%s", responseCurrent, responseNext4Week), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
