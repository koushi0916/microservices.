package com.microservices.orderservice.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private BigDecimal amount;

    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public Order() {
    }

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Order(
            Long userId,
            String productName,
            BigDecimal amount) {

        this.userId = userId;
        this.productName = productName;
        this.amount = amount;
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    // =========================================================
    // SETTERS
    // =========================================================

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}