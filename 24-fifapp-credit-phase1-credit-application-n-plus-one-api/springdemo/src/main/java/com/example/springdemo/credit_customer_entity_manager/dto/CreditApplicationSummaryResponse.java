package com.example.springdemo.credit_customer_entity_manager.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditApplicationSummaryResponse {

    private Long id;

    private String status;

    private String customerName;

    private String customerPhoneNumber;

    private String vehicleName;

    private String plateNumber;

    private BigDecimal loanAmount;

    private Integer tenorMonth;
}
