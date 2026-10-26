package com.dnaita.agenticailab.llm;

import java.time.Duration;

public record RetryPolicy(
        int maxAttempts,
        Duration delayBetweenAttempts) {

    public RetryPolicy {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be at least 1");
        }
        if (delayBetweenAttempts == null || delayBetweenAttempts.isNegative()) {
            throw new IllegalArgumentException(
                    "delayBetweenAttempts must not be negative or null");
        }
    }

    public static RetryPolicy standard() {
        return new RetryPolicy(3, Duration.ofMillis(100));
    }

    public static RetryPolicy noRetries() {
        return new RetryPolicy(1, Duration.ZERO);
    }
}
