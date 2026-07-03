package com.example.springdemo.credit_customer_entity_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardGroupResponse {

    private String label;

    private Long applicationCount;

    private BigDecimal totalLoanAmount;

}
