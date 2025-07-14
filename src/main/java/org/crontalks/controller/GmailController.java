package org.crontalks.controller;

import lombok.RequiredArgsConstructor;
import org.crontalks.constants.Messages;
import org.crontalks.constants.Params;
import org.crontalks.exception.EmailRecipientNotInformedException;
import org.crontalks.mapper.ScheduledTalkMapper;
import org.crontalks.service.GSheetService;
import org.crontalks.service.GmailSmtpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.crontalks.constants.Messages.EMAIL_DEFAULT_SUBJECT;
import static org.crontalks.constants.Messages.EMAIL_NOT_INFORMED_SUBJECT;
import static org.crontalks.entity.EmailTemplate.emailSpeakerNotInformedTemplate;
import static org.crontalks.entity.EmailTemplate.emailSpeakerTemplate;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GmailController {

    private final GmailSmtpService emailService;
    private final GSheetService gSheetService;

    @PostMapping("/email")
    public ResponseEntity<?> sendMail(@RequestParam String to, @RequestParam String subject, @RequestParam String body) {
        try {
            emailService.sendSimpleEmail(to, subject, body);

            return new ResponseEntity<>(String.format(Messages.EMAIL_SENT_CORRECTLY, to, body), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(String.format(Messages.ERROR_SENDING_EMAIL, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/email/speaker/current")
    public ResponseEntity<?> sendMail() {
        final String speakerSheetRange = "A1:H";
        var speakerThisWeek = gSheetService.getSheetValues(
            Params.GSheets.getInstance().getThisWeekSpeaker(), speakerSheetRange, true);

        if (speakerThisWeek.isEmpty())
            return new ResponseEntity<>(Messages.ERROR_GETTING_DATA_FROM_GSHEET, HttpStatus.INTERNAL_SERVER_ERROR);

        var scheduledTalk = ScheduledTalkMapper.toScheduledTalk(speakerThisWeek.getFirst());

        var body = emailSpeakerTemplate(scheduledTalk);

        try {
            emailService.sendSimpleEmail(scheduledTalk.email(), EMAIL_DEFAULT_SUBJECT, body);
            return new ResponseEntity<>(String.format(Messages.EMAIL_SENT_CORRECTLY, scheduledTalk.email(), body), HttpStatus.OK);

        } catch (EmailRecipientNotInformedException e) {
            body = emailSpeakerNotInformedTemplate(scheduledTalk);
            emailService.sendSimpleEmail(Params.Scheduling.getInstance().getOverseerEmail(), EMAIL_NOT_INFORMED_SUBJECT, body);
            return new ResponseEntity<>(String.format(Messages.ERROR_SENDING_EMAIL, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            return new ResponseEntity<>(String.format(Messages.ERROR_SENDING_EMAIL, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/speaker")
    public ResponseEntity<?> speaker() {
        final String speakerSheetRange = "A1:H";
        var speakerThisWeek = gSheetService.getSheetValues(
            Params.GSheets.getInstance().getThisWeekSpeaker(), speakerSheetRange, true);

        if (speakerThisWeek.isEmpty())
            return new ResponseEntity<>(Messages.ERROR_GETTING_DATA_FROM_GSHEET, HttpStatus.INTERNAL_SERVER_ERROR);

        var scheduledTalk = ScheduledTalkMapper.toScheduledTalk(speakerThisWeek.getFirst());

        return new ResponseEntity<>(scheduledTalk, HttpStatus.OK);
    }
}
