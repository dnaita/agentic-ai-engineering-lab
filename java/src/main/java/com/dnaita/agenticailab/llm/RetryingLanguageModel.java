package com.dnaita.agenticailab.llm;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;

public final class RetryingLanguageModel implements LanguageModel {

    private final LanguageModel delegate;
    private final RetryPolicy retryPolicy;
    private final Consumer<Duration> delayBeforeRetry;

    public RetryingLanguageModel(
            LanguageModel delegate,
            RetryPolicy retryPolicy) {
        this(delegate, retryPolicy, RetryingLanguageModel::sleep);
    }

    /**
     * The injectable delay allows tests to avoid real waiting.
     */
    public RetryingLanguageModel(
            LanguageModel delegate,
            RetryPolicy retryPolicy,
            Consumer<Duration> delayBeforeRetry) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.retryPolicy = Objects.requireNonNull(
                retryPolicy, "retryPolicy must not be null");
        this.delayBeforeRetry = Objects.requireNonNull(
                delayBeforeRetry, "delayBeforeRetry must not be null");
    }

    @Override
    public ModelResponse generate(ModelRequest request, Duration timeout) {
        Objects.requireNonNull(request, "request must not be null");

        if (timeout == null || timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("timeout must be positive");
        }

        for (int attempt = 1; attempt <= retryPolicy.maxAttempts(); attempt++) {
            try {
                return delegate.generate(request, timeout);
            } catch (ModelException failure) {
                boolean lastAttempt = attempt == retryPolicy.maxAttempts();

                if (!failure.isRetryable() || lastAttempt) {
                    throw failure;
                }

                delayBeforeRetry.accept(retryPolicy.delayBetweenAttempts());
            }
        }

        throw new IllegalStateException("Retry loop ended unexpectedly");
    }

    private static void sleep(Duration delay) {
        if (delay.isZero()) {
            return;
        }

        try {
            Thread.sleep(
                    delay.toMillis(),
                    (int) (delay.toNanosPart() % 1_000_000));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ModelException(
                    ModelErrorType.INTERRUPTED,
                    "Interrupted while waiting before retry",
                    exception);
        }
    }
}