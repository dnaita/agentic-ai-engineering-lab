package com.dnaita.agenticailab.llm;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FakeLanguageModelTest {

    private final LanguageModel languageModel = new FakeLanguageModel();

    @Test
    void returnsDeterministicResponseAndMetadata() {
        ModelRequest request = new ModelRequest(
                "You are a Java expert.",
                "Explain retries");

        ModelResponse response = languageModel.generate(request);

        assertEquals("FAKE RESPONSE: Explain retries", response.text());
        assertEquals("fake", response.metadata().provider());
        assertEquals("deterministic", response.metadata().model());
        assertEquals(Duration.ZERO, response.metadata().latency());
        assertTrue(response.metadata().inputTokens().isEmpty());
        assertTrue(response.metadata().outputTokens().isEmpty());
    }

    @Test
    void normalizesOptionalSystemPromptAndTrimsUserPrompt() {
        ModelRequest request = new ModelRequest(null, "  Explain retries  ");

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

    @Test
    void rejectsNonPositiveTimeout() {
        ModelRequest request = new ModelRequest("", "Explain retries");

        assertThrows(
                IllegalArgumentException.class,
                () -> languageModel.generate(request, Duration.ZERO));
    }
}