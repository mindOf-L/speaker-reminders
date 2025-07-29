package org.crontalks.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class CronSchedulerServiceTest {

    @Mock
    private CronCallerService cronCallerService;

    @InjectMocks
    private CronSchedulerService cronSchedulerService;

    @Test
    void weeklyReminderTask_ShouldCallRunInitialAttempt() {
        doNothing().when(cronCallerService).runInitialAttempt();

        cronSchedulerService.weeklyReminderTask();

        verify(cronCallerService).runInitialAttempt();
        verifyNoMoreInteractions(cronCallerService);
    }

    @Test
    void retryTask_ShouldCallRunNextAttempt() {
        doNothing().when(cronCallerService).runNextAttempt();

        cronSchedulerService.retryTask();

        verify(cronCallerService).runNextAttempt();
        verifyNoMoreInteractions(cronCallerService);
    }
}
