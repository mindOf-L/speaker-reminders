package org.crontalks.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RetryOnMemoryServiceTest {

    private RetryOnMemoryService retryOnMemoryService;

    @BeforeEach
    void setUp() {
        retryOnMemoryService = new RetryOnMemoryService();
    }

    @Test
    void registerFailAttempt_ShouldIncrementAttemptsAndSetRetrying() {
        // Initial state
        assertEquals(0, retryOnMemoryService.checkActualAttempt());
        assertFalse(retryOnMemoryService.shouldRetry());

        retryOnMemoryService.registerFailAttempt();

        assertEquals(1, retryOnMemoryService.checkActualAttempt());
        assertTrue(retryOnMemoryService.shouldRetry());
    }

    @Test
    void shouldRetry_ShouldReturnFalse_WhenNotRetrying() {
        // Initial state (not retrying)
        assertFalse(retryOnMemoryService.shouldRetry());
    }

    @Test
    void shouldRetry_ShouldReturnTrue_WhenRetryingAndAttemptsLessThanMax() {
        // Setup retrying state
        retryOnMemoryService.registerFailAttempt(); // attempts = 1

        assertTrue(retryOnMemoryService.shouldRetry());
    }

    @Test
    void shouldRetry_ShouldReturnFalse_WhenAttemptsReachMax() {
        // Setup max attempts (6)
        for (int i = 0; i < 6; i++) {
            retryOnMemoryService.registerFailAttempt();
        }

        assertFalse(retryOnMemoryService.shouldRetry());
        assertEquals(6, retryOnMemoryService.checkActualAttempt());
    }

    @Test
    void resetRetriesCounter_ShouldResetAttemptsAndRetryingFlag() {
        // Setup retrying state
        retryOnMemoryService.registerFailAttempt();
        retryOnMemoryService.registerFailAttempt();
        assertEquals(2, retryOnMemoryService.checkActualAttempt());
        assertTrue(retryOnMemoryService.shouldRetry());

        retryOnMemoryService.resetRetriesCounter();

        assertEquals(0, retryOnMemoryService.checkActualAttempt());
        assertFalse(retryOnMemoryService.shouldRetry());
    }

    @Test
    void checkActualAttempt_ShouldReturnCurrentAttemptCount() {
        // Initial state
        assertEquals(0, retryOnMemoryService.checkActualAttempt());

        // After one attempt
        retryOnMemoryService.registerFailAttempt();
        assertEquals(1, retryOnMemoryService.checkActualAttempt());

        // After another attempt
        retryOnMemoryService.registerFailAttempt();
        assertEquals(2, retryOnMemoryService.checkActualAttempt());

        // After reset
        retryOnMemoryService.resetRetriesCounter();
        assertEquals(0, retryOnMemoryService.checkActualAttempt());
    }
}
