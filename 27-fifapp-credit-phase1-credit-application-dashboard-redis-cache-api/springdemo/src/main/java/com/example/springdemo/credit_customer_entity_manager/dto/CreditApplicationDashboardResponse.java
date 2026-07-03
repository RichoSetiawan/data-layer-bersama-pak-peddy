package com.example.springdemo.credit_customer_entity_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreditApplicationDashboardResponse {

    private Long branchId;

    private Long totalApplications;

    private BigDecimal totalLoanAmount;

    private BigDecimal averageLoanAmount;

    private BigDecimal minimumLoanAmount;

    private BigDecimal maximumLoanAmount;

    private List<DashboardGroupResponse> applicationsByStatus;

    private List<DashboardGroupResponse> applicationsByTenorMonth;

    private List<DashboardGroupResponse> applicationsByVehicleBrand;

    public Long getBranchId() {
        return branchId;
    }

    public Long getTotalApplications() {
        return totalApplications;
    }

    public BigDecimal getTotalLoanAmount() {
        return totalLoanAmount;
    }

    public BigDecimal getAverageLoanAmount() {
        return averageLoanAmount;
    }

    public BigDecimal getMinimumLoanAmount() {
        return minimumLoanAmount;
    }

    public BigDecimal getMaximumLoanAmount() {
        return maximumLoanAmount;
    }

    public List<DashboardGroupResponse> getApplicationsByStatus() {
        return applicationsByStatus;
    }

    public List<DashboardGroupResponse> getApplicationsByTenorMonth() {
        return applicationsByTenorMonth;
    }

    public List<DashboardGroupResponse> getApplicationsByVehicleBrand() {
        return applicationsByVehicleBrand;
    }
}
