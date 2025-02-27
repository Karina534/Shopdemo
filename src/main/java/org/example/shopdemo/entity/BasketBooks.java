package org.example.shopdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasketBooks {
    private Long basketBooksId;
    private Baskets basket;
    private Books book;
    private Integer quantity;
}
