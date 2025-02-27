package org.example.shopdemo.dao;

import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.entity.*;
import org.example.shopdemo.exception.DaoException;
import org.example.shopdemo.utils.BDConnectionService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class BasketBooksDao implements Dao<Long, BasketBooks>{
    private final static BasketBooksDao INSTANCE = new BasketBooksDao();

    private final static String SAVE_SQL = """
        INSERT INTO basketbooks (basket_id, book_id, quantity)
        VALUES (?, ?, ?)
        """;

    private final static String DELETE_SQL = """
            DELETE from basketbooks
            where basketbooks_id = ?
            """;

    private final static String UPDATE_SQL = """
            UPDATE basketbooks
            set basket_id = ?,
                book_id = ?,
                quantity = ?
            where basketbooks_id = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT bb.basketbooks_id, bb.basket_id, bb.book_id, bb.quantity, c.consumer_name, c.surname, c.email,
             c.password, c.telephone, c.role,
                b.consumer_id, b2.author, b2.title, b2.description, b2.price, b2.currency_id, b2.remains, b2.publisher_id,
                p.publisher_name
            from basketbooks bb
            join public.baskets b on b.basket_id = bb.basket_id
            join public.books b2 on bb.book_id = b2.book_id
            join public.consumers c on c.consumer_id = b.consumer_id
            join public.publishers p on p.publisher_id = b2.publisher_id
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT bb.basketbooks_id, bb.basket_id, bb.book_id, bb.quantity, c.consumer_name, c.surname, c.email,
             c.password, c.telephone, c.role,
                b.consumer_id, b2.author, b2.title, b2.description, b2.price, b2.currency_id, b2.remains, b2.publisher_id,
                p.publisher_name
            from basketbooks bb
            join public.baskets b on b.basket_id = bb.basket_id
            join public.books b2 on bb.book_id = b2.book_id
            join public.consumers c on c.consumer_id = b.consumer_id
            join public.publishers p on p.publisher_id = b2.publisher_id
            where basketbooks_id = ?
            """;

    private static final String FIND_BY_BOOK_ID_SQL = """
            SELECT bb.basketbooks_id, bb.basket_id, bb.book_id, bb.quantity, c.consumer_name, c.surname, c.email,
             c.password, c.telephone, c.role,
                b.consumer_id, b2.author, b2.title, b2.description, b2.price, b2.currency_id, b2.remains, b2.publisher_id,
                p.publisher_name
            from basketbooks bb
            join public.baskets b on b.basket_id = bb.basket_id
            join public.books b2 on bb.book_id = b2.book_id
            join public.consumers c on c.consumer_id = b.consumer_id
            join public.publishers p on p.publisher_id = b2.publisher_id
            where b2.book_id = ?
            """;
    private static final String FIND_BY_BASKET_ID_SQL = """
            SELECT bb.basketbooks_id, bb.basket_id, bb.book_id, bb.quantity, c.consumer_name, c.surname, c.email,
             c.password, c.telephone, c.role,
                b.consumer_id, b2.author, b2.title, b2.description, b2.price, b2.currency_id, b2.remains, b2.publisher_id,
                p.publisher_name
            from basketbooks bb
            join public.baskets b on b.basket_id = bb.basket_id
            join public.books b2 on bb.book_id = b2.book_id
            join public.consumers c on c.consumer_id = b.consumer_id
            join public.publishers p on p.publisher_id = b2.publisher_id
            where b.basket_id = ?
            """;
    private static final String FIND_BY_BASKET_AND_BOOK_ID_SQL = """
            SELECT bb.basketbooks_id, bb.basket_id, bb.book_id, bb.quantity, c.consumer_name, c.surname, c.email,
             c.password, c.telephone, c.role,
                b.consumer_id, b2.author, b2.title, b2.description, b2.price, b2.currency_id, b2.remains, b2.publisher_id,
                p.publisher_name
            from basketbooks bb
            join public.baskets b on b.basket_id = bb.basket_id
            join public.books b2 on bb.book_id = b2.book_id
            join public.consumers c on c.consumer_id = b.consumer_id
            join public.publishers p on p.publisher_id = b2.publisher_id
            where b.basket_id = ? and b2.book_id = ?
            """;

    private final static String DELETE_BOOKS_BY_BASKET_ID_SQL = """
            DELETE from basketbooks
            where basket_id = ?
            """;

    public BasketBooks save(BasketBooks basketBooks){
        log.info("Saving basketBooks: {}", basketBooks);
        try(var connection = BDConnectionService.connection();
            var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, basketBooks.getBasket().getBasketId());
            statement.setLong(2, basketBooks.getBook().getBookId());
            statement.setInt(3, basketBooks.getQuantity());

            statement.executeUpdate();

            var key = statement.getGeneratedKeys();
            if (key.next()){
                basketBooks.setBasketBooksId(key.getLong("basketbooks_id"));
            }
            log.info("BasketBooks saved with ID: {}", basketBooks.getBasketBooksId());
            return basketBooks;
        } catch (SQLException e) {
            log.error("Failed to save BasketBooks: {}", basketBooks, e);
            throw new DaoException(e);
        }
    }

    public boolean delete(Long basketbooks_id){
        log.info("Deleting BasketBooks with ID: {}", basketbooks_id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(DELETE_SQL)) {

            statement.setLong(1, basketbooks_id);
            boolean result = statement.executeUpdate() > 0;
            log.info("BasketBooks deletion result for ID {}: {}", basketbooks_id, result);
            return result;
        } catch (SQLException e) {
            log.error("Failed to delete BasketBooks with ID: {}", basketbooks_id, e);
            throw new DaoException(e);
        }
    }

    public boolean update(BasketBooks basketBooks){
        log.info("Updating basketBooks: {}", basketBooks);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setLong(1, basketBooks.getBasket().getBasketId());
            statement.setLong(2, basketBooks.getBook().getBookId());
            statement.setInt(3, basketBooks.getQuantity());
            statement.setLong(4, basketBooks.getBasketBooksId());

            boolean result = statement.executeUpdate() > 0;
            log.info("BasketBooks update result for ID {}: {}", basketBooks.getBasketBooksId(), result);
            return result;
        } catch (SQLException e) {
            log.error("Failed to update BasketBooks: {}", basketBooks, e);
            throw new DaoException(e);
        }
    }

    public List<BasketBooks> findAll(){
        log.info("Find all BasketBooks");
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {

            List<BasketBooks> allBasketBooks = new ArrayList<>();
            var res = statement.executeQuery();
            while (res.next()){
                allBasketBooks.add(
                        makeBasketBooks(res)
                );
            }
            log.info("Found {} BasketBooks", allBasketBooks.size());
            return allBasketBooks;
        } catch (SQLException e) {
            log.error("Failed to found all BasketBooks", e);
            throw new DaoException(e);
        }
    }

    private BasketBooks makeBasketBooks(ResultSet res) throws SQLException {
        Consumers consumer = new Consumers(res.getLong("consumer_id"),
                res.getString("consumer_name"),
                res.getString("surname"),
                res.getString("email"),
                res.getString("password"),
                res.getString("telephone"),
                res.getString("role"));

        Baskets basket = new Baskets(res.getLong("basket_id"),
                consumer);

        Currencies currency = Currencies.fromId(res.getInt("currency_id"));

        Publishers publisher = new Publishers(res.getLong("publisher_id"),
                res.getString("publisher_name"));

        Books book = new Books(res.getLong("book_id"),
                res.getString("author"),
                res.getString("title"),
                res.getString("description"),
                res.getBigDecimal("price"),
                currency,
                res.getInt("remains"),
                publisher);

        return new BasketBooks(res.getLong("basketbooks_id"),
                basket,
                book,
                res.getInt("quantity"));
    }

    public Optional<BasketBooks> findById(Long basketbooks_id){
        log.info("Finding BasketBooks by ID: {}", basketbooks_id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setLong(1, basketbooks_id);
            BasketBooks basketBooks = null;
            var res = statement.executeQuery();
            if (res.next()) basketBooks = makeBasketBooks(res);

            log.info("BasketBooks found by ID {}: {}", basketbooks_id, basketBooks != null);
            return Optional.ofNullable(basketBooks);
        } catch (SQLException e) {
            log.error("Failed to find BasketBooks by ID: {}", basketbooks_id, e);
            throw new DaoException(e);
        }
    }

    public Optional<BasketBooks> findByBookId(Long id){
        log.info("Finding BasketBooks by Book ID: {}", id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_BY_BOOK_ID_SQL)) {

            statement.setLong(1, id);
            BasketBooks basketBooks = null;
            var res = statement.executeQuery();
            if (res.next()) basketBooks = makeBasketBooks(res);

            log.info("BasketBooks found by Book ID {}: {}", id, basketBooks != null);
            return Optional.ofNullable(basketBooks);
        } catch (SQLException e) {
            log.error("Failed to find BasketBooks by Book ID: {}", id, e);
            throw new DaoException(e);
        }
    }

    public List<BasketBooks> findByBasketId(Long id){
        log.info("Find BasketBooks by Basket ID: {}", id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_BY_BASKET_ID_SQL)) {

            statement.setLong(1, id);
            List<BasketBooks> allBasketBooks = new ArrayList<>();
            var res = statement.executeQuery();
            while (res.next()){
                allBasketBooks.add(
                        makeBasketBooks(res)
                );
            }
            log.info("Found {} BasketBooks by basket id: {}", allBasketBooks.size(), id);
            return allBasketBooks;
        } catch (SQLException e) {
            log.error("Failed to found BasketBooks by basket id: {}", id, e);
            throw new DaoException(e);
        }
    }

    public Optional<BasketBooks> findByBasketAndBookId(Long basketId, Long bookId){
        log.info("Find BasketBooks by BasketID: {} and Book ID: {}", basketId, bookId);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_BY_BASKET_AND_BOOK_ID_SQL)) {

            statement.setLong(1, basketId);
            statement.setLong(2, bookId);
            BasketBooks basketBook = null;
            var res = statement.executeQuery();
            if (res.next()) basketBook = makeBasketBooks(res);

            log.info("Found BasketBooks by basket and book id. BasketBook isn't nul: {}", basketBook != null);
            return Optional.ofNullable(basketBook);
        } catch (SQLException e) {
            log.error("Failed to found BasketBooks by basket id: {}, and book id: {}", basketId, bookId, e);
            throw new DaoException(e);
        }
    }

    public boolean deleteAllBooksByBasketId(Long basket_id){
        log.info("Deleting all books where basket ID: {}", basket_id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(DELETE_BOOKS_BY_BASKET_ID_SQL)) {

            statement.setLong(1, basket_id);
            boolean result = statement.executeUpdate() > 0;
            log.info("All Books deletion result for basket ID {}: {}", basket_id, result);
            return result;
        } catch (SQLException e) {
            log.error("Failed to delete all Books for basket ID: {}", basket_id, e);
            throw new DaoException(e);
        }
    }

    public static BasketBooksDao getInstance(){
        return INSTANCE;
    }
    private BasketBooksDao() {
    }
}
