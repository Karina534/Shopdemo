package org.example.shopdemo.service;

import org.example.shopdemo.dao.BooksDao;
import org.example.shopdemo.dto.BooksDto;
import org.example.shopdemo.entity.Books;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BooksService {
    private final static BooksService INSTANCE = new BooksService();
    private final BooksDao booksDao = BooksDao.getInstance();

    public List<BooksDto> findAll(){
        return booksDao.findAll().stream().map(book -> BooksDto
                .builder()
                .booksId(book.getBookId())
                .author(book.getAuthor())
                .title(book.getTitle())
                .description(book.getDescription())
                .price(book.getPrice())
                .currencyId(book.getCurrency().getId())
                .remains(book.getRemains())
                .publisherId(book.getPublisher().getPublisherId())
                .build()
        ).collect(Collectors.toList());
    }

    public List<BooksDto> filterByRemains(List<BooksDto> list){
        return list.stream().filter(booksDto -> booksDto.getRemains() > 0).collect(Collectors.toList());
    }

//    public Optional<BooksDto> findBookById(Long id){
//        Optional<Books> book = booksDao.findById(id);
//        return book.map(books -> BooksDto.builder().booksId(books.getBookId()).author(books.getAuthor())
//                .title(books.getTitle()).description(books.getDescription()).price(books.getPrice())
//                .currencyId(books.getCurrency().getId()).remains(books.getRemains())
//                .publisherId(books.getPublisher().getPublisherId()).build());
//    }

    public void decreaseRemains(Books book){
        if (book.getRemains() > 0) {
            book.setRemains(book.getRemains() - 1);
            booksDao.update(book);
        } else {
            throw new RuntimeException("Unfortunately remains of this book = 0");
        }
    }

    public static BooksService getInstance(){
        return INSTANCE;
    }
    private BooksService() {
    }
}
