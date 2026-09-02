package com.microservices.userservice.config;

import com.microservices.userservice.filter.CorrelationIdFilter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<CorrelationIdFilter>
    correlationIdFilterRegistration() {

        FilterRegistrationBean<CorrelationIdFilter> registration =
                new FilterRegistrationBean<>();

        registration.setFilter(
                new CorrelationIdFilter()
        );

        registration.addUrlPatterns("/*");

        registration.setOrder(1);

        return registration;
    }
}