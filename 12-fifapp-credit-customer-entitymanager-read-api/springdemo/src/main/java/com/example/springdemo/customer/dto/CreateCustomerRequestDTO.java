//package com.example.springdemo.customer.dto;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class CreateCustomerRequestDTO {
//    @NotBlank(message = "full_name is required")
//    @JsonProperty("full_name")
//    private String fullName;
//    @NotBlank(message = "nik is required")
//    @Size(min = 16, max = 16, message = "nik must be 16 characters")
//    @JsonProperty("identity_number")
//    private String identityNumber;
//    @NotBlank(message = "phone_number is required")
//    @JsonProperty("phone_number")
//    private String phoneNumber;
//}
