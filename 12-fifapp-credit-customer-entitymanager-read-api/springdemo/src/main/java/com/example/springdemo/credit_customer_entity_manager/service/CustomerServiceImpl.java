package com.example.springdemo.credit_customer_entity_manager.service;

import com.example.springdemo.credit_customer_entity_manager.dao.CustomerDAO;
import com.example.springdemo.credit_customer_entity_manager.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService{
    private final CustomerDAO customerDAO;

    @Override
    public List<Customer> findAll(){
        return customerDAO.findAll();
    }

    @Override
    public Customer findById(Long id){
        return customerDAO.findById(id);
    }
}
