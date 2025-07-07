package org.crontalks.controller;

import lombok.RequiredArgsConstructor;
import org.crontalks.constants.Messages;
import org.crontalks.constants.Params;
import org.crontalks.service.GSheetService;
import org.crontalks.service.GmailSmtpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GmailController {

    private final GmailSmtpService emailService;
    private final GSheetService gSheetService;

    @PostMapping("/send-email")
    public ResponseEntity<?> sendMail(@RequestParam String to, @RequestParam String subject, @RequestParam String body) {
        try {
            emailService.sendSimpleEmail(to, subject, body);
            return new ResponseEntity<>(String.format(Messages.EMAIL_SENT_CORRECTLY, to), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(String.format(Messages.ERROR_SENDING_EMAIL, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/speaker")
    public ResponseEntity<?> speaker() {
        final String speakerSheetRange = "A1:G";
        var speakerThisWeek = gSheetService.getSheetValues(
            Params.GSheets.getInstance().getThisWeekSpeaker(), speakerSheetRange, true);

        if (speakerThisWeek.isEmpty())
            return new ResponseEntity<>(Messages.ERROR_GETTING_DATA_FROM_GSHEET, HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(Arrays.toString(speakerThisWeek.getFirst().toArray()), HttpStatus.OK);
    }
}
