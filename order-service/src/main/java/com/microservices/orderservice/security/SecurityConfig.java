package com.microservices.orderservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // =========================================================
    // PASSWORD ENCODER
    // =========================================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    // =========================================================
    // USERS
    // =========================================================

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder) {

        UserDetails user =
                User.builder()
                        .username("user")
                        .password(passwordEncoder.encode("user123"))
                        .roles("USER")
                        .build();

        UserDetails manager =
                User.builder()
                        .username("manager")
                        .password(passwordEncoder.encode("manager123"))
                        .roles("MANAGER")
                        .build();

        UserDetails admin =
                User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .roles("ADMIN")
                        .build();

        return new InMemoryUserDetailsManager(
                user,
                manager,
                admin
        );
    }

    // =========================================================
    // SECURITY FILTER CHAIN
    // =========================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                // =====================================================
                // CSRF
                // =====================================================
                // Disable CSRF because this is a REST API.
                .csrf(csrf -> csrf.disable())

                // =====================================================
                // H2 CONSOLE FRAME SUPPORT
                // =====================================================
                // H2 Console runs inside a browser frame.
                .headers(headers ->
                        headers.frameOptions(frameOptions ->
                                frameOptions.sameOrigin()
                        )
                )

                // =====================================================
                // SESSION
                // =====================================================
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // =====================================================
                // AUTHORIZATION
                // =====================================================
                .authorizeHttpRequests(auth -> auth

                        // -------------------------------------------------
                        // H2 CONSOLE
                        // -------------------------------------------------
                        .requestMatchers("/h2-console/**")
                        .permitAll()

                        // -------------------------------------------------
                        // PUBLIC ACTUATOR ENDPOINTS
                        // -------------------------------------------------
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/info"
                        )
                        .permitAll()

                        // -------------------------------------------------
                        // ORDERS
                        // -------------------------------------------------
                        .requestMatchers("/orders/**")
                        .hasAnyRole(
                                "USER",
                                "MANAGER",
                                "ADMIN"
                        )

                        // -------------------------------------------------
                        // EVERYTHING ELSE
                        // -------------------------------------------------
                        .anyRequest()
                        .authenticated()
                )

                // =====================================================
                // HTTP BASIC
                // =====================================================
                .httpBasic(basic -> {});

        return http.build();
    }
}