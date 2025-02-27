package org.example.shopdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.dao.BooksDao;
import org.example.shopdemo.dto.BooksDto;
import org.example.shopdemo.entity.Books;
import org.example.shopdemo.exception.EntityNotFoundException;

import java.util.Collections;
import java.util.Optional;

@Slf4j
public class BookPageService {
    private final static BookPageService INSTANCE = new BookPageService();
    private final BooksDao booksDao = BooksDao.getInstance();

    public BooksDto openBookPage(Long bookId){
        Optional<Books> book = booksDao.findById(bookId);
        if (book.isEmpty()){
            log.debug("Book with id {} wasn't found in bd", bookId);
            throw new EntityNotFoundException("I haven't found such book in bd");
        }
        return book.map(books -> BooksDto
                .builder()
                .booksId(book.get().getBookId())
                .author(book.get().getAuthor())
                .title(book.get().getTitle())
                .description(book.get().getDescription())
                .price(book.get().getPrice())
                .currencyId(book.get().getCurrency().getId())
                .remains(book.get().getRemains())
                .publisherId(book.get().getPublisher().getPublisherId())
                .build()
        ).orElse(null);
    }

    public static BookPageService getInstance(){
        return INSTANCE;
    }
    private BookPageService() {
    }
}
