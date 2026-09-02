package com.microservices.orderservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Component
public class PaymentServiceClient {

    private final RestClient restClient;

    public PaymentServiceClient(
            @Value("${payment-service.base-url}") String baseUrl) {

        System.out.println(
                "PAYMENT SERVICE BASE URL = " + baseUrl
        );

        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(3000);

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public Map<String, Object> makePayment(
            Long orderId,
            double amount) {

        System.out.println(
                "Calling Payment Service: http://127.0.0.1:8083/api/payments/"
                        + orderId
        );

        try {

            Map<String, Object> response = restClient
                    .get()
                    .uri("/api/payments/{paymentId}", orderId)
                    .retrieve()
                    .body(Map.class);

            System.out.println(
                    "Payment Service response: " + response
            );

            return response;

        } catch (Exception e) {

            System.out.println(
                    "PAYMENT SERVICE ERROR: "
                            + e.getClass().getName()
            );

            System.out.println(
                    "PAYMENT SERVICE ERROR MESSAGE: "
                            + e.getMessage()
            );

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Payment service failed: " + e.getMessage(),
                    e
            );
        }
    }
}