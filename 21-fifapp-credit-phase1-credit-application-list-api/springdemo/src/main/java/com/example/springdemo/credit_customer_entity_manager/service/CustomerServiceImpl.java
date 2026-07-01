package com.example.springdemo.credit_customer_entity_manager.service;

import com.example.springdemo.credit_customer_entity_manager.common.CustomerNotFoundException;
import com.example.springdemo.credit_customer_entity_manager.dao.CustomerRepository;
import com.example.springdemo.credit_customer_entity_manager.dto.CreateCustomerRequestDTO;
import com.example.springdemo.credit_customer_entity_manager.dto.CustomerResponseDTO;
import com.example.springdemo.credit_customer_entity_manager.entity.Customer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService{
    private final CustomerRepository customerRepository;

    @Override
    public List<Customer> findAll(){
        return customerRepository.findAll();
    }

    @Override
    public CustomerResponseDTO findById(Long id){
        Customer result = customerRepository.findById(id).orElseThrow(()-> new CustomerNotFoundException(id));
        return toResponse(result);
    }

    @Transactional
    public Customer save(Customer customer){
        return customerRepository.save(customer);
    }

    @Transactional
    public void deleteById(Long id){
        customerRepository.deleteById(id);
    }

    @Override
    public Customer findByEmailUsingJpql(String email) {
        return customerRepository.findByEmailUsingJpql(email)
                .orElseThrow(() -> new CustomerNotFoundException(email));
    }

    @Transactional
    public CustomerResponseDTO createCustomer(CreateCustomerRequestDTO requestDTO){
        Customer customer = Customer.builder()
                .fullName(requestDTO.getFullName())
                .email(requestDTO.getEmail())
                .phoneNumber(requestDTO.getPhoneNumber())
                .build();
        customerRepository.save(customer);
        return toResponse(customer);
    }

        private CustomerResponseDTO toResponse(Customer entity) {
        return CustomerResponseDTO.builder()
                .fullName(entity.getFullName())
                .phoneNumber(entity.getPhoneNumber())
                .email(entity.getEmail())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
