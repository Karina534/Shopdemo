package org.example.shopdemo.dao;

import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.dto.BooksFilter;
import org.example.shopdemo.entity.Books;
import org.example.shopdemo.entity.Currencies;
import org.example.shopdemo.entity.Publishers;
import org.example.shopdemo.exception.DaoException;
import org.example.shopdemo.utils.BDConnectionService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class BooksDao implements Dao<Long, Books>{
    private final static BooksDao INSTANCE = new BooksDao();

    private static final String SAVE_SQL = """
            INSERT INTO books (author, title, description, price, currency_id, remains, publisher_id)
            values (?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String DELETE_SQL = """
            DELETE FROM books
            WHERE book_id = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT b.book_id, b.author, b.title, b.description, b.price, b.currency_id, b.remains, b.publisher_id,
            currency_name, publisher_name
            FROM books b
            join public.currencies c on c.currency_id = b.currency_id
            join public.publishers p on p.publisher_id = b.publisher_id
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT b.book_id, b.author, b.title, b.description, b.price, b.currency_id, b.remains, b.publisher_id,
            currency_name, publisher_name
            from books b
            join public.currencies c on c.currency_id = b.currency_id
            join public.publishers p on p.publisher_id = b.publisher_id
            where b.book_id = ?
            """;

    private static final String UPDATE_SQL = """
        UPDATE books
        SET author = ?,
            title = ?,
            description = ?,
            price = ?,
            currency_id = ?,
            remains = ?,
            publisher_id = ?
        where book_id = ?
        """;

    public Books save(Books book){
        log.info("Saving book: {}", book);
        try(var connection = BDConnectionService.connection();
        var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, book.getAuthor());
            statement.setString(2, book.getTitle());
            statement.setString(3, book.getDescription());
            statement.setBigDecimal(4, book.getPrice());
            statement.setLong(5, book.getCurrency().getId());
            statement.setInt(6, book.getRemains());
            statement.setLong(7, book.getPublisher().getPublisherId());

            statement.executeUpdate();

            var key = statement.getGeneratedKeys();
            if (key.next()){
                book.setBookId(key.getLong("book_id"));
            }

            log.info("Book saved with ID: {}", book.getBookId());
            return book;
        } catch (SQLException e) {
            log.error("Failed to save book: {}", book, e);
            throw new DaoException(e);
        }
    }

    public boolean delete(Long bookId){
        log.info("Deleting book with ID: {}", bookId);
        try (var connection = BDConnectionService.connection();
        var statement = connection.prepareStatement(DELETE_SQL)) {

            statement.setLong(1, bookId);

            boolean result = statement.executeUpdate() > 0;
            log.info("Book deletion result for ID {}: {}", bookId, result);
            return result;
        } catch (SQLException e) {
            log.error("Failed to delete book with ID: {}", bookId, e);
            throw new DaoException(e);
        }
    }

    public boolean update(Books book){
        log.info("Updating book: {}", book);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setString(1, book.getAuthor());
            statement.setString(2, book.getTitle());
            statement.setString(3, book.getDescription());
            statement.setBigDecimal(4, book.getPrice());
            statement.setLong(5, book.getCurrency().getId());
            statement.setInt(6, book.getRemains());
            statement.setLong(7, book.getPublisher().getPublisherId());
            statement.setLong(8, book.getBookId());

            boolean result = statement.executeUpdate() > 0;
            log.info("Book update result for ID {}: {}", book.getBookId(), result);
            return result;
        } catch (SQLException e) {
            log.error("Failed to update book: {}", book, e);
            throw new DaoException(e);
        }
    }

    public List<Books> findAll(){
        log.info("Find all Books");
        try (var connection = BDConnectionService.connection();
        var statement = connection.prepareStatement(FIND_ALL_SQL)) {

            List<Books> allBooks = new ArrayList<>();
            var res = statement.executeQuery();
            while (res.next()){
                allBooks.add(
                        makeBook(res)
                );
            }

            log.info("Found {} Books", allBooks.size());
            return allBooks;
        } catch (SQLException e) {
            log.error("Failed to found all Books", e);
            throw new DaoException(e);
        }
    }

    public List<Books> findAll(BooksFilter filter){
        log.info("Find all Books with filters: {}", filter);
        List<Object> parameters = new ArrayList<>();
        List<String> whereSql = new ArrayList<>();

        if (filter.author() != null){
            parameters.add(filter.author());
            whereSql.add("author = ?");
        }
        if (filter.title() != null){
            parameters.add(filter.title());
            whereSql.add("title = ?");
        }
        if (filter.price() != null){
            parameters.add(filter.price());
            whereSql.add("price = ?");
        }
        if (filter.currencyId() != null){
            parameters.add(filter.currencyId());
            whereSql.add("currency_id = ?");
        }
        if (filter.remains() != null){
            parameters.add(filter.remains());
            whereSql.add("remains = ?");
        }
        if (filter.publisherId() != null){
            parameters.add(filter.publisherId());
            whereSql.add("publisher_id = ?");
        }
        parameters.add(filter.limit());
        parameters.add(filter.offset());
        String sqlplus = whereSql.stream().collect(Collectors.joining(
                " AND ",
                parameters.size() > 2 ? " WHERE " : " ",
                " LIMIT ? OFFSET ? "
        ));

        String sql = FIND_ALL_SQL + sqlplus;
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(sql)) {

            List<Books> allBooks = new ArrayList<>();
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i+1, parameters.get(i));
            }

            var res = statement.executeQuery();
            while (res.next()){
                allBooks.add(
                        makeBook(res)
                );
            }

            log.info("Found {} Books", allBooks.size());
            return allBooks;
        } catch (SQLException e) {
            log.error("Failed to found all Books", e);
            throw new DaoException(e);
        }
    }

    private static Books makeBook(ResultSet res) throws SQLException {
        Currencies currency = Currencies.fromId(res.getInt("currency_id"));
        Publishers publisher = new Publishers(res.getLong("publisher_id"),
                res.getString("publisher_name"));

        return new Books(res.getLong("book_id"),
                res.getString("author"),
                res.getString("title"),
                res.getString("description"),
                res.getBigDecimal("price"),
                currency,
                res.getInt("remains"),
                publisher);
    }

    public Optional<Books> findById(Long book_id){
        log.info("Finding Books by ID: {}", book_id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setLong(1, book_id);
            Books books = null;
            var res = statement.executeQuery();
            if (res.next()) books = makeBook(res);

            log.info("Books found by ID {}: {}", book_id, books != null);
            return Optional.ofNullable(books);
        } catch (SQLException e) {
            log.error("Failed to find Books by ID: {}", book_id, e);
            throw new DaoException(e);
        }
    }

    public static BooksDao getInstance(){
        return INSTANCE;
    }

    private BooksDao(){}
}
