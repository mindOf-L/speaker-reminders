package org.crontalks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class CronSchedulerService {

    private final CronCallerService cronCallerService;

    @Scheduled(cron = "#{@cronProperties.schedule}", zone = "Europe/Madrid")
    public void weeklyReminderTask() {
        cronCallerService.runInitialAttempt();
    }

    @Scheduled(cron = "*/10 * * * * *")
    public void retryTask() {
        cronCallerService.runNextAttempt();
    }
}
