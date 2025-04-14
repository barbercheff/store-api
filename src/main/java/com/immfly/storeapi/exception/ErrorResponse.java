package com.immfly.storeapi.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {
    private List<String> errors;
    private int status;
    private LocalDateTime timestamp;

    public ErrorResponse(List<String> errors, int status, LocalDateTime timestamp) {
        this.errors = errors;
        this.status = status;
        this.timestamp = timestamp;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
