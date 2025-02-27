package org.example.shopdemo.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.dao.BasketBooksDao;
import org.example.shopdemo.dao.BasketsDao;
import org.example.shopdemo.dao.BooksDao;
import org.example.shopdemo.dto.BooksDto;
import org.example.shopdemo.entity.BasketBooks;
import org.example.shopdemo.entity.Baskets;
import org.example.shopdemo.entity.Books;
import org.example.shopdemo.exception.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BasketBooksService {
    private final static BasketBooksService INSTANCE = new BasketBooksService();

    @Setter // Только для тестов
    private BasketBooksDao basketBooksDao = BasketBooksDao.getInstance();

    @Setter // Только для тестов
    private BasketsDao basketsDao = BasketsDao.getInstance();

    @Setter // Только для тестов
    private BooksDao booksDao = BooksDao.getInstance();

    public void saveBook(Long consumerId, Long bookId){
        LogService.logInfo("Trying to find basket by consumer id, and book by book id.",
                "Consumer id: %s, Book Id: %s", consumerId, bookId);
        Optional<Baskets> basket = findBasketByConsumerId(consumerId);
        Optional<Books> book = findBookByBookId(bookId);
        LogService.logInfo("Find basket by consumer id, and book by book id successfully found.",
                "Consumer id: %s, Book Id: %s", consumerId, bookId);

        LogService.logInfo("Checking is such book already in basket.", "Book id: %s", book.get().getBookId());
        Optional<BasketBooks> isBookInBasketBooks = isBookInBasketBooks(basket.get(), book.get());

        if (isBookInBasketBooks.isPresent()){
            LogService.logInfo("Book already exist in basketBooks. Updating quantity.", "Book id: %s", bookId);
            isBookInBasketBooks.get().setQuantity(isBookInBasketBooks.get().getQuantity() + 1);
            basketBooksDao.update(isBookInBasketBooks.get());
        } else {
            BasketBooks basketBooks = new BasketBooks(null, basket.get(), book.get(), 1);
            BasketBooks basketBooksNew = basketBooksDao.save(basketBooks);
            LogService.logInfo("Book wasn't exist in basketBooks. Create new entity.",
                    "BasketBook id: %s", basketBooksNew.getBasketBooksId());
        }
    }

    private Optional<Baskets> findBasketByConsumerId(Long id){
        Optional<Baskets> basket = basketsDao.findByConsumerId(id);
        if (basket.isEmpty()){
            LogService.logError("Basket wasn't found for consumer.", "Consumer id: %s", id);
            throw new EntityNotFoundException("Basket wasn't found for consumer.");
        }
        return basket;
    }

    private Optional<Books> findBookByBookId(Long id){
        Optional<Books> book = booksDao.findById(id);
        if (book.isEmpty()){
            LogService.logError("Book wasn't found by id for add it in basket.", "Book id: %s", id);
            throw new EntityNotFoundException("Book wasn't found to add it in basket.");
        }
        return book;
    }

    private Optional<BasketBooks> isBookInBasketBooks(Baskets baskets, Books book){
        return basketBooksDao.findByBasketAndBookId(baskets.getBasketId(), book.getBookId());
    }

    public List<BasketBooks> findByBasketId(Long id){
        return basketBooksDao.findByBasketId(id);
    }

    public List<BooksDto> getBooksFromBasketBooks(List<BasketBooks> basketBooks){
        log.info("Start take books from basketBooks. BasketBooks size: {}", basketBooks.size());
        List<BooksDto> books = new ArrayList<>();
        if (!basketBooks.isEmpty()) {
            for (BasketBooks basket : basketBooks) {
                Books books1 = basket.getBook();
                books.add(BooksDto.builder()
                        .booksId(books1.getBookId())
                        .author(books1.getAuthor())
                        .title(books1.getTitle())
                        .description(books1.getDescription())
                        .price(books1.getPrice())
                        .currencyId(books1.getCurrency().getId())
                        .remains(books1.getRemains())
                        .publisherId(books1.getPublisher().getPublisherId())
                        .quantity(basket.getQuantity()).build());
            }
        }

        log.info("List size of found books from basketBooks: {}", books.size());
        return books;
    }

    public boolean deleteBook(Long basketId, Long bookId){
        log.info("Start delete basketBook for basketId: {} and BookId: {}", basketId, bookId);
        Optional<BasketBooks> basketBook = basketBooksDao.findByBasketAndBookId(basketId, bookId);
        if (basketBook.isEmpty()){
            log.error("BasketBook wasn't found by thees basket id: {} and book id: {}",
                    basketId, bookId);
            throw new EntityNotFoundException("BasketBook wasn't found by thees basket id and book id.");
        }

        log.info("BasketBook for deleting was successfully found for thees basketId: {} and BookId: {}", basketId, bookId);
        return basketBooksDao.delete(basketBook.get().getBasketBooksId());
    }

    public boolean decreaseBookQuantity(Long basketId, Long bookId, Integer quantity){
        log.info("Start decrease basketBook quantity for basketId: {} and BookId: {}", basketId, bookId);
        Optional<BasketBooks> basketBook = basketBooksDao.findByBasketAndBookId(basketId, bookId);
        if (basketBook.isEmpty()){
            log.error("BasketBook wasn't found by thees basket id: {} and book id: {}",
                    basketId, bookId);
            throw new EntityNotFoundException("BasketBook wasn't found by thees basket id and book id.");
        }

        log.info("BasketBook for decrease basketBook quantity was found for thees basketId: {} and BookId: {}", basketId, bookId);
        basketBook.get().setQuantity(quantity - 1);
        return basketBooksDao.update(basketBook.get());
    }

    public boolean deleteAllBooksByBasketId(Long basketId){
        return basketBooksDao.deleteAllBooksByBasketId(basketId);
    }


    public static BasketBooksService getInstance(){
        return INSTANCE;
    }

}
