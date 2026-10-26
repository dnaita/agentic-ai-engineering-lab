package com.dnaita.agenticailab.llm;

/**
 * Provider-independent output returned by a language model.
 *
 * @param text the model's response text
 */
public record ModelResponse(String text) {

    public ModelResponse {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "text must not be blank");
        }
    }
}