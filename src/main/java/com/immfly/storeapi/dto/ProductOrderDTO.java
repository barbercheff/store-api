package com.immfly.storeapi.dto;

public class ProductOrderDTO {

    private Long orderId;

    private Long productId;

    public ProductOrderDTO() {
    }

    public ProductOrderDTO(Long orderId, Long productId) {
        this.orderId = orderId;
        this.productId = productId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
