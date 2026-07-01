package com.example.springdemo.customer;

import com.example.springdemo.customer.dto.CreateCustomerRequestDTO;
import com.example.springdemo.customer.dto.CustomerResponseDTO;
import com.example.springdemo.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> create(@Valid @RequestBody CreateCustomerRequestDTO request) {
        CustomerResponseDTO data = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Customer created successfully", data));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CustomerResponseDTO>>> customerAll(){
        List<CustomerResponseDTO> data = customerService.getListCustomer();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Fetch All customer successfully", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> getCustomerById(
            @PathVariable Long id
    ){
        CustomerResponseDTO data = customerService.getCustomerById(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Fetch customer successfully", data));
    }
}
