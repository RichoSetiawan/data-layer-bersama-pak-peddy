package com.example.springdemo.credit_customer_entity_manager.service;


import com.example.springdemo.credit_customer_entity_manager.dto.CreateCreditApplicationRequest;
import com.example.springdemo.credit_customer_entity_manager.dto.CreditApplicationResponse;
import com.example.springdemo.credit_customer_entity_manager.dto.CreditApplicationSummaryResponse;

import java.util.List;

public interface CreditApplicationService {

    CreditApplicationResponse createCreditApplication(CreateCreditApplicationRequest request);

    CreditApplicationResponse getCreditApplicationById(Long id);

    List<CreditApplicationResponse> getAllCreditApplications();

    List<CreditApplicationResponse> getCreditApplicationsByStatus(String status);

    CreditApplicationSummaryResponse getSummary(Long id);
}
