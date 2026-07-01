package com.example.springdemo.credit_customer_entity_manager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponseDTO {
        @JsonProperty("full_name")
        private String fullName;

//        @JsonProperty("identity_number")
//        private String identityNumber;

        @JsonProperty("email")
        private String email;

        @JsonProperty("phone_number")
        private String phoneNumber;

        @JsonProperty("created_at")
        private ZonedDateTime createdAt;

}
