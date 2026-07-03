package com.example.springdemo.credit_customer_entity_manager.database;

public record DatabasePoolStatus(
        String poolName,
        int totalConnections,
        int activeConnections,
        int idleConnections,
        int threadsAwaitingConnection,
        int maximumPoolSize,
        int minimumIdle,
        long connectionTimeoutMs,
        String status
) {
}
