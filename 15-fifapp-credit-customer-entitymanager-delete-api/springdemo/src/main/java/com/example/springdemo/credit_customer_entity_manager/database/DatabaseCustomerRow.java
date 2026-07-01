package com.example.springdemo.credit_customer_entity_manager.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseCustomerRow {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String email;
}
