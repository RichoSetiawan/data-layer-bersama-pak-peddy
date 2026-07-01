package com.example.springdemo.credit_customer_entity_manager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCustomerRequestDTO {
    @NotBlank(message = "full_name is required")
    @JsonProperty("full_name")
    private String fullName;
    @NotBlank(message = "nik is required")
    @Email(message = "format harus email")
    @JsonProperty("identity_number")
    private String email;
    @NotBlank(message = "phone_number is required")
    @JsonProperty("phone_number")
    private String phoneNumber;
}