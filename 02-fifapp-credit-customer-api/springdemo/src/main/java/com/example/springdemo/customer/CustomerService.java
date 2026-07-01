package com.example.springdemo.customer;

import org.springframework.stereotype.Service;

@Service
public class CustomerService {


    public CustomerEntity createCustomer(){
    return CustomerEntity.builder()
            .id(1L)
            .fullName("Budi Santoso")
            .identityNumber("317101010900001")
            .phoneNumber("ASAL AJA")
            .build();
    }
}
