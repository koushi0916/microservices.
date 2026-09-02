package com.microservices.orderservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public OrderItem() {
    }

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public OrderItem(
            Long orderId,
            Long productId,
            Integer quantity) {

        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    // =========================================================
    // SETTERS
    // =========================================================

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}