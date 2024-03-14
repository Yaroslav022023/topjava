package ru.javawebinar.topjava.util.exception;

public enum ErrorType {
    APP_ERROR("common.appError"),
    DATA_NOT_FOUND("common.notfoundError"),
    DATA_ERROR("common.conflictError"),
    VALIDATION_ERROR("common.validationError");

    private final String code;

    ErrorType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}