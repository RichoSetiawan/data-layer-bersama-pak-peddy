package com.example.springdemo.credit_customer_entity_manager.database;

public record DatabaseConnectionHoldResponse(
        String message,
        long durationMs,
        String connectionDescription,
        DatabasePoolStatus poolStatusWhileHeld
) {
}
