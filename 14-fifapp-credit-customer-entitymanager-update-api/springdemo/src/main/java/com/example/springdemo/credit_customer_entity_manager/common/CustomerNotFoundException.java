package com.example.springdemo.credit_customer_entity_manager.common;

public class CustomerNotFoundException extends RuntimeException {
        public CustomerNotFoundException(String message) {
            super(message);
        }
    }
