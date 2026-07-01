package com.example.springdemo.credit_customer_entity_manager.dao;

import com.example.springdemo.credit_customer_entity_manager.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByFullNameBefore(String fullName);
    List<Customer> findByFullNameAfter(String fullName);

    @Query(value = "SELECT * FROM customers WHERE email = :email LIMIT 1", nativeQuery = true)
    Optional<Customer> findByEmailUsingJpql(@Param("email") String email);
}
