package org.example.shopdemo.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Data
@Builder
public class ConsumerDto {
    Long consumerId;
    String consumerName;
    String surname;
    String email;
    String password;
    String telephone;
    String role;
}
