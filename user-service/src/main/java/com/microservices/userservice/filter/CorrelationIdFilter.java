package com.microservices.userservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CorrelationIdFilter
        extends OncePerRequestFilter {

    private static final String CORRELATION_ID =
            "X-Correlation-ID";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String correlationId =
                request.getHeader(CORRELATION_ID);

        if (correlationId == null ||
                correlationId.isBlank()) {

            correlationId = "MISSING";
        }

        System.out.println(
                "========== CORRELATION FILTER RUNNING =========="
        );

        System.out.println(
                "[" + correlationId + "] User Service -> "
                        + request.getMethod()
                        + " "
                        + request.getRequestURI()
        );

        response.setHeader(
                CORRELATION_ID,
                correlationId
        );

        filterChain.doFilter(
                request,
                response
        );
    }
}