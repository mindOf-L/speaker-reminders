package org.crontalks.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RetryOnMemoryService {

    private final AtomicBoolean isRetrying = new AtomicBoolean(false);
    private final AtomicInteger attempts = new AtomicInteger(0);

    public synchronized void registerFailAttempt() {
        attempts.incrementAndGet();
        isRetrying.set(true);
    }

    public synchronized boolean shouldRetry() {
        return isRetrying.get() && attempts.get() < 6;
    }

    public synchronized void resetRetriesCounter() {
        attempts.set(0);
        isRetrying.set(false);
    }

    public int checkActualAttempt() {
        return attempts.get();
    }

}
