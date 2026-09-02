package com.microservices.orderservice.service;

import com.microservices.orderservice.client.UserServiceClient;
import com.microservices.orderservice.dto.OrderRequest;
import com.microservices.orderservice.dto.UserInfo;
import com.microservices.orderservice.model.Order;
import com.microservices.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserServiceClient userServiceClient;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            UserServiceClient userServiceClient) {

        this.orderRepository = orderRepository;
        this.userServiceClient = userServiceClient;
    }

    // =========================================================
    // GET ALL ORDERS
    // =========================================================

    @Override
    public Object getAllOrders() {

        return orderRepository.findAll();
    }

    // =========================================================
    // CREATE ORDER
    // =========================================================

    @Override
    @Transactional
    public Object createOrder(OrderRequest request) {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("CREATE ORDER STARTED");
        System.out.println("User ID = " + request.getUserId());
        System.out.println("Product = " + request.getProductName());
        System.out.println("Amount = " + request.getAmount());
        System.out.println("==========================================");

        // STEP 1: Call USER-SERVICE

        System.out.println(
                "STEP 1 -> Verifying user with USER-SERVICE"
        );

        UserInfo user =
                userServiceClient.getUserById(
                        request.getUserId()
                );

        System.out.println(
                "USER VERIFIED -> " + user
        );

        // STEP 2: Check account status

        System.out.println(
                "STEP 2 -> Checking user account status"
        );

        if (!"ACTIVE".equalsIgnoreCase(
                user.getAccountStatus())) {

            System.out.println(
                    "ORDER REJECTED -> User account is "
                            + user.getAccountStatus()
            );

            throw new RuntimeException(
                    "User account is not active. "
                            + "Order cannot be created."
            );
        }

        System.out.println(
                "USER ACCOUNT STATUS = ACTIVE"
        );

        // STEP 3: Create Order

        System.out.println(
                "STEP 3 -> Creating order"
        );

        Order order = new Order();

        order.setUserId(user.getId());

        order.setProductName(
                request.getProductName()
        );

        order.setAmount(
                request.getAmount()
        );

        // STEP 4: Save Order

        System.out.println(
                "STEP 4 -> Saving order to ORDER DB"
        );

        Order savedOrder =
                orderRepository.save(order);

        System.out.println(
                "ORDER CREATED -> ID = "
                        + savedOrder.getId()
        );

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "CREATE ORDER COMPLETED"
        );

        System.out.println(
                "==========================================");

        return savedOrder;
    }

    // =========================================================
    // ROLLBACK TEST
    // =========================================================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void testRollback(
            Map<String, Object> body) {

        System.out.println();
        System.out.println(
                "=========================================="
        );

        System.out.println(
                "ROLLBACK TEST STARTED"
        );

        System.out.println(
                "Request Body = " + body
        );

        System.out.println(
                "=========================================="
        );

        Order order = new Order();

        order.setUserId(999L);

        order.setProductName(
                "ROLLBACK-TEST"
        );

        order.setAmount(
                new BigDecimal("100.00")
        );

        Order savedOrder =
                orderRepository.save(order);

        System.out.println(
                "ORDER SAVED"
        );

        System.out.println(
                "Order ID = "
                        + savedOrder.getId()
        );

        System.out.println();

        System.out.println(
                "FORCING EXCEPTION - "
                        + "ROLLBACK SHOULD HAPPEN"
        );

        System.out.println();

        throw new RuntimeException(
                "FORCING EXCEPTION - "
                        + "ROLLBACK SHOULD HAPPEN"
        );
    }
}