package com.dnaita.agenticailab.llm;

import java.util.Objects;

/**
 * A deterministic language model for local development and tests.
 *
 * This class makes no network calls.
 */
public final class FakeLanguageModel implements LanguageModel {

    @Override
    public ModelResponse generate(ModelRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        return new ModelResponse(
                "FAKE RESPONSE: " + request.userPrompt());
    }
}