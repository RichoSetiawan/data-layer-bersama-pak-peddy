package com.example.springdemo.database;

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
