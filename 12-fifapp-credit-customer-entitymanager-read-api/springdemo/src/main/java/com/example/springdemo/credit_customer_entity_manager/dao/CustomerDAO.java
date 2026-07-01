package com.example.springdemo.credit_customer_entity_manager.dao;

import com.example.springdemo.credit_customer_entity_manager.entity.Customer;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface CustomerDAO {
    List<Customer> findAll();

    Customer findById(Long id);
}
