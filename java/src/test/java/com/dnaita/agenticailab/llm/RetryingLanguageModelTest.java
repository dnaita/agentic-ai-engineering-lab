package com.dnaita.agenticailab.llm;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RetryingLanguageModelTest {

    private final ModelRequest request = new ModelRequest("", "Explain retries");

    @Test
    void retriesTransientFailureAndThenSucceeds() {
        AtomicInteger calls = new AtomicInteger();

        LanguageModel temporarilyUnavailable = (modelRequest, timeout) -> {
            if (calls.incrementAndGet() < 3) {
                throw new ModelException(
                        ModelErrorType.UNAVAILABLE,
                        "Temporary provider failure");
            }

            return new ModelResponse("Recovered");
        };

        RetryingLanguageModel model = createRetryingModel(
                temporarilyUnavailable, 3);

        ModelResponse response = model.generate(request, Duration.ofSeconds(2));

        assertEquals("Recovered", response.text());
        assertEquals(3, calls.get());
    }

    @Test
    void doesNotRetryAuthenticationFailure() {
        AtomicInteger calls = new AtomicInteger();

        LanguageModel authenticationFailure = (modelRequest, timeout) -> {
            calls.incrementAndGet();
            throw new ModelException(
                    ModelErrorType.AUTHENTICATION,
                    "Invalid credentials");
        };

        RetryingLanguageModel model = createRetryingModel(
                authenticationFailure, 3);

        ModelException exception = assertThrows(
                ModelException.class,
                () -> model.generate(request, Duration.ofSeconds(2)));

        assertEquals(ModelErrorType.AUTHENTICATION, exception.errorType());
        assertEquals(1, calls.get());
    }

    @Test
    void stopsAfterMaximumAttempts() {
        AtomicInteger calls = new AtomicInteger();

        LanguageModel alwaysUnavailable = (modelRequest, timeout) -> {
            calls.incrementAndGet();
            throw new ModelException(
                    ModelErrorType.UNAVAILABLE,
                    "Provider is unavailable");
        };

        RetryingLanguageModel model = createRetryingModel(
                alwaysUnavailable, 2);

        assertThrows(
                ModelException.class,
                () -> model.generate(request, Duration.ofSeconds(2)));

        assertEquals(2, calls.get());
    }

    @Test
    void passesTimeoutToUnderlyingModel() {
        AtomicReference<Duration> receivedTimeout = new AtomicReference<>();

        LanguageModel capturingModel = (modelRequest, timeout) -> {
            receivedTimeout.set(timeout);
            return new ModelResponse("Completed");
        };

        RetryingLanguageModel model = createRetryingModel(capturingModel, 1);
        Duration expectedTimeout = Duration.ofMillis(750);

        model.generate(request, expectedTimeout);

        assertEquals(expectedTimeout, receivedTimeout.get());
    }

    @Test
    void rejectsInvalidTimeoutBeforeCallingModel() {
        AtomicInteger calls = new AtomicInteger();

        LanguageModel unusedModel = (modelRequest, timeout) -> {
            calls.incrementAndGet();
            return new ModelResponse("Completed");
        };

        RetryingLanguageModel model = createRetryingModel(unusedModel, 3);

        assertThrows(
                IllegalArgumentException.class,
                () -> model.generate(request, Duration.ZERO));

        assertEquals(0, calls.get());
    }

    @Test
    void preservesProviderFailureWhenRetryDelayFails() {
        AtomicInteger calls = new AtomicInteger();
        RuntimeException delayFailure = new IllegalStateException("Retry delay failed");

        LanguageModel unavailableModel = (modelRequest, timeout) -> {
            calls.incrementAndGet();
            throw new ModelException(
                    ModelErrorType.UNAVAILABLE,
                    "Provider unavailable");
        };

        RetryingLanguageModel model = new RetryingLanguageModel(
                unavailableModel,
                new RetryPolicy(3, Duration.ofMillis(100)),
                ignored -> {
                    throw delayFailure;
                });

        ModelException exception = assertThrows(
                ModelException.class,
                () -> model.generate(request, Duration.ofSeconds(2)));

        assertEquals(ModelErrorType.UNAVAILABLE, exception.errorType());
        assertEquals("Provider unavailable", exception.getMessage());
        assertEquals(1, calls.get());
        assertEquals(1, exception.getSuppressed().length);
        assertSame(delayFailure, exception.getSuppressed()[0]);
    }

    private static RetryingLanguageModel createRetryingModel(
            LanguageModel delegate,
            int maxAttempts) {
        return new RetryingLanguageModel(
                delegate,
                new RetryPolicy(maxAttempts, Duration.ofMillis(100)),
                ignored -> {
                    // Do not actually wait during tests.
                });
    }
}
