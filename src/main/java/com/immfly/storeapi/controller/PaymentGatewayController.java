package com.immfly.storeapi.controller;

import com.immfly.storeapi.enums.PaymentStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/mock-payment")
public class PaymentGatewayController {

    @PostMapping("/stripe")
    public PaymentStatus processStripePayment(@RequestParam String cardToken, @RequestParam BigDecimal amount) {
        if (cardToken != null && cardToken.startsWith("tok_") && amount.compareTo(BigDecimal.ZERO) > 0) {
            return PaymentStatus.PAID;
        } else {
            return PaymentStatus.FAILED;
        }
    }

    @PostMapping("/paypal")
    public PaymentStatus processPaypalPayment(@RequestParam String cardToken, @RequestParam BigDecimal amount) {
        if (cardToken != null && cardToken.startsWith("tok_") && amount.compareTo(BigDecimal.ZERO) > 0) {
            return PaymentStatus.PAID;
        } else {
            return PaymentStatus.FAILED;
        }
    }
}
