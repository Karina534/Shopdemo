package org.example.shopdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admins {
    private Long adminId;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String telephone;
    private String role;
    private Long individualNumId;
}
