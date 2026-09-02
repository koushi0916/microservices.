package com.microservices.orderservice.controller;

import com.microservices.orderservice.dto.OrderRequest;
import com.microservices.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // =========================================================
    // GET ALL ORDERS
    // =========================================================

    @GetMapping
    public ResponseEntity<?> getAllOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }

    // =========================================================
    // CREATE ORDER
    // =========================================================

    @PostMapping
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody OrderRequest request) {

        return ResponseEntity.ok(
                orderService.createOrder(request)
        );
    }

    // =========================================================
    // ROLLBACK TEST
    // =========================================================

    @PostMapping("/test-rollback")
    public ResponseEntity<?> testRollback(
            @RequestBody(required = false) Map<String, Object> body) {

        orderService.testRollback(body);

        // This line should never execute because
        // testRollback() deliberately throws an exception.
        return ResponseEntity.ok("Rollback test completed");
    }

    // =========================================================
    // SAFE LOGGING TEST
    // =========================================================

    @PostMapping("/test-logging")
    public ResponseEntity<?> testLogging() {

        System.out.println(
                "SAFE LOG -> Order request received"
        );

        return ResponseEntity.ok(
                "Logging test completed"
        );
    }
}