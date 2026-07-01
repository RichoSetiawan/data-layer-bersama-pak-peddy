package com.example.springdemo.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    @PostMapping("/create")
    public ResponseEntity<CustomerEntity> createCustomer(@RequestBody CustomerEntity customerNew){
        CustomerEntity customer = customerService.createCustomer(customerNew);
        return new ResponseEntity<>(customer, HttpStatus.CREATED);
    }
    @GetMapping("/all")
    public List<CustomerEntity> customerAll(){
        return customerService.createListCustomer();
    }
    @GetMapping("/{id}")
    public Optional<CustomerEntity> getCustomerById(
            @PathVariable Long id
    ){
        return customerService.getCustomerById(id);
    }
}
