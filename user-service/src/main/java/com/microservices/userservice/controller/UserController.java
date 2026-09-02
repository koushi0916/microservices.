package com.microservices.userservice.controller;

import com.microservices.userservice.dto.UserRequest;
import com.microservices.userservice.dto.UserResponse;
import com.microservices.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;

    @Value("${app.environment:NOT-SET}")
    private String environment;

    // Used only for the Controlled Retry challenge
    private int retryTestCount = 0;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // POST /api/users
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(request));
    }

    // GET /api/users/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable("userId") Long userId) {

        return ResponseEntity.ok(
                userService.getUserById(userId)
        );
    }

    // GET /api/users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }

    // GET /api/users/environment
    @GetMapping("/environment")
    public ResponseEntity<String> getEnvironment() {

        return ResponseEntity.ok(environment);
    }

    // GET /api/users/slow
    // Used for the Slow Dependency challenge
    @GetMapping("/slow")
    public ResponseEntity<String> slowEndpoint()
            throws InterruptedException {

        Thread.sleep(10000);

        return ResponseEntity.ok(
                "User Service response after 10 seconds"
        );
    }

    // GET /api/users/retry-test
    // Used for the Controlled Retry challenge
    @GetMapping("/retry-test")
    public ResponseEntity<String> retryTest() {

        retryTestCount++;

        System.out.println(
                "Retry test attempt: " + retryTestCount
        );

        // First attempt intentionally fails
        if (retryTestCount == 1) {

            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Temporary failure - try again");
        }

        // Second attempt succeeds
        int successfulAttempt = retryTestCount;

        // Reset so the next test starts from attempt 1
        retryTestCount = 0;

        return ResponseEntity.ok(
                "Success on attempt " + successfulAttempt
        );
    }
}