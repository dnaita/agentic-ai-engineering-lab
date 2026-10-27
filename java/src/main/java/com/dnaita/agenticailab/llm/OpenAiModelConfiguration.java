package com.dnaita.agenticailab.llm;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers the OpenAI implementation only when explicitly selected.
 *
 * <p>Credentials and the model name are supplied through environment-backed
 * Spring properties. Creating these beans does not make an API request.</p>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
        prefix = "agentic-ai.model",
        name = "provider",
        havingValue = "openai")
public class OpenAiModelConfiguration {

    @Bean(destroyMethod = "close")
    OpenAIClient openAiClient(
            @Value("${OPENAI_API_KEY:}") String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Set the OPENAI_API_KEY environment variable when "
                            + "AGENTIC_AI_MODEL_PROVIDER=openai");
        }

        // The shared RetryingLanguageModel owns retries for consistent policy.
        return OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .maxRetries(0)
                .build();
    }

    @Bean
    LanguageModel languageModel(
            OpenAIClient client,
            @Value("${OPENAI_MODEL:}") String modelName) {
        if (modelName == null || modelName.isBlank()) {
            throw new IllegalStateException(
                    "Set the OPENAI_MODEL environment variable when "
                            + "AGENTIC_AI_MODEL_PROVIDER=openai");
        }

        return new OpenAiLanguageModel(client, modelName);
    }
}
