package com.example.springdemo.credit_customer_entity_manager.dao;

import com.example.springdemo.credit_customer_entity_manager.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(path = "pelanggan")
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
