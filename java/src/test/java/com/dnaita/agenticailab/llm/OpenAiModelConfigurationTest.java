package com.dnaita.agenticailab.llm;

import com.openai.client.OpenAIClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenAiModelConfigurationTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withUserConfiguration(OpenAiModelConfiguration.class);

    @Test
    void doesNotCreateProviderBeansUnlessOpenAiIsSelected() {
        contextRunner.run(context -> {
            assertFalse(context.containsBean("openAiClient"));
            assertFalse(context.getBeansOfType(LanguageModel.class).size() > 0);
        });
    }

    @Test
    void createsOpenAiBeansFromConfiguredPropertiesWithoutCallingProvider() {
        contextRunner
                .withPropertyValues(
                        "agentic-ai.model.provider=openai",
                        "OPENAI_API_KEY=test-api-key",
                        "OPENAI_MODEL=test-model")
                .run(context -> {
                    assertTrue(context.getStartupFailure() == null,
                            () -> String.valueOf(context.getStartupFailure()));
                    assertTrue(context.getBean(LanguageModel.class)
                            instanceof OpenAiLanguageModel);
                    assertNotNull(context.getBean(OpenAIClient.class));
                });
    }

    @Test
    void failsFastWhenApiKeyIsMissing() {
        contextRunner
                .withPropertyValues(
                        "agentic-ai.model.provider=openai",
                        "OPENAI_MODEL=test-model")
                .run(context -> {
                    assertNotNull(context.getStartupFailure());
                    assertEquals(
                            "Set the OPENAI_API_KEY environment variable when "
                                    + "AGENTIC_AI_MODEL_PROVIDER=openai",
                            rootCause(context.getStartupFailure()).getMessage());
                });
    }

    @Test
    void failsFastWhenModelNameIsMissing() {
        contextRunner
                .withPropertyValues(
                        "agentic-ai.model.provider=openai",
                        "OPENAI_API_KEY=test-api-key")
                .run(context -> {
                    assertNotNull(context.getStartupFailure());
                    assertEquals(
                            "Set the OPENAI_MODEL environment variable when "
                                    + "AGENTIC_AI_MODEL_PROVIDER=openai",
                            rootCause(context.getStartupFailure()).getMessage());
                });
    }

    private static Throwable rootCause(Throwable failure) {
        Throwable current = failure;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }
}
