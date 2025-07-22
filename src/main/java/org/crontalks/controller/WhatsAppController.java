package org.crontalks.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.crontalks.constants.Messages.NOT_IMPLEMENTED_YET;

@Slf4j
@RestController
@RequestMapping("/whatsapp")
@RequiredArgsConstructor
public class WhatsAppController {

    @PostMapping("/speaker/current")
    public ResponseEntity<?> sendWhatsAppSpeakerCurrent() {
        return new ResponseEntity<>(NOT_IMPLEMENTED_YET, HttpStatus.NOT_IMPLEMENTED);
    }

}
