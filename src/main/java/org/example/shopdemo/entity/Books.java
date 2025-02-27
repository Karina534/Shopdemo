package org.example.shopdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Books {
    private Long bookId;
    private String author;
    private String title;
    private String description;
    private BigDecimal price;
    private Currencies currency;
    private Integer remains;
    private Publishers publisher;
}
