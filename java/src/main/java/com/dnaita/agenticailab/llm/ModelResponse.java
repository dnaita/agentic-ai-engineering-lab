package com.dnaita.agenticailab.llm;

import java.util.Objects;

public record ModelResponse(
        String text,
        ModelExecutionMetadata metadata) {

    // Keep the original one-argument constructor working.
    public ModelResponse(String text) {
        this(text, ModelExecutionMetadata.unknown());
    }

    public ModelResponse {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("text must not be blank");
        }

        Objects.requireNonNull(metadata, "metadata must not be null");
    }
}