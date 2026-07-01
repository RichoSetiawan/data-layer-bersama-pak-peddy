package com.example.springdemo.credit_customer_entity_manager.rest;

import com.example.springdemo.credit_customer_entity_manager.common.CustomerNotFoundException;
import com.example.springdemo.credit_customer_entity_manager.entity.Customer;
import com.example.springdemo.credit_customer_entity_manager.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CustomerRestController {
    private final CustomerService customerService;

    @GetMapping("/customers")
    public List<Customer> findAll(){
        return customerService.findAll();
    }

    @GetMapping("/customers/{customerId}")
    public Customer getCustomer(@PathVariable Long customerId){
        Customer customer = customerService.findById(customerId);

        if(customer == null){
            throw new CustomerNotFoundException(String.valueOf(customerId));
        }
        return customer;
    }

    /**
     * Lab-only "before" endpoint. It demonstrates why string-concatenated SQL is unsafe.
     */
    @GetMapping("/customers/search/before")
    public List<Customer> searchBefore(@RequestParam String fullName) {
        return customerService.findByFullNameBefore(fullName);
    }

    /**
     * "After" endpoint. It uses a parameterized native query.
     */
    @GetMapping("/customers/search/after")
    public List<Customer> searchAfter(@RequestParam String fullName) {
        return customerService.findByFullNameAfter(fullName);
    }
}
