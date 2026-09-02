package com.microservices.orderservice.client;

import com.microservices.orderservice.dto.UserInfo;
import com.microservices.orderservice.exception.DownstreamServiceException;
import com.microservices.orderservice.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class UserServiceClient {

    private static final String CORRELATION_ID =
            "X-Correlation-ID";

    private final RestClient userServiceRestClient;
    private final HttpServletRequest httpServletRequest;

    public UserServiceClient(
            @Value("${user-service.base-url}") String baseUrl,
            RestClient.Builder restClientBuilder,
            HttpServletRequest httpServletRequest) {

        System.out.println(
                "USER SERVICE BASE URL = " + baseUrl
        );

        this.httpServletRequest = httpServletRequest;

        this.userServiceRestClient =
                restClientBuilder
                        .baseUrl(baseUrl)
                        .build();
    }

    // =========================================================
    // NORMAL USER SERVICE CALL
    // =========================================================

    public UserInfo getUserById(Long userId) {

        System.out.println(
                "HTTP CALL -> User Service: "
                        + userId
        );

        String correlationId =
                httpServletRequest.getHeader(
                        CORRELATION_ID
                );

        System.out.println(
                "[" + correlationId + "] "
                        + "Order Service -> User Service"
        );

        try {

            return userServiceRestClient
                    .get()

                    .uri(
                            "/api/users/{userId}",
                            userId
                    )

                    .header(
                            CORRELATION_ID,
                            correlationId
                    )

                    .retrieve()

                    .onStatus(
                            HttpStatusCode::is4xxClientError,
                            (request, response) -> {

                                if (response
                                        .getStatusCode()
                                        .value() == 404) {

                                    throw new ResourceNotFoundException(
                                            "User not found with id: "
                                                    + userId
                                    );
                                }

                                throw new DownstreamServiceException(
                                        "User Service returned client error: "
                                                + response.getStatusCode()
                                );
                            }
                    )

                    .onStatus(
                            HttpStatusCode::is5xxServerError,
                            (request, response) -> {

                                throw new DownstreamServiceException(
                                        "User Service returned server error: "
                                                + response.getStatusCode()
                                );
                            }
                    )

                    .body(UserInfo.class);

        } catch (ResourceAccessException ex) {

            System.out.println(
                    "USER SERVICE CONNECTION/TIMEOUT ERROR"
            );

            throw new DownstreamServiceException(
                    "User Service request failed or timed out",
                    ex
            );
        }
    }

    // =========================================================
    // SLOW USER SERVICE
    // =========================================================

    public String callSlowUserService() {

        System.out.println(
                "Calling slow User Service endpoint..."
        );

        try {

            return userServiceRestClient
                    .get()
                    .uri("/api/users/slow")
                    .retrieve()
                    .body(String.class);

        } catch (ResourceAccessException ex) {

            System.out.println(
                    "SLOW USER SERVICE TIMEOUT"
            );

            throw new DownstreamServiceException(
                    "User Service timed out while processing slow request",
                    ex
            );
        }
    }

    // =========================================================
    // RETRYABLE GET
    // =========================================================

    public String callRetryableGet() {

        System.out.println(
                "Calling retryable GET User Service..."
        );

        return userServiceRestClient
                .get()
                .uri("/api/users/retry-test")
                .retrieve()
                .body(String.class);
    }

    // =========================================================
    // MANUAL CONTROLLED RETRY TEST
    // =========================================================

    public String callRetryTest() {

        int maxAttempts = 3;

        for (int attempt = 1;
             attempt <= maxAttempts;
             attempt++) {

            System.out.println(
                    "Calling User Service - retry attempt "
                            + attempt
                            + " of "
                            + maxAttempts
            );

            try {

                String response =
                        userServiceRestClient
                                .get()
                                .uri("/api/users/retry-test")
                                .retrieve()

                                .onStatus(
                                        HttpStatusCode::is4xxClientError,
                                        (request, response1) -> {

                                            throw new DownstreamServiceException(
                                                    "User Service returned "
                                                            + "client error: "
                                                            + response1.getStatusCode()
                                            );
                                        }
                                )

                                .onStatus(
                                        HttpStatusCode::is5xxServerError,
                                        (request, response1) -> {

                                            throw new DownstreamServiceException(
                                                    "User Service returned "
                                                            + "server error: "
                                                            + response1.getStatusCode()
                                            );
                                        }
                                )

                                .body(String.class);

                System.out.println(
                        "User Service succeeded on attempt "
                                + attempt
                );

                return response;

            } catch (DownstreamServiceException ex) {

                System.out.println(
                        "Attempt "
                                + attempt
                                + " failed: "
                                + ex.getMessage()
                );

                if (attempt == maxAttempts) {

                    System.out.println(
                            "All retry attempts failed."
                    );

                    throw new DownstreamServiceException(
                            "User Service failed after "
                                    + maxAttempts
                                    + " attempts",
                            ex
                    );
                }

                System.out.println(
                        "Retrying User Service..."
                );
            }
        }

        throw new DownstreamServiceException(
                "User Service retry failed"
        );
    }
}