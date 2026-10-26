package com.dnaita.agenticailab.llm;

/**
 * Common contract for all language model implementations.
 *
 * Provider-specific API details belong in adapter implementations,
 * not in application business logic.
 */
public interface LanguageModel {

    ModelResponse generate(ModelRequest request);
}