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

    /**
     * Intentionally unsafe: used only to demonstrate SQL injection in a local lab.
     * Never construct SQL by concatenating data received from a request.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Customer> findByFullNameBefore(String fullName) {
        return customerDAO.findByFullNameBefore(fullName);
    }

    /**
     * Safe version: the value is bound as data, so it cannot alter the SQL syntax.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Customer> findByFullNameAfter(String fullName) {
        return customerDAO.findByFullNameAfter(fullName);
    }
}
