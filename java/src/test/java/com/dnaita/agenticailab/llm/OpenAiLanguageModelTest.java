package com.dnaita.agenticailab.llm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenAiLanguageModelTest {

    @Test
    void mapsRequestTimeout() {
        assertEquals(
                ModelErrorType.TIMEOUT,
                OpenAiLanguageModel.classifyStatusCode(408));
    }

    @Test
    void mapsAuthenticationFailure() {
        assertEquals(
                ModelErrorType.AUTHENTICATION,
                OpenAiLanguageModel.classifyStatusCode(401));

        assertEquals(
                ModelErrorType.AUTHENTICATION,
                OpenAiLanguageModel.classifyStatusCode(403));
    }

    @Test
    void mapsRateLimit() {
        assertEquals(
                ModelErrorType.RATE_LIMITED,
                OpenAiLanguageModel.classifyStatusCode(429));
    }

    @Test
    void mapsClientErrors() {
        assertEquals(
                ModelErrorType.INVALID_REQUEST,
                OpenAiLanguageModel.classifyStatusCode(400));

        assertEquals(
                ModelErrorType.INVALID_REQUEST,
                OpenAiLanguageModel.classifyStatusCode(422));
    }

    @Test
    void mapsTransientServerErrors() {
        assertEquals(
                ModelErrorType.UNAVAILABLE,
                OpenAiLanguageModel.classifyStatusCode(500));

        assertEquals(
                ModelErrorType.UNAVAILABLE,
                OpenAiLanguageModel.classifyStatusCode(503));
    }
}