package com.dnaita.agenticailab.llm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FakeLanguageModelTest {

    // The application-facing type is the interface, not the fake class.
    private final LanguageModel languageModel = new FakeLanguageModel();

    @Test
    void returnsDeterministicResponse() {
        ModelRequest request = new ModelRequest(
                "You are a Java expert.",
                "Explain retries");

        ModelResponse response = languageModel.generate(request);

        assertEquals(
                "FAKE RESPONSE: Explain retries",
                response.text());
    }

    @Test
    void normalizesOptionalSystemPromptAndTrimsUserPrompt() {
        ModelRequest request = new ModelRequest(
                null,
                "  Explain retries  ");

        assertEquals("", request.systemPrompt());
        assertEquals("Explain retries", request.userPrompt());
    }

    @Test
    void rejectsBlankUserPrompt() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ModelRequest("Instructions", "   "));
    }

    @Test
    void rejectsBlankModelResponse() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ModelResponse("   "));
    }

    @Test
    void rejectsNullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> languageModel.generate(null));
    }
}