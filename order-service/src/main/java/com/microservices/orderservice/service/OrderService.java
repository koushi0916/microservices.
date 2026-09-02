package com.microservices.orderservice.service;

import com.microservices.orderservice.dto.OrderRequest;

import java.util.Map;

public interface OrderService {

    // =========================================================
    // GET ALL ORDERS
    // =========================================================

    Object getAllOrders();

    // =========================================================
    // CREATE ORDER
    // =========================================================

    Object createOrder(OrderRequest request);

    // =========================================================
    // ROLLBACK TEST
    // =========================================================

    void testRollback(Map<String, Object> body);
}