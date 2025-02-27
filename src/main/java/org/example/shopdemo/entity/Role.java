package org.example.shopdemo.entity;

import java.util.Arrays;
import java.util.Optional;

public enum Role {
    U,
    A;

    public static Optional<Role> find(String role){
        return Arrays.stream(values()).filter(role1 -> role1.name().equals(role)).findFirst();
    }

    public String getDescription(){
        return switch (this) {
            case U -> "USER";
            case A -> "ADMIN";
            default -> "UNKNOWN";
        };
    }
}
