package com.example.springdemo.credit_customer_entity_manager.service;

import com.example.springdemo.credit_customer_entity_manager.common.CustomerNotFoundException;
import com.example.springdemo.credit_customer_entity_manager.dao.VehicleRepository;
import com.example.springdemo.credit_customer_entity_manager.dto.CreateVehicleRequestDTO;
import com.example.springdemo.credit_customer_entity_manager.dto.VehicleResponseDTO;
import com.example.springdemo.credit_customer_entity_manager.entity.Vehicle;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;

    @Override
    public VehicleResponseDTO findById(Long id){
        Vehicle result = vehicleRepository.findById(id).orElseThrow(()-> new CustomerNotFoundException(id));
        return toResponse(result);
    }

    @Transactional
    public VehicleResponseDTO createVehicle(CreateVehicleRequestDTO requestDTO){
        Vehicle vehicle = Vehicle.builder()
                .plateNumber(requestDTO.getPlateNumber())
                .brand(requestDTO.getBrand())
                .model(requestDTO.getModel())
                .manufacturingYear(requestDTO.getManufacturingYear())
                .createdAt(ZonedDateTime.now())
                .build();
        vehicleRepository.save(vehicle);
        return toResponse(vehicle);
    }

    private VehicleResponseDTO toResponse(Vehicle entity) {
        return VehicleResponseDTO.builder()
                .plateNumber(entity.getPlateNumber())
                .brand(entity.getBrand())
                .model(entity.getModel())
                .manufacturingYear(entity.getManufacturingYear())
                .created_at(entity.getCreatedAt())
                .build();
    }
}
