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
        return generateMockPaymentResponse(cardToken, "Stripe");
    }

    @PostMapping("/paypal")
    public PaymentResponse processPaypalPayment(@RequestParam String cardToken, @RequestParam BigDecimal amount) {
        return generateMockPaymentResponse(cardToken, "PayPal");
    }

    private PaymentResponse generateMockPaymentResponse(String cardToken, String gateway) {
        if (cardToken != null) {
            if (cardToken.startsWith("tok_")) {
                return new PaymentResponse(
                        "success",
                        UUID.randomUUID().toString(),
                        gateway + " payment successful"
                );
            } else if (cardToken.startsWith("offline_")) {
                return new PaymentResponse(
                        "offline",
                        UUID.randomUUID().toString(),
                        gateway + " offline payment"
                );
            } else {
                return new PaymentResponse(
                        "failed",
                        UUID.randomUUID().toString(),
                        gateway + " payment failed"
                );
            }
        } else {
            return new PaymentResponse(
                    "failed",
                    UUID.randomUUID().toString(),
                    gateway + " payment failed (no cardToken provided)"
            );
        }
    }
}
