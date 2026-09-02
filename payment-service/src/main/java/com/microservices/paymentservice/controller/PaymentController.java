package com.microservices.paymentservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final Map<Long, Map<String, Object>> payments =
            new ConcurrentHashMap<>();

    public PaymentController() {

        payments.put(
                1L,
                Map.of(
                        "paymentId", 1L,
                        "orderId", 1L,
                        "status", "SUCCESS",
                        "message", "Payment processed successfully"
                )
        );

        payments.put(
                2L,
                Map.of(
                        "paymentId", 2L,
                        "orderId", 2L,
                        "status", "SUCCESS",
                        "message", "Payment processed successfully"
                )
        );
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<Map<String, Object>> getPayment(
            @PathVariable("paymentId") Long paymentId) {

        Map<String, Object> payment = payments.get(paymentId);

        if (payment == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "status", 404,
                                    "error", "Payment Not Found",
                                    "message",
                                    "Payment not found with id: " + paymentId
                            )
                    );
        }

        return ResponseEntity.ok(payment);
    }

    @GetMapping("/health-test")
    public ResponseEntity<String> healthTest() {

        return ResponseEntity.ok(
                "Payment Service is running"
        );
    }

    @GetMapping("/slow-test")
    public ResponseEntity<String> slowPaymentTest()
            throws InterruptedException {

        Thread.sleep(10000);

        return ResponseEntity.ok(
                "Payment Service responded after delay"
        );
    }
}