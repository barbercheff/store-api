package com.immfly.storeapi.exception;

public class OrderNotDeletableException extends RuntimeException {
    public OrderNotDeletableException(String message) {
        super(message);
    }
}