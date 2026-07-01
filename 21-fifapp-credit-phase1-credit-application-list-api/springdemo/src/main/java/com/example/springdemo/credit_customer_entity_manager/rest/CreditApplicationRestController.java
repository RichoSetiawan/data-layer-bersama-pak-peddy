package com.example.springdemo.credit_customer_entity_manager.rest;

import com.example.springdemo.credit_customer_entity_manager.common.CustomerNotFoundException;
import com.example.springdemo.credit_customer_entity_manager.dto.CreateCreditApplicationRequest;
import com.example.springdemo.credit_customer_entity_manager.dto.CreditApplicationResponse;
import com.example.springdemo.credit_customer_entity_manager.service.CreditApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CreditApplicationRestController {

    private final CreditApplicationService creditApplicationService;

    public CreditApplicationRestController(CreditApplicationService creditApplicationService) {
        this.creditApplicationService = creditApplicationService;
    }

    @GetMapping("/credit-applications/{creditApplicationId}")
    public CreditApplicationResponse getCreditApplication(@PathVariable Long creditApplicationId) {
        CreditApplicationResponse creditApplication = creditApplicationService.getCreditApplicationById(
                creditApplicationId
        );

        if (creditApplication == null) {
            throw new CustomerNotFoundException(creditApplicationId);
        }

        return creditApplication;
    }

    @PostMapping("/credit-applications")
    public CreditApplicationResponse addCreditApplication(@RequestBody CreateCreditApplicationRequest request) {
        return creditApplicationService.createCreditApplication(request);
    }

    @GetMapping("/credit-applications")
    public List<CreditApplicationResponse> getAllCredit(){
        return creditApplicationService.getAllCreditApplications();
    }
}
