package org.example.shopdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BooksImagesDto {
    private Long booksImagesId;
    private Long bookId;
    private String imageUrl;
}
