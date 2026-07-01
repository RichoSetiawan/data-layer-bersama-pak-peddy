package com.example.springdemo.credit_customer_entity_manager.service;

import com.example.springdemo.credit_customer_entity_manager.common.CustomerNotFoundException;
import com.example.springdemo.credit_customer_entity_manager.dao.CustomerRepository;
import com.example.springdemo.credit_customer_entity_manager.entity.Customer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService{
    private final CustomerRepository customerRepository;

    @Override
    public List<Customer> findAll(){
        return customerRepository.findAll();
    }

    @Override
    public Customer findById(Long id){
        Optional<Customer> result = customerRepository.findById(id);
        return result.orElse(null);
    }

    /**
     * Intentionally unsafe: used only to demonstrate SQL injection in a local lab.
     * Never construct SQL by concatenating data received from a request.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Customer> findByFullNameBefore(String fullName) {
        return customerRepository.findByFullNameBefore(fullName);
    }

    /**
     * Safe version: the value is bound as data, so it cannot alter the SQL syntax.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Customer> findByFullNameAfter(String fullName) {
        return customerRepository.findByFullNameAfter(fullName);
    }

    @Override
    public Customer save(Customer customer){
        return customerRepository.save(customer);
    }

    @Override
    public void deleteById(Long id){
        customerRepository.deleteById(id);
    }

    @Override
    public Customer findByEmailUsingJpql(String email) {
        return customerRepository.findByEmailUsingJpql(email)
                .orElseThrow(() -> new CustomerNotFoundException(email));
    }
}
