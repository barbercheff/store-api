package com.immfly.storeapi.dto;

import com.immfly.storeapi.enums.PaymentGateway;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FinishOrderRequest {

    @NotBlank
    private String cardToken;

    @NotNull
    private PaymentGateway paymentGateway;

    public FinishOrderRequest() {
    }

    public FinishOrderRequest(String cardToken, PaymentGateway paymentGateway) {
        this.cardToken = cardToken;
        this.paymentGateway = paymentGateway;
    }

    public String getCardToken() {
        return cardToken;
    }

    public void setCardToken(String cardToken) {
        this.cardToken = cardToken;
    }

    public PaymentGateway getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }
}
