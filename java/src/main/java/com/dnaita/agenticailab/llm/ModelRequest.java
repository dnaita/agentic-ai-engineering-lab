package com.dnaita.agenticailab.llm;

/**
 * Provider-independent input sent to a language model.
 *
 * @param systemPrompt instructions describing the model's role
 * @param userPrompt   the user's actual request
 */
public record ModelRequest(String systemPrompt, String userPrompt) {

    public ModelRequest {
        // A system prompt is optional.
        systemPrompt = systemPrompt == null ? "" : systemPrompt.trim();

        // A user request must contain meaningful text.
        if (userPrompt == null || userPrompt.isBlank()) {
            throw new IllegalArgumentException(
                    "userPrompt must not be blank");
        }

        userPrompt = userPrompt.trim();
    }
}
