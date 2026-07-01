package com.example.springdemo.credit_customer_entity_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateVehicleRequestDTO {

    private String plateNumber;

    private String brand;

    private String model;

    private Integer manufacturingYear;
}
