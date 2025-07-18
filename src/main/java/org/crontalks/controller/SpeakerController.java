package org.crontalks.controller;

import lombok.RequiredArgsConstructor;
import org.crontalks.constants.Messages;
import org.crontalks.service.SpeakerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SpeakerController {

    private final SpeakerService speakerService;

    @GetMapping("/speaker")
    public ResponseEntity<?> speaker() {
        var scheduledTalk = speakerService.getCurrentScheduledTalk();

        if (scheduledTalk == null)
            return new ResponseEntity<>(Messages.ERROR_GETTING_DATA_FROM_GSHEET, HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(scheduledTalk, HttpStatus.OK);
    }
}
