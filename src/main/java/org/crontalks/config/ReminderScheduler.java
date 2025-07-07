package org.crontalks.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final JobLauncher jobLauncher;

    private final Job reminderJob;

    @Scheduled(cron = "#{@cronProperties.schedule}")
    public void runReminderJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
            .addLong("run.id", System.currentTimeMillis()) // parámetro único
            .toJobParameters();

        System.out.println(">>> Lanzando reminderJob programado...");
        jobLauncher.run(reminderJob, params);
    }

}
