package org.example.shopdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersBooks {
    private Long orderBookId;
    private Orders order;
    private Books book;
    private Integer quantity;
}
