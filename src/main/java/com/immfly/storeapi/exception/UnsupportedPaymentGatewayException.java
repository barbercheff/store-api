package com.immfly.storeapi.exception;

public class UnsupportedPaymentGatewayException extends RuntimeException {
    public UnsupportedPaymentGatewayException(String message) {
        super(message);
    }
}
