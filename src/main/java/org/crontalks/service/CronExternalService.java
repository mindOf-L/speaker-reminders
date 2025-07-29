package org.crontalks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

import static org.crontalks.constants.Messages.EMAIL_TEST_SUBJECT;

@Slf4j
@Service
@RequiredArgsConstructor
public class CronExternalService {

    private final TestService testService;

    public void callExternalSystem() throws Exception {
        if (getRandomBoolean()) {
            testService.sendMailTest(null, EMAIL_TEST_SUBJECT, null);
            log.info("✅ OK!");
        } else {
            log.error("❌ FAIL!");
            testService.sendWrongMailTest();
            throw new Exception("Fail calling external service");
        }
    }
    
    protected boolean getRandomBoolean() {
        return new Random().nextBoolean();
    }

}
