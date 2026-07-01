package com.example.springdemo.credit_customer_entity_manager.service;

import com.example.springdemo.credit_customer_entity_manager.common.CustomerNotFoundException;
import com.example.springdemo.credit_customer_entity_manager.dao.CreditApplicationRepository;
import com.example.springdemo.credit_customer_entity_manager.dao.CustomerRepository;
import com.example.springdemo.credit_customer_entity_manager.dao.VehicleRepository;
import com.example.springdemo.credit_customer_entity_manager.dto.CreateCreditApplicationRequest;
import com.example.springdemo.credit_customer_entity_manager.dto.CreditApplicationResponse;
import com.example.springdemo.credit_customer_entity_manager.entity.CreditApplication;
import com.example.springdemo.credit_customer_entity_manager.entity.Customer;
import com.example.springdemo.credit_customer_entity_manager.entity.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreditApplicationServiceImpl implements CreditApplicationService {

    private static final String SUBMITTED_STATUS = "SUBMITTED";

    private final CreditApplicationRepository creditApplicationRepository;

    private final CustomerRepository customerRepository;

    private final VehicleRepository vehicleRepository;

    @Override
    public CreditApplicationResponse createCreditApplication(CreateCreditApplicationRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new CustomerNotFoundException(request.getVehicleId()));

        CreditApplication creditApplication = CreditApplication.builder()
                .customer(customer)
                .vehicle(vehicle)
                .loanAmount(request.getLoanAmount())
                .tenorMonth(request.getTenorMonth())
                .status(SUBMITTED_STATUS)
                .build();

        CreditApplication savedCreditApplication = creditApplicationRepository.save(creditApplication);

        return toResponse(savedCreditApplication);
    }

    @Override
    public CreditApplicationResponse getCreditApplicationById(Long id) {
        return creditApplicationRepository.findById(id)
                .map(this::toResponse)
                .orElse(null);
    }

    private CreditApplicationResponse toResponse(CreditApplication creditApplication) {
        Customer customer = creditApplication.getCustomer();
        Vehicle vehicle = creditApplication.getVehicle();

        return new CreditApplicationResponse(
                creditApplication.getId(),
                customer.getId(),
                customer.getFullName(),
                vehicle.getId(),
                vehicle.getPlateNumber(),
                creditApplication.getLoanAmount(),
                creditApplication.getTenorMonth(),
                creditApplication.getStatus()
        );
    }
}
