package com.dnaita.agenticailab.llm;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenAiLanguageModelTest {

    private static final String SUCCESS_RESPONSE = """
            {
              "id": "resp_test",
              "object": "response",
              "created_at": 1741476542,
              "status": "completed",
              "background": false,
              "error": null,
              "incomplete_details": null,
              "instructions": null,
              "max_output_tokens": null,
              "max_tool_calls": null,
              "model": "test-model",
              "output": [
                {
                  "id": "msg_test",
                  "type": "message",
                  "status": "completed",
                  "role": "assistant",
                  "content": [
                    {
                      "type": "output_text",
                      "annotations": [],
                      "logprobs": [],
                      "text": "Hello from the mock server"
                    }
                  ]
                }
              ],
              "parallel_tool_calls": true,
              "previous_response_id": null,
              "reasoning": {"effort": null, "summary": null},
              "store": true,
              "temperature": 1.0,
              "text": {"format": {"type": "text"}},
              "tool_choice": "auto",
              "tools": [],
              "top_p": 1.0,
              "truncation": "disabled",
              "usage": {
                "input_tokens": 7,
                "input_tokens_details": {"cached_tokens": 0},
                "output_tokens": 3,
                "output_tokens_details": {"reasoning_tokens": 0},
                "total_tokens": 10
              },
              "metadata": {}
            }
            """;

    private static final String RATE_LIMIT_RESPONSE = """
            {
              "error": {
                "message": "Too many requests",
                "type": "rate_limit_error",
                "param": null,
                "code": "rate_limit_exceeded"
              }
            }
            """;

    private final AtomicInteger capturedRequestCount = new AtomicInteger();
    private final AtomicReference<String> capturedPath = new AtomicReference<>();
    private final AtomicReference<String> capturedMethod = new AtomicReference<>();
    private final AtomicReference<String> capturedAuthorization = new AtomicReference<>();
    private final AtomicReference<String> capturedBody = new AtomicReference<>();

    private HttpServer server;
    private OpenAIClient client;
    private int responseStatus;
    private String responseBody;
    private long responseDelayMillis;

    @BeforeEach
    void startMockServer() throws IOException {
        responseStatus = 200;
        responseBody = SUCCESS_RESPONSE;
        responseDelayMillis = 0;

        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", this::handleRequest);
        server.start();

        String baseUrl = "http://127.0.0.1:"
                + server.getAddress().getPort()
                + "/v1";

        client = OpenAIOkHttpClient.builder()
                .apiKey("test-api-key")
                .baseUrl(baseUrl)
                .maxRetries(0)
                .build();
    }

    @AfterEach
    void stopMockServer() {
        if (client != null) {
            client.close();
        }
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void sendsModelInputAndInstructionsAndReturnsUsageMetadata() {
        OpenAiLanguageModel model = new OpenAiLanguageModel(client, "test-model");

        ModelResponse response = model.generate(
                new ModelRequest("Respond in one sentence.", "Say hello"),
                Duration.ofSeconds(3));

        assertEquals("Hello from the mock server", response.text());
        assertEquals("openai", response.metadata().provider());
        assertEquals("test-model", response.metadata().model());
        assertTrue(!response.metadata().latency().isNegative());
        assertEquals(7, response.metadata().inputTokens().orElseThrow());
        assertEquals(3, response.metadata().outputTokens().orElseThrow());

        assertEquals("POST", capturedMethod.get());
        assertEquals("/v1/responses", capturedPath.get());
        assertEquals("Bearer test-api-key", capturedAuthorization.get());

        String requestBody = capturedBody.get();
        assertNotNull(requestBody);
        assertTrue(requestBody.contains("\"model\":\"test-model\""), requestBody);
        assertTrue(requestBody.contains("\"input\":\"Say hello\""), requestBody);
        assertTrue(
                requestBody.contains("\"instructions\":\"Respond in one sentence.\""),
                requestBody);
    }

    @Test
    void translatesActualRateLimitResponseFromSdk() {
        responseStatus = 429;
        responseBody = RATE_LIMIT_RESPONSE;
        OpenAiLanguageModel model = new OpenAiLanguageModel(client, "test-model");

        ModelException exception = assertThrows(
                ModelException.class,
                () -> model.generate(
                        new ModelRequest("", "Try again"),
                        Duration.ofSeconds(3)));

        assertEquals(ModelErrorType.RATE_LIMITED, exception.errorType());
        assertNotNull(exception.getCause());
        assertEquals(1, serverRequestCount());
    }

    @Test
    void translatesActualServerErrorFromSdk() {
        responseStatus = 503;
        responseBody = """
                {
                  "error": {
                    "message": "Service unavailable",
                    "type": "server_error",
                    "param": null,
                    "code": "server_error"
                  }
                }
                """;
        OpenAiLanguageModel model = new OpenAiLanguageModel(client, "test-model");

        ModelException exception = assertThrows(
                ModelException.class,
                () -> model.generate(
                        new ModelRequest("", "Try again"),
                        Duration.ofSeconds(3)));

        assertEquals(ModelErrorType.UNAVAILABLE, exception.errorType());
        assertNotNull(exception.getCause());
        assertEquals(1, serverRequestCount());
    }

    @Test
    void appliesRequestTimeoutAndMapsTimeoutFailure() {
        responseDelayMillis = 500;
        OpenAiLanguageModel model = new OpenAiLanguageModel(client, "test-model");

        ModelException exception = assertThrows(
                ModelException.class,
                () -> model.generate(
                        new ModelRequest("", "Take your time"),
                        Duration.ofMillis(50)));

        assertEquals(ModelErrorType.TIMEOUT, exception.errorType());
    }

    @Test
    void mapsRequestTimeoutStatus() {
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

    private void handleRequest(HttpExchange exchange) throws IOException {
        capturedRequestCount.incrementAndGet();
        capturedPath.set(exchange.getRequestURI().getPath());
        capturedMethod.set(exchange.getRequestMethod());
        capturedAuthorization.set(
                exchange.getRequestHeaders().getFirst("Authorization"));
        capturedBody.set(new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8));

        try {
            if (responseDelayMillis > 0) {
                Thread.sleep(responseDelayMillis);
            }

            byte[] body = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(responseStatus, body.length);
            exchange.getResponseBody().write(body);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        } catch (IOException ignored) {
            // The client may close the connection when a timeout expires.
        } finally {
            exchange.close();
        }
    }

    private int serverRequestCount() {
        return capturedRequestCount.get();
    }
}
