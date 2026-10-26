package com.dnaita.agenticailab.llm;

import java.time.Duration;
import java.util.OptionalInt;

public record ModelExecutionMetadata(
        String provider,
        String model,
        Duration latency,
        OptionalInt inputTokens,
        OptionalInt outputTokens) {

    public ModelExecutionMetadata {
        if (provider == null || provider.isBlank()) {
            throw new IllegalArgumentException("provider must not be blank");
        }
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("model must not be blank");
        }
        if (latency == null || latency.isNegative()) {
            throw new IllegalArgumentException("latency must not be negative or null");
        }
        if (inputTokens == null || outputTokens == null) {
            throw new IllegalArgumentException("Token counts must not be null");
        }
        if (inputTokens.isPresent() && inputTokens.getAsInt() < 0) {
            throw new IllegalArgumentException("inputTokens must be non-negative");
        }
        if (outputTokens.isPresent() && outputTokens.getAsInt() < 0) {
            throw new IllegalArgumentException("outputTokens must be non-negative");
        }

        provider = provider.trim();
        model = model.trim();
    }

    public static ModelExecutionMetadata unknown() {
        return new ModelExecutionMetadata(
                "unknown",
                "unknown",
                Duration.ZERO,
                OptionalInt.empty(),
                OptionalInt.empty());
    }
}
