package com.example.springdemo.credit_customer_entity_manager.common;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldErrorResponse {
    @JsonProperty("field")
    private String field;

    @JsonProperty("message")
    private String message;
}
