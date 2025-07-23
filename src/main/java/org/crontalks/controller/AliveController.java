package org.crontalks.controller;

import lombok.RequiredArgsConstructor;
import org.crontalks.constants.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alive")
@RequiredArgsConstructor
public class AliveController {

    @GetMapping
    public ResponseEntity<?> getAlive() {
        return new ResponseEntity<>(Messages.IM_ALIVE, HttpStatus.OK);
    }
}
