package org.example.shopdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Consumers {
    private Long consumerId;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String telephone;
    private String role;
}
