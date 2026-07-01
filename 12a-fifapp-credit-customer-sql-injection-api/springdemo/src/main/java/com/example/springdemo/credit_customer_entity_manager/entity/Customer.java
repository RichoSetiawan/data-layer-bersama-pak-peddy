package com.example.springdemo.credit_customer_entity_manager.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "full_name")
    private String fullName;
//    @Column(name = "identity_number")
//    private String identityNumber;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "created_at")
    private ZonedDateTime createdAt;
//    private ZonedDateTime updatedAt;
}
