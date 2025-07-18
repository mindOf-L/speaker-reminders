package org.crontalks.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class CronExternalService {

    public void callExternalSystem() throws Exception {
        // random fail throwing
        if (new Random().nextBoolean())
            log.info("✅ OK!");
        else {
            log.error("❌ FAIL!");
            throw new Exception("Fail calling external service");
        }
    }

}
