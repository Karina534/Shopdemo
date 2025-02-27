package org.example.shopdemo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublishersDto {
    private Long publisherId;
    private String name;
}
