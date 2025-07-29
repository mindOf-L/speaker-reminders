package org.crontalks.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CronCallerServiceTest {

    @Mock
    private GmailService gmailService;

    @Mock
    private RetryOnMemoryService retryOnMemoryService;

    @InjectMocks
    private CronCallerService cronCallerService;

    @Test
    void runInitialAttempt_ShouldResetRetriesCounter_WhenSuccessful() throws Exception {
        doReturn(ResponseEntity.ok("Success")).when(gmailService).sendMailCurrent();

        cronCallerService.runInitialAttempt();

        verify(gmailService).sendMailCurrent();
        verify(retryOnMemoryService).resetRetriesCounter();
        verifyNoMoreInteractions(retryOnMemoryService);
    }

    @Test
    void runInitialAttempt_ShouldRegisterFailAttempt_WhenExceptionOccurs() throws Exception {
        doThrow(new RuntimeException("Test exception")).when(gmailService).sendMailCurrent();

        cronCallerService.runInitialAttempt();

        verify(gmailService).sendMailCurrent();
        verify(retryOnMemoryService).registerFailAttempt();
        verifyNoMoreInteractions(retryOnMemoryService);
    }

    @Test
    void runNextAttempt_ShouldDoNothing_WhenShouldRetryReturnsFalse() {
        when(retryOnMemoryService.shouldRetry()).thenReturn(false);

        cronCallerService.runNextAttempt();

        verify(retryOnMemoryService).shouldRetry();
        verifyNoMoreInteractions(gmailService, retryOnMemoryService);
    }

    @Test
    void runNextAttempt_ShouldResetRetriesCounter_WhenSuccessful() throws Exception {
        when(retryOnMemoryService.shouldRetry()).thenReturn(true);
        when(retryOnMemoryService.checkActualAttempt()).thenReturn(1);
        doReturn(ResponseEntity.ok("Success")).when(gmailService).sendMailCurrent();

        cronCallerService.runNextAttempt();

        verify(retryOnMemoryService).shouldRetry();
        verify(retryOnMemoryService, times(2)).checkActualAttempt();
        verify(gmailService).sendMailCurrent();
        verify(retryOnMemoryService).resetRetriesCounter();
    }

    @Test
    void runNextAttempt_ShouldRegisterFailAttempt_WhenExceptionOccurs() throws Exception {
        when(retryOnMemoryService.shouldRetry()).thenReturn(true);
        when(retryOnMemoryService.checkActualAttempt()).thenReturn(1);
        doThrow(new RuntimeException("Test exception")).when(gmailService).sendMailCurrent();

        cronCallerService.runNextAttempt();

        verify(retryOnMemoryService, times(2)).shouldRetry();
        verify(retryOnMemoryService, times(2)).checkActualAttempt();
        verify(gmailService).sendMailCurrent();
        verify(retryOnMemoryService).registerFailAttempt();
    }

    @Test
    void runNextAttempt_ShouldLogMaxAttemptReached_WhenMaxAttemptsReached() throws Exception {
        when(retryOnMemoryService.shouldRetry())
                .thenReturn(true)  // First call returns true to enter the try block
                .thenReturn(false); // Second call returns false to enter the max attempts block
        when(retryOnMemoryService.checkActualAttempt()).thenReturn(5);
        doThrow(new RuntimeException("Test exception")).when(gmailService).sendMailCurrent();

        cronCallerService.runNextAttempt();

        verify(retryOnMemoryService, times(2)).shouldRetry();
        verify(retryOnMemoryService, times(2)).checkActualAttempt();
        verify(gmailService).sendMailCurrent();
        verify(retryOnMemoryService).registerFailAttempt();
    }
}
