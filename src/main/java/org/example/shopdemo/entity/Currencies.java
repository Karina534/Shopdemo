package org.example.shopdemo.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public enum Currencies {
    RUBEL(1),
    DOLLAR(2),
    EURO(3);

    private final int id;

    Currencies(int id){
        this.id = id;
    }

    public static Currencies fromId(int id){
        for (Currencies currency: values()){
            if (currency.getId() == id){
                return currency;
            }
        }

        throw new IllegalArgumentException("No currency with id" + id);
    }

    public String getDescription() {
        return switch (this) {
            case RUBEL -> "Р";
            case DOLLAR -> "$";
            case EURO -> "€";
            default -> "Неизвестная валюта";
        };
    }
}
