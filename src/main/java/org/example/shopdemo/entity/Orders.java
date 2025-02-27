package org.example.shopdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orders {
    private Long orderId;
    private BigDecimal totalPrice;
    private LocalDate creationDate;
    private LocalDate endDate;
    private Statuses status;
    private Consumers consumer;
    private String address;
}
