package com.example.springdemo.credit_customer_entity_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditApplicationResponse {

    private Long id;

    private Long customerId;

    private String customerName;

    private Long vehicleId;

    private String plateNumber;

    private BigDecimal loanAmount;

    private Integer tenorMonth;

    private String status;

//    public CreditApplicationResponse(Long id, Long customerId, String customerName, Long vehicleId, String plateNumber,
//                                     BigDecimal loanAmount, Integer tenorMonth, String status) {
//        this.id = id;
//        this.customerId = customerId;
//        this.customerName = customerName;
//        this.vehicleId = vehicleId;
//        this.plateNumber = plateNumber;
//        this.loanAmount = loanAmount;
//        this.tenorMonth = tenorMonth;
//        this.status = status;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public Long getCustomerId() {
//        return customerId;
//    }
//
//    public String getCustomerName() {
//        return customerName;
//    }
//
//    public Long getVehicleId() {
//        return vehicleId;
//    }
//
//    public String getPlateNumber() {
//        return plateNumber;
//    }
//
//    public BigDecimal getLoanAmount() {
//        return loanAmount;
//    }
//
//    public Integer getTenorMonth() {
//        return tenorMonth;
//    }
//
//    public String getStatus() {
//        return status;
//    }
}
