package org.example.shopdemo.dto;

import lombok.Builder;
import lombok.Data;

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
