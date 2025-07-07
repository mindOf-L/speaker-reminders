package org.crontalks.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Bean
    public Job reminderJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("reminderJob", jobRepository)
            .start(step1)
            .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
            .tasklet((_, _) -> {
                System.out.println(">>> Executing step1 from job...");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

    @Bean
    public JobRepository jobRepository() {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDatabaseType("H2");
        try {
            return factory.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
