package org.crontalks.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.crontalks.service.TestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/email")
    public ResponseEntity<?> getMailTest() {
        try {
            return new ResponseEntity<>(testService.getMailTest(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/email")
    public ResponseEntity<?> sendMailTest(
        @RequestParam(required = false) String to,
        @RequestParam String subject,
        @RequestParam(required = false) String[] cc) {
        try {
            String response = testService.sendMailTest(StringUtils.isBlank(to) ? null : to, subject, cc);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/whatsapp")
    public ResponseEntity<?> getWhatsAppTest() {
        try {
            return new ResponseEntity<>(testService.getWhatsAppTest(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/whatsapp")
    public ResponseEntity<?> sendWhatsAppTest() {
        try {
            String response = testService.sendWhatsAppTest();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RestClientException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
