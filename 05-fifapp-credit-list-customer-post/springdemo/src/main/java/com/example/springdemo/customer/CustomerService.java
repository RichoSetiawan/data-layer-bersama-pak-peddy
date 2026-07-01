package com.example.springdemo.customer;

import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CustomerService {

    private final Map<Long, CustomerEntity> customerMap = new ConcurrentHashMap<>();

    public CustomerService() {
        customerMap.put(1L, CustomerEntity.builder().id(1L).fullName("Budi Santoso").identityNumber("3171").phoneNumber("081").build());
        customerMap.put(2L, CustomerEntity.builder().id(2L).fullName("Andy").identityNumber("3172").phoneNumber("082").build());
        customerMap.put(3L, CustomerEntity.builder().id(3L).fullName("Richo").identityNumber("3173").phoneNumber("083").build());
        customerMap.put(4L, CustomerEntity.builder().id(4L).fullName("Steven").identityNumber("3174").phoneNumber("084").build());
    }
    private final AtomicLong idGenerator = new AtomicLong(5);

    public CustomerResponseDTO createCustomer(CreateCustomerRequestDTO customerNew){
        Long newId = idGenerator.getAndIncrement();

        CustomerEntity customer = CustomerEntity.builder()
                .id(newId)
                .fullName(customerNew.getFullName())
                .identityNumber(customerNew.getIdentityNumber())
                .phoneNumber(customerNew.getPhoneNumber())
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        customerMap.put(newId, customer);
        return toResponse(customer);
    }

    public List<CustomerResponseDTO> getListCustomer(){
        return customerMap.values().stream()
                .map(this::toResponse)
                .toList();
    }

    public CustomerResponseDTO getCustomerById(Long id){
        return toResponse(customerMap.get(id));
    }

    private CustomerResponseDTO toResponse(CustomerEntity entity) {
        return CustomerResponseDTO.builder()
                .fullName(entity.getFullName())
                .identityNumber(entity.getIdentityNumber())
                .phoneNumber(entity.getPhoneNumber())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
