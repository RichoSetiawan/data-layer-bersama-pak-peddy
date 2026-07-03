package com.example.springdemo.credit_customer_entity_manager.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleResponseDTO {

    private String plateNumber;

    private String brand;

    private String model;

    private Integer manufacturingYear;

    private ZonedDateTime created_at;
}
