package com.example.springdemo.credit_customer_entity_manager.service;

import com.example.springdemo.credit_customer_entity_manager.dto.CreateCustomerRequestDTO;
import com.example.springdemo.credit_customer_entity_manager.dto.CreateVehicleRequestDTO;
import com.example.springdemo.credit_customer_entity_manager.dto.VehicleResponseDTO;

public interface VehicleService {
    VehicleResponseDTO findById(Long vehicleId);
    VehicleResponseDTO createVehicle(CreateVehicleRequestDTO requestDTO);
}
