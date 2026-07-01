package com.example.springdemo.credit_customer_entity_manager.rest;

import com.example.springdemo.credit_customer_entity_manager.common.CustomerNotFoundException;
import com.example.springdemo.credit_customer_entity_manager.dao.CustomerRepository;
import com.example.springdemo.credit_customer_entity_manager.dto.CreateCustomerRequestDTO;
import com.example.springdemo.credit_customer_entity_manager.dto.CustomerResponseDTO;
import com.example.springdemo.credit_customer_entity_manager.entity.Customer;
import com.example.springdemo.credit_customer_entity_manager.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CustomerRestController {
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    @GetMapping("/customers")
    public List<CustomerResponseDTO> findAll(){
        return customerService.findAll();
    }

    @GetMapping("/customers/{customerId}")
    public CustomerResponseDTO getCustomer(@PathVariable Long customerId){
        return customerService.findById(customerId);
    }

    @PostMapping("/customers")
    public CustomerResponseDTO addCustomer(@Valid @RequestBody CreateCustomerRequestDTO request){
        return customerService.createCustomer(request);
    }

    @PutMapping("/customers")
    public Customer updateCustomer(Customer customer){
        Optional<Customer> existingCustomer = customerRepository.findById(customer.getId());
        if(existingCustomer.isEmpty()){
            throw new CustomerNotFoundException(String.valueOf(customer.getId()));
        }
        return customerService.save(existingCustomer.orElse(null));
    }

    @DeleteMapping("/customers/{customerId}")
    public String deleteCustomer(@PathVariable Long customerId){
        Optional<Customer> existingCustomer = customerRepository.findById(customerId);
        if(existingCustomer.isEmpty()){
            throw new CustomerNotFoundException(String.valueOf(customerId));
        }
        customerService.deleteById(customerId);
        return "Deleted customer id - " + customerId;
    }

    @GetMapping("/customers/search")
    public Customer findCustomerByEmail(@RequestParam String email){
        Customer customer = customerService.findByEmailUsingJpql(email);
        if(customer == null){
            throw new CustomerNotFoundException(email);
        }
        return customer;
    }
}
