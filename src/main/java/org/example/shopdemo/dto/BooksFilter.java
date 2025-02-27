package org.example.shopdemo.dto;

import java.math.BigDecimal;

public record BooksFilter (String author, String title, BigDecimal price, Long currencyId,Integer remains,
                           Long publisherId, int limit, int offset){
}
