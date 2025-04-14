package com.immfly.storeapi.dto;

import com.immfly.storeapi.enums.OrderStatus;
import com.immfly.storeapi.enums.PaymentGateway;
import com.immfly.storeapi.enums.PaymentStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {

    private Long id;

    private BigDecimal totalPrice;

    private PaymentStatus paymentStatus;

    private LocalDateTime paymentDate;

    private OrderStatus status;

    @NotBlank(message = "Buyer email must not be blank")
    @Email(message = "Buyer email must be a valid email address")
    private String buyerEmail;

    @NotNull(message = "Seat letter must not be null")
    private Character seatLetter;

    @NotNull(message = "Seat number must not be null")
    @Min(value = 1, message = "Seat number must be greater than 0")
    private Integer seatNumber;

    private List<Long> productIds;

    public OrderDTO() {
    }

    public OrderDTO(Long id, BigDecimal totalPrice, PaymentStatus paymentStatus,
                    LocalDateTime paymentDate, OrderStatus status, String buyerEmail,
                    Character seatLetter, Integer seatNumber, List<Long> productIds) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
        this.status = status;
        this.buyerEmail = buyerEmail;
        this.seatLetter = seatLetter;
        this.seatNumber = seatNumber;
        this.productIds = productIds;
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

    public Character getSeatLetter() {
        return seatLetter;
    }

    public void setSeatLetter(Character seatLetter) {
        this.seatLetter = seatLetter;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }
}
