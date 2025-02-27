package org.example.shopdemo.dto;

import lombok.Builder;
import lombok.Data;
import org.example.shopdemo.entity.Currencies;

import java.math.BigDecimal;

@Data
@Builder
public class BooksWithImgDto {
    private Long booksId;
    private String author;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer currencyId;
    private Integer remains;
    private Long publisherId;
    private String imgUrl;
    private Integer quantity;

    public String getCurrencyName(){
        return Currencies.fromId(this.currencyId).getDescription();
    }
}
