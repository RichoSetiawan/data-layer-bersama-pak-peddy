package com.example.springdemo.credit_customer_entity_manager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Table(name = "credit_applications", schema = "flyway_training")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

//    @ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(name = "loan_amount")
    private BigDecimal loanAmount;

    @Column(name = "tenor_month")
    private Integer tenorMonth;

    @Column(name = "status")
    private String status;

    @Column(name = "branch_id")
    private Long branchId;
}
