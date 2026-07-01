package com.example.springdemo.credit_customer_entity_manager.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseSchemaStatus {
    private String databaseSchema;
    private List<String> tables;
    private String message;
}
