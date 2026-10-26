package com.dnaita.agenticailab.llm;

import java.time.Duration;

public interface LanguageModel {

    /**
     * Runs one model attempt.
     *
     * Implementations must apply the timeout at the provider/client layer.
     */
    ModelResponse generate(ModelRequest request, Duration timeout);

    /**
     * Convenience method using a default per-attempt timeout of 30 seconds.
     */
    default ModelResponse generate(ModelRequest request) {
        return generate(request, Duration.ofSeconds(30));
    }
}
