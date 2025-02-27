package service;

import org.example.shopdemo.dao.BasketBooksDao;
import org.example.shopdemo.dao.BasketsDao;
import org.example.shopdemo.dao.BooksDao;
import org.example.shopdemo.entity.BasketBooks;
import org.example.shopdemo.entity.Baskets;
import org.example.shopdemo.entity.Books;
import org.example.shopdemo.service.BasketBooksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BasketBooksServiceTest {
    private BasketBooksService basketBooksService;
    @Mock
    private BasketBooksDao basketBooksDao;

    @Mock
    private BasketsDao basketsDao;

    @Mock
    private BooksDao booksDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        basketBooksService = BasketBooksService.getInstance();
        basketBooksService.setBasketBooksDao(basketBooksDao);
        basketBooksService.setBooksDao(booksDao);
        basketBooksService.setBasketsDao(basketsDao);
    }

    @Test
    void SaveBook_BookAlreadyInBasket_QuantityIncreases(){
        Long consumerId = 1L;
        Long bookId = 10L;

        Baskets basket = new Baskets(1L, null);
        Books book = new Books(bookId, "Test Author", "Test Title", "Test Desc",
                new BigDecimal("100.0"), null, 10, null);
        BasketBooks basketBook = new BasketBooks(1L, basket, book, 2);

        when(basketsDao.findByConsumerId(consumerId)).thenReturn(Optional.of(basket));
        when(booksDao.findById(bookId)).thenReturn(Optional.of(book));
        when(basketBooksDao.findByBookId(bookId)).thenReturn(Optional.of(basketBook));

        basketBooksService.saveBook(consumerId, bookId);

        assertEquals(3, basketBook.getQuantity());
        verify(basketBooksDao).update(basketBook);
    }

    @Test
    void SaveBook_BookNotInBasket_NewBasketBooksCreated(){
        Long consumerId = 2L;
        Long bookId = 20L;

        Baskets basket = new Baskets(2L, null);
        Books book = new Books(bookId, "New Author", "New Title", "New Desc",
                new BigDecimal("100.0"), null, 5, null);

        when(basketsDao.findByConsumerId(consumerId)).thenReturn(Optional.of(basket));
        when(booksDao.findById(bookId)).thenReturn(Optional.of(book));
        when(basketBooksDao.findByBookId(bookId)).thenReturn(Optional.empty());

        basketBooksService.saveBook(consumerId, bookId);

        // Проверяем, что save был вызван один раз с объектом BasketBooks
        ArgumentCaptor<BasketBooks> captor = ArgumentCaptor.forClass(BasketBooks.class);
        verify(basketBooksDao).save(captor.capture());

        BasketBooks savedBasketBooks = captor.getValue();
        assertEquals(book, savedBasketBooks.getBook());
        assertEquals(basket, savedBasketBooks.getBasket());
        assertEquals(1, savedBasketBooks.getQuantity());
    }
}
