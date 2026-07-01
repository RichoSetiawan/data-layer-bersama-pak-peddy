package com.example.springdemo.credit_customer_entity_manager.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseStatus {
    private Boolean connected;
    private String databaseName;
    private String message;
}
