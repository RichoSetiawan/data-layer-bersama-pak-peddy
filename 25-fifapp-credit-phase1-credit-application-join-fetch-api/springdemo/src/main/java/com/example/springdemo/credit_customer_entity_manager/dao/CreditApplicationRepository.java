package com.example.springdemo.credit_customer_entity_manager.dao;

import com.example.springdemo.credit_customer_entity_manager.entity.CreditApplication;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditApplicationRepository extends JpaRepository<CreditApplication, Long> {
    List<CreditApplication> findByStatus(String status);

//    @Override
//    @EntityGraph(attributePaths = {"customer", "vehicle"})
//    List<CreditApplication> findAll();

    @Query("""
        select creditApplication
        from CreditApplication creditApplication
        join fetch creditApplication.customer
        join fetch creditApplication.vehicle
        """)
    List<CreditApplication> findAllWithCustomerAndVehicle();

    @Query("""
        select creditApplication
        from CreditApplication creditApplication
        join fetch creditApplication.customer
        join fetch creditApplication.vehicle
        where creditApplication.status = :status
        """)
    List<CreditApplication> findByStatusWithCustomerAndVehicle(String status);
}
