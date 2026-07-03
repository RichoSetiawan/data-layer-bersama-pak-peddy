package com.example.springdemo.credit_customer_entity_manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper(); // Ini akan mendaftarkan bean ObjectMapper ke Spring
    }
}
