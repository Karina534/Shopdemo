package org.example.shopdemo.dto;

import lombok.Builder;
import lombok.Data;
import org.example.shopdemo.dao.PublishersDao;
import org.example.shopdemo.entity.Currencies;
import org.example.shopdemo.entity.Publishers;

import java.math.BigDecimal;
import java.util.Optional;

@Data
@Builder
public class BooksDto{
        private Long booksId;
        private String author;
        private String title;
        private String description;
        private BigDecimal price;
        private Integer currencyId;
        private Integer remains;
        private Long publisherId;
        private Integer quantity;

        public String getCurrencyName(){
                return Currencies.fromId(this.currencyId).getDescription();
        }

        public String getPublisherName(){
                Optional<Publishers> publishers = PublishersDao.getInstance().findById(publisherId);
                if (publishers.isPresent()){
                        return publishers.get().getName();
                }
                return "none";
        }
}
