package com.example.springdemo.credit_customer_entity_manager.rest;

import com.example.springdemo.credit_customer_entity_manager.dto.CreateCustomerRequestDTO;
import com.example.springdemo.credit_customer_entity_manager.dto.CreateVehicleRequestDTO;
import com.example.springdemo.credit_customer_entity_manager.dto.CustomerResponseDTO;
import com.example.springdemo.credit_customer_entity_manager.dto.VehicleResponseDTO;
import com.example.springdemo.credit_customer_entity_manager.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VehicleRestController {
    private final VehicleService vehicleService;

    @GetMapping("/vehicle/{vehicleId}")
    public VehicleResponseDTO getCustomer(@PathVariable Long vehicleId){
        return vehicleService.findById(vehicleId);
    }

    @PostMapping("/vehicle")
    public VehicleResponseDTO addCustomer(@Valid @RequestBody CreateVehicleRequestDTO request){
        return vehicleService.createVehicle(request);
    }
}
