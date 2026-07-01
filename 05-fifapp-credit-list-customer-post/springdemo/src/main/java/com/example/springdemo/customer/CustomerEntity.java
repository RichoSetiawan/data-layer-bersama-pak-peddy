package com.example.springdemo.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity {
    private Long id;
    private String fullName;
    private String identityNumber;
    private String phoneNumber;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
