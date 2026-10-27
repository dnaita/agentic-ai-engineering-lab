package com.dnaita.agenticailab.llm;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.RequestOptions;
import com.openai.errors.OpenAIException;
import com.openai.errors.OpenAIInvalidDataException;
import com.openai.errors.OpenAIIoException;
import com.openai.errors.OpenAIRetryableException;
import com.openai.errors.OpenAIServiceException;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public final class OpenAiLanguageModel implements LanguageModel {

    private final OpenAIClient client;
    private final String modelName;

    public OpenAiLanguageModel(OpenAIClient client, String modelName) {
        this.client = Objects.requireNonNull(
                client, "client must not be null");

        if (modelName == null || modelName.isBlank()) {
            throw new IllegalArgumentException(
                    "modelName must not be blank");
        }

        this.modelName = modelName.trim();
    }

    /**
     * Creates a client using environment-based credentials and configuration.
     * No API request is made during construction.
     */
    public static OpenAiLanguageModel fromEnvironment() {
        String apiKey = System.getenv("OPENAI_API_KEY");
        String modelName = System.getenv("OPENAI_MODEL");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Set the OPENAI_API_KEY environment variable");
        }

        if (modelName == null || modelName.isBlank()) {
            throw new IllegalStateException(
                    "Set the OPENAI_MODEL environment variable");
        }

        OpenAIClient client = OpenAIOkHttpClient.builder()
                .fromEnv()
                .maxRetries(0)
                .build();

        return new OpenAiLanguageModel(client, modelName);
    }

    @Override
    public ModelResponse generate(
            ModelRequest request,
            Duration timeout) {

        Objects.requireNonNull(request, "request must not be null");

        if (timeout == null || timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException(
                    "timeout must be positive");
        }

        long startNanos = System.nanoTime();

        try {
            ResponseCreateParams.Builder paramsBuilder = ResponseCreateParams.builder()
                    .model(modelName)
                    .input(request.userPrompt());

            if (!request.systemPrompt().isBlank()) {
                paramsBuilder.instructions(request.systemPrompt());
            }

            Response response = client.responses().create(
                    paramsBuilder.build(),
                    RequestOptions.builder()
                            .timeout(timeout)
                            .build());

            String responseText = response.output().stream()
                    .flatMap(item -> item.message().stream())
                    .flatMap(message -> message.content().stream())
                    .flatMap(content -> content.outputText().stream())
                    .map(outputText -> outputText.text())
                    .collect(Collectors.joining())
                    .trim();

            if (responseText.isBlank()) {
                throw new ModelException(
                        ModelErrorType.INVALID_RESPONSE,
                        "OpenAI returned no text output");
            }

            Duration latency = Duration.ofNanos(
                    System.nanoTime() - startNanos);

            OptionalInt inputTokens = response.usage()
                    .map(usage -> OptionalInt.of(
                            Math.toIntExact(usage.inputTokens())))
                    .orElseGet(OptionalInt::empty);

            OptionalInt outputTokens = response.usage()
                    .map(usage -> OptionalInt.of(
                            Math.toIntExact(usage.outputTokens())))
                    .orElseGet(OptionalInt::empty);

            ModelExecutionMetadata metadata = new ModelExecutionMetadata(
                    "openai",
                    modelName,
                    latency,
                    inputTokens,
                    outputTokens);

            return new ModelResponse(responseText, metadata);

        } catch (ModelException exception) {
            throw exception;
        } catch (OpenAIException exception) {
            throw translateException(exception);
        }
    }

    private static ModelException translateException(
            OpenAIException exception) {

        ModelErrorType errorType;

        if (exception instanceof OpenAIServiceException serviceException) {
            errorType = classifyStatusCode(
                    serviceException.statusCode());
        } else if (hasTimeoutCause(exception)) {
            errorType = ModelErrorType.TIMEOUT;
        } else if (exception instanceof OpenAIInvalidDataException) {
            errorType = ModelErrorType.INVALID_RESPONSE;
        } else if (exception instanceof OpenAIIoException
                || exception instanceof OpenAIRetryableException) {
            errorType = ModelErrorType.UNAVAILABLE;
        } else {
            errorType = ModelErrorType.UNKNOWN;
        }

        return new ModelException(
                errorType,
                "OpenAI request failed: " + errorType,
                exception);
    }

    static ModelErrorType classifyStatusCode(int statusCode) {
        return switch (statusCode) {
            case 408 -> ModelErrorType.TIMEOUT;
            case 401, 403 -> ModelErrorType.AUTHENTICATION;
            case 429 -> ModelErrorType.RATE_LIMITED;
            case 400, 404, 422 -> ModelErrorType.INVALID_REQUEST;
            case 409 -> ModelErrorType.UNAVAILABLE;
            default -> statusCode >= 500
                    ? ModelErrorType.UNAVAILABLE
                    : statusCode >= 400
                            ? ModelErrorType.UNKNOWN
                            : ModelErrorType.UNKNOWN;
        };
    }

    private static boolean hasTimeoutCause(Throwable failure) {
        Throwable current = failure;

        while (current != null) {
            if (current instanceof SocketTimeoutException
                    || current instanceof TimeoutException
                    || current instanceof InterruptedIOException) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }
}