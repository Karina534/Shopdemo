package org.example.shopdemo.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Data
@Builder
public class AdminsDto {
    Long adminId;
    String name;
    String surname;
    String email;
    String password;
    String telephone;
    String role;
    Long individualNum;
}
