package org.crontalks.service;

import org.springframework.stereotype.Component;

@Component
public class RetryOnMemoryService {

    private boolean isRetrying = false;
    private int attempts = 0;

    public synchronized void registerFailAttempt() {
        attempts++;
        isRetrying = true;
    }

    public synchronized boolean shouldRetry() {
        return isRetrying && attempts < 6;
    }

    public synchronized void resetRetriesCounter() {
        attempts = 0;
        isRetrying = false;
    }

    public synchronized int checkActualAttempt() {
        return attempts;
    }

}
