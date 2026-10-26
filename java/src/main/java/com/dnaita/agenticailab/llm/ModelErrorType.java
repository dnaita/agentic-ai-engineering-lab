package com.dnaita.agenticailab.llm;

public enum ModelErrorType {
    TIMEOUT(true),
    RATE_LIMITED(true),
    UNAVAILABLE(true),
    AUTHENTICATION(false),
    INVALID_REQUEST(false),
    INVALID_RESPONSE(false),
    INTERRUPTED(false),
    UNKNOWN(false);

    private final boolean retryable;

    ModelErrorType(boolean retryable) {
        this.retryable = retryable;
    }

    public boolean isRetryable() {
        return retryable;
    }
}
