package com.example.springdemo.customer;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CustomerService {

    private final Map<Long, CustomerEntity> customerMap = new ConcurrentHashMap<>();

    public CustomerService() {
        customerMap.put(1L, CustomerEntity.builder().id(1L).fullName("Budi Santoso").identityNumber("3171").phoneNumber("081").build());
        customerMap.put(2L, CustomerEntity.builder().id(2L).fullName("Andy").identityNumber("3172").phoneNumber("082").build());
        customerMap.put(3L, CustomerEntity.builder().id(3L).fullName("Richo").identityNumber("3173").phoneNumber("083").build());
        customerMap.put(4L, CustomerEntity.builder().id(4L).fullName("Steven").identityNumber("3174").phoneNumber("084").build());
    }

    public CustomerEntity createCustomer(CustomerEntity customerNew){
        if (customerNew.getId() == null) {
            throw new IllegalArgumentException("ID customer tidak boleh kosong");
        }

        CustomerEntity customer = CustomerEntity.builder()
                .id(customerNew.getId())
                .fullName(customerNew.getFullName())
                .identityNumber(customerNew.getIdentityNumber())
                .phoneNumber(customerNew.getPhoneNumber())
                .build();

        customerMap.put(customer.getId(), customer);
        return customer;
    }

    public List<CustomerEntity> createListCustomer(){
        return List.copyOf(customerMap.values());
    }

    public Optional<CustomerEntity> getCustomerById(Long id){
        return Optional.ofNullable(customerMap.get(id));
    }


    //    List<CustomerEntity> customerList = List.of(
//            CustomerEntity.builder()
//            .id(1L)
//                       .fullName("Budi Santoso")
//                       .identityNumber("317101010900001")
//                       .phoneNumber("ASAL AJA")
//                       .build(),
//                CustomerEntity.builder()
//                        .id(2L)
//                        .fullName("Andy")
//                        .identityNumber("317101010900001")
//                        .phoneNumber("ASAL AJA")
//                        .build(),
//                CustomerEntity.builder()
//                        .id(2L)
//                        .fullName("Richo")
//                        .identityNumber("317101010900001")
//                        .phoneNumber("ASAL AJA")
//                        .build(),
//                CustomerEntity.builder()
//                        .id(2L)
//                        .fullName("Steven")
//                        .identityNumber("317101010900001")
//                        .phoneNumber("ASAL AJA")
//                        .build()
//        );
//    public List<CustomerEntity> createListCustomer(){
//        return customerList;
//    }
//
//    public Optional<CustomerEntity> getCustomerById(Long id){
//        Optional<CustomerEntity> match = customerList.stream()
//                .filter(customer -> id.equals(customer.getId()))
//                .findFirst();
//        return match;
//    }
}
