package com.example.springdemo.credit_customer_entity_manager.service;

import com.example.springdemo.credit_customer_entity_manager.dto.CreateCustomerRequestDTO;
import com.example.springdemo.credit_customer_entity_manager.dto.CustomerResponseDTO;
import com.example.springdemo.credit_customer_entity_manager.entity.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<Customer> findAll();
    CustomerResponseDTO findById(Long id);
    Customer save(Customer customer);
    void deleteById(Long id);
    Customer findByEmailUsingJpql(String email);
    CustomerResponseDTO createCustomer(CreateCustomerRequestDTO requestDTO);
}
