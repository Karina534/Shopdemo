package org.example.shopdemo.dto;

import lombok.Builder;
import lombok.Data;
import org.example.shopdemo.entity.Statuses;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class OrdersDto {
    private Long orderId;
    private BigDecimal totalPrice;
    private LocalDate creationDate;
    private LocalDate endDate;
    private Integer statusId;
    private Long consumerId;
    private String address;

    public Statuses getStatus() {
        return Statuses.fromId(this.statusId);
    }
}
