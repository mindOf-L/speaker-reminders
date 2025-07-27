package org.crontalks.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.crontalks.service.TestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/email")
    public ResponseEntity<?> getMailTest() {
        return testService.getMailTest();
    }

    @PostMapping("/email")
    public ResponseEntity<?> sendMailTest(
        @RequestParam(required = false) String to,
        @RequestParam String subject,
        @RequestParam(required = false) String[] cc) {
        if(StringUtils.isBlank(to))
            return testService.sendMailTest(null, subject, cc);

        return testService.sendMailTest(to, subject, cc);
    }

    @GetMapping("/whatsapp")
    public ResponseEntity<?> getWhatsAppTest() {
        return testService.getWhatsAppTest();
    }

    @PostMapping("/whatsapp")
    public ResponseEntity<?> sendWhatsAppTest() {
        return testService.sendWhatsAppTest();
    }

}
