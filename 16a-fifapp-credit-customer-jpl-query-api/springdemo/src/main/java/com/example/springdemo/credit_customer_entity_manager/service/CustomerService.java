package com.example.springdemo.credit_customer_entity_manager.service;

import com.example.springdemo.credit_customer_entity_manager.entity.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<Customer> findAll();
    Customer findById(Long id);
    List<Customer> findByFullNameAfter(String fullName);
    List<Customer> findByFullNameBefore(String fullName);
    Customer save(Customer customer);
    void deleteById(Long id);
    Customer findByEmailUsingJpql(String email);
}
