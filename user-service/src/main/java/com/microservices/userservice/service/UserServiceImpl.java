package com.microservices.userservice.service;

import com.microservices.userservice.dto.UserRequest;
import com.microservices.userservice.dto.UserResponse;
import com.microservices.userservice.exception.BusinessException;
import com.microservices.userservice.exception.ResourceNotFoundException;
import com.microservices.userservice.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserServiceImpl implements UserService {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    @PostConstruct
    void seedSampleData() {

        saveUser(new User(
                null,
                "Alice Johnson",
                "alice@example.com",
                "ACTIVE"
        ));

        saveUser(new User(
                null,
                "Bob Smith",
                "bob@example.com",
                "ACTIVE"
        ));

        saveUser(new User(
                null,
                "Charlie Brown",
                "charlie@example.com",
                "INACTIVE"
        ));
    }

    @Override
    public UserResponse createUser(UserRequest request) {

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();

        boolean emailExists =
                users.values()
                        .stream()
                        .anyMatch(user ->
                                user.getEmail()
                                        .equalsIgnoreCase(email)
                        );

        if (emailExists) {
            throw new BusinessException(
                    "User with this email already exists"
            );
        }

        User user = saveUser(new User(
                null,
                request.getName().trim(),
                email,
                "ACTIVE"
        ));

        return mapToResponse(user);
    }

    @Override
    public UserResponse getUserById(Long id) {

        User user = users.get(id);

        if (user == null) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + id
            );
        }

        return mapToResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {

        List<UserResponse> responses =
                new ArrayList<>();

        for (User user : users.values()) {
            responses.add(mapToResponse(user));
        }

        return responses;
    }

    private User saveUser(User user) {

        long id =
                idSequence.getAndIncrement();

        user.setId(id);

        users.put(id, user);

        return user;
    }

    private UserResponse mapToResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAccountStatus()
        );
    }
}