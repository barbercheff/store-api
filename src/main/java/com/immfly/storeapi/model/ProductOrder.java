package com.immfly.storeapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products_orders")
public class ProductOrder {

    @EmbeddedId
    private ProductOrderId id;

    @ManyToOne
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Order order;

    public ProductOrder() {

    }

    public ProductOrderId getId() {
        return id;
    }

    public void setId(ProductOrderId id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
