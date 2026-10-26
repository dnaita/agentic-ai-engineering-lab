package com.dnaita.agenticailab.llm;

import java.time.Duration;
import java.util.Objects;
import java.util.OptionalInt;

public final class FakeLanguageModel implements LanguageModel {

    @Override
    public ModelResponse generate(ModelRequest request, Duration timeout) {
        Objects.requireNonNull(request, "request must not be null");

        if (timeout == null || timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("timeout must be positive");
        }

        ModelExecutionMetadata metadata = new ModelExecutionMetadata(
                "fake",
                "deterministic",
                Duration.ZERO,
                OptionalInt.empty(),
                OptionalInt.empty());

        return new ModelResponse(
                "FAKE RESPONSE: " + request.userPrompt(),
                metadata);
    }
}
