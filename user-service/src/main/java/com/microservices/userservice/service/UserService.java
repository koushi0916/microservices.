package com.microservices.userservice.service;

import com.microservices.userservice.dto.UserRequest;
import com.microservices.userservice.dto.UserResponse;

import java.util.List;

public interface UserService {

	UserResponse createUser(UserRequest request);

	UserResponse getUserById(Long id);

	List<UserResponse> getAllUsers();
}
