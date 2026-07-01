package com.example.springdemo.credit_customer_entity_manager.rest;

import com.example.springdemo.credit_customer_entity_manager.common.CustomerNotFoundException;
import com.example.springdemo.credit_customer_entity_manager.dto.CreateCreditApplicationRequest;
import com.example.springdemo.credit_customer_entity_manager.dto.CreditApplicationResponse;
import com.example.springdemo.credit_customer_entity_manager.dto.CreditApplicationSummaryResponse;
import com.example.springdemo.credit_customer_entity_manager.service.CreditApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/credit-applications/{id}/summary")
    public CreditApplicationSummaryResponse getSummary(@PathVariable Long id){
        CreditApplicationSummaryResponse summary = creditApplicationService.getSummary(id);

        if(summary == null){
            throw new CustomerNotFoundException(String.valueOf(id));
        }
        return summary;
    }

    @GetMapping("/credit-applications")
    public List<CreditApplicationResponse> getAllCredit(){
        return creditApplicationService.getAllCreditApplications();
    }

    @GetMapping("/credit-applications/status")
    public List<CreditApplicationResponse> getByStatus(@RequestParam(required = false) String status){
        return creditApplicationService.getCreditApplicationsByStatus(status);
    }
}
