package com.example.springdemo.credit_customer_entity_manager.rest;

import com.example.springdemo.credit_customer_entity_manager.dto.CreditApplicationResponse;
import com.example.springdemo.credit_customer_entity_manager.service.CreditApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
public class NPlusOneDemoController {
    private final CreditApplicationService creditApplicationService;

    @GetMapping("/n-plus-one/credit-applications")
    public List<CreditApplicationResponse> getCreditApplicationsForNPlusOneDemo(){
        return creditApplicationService.getCreditApplicationsByStatus(null);
    }
}
