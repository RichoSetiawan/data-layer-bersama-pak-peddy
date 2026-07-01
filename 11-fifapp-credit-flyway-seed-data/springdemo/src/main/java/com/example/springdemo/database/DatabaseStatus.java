package com.example.springdemo.database;

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
