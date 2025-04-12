package com.immfly.storeapi.dto;

import com.immfly.storeapi.enums.OrderStatus;
import com.immfly.storeapi.enums.PaymentGateway;
import com.immfly.storeapi.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderDTO {
    private Long id;
    private BigDecimal totalPrice;
    private String cardToken;
    private PaymentStatus paymentStatus;
    private LocalDateTime paymentDate;
    private PaymentGateway paymentGateway;
    private OrderStatus status;
    private String buyerEmail;
    private String seatLetter;
    private int seatNumber;

    public OrderDTO() {
    }

    public OrderDTO(Long id, BigDecimal totalPrice, String cardToken, PaymentStatus paymentStatus,
                    LocalDateTime paymentDate, PaymentGateway paymentGateway, OrderStatus status,
                    String buyerEmail, String seatLetter, int seatNumber) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.cardToken = cardToken;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
        this.paymentGateway = paymentGateway;
        this.status = status;
        this.buyerEmail = buyerEmail;
        this.seatLetter = seatLetter;
        this.seatNumber = seatNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCardToken() {
        return cardToken;
    }

    public void setCardToken(String cardToken) {
        this.cardToken = cardToken;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentGateway getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public String getSeatLetter() {
        return seatLetter;
    }

    public void setSeatLetter(String seatLetter) {
        this.seatLetter = seatLetter;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }
}
