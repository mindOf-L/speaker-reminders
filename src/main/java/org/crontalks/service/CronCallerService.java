package org.crontalks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static org.crontalks.constants.Messages.MESSAGES_ATTEMPT_NUM;
import static org.crontalks.constants.Messages.MESSAGES_ATTEMPT_NUM_KO;
import static org.crontalks.constants.Messages.MESSAGES_ATTEMPT_NUM_OK;
import static org.crontalks.constants.Messages.MESSAGES_FIRST_ATTEMPT_KO;
import static org.crontalks.constants.Messages.MESSAGES_FIRST_ATTEMPT_OK;
import static org.crontalks.constants.Messages.MESSAGES_MAX_ATTEMPT_KO;

@Slf4j
@Component
@RequiredArgsConstructor
public class CronCallerService {

    private final GmailService gmailService;
    private final RetryOnMemoryService retryOnMemoryService;

    public void runInitialAttempt() {
        try {
            gmailService.sendMailCurrent();
            log.info(MESSAGES_FIRST_ATTEMPT_OK);
            retryOnMemoryService.resetRetriesCounter();
        } catch (Exception e) {
            log.warn(MESSAGES_FIRST_ATTEMPT_KO, e.getMessage());
            retryOnMemoryService.registerFailAttempt();
        }
    }

    public void runNextAttempt() {
        if (retryOnMemoryService.shouldRetry()) {
            try {
                log.info(MESSAGES_ATTEMPT_NUM, retryOnMemoryService.checkActualAttempt() + 1);
                gmailService.sendMailCurrent();
                log.info(MESSAGES_ATTEMPT_NUM_OK, retryOnMemoryService.checkActualAttempt() + 1);
                retryOnMemoryService.resetRetriesCounter();
            } catch (Exception e) {
                retryOnMemoryService.registerFailAttempt();
                log.warn(MESSAGES_ATTEMPT_NUM_KO, retryOnMemoryService.checkActualAttempt(), e.getMessage());

                if (!retryOnMemoryService.shouldRetry()) {
                    log.error(MESSAGES_MAX_ATTEMPT_KO);
                }
            }
        }
    }

}
