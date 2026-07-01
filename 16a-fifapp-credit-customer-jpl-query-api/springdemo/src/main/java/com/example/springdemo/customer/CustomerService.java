//package com.example.springdemo.customer;
//
//import com.example.springdemo.customer.dto.CreateCustomerRequestDTO;
//import com.example.springdemo.customer.dto.CustomerResponseDTO;
//import com.example.springdemo.credit_customer_entity_manager.common.CustomerNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class CustomerService {
//
//    private final CustomerRepository customerRepository;
//
//    public CustomerResponseDTO createCustomer(CreateCustomerRequestDTO customerNew){
//        customerRepository.save(customerNew);
//        return toResponse(customerRepository.save(customerNew));
//    }
//
//    public List<CustomerResponseDTO> getListCustomer(){
//        List<CustomerEntity> customerMap = customerRepository.findAllCustomer();
//        return customerMap.stream()
//                .map(this::toResponse)
//                .toList();
//    }
//
//    public CustomerResponseDTO getCustomerById(Long id){
//        if(customerRepository.findById(id) == null){
//            throw new CustomerNotFoundException("Customer with ID " + id + " not found");
//        }
//        return toResponse(customerRepository.findById(id));
//    }
//
//    private CustomerResponseDTO toResponse(CustomerEntity entity) {
//        return CustomerResponseDTO.builder()
//                .fullName(entity.getFullName())
//                .identityNumber(entity.getIdentityNumber())
//                .phoneNumber(entity.getPhoneNumber())
//                .createdAt(entity.getCreatedAt())
//                .updatedAt(entity.getUpdatedAt())
//                .build();
//    }
//}
