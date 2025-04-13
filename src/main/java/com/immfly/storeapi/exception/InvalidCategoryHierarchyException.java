package com.immfly.storeapi.exception;

public class InvalidCategoryHierarchyException extends RuntimeException {
    public InvalidCategoryHierarchyException(String message) {
        super(message);
    }
}
