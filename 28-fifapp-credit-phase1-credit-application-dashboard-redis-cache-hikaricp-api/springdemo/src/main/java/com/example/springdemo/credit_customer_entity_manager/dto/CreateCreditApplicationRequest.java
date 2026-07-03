package com.example.springdemo.credit_customer_entity_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCreditApplicationRequest {

    private Long customerId;

    private Long vehicleId;

    private BigDecimal loanAmount;

    private Integer tenorMonth;

    private Long branchId;

//    public Long getCustomerId() {
//        return customerId;
//    }
//
//    public void setCustomerId(Long customerId) {
//        this.customerId = customerId;
//    }
//
//    public Long getVehicleId() {
//        return vehicleId;
//    }
//
//    public void setVehicleId(Long vehicleId) {
//        this.vehicleId = vehicleId;
//    }
//
//    public BigDecimal getLoanAmount() {
//        return loanAmount;
//    }
//
//    public void setLoanAmount(BigDecimal loanAmount) {
//        this.loanAmount = loanAmount;
//    }
//
//    public Integer getTenorMonth() {
//        return tenorMonth;
//    }
//
//    public void setTenorMonth(Integer tenorMonth) {
//        this.tenorMonth = tenorMonth;
//    }
}
