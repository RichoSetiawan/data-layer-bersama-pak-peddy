package com.example.springdemo.customer;

import com.example.springdemo.customer.dto.CreateCustomerRequestDTO;
import com.example.springdemo.exception.CustomerNotFoundException;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CustomerRepository {
    private final Map<Long, CustomerEntity> customerMap = new ConcurrentHashMap<>();

    public CustomerRepository() {
        customerMap.put(1L, CustomerEntity.builder().id(1L).fullName("Budi Santoso").identityNumber("3171").phoneNumber("081").build());
        customerMap.put(2L, CustomerEntity.builder().id(2L).fullName("Andy").identityNumber("3172").phoneNumber("082").build());
        customerMap.put(3L, CustomerEntity.builder().id(3L).fullName("Richo").identityNumber("3173").phoneNumber("083").build());
        customerMap.put(4L, CustomerEntity.builder().id(4L).fullName("Steven").identityNumber("3174").phoneNumber("084").build());
    }
    private final AtomicLong idGenerator = new AtomicLong(5);

    public CustomerEntity save(CreateCustomerRequestDTO customerNew){
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
        return customer;
    }

    public List<CustomerEntity> findAllCustomer(){
        return customerMap.values().stream()
                .toList();
    }

    public CustomerEntity findById(Long id){
        if(customerMap.get(id) == null){
            throw new CustomerNotFoundException("Customer with ID " + id + " not found");
        }
        return customerMap.get(id);
    }
}
