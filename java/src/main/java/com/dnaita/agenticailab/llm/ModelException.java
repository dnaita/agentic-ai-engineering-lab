package com.dnaita.agenticailab.llm;

public final class ModelException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ModelErrorType errorType;

    public ModelException(ModelErrorType errorType, String message) {
        super(message);
        this.errorType = requireErrorType(errorType);
    }

    public ModelException(
            ModelErrorType errorType,
            String message,
            Throwable cause) {
        super(message, cause);
        this.errorType = requireErrorType(errorType);
    }

    public ModelErrorType errorType() {
        return errorType;
    }

    public boolean isRetryable() {
        return errorType.isRetryable();
    }

    private static ModelErrorType requireErrorType(ModelErrorType errorType) {
        if (errorType == null) {
            throw new IllegalArgumentException("errorType must not be null");
        }
        return errorType;
    }
}