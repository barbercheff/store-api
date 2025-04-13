package com.immfly.storeapi.exception;

public class OrderAlreadyFinishedException extends RuntimeException {
    public OrderAlreadyFinishedException(String message) {
        super(message);
    }
}
