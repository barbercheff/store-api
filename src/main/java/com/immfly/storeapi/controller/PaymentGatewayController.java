package com.immfly.storeapi.controller;

import com.immfly.storeapi.dto.PaymentResponse;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/mock-payment")
public class PaymentGatewayController {

    @PostMapping("/stripe")
    public PaymentResponse processStripePayment(@RequestParam String cardToken, @RequestParam BigDecimal amount) {
        if (cardToken != null && cardToken.startsWith("tok_")) {
            return new PaymentResponse(
                    "success",
                    UUID.randomUUID().toString(),
                    "Stripe payment successful"
            );
        } else {
            return new PaymentResponse(
                    "failed",
                    UUID.randomUUID().toString(),
                    "Stripe payment failed"
            );
        }
    }

    @PostMapping("/paypal")
    public PaymentResponse processPaypalPayment(@RequestParam String cardToken, @RequestParam BigDecimal amount) {
        if (cardToken != null && cardToken.startsWith("tok_")) {
            return new PaymentResponse(
                    "success",
                    UUID.randomUUID().toString(),
                    "PayPal payment successful"
            );
        } else {
            return new PaymentResponse(
                    "failed",
                    UUID.randomUUID().toString(),
                    "PayPal payment failed"
            );
        }
    }
}