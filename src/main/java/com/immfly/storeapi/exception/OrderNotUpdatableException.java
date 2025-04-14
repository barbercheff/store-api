package com.immfly.storeapi.exception;

public class OrderNotUpdatableException extends RuntimeException {
    public OrderNotUpdatableException(String message) {
        super(message);
    }
}