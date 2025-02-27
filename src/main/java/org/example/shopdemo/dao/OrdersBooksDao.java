package org.example.shopdemo.dao;

import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.entity.*;
import org.example.shopdemo.utils.BDConnectionService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class OrdersBooksDao implements Dao<Long, OrdersBooks> {
    private final static OrdersBooksDao INSTANCE = new OrdersBooksDao();

    private final static String SAVE_SQL = """
        INSERT INTO ordersbooks (order_id, book_id, quantity)
        VALUES (?, ?, ?)
        """;

    private final static String DELETE_SQL = """
            DELETE from ordersbooks
            where order_book_id = ?
            """;

    private final static String UPDATE_SQL = """
            UPDATE ordersbooks
            set order_id = ?,
                book_id = ?,
                quantity = ?
            where order_book_id = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT ob.order_book_id, ob.order_id, ob.book_id, ob.quantity,
            o.total_price, o.creation_date, o.end_date, o.status_id, o.consumer_id,
            b.author, b.title, b.description, b.price, b.currency_id, b.remains, b.publisher_id
            from ordersbooks ob
            join public.orders o on ob.order_id = o.order_id
            join public.books b on ob.book_id = b.book_id
            join public.consumers c on c.consumer_id = o.consumer_id
            join public.publishers p on p.publisher_id = b.publisher_id
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT ob.order_book_id, ob.order_id, ob.book_id, ob.quantity,
            o.total_price, o.creation_date, o.end_date, o.status_id, o.consumer_id,
            b.author, b.title, b.description, b.price, b.currency_id, b.remains, b.publisher_id
            from ordersbooks ob
            join public.orders o on ob.order_id = o.order_id
            join public.books b on ob.book_id = b.book_id
            join public.consumers c on c.consumer_id = o.consumer_id
            join public.publishers p on p.publisher_id = b.publisher_id
            where order_book_id = ?
            """;

    private static final String FIND_ALL_BY_ORDER_ID_SQL = """
            SELECT ob.order_book_id, ob.order_id, ob.book_id, ob.quantity,
            o.total_price, o.creation_date, o.end_date, o.status_id, o.consumer_id, o.address,
            b.author, b.title, b.description, b.price, b.currency_id, b.remains, b.publisher_id,
            c.consumer_name, c.surname, c.email, c.password, c.telephone, c.role, p.publisher_name
            from ordersbooks ob
            join public.orders o on ob.order_id = o.order_id
            join public.books b on ob.book_id = b.book_id
            join public.consumers c on c.consumer_id = o.consumer_id
            join public.publishers p on p.publisher_id = b.publisher_id
            where o.order_id = ?
            """;

    public OrdersBooks save(OrdersBooks ordersBooks){
        log.info("Saving ordersBook: {}", ordersBooks);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, ordersBooks.getOrder().getOrderId());
            statement.setLong(2, ordersBooks.getBook().getBookId());
            statement.setInt(3, ordersBooks.getQuantity());
            statement.executeUpdate();

            var key = statement.getGeneratedKeys();
            if (key.next()){
                ordersBooks.setOrderBookId(key.getLong("order_book_id"));
            }

            log.info("OrdersBooks saved with ID: {}", ordersBooks.getOrderBookId());
            return ordersBooks;
        } catch (SQLException e) {
            log.error("Failed to save ordersBook: {}", ordersBooks, e);
            throw new RuntimeException(e);
        }
    }

    public boolean delete(Long order_book_id){
        log.info("Deleting ordersBook with ID: {}", order_book_id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(DELETE_SQL)) {

            statement.setLong(1, order_book_id);
            boolean result = statement.executeUpdate() > 0;
            log.info("OrdersBook deletion result for ID {}: {}", order_book_id, result);
            return result;
        } catch (SQLException e) {
            log.error("Failed to delete ordersBook with ID: {}", order_book_id, e);
            throw new RuntimeException(e);
        }
    }

    public boolean update(OrdersBooks ordersBooks){
        log.info("Updating ordersBook: {}", ordersBooks);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setLong(1, ordersBooks.getOrder().getOrderId());
            statement.setLong(2, ordersBooks.getBook().getBookId());
            statement.setInt(3, ordersBooks.getQuantity());
            statement.setLong(4, ordersBooks.getOrderBookId());

            boolean result = statement.executeUpdate() > 0;
            log.info("OrdersBook update result for ID {}: {}", ordersBooks.getOrderBookId(), result);
            return result;
        } catch (SQLException e) {
            log.error("Failed to update ordersBook: {}", ordersBooks, e);
            throw new RuntimeException(e);
        }
    }

    public List<OrdersBooks> findAll(){
        log.info("Find all ordersBook");
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {

            List<OrdersBooks> allOrdersBooks = new ArrayList<>();
            var res = statement.executeQuery();
            while (res.next()){
                allOrdersBooks.add(
                        makeOrdersBooks(res)
                );
            }

            log.info("Found {} ordersBook", allOrdersBooks.size());
            return allOrdersBooks;
        } catch (SQLException e) {
            log.error("Failed to found all ordersBook", e);
            throw new RuntimeException(e);
        }
    }

    public Optional<OrdersBooks> findById(Long order_book_id){
        log.info("Finding ordersBook by ID: {}", order_book_id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setLong(1, order_book_id);
            OrdersBooks ordersBooks = null;
            var res = statement.executeQuery();
            if (res.next()){
                ordersBooks = makeOrdersBooks(res);
            }

            log.info("OrdersBook found by ID {}: {}", order_book_id, ordersBooks != null);
            return Optional.ofNullable(ordersBooks);
        } catch (SQLException e) {
            log.error("Failed to find ordersBook by ID: {}", order_book_id, e);
            throw new RuntimeException(e);
        }
    }

    private OrdersBooks makeOrdersBooks(ResultSet res) throws SQLException {
        Statuses status = Statuses.fromId(res.getInt("status_id"));

        Consumers consumer = new Consumers(res.getLong("consumer_id"),
                res.getString("consumer_name"),
                res.getString("surname"),
                res.getString("email"),
                res.getString("password"),
                res.getString("telephone"),
                res.getString("role"));

        Orders order = new Orders(res.getLong("order_id"),
                res.getBigDecimal("total_price"),
                res.getDate("creation_date").toLocalDate(),
                res.getDate("end_date").toLocalDate(),
                status,
                consumer,
                res.getString("address"));

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

        return new OrdersBooks(res.getLong("order_book_id"),
                order,
                book,
                res.getInt("quantity"));
    }

    public List<OrdersBooks> findAllByOrderId(Long orderId){
        log.info("Find all ordersBook by orderId: {}", orderId);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_ALL_BY_ORDER_ID_SQL)) {

            statement.setLong(1, orderId);
            List<OrdersBooks> allOrdersBooks = new ArrayList<>();
            var res = statement.executeQuery();
            while (res.next()){
                allOrdersBooks.add(
                        makeOrdersBooks(res)
                );
            }

            log.info("Found {} ordersBook by orderId: {}", allOrdersBooks.size(), orderId);
            return allOrdersBooks;
        } catch (SQLException e) {
            log.error("Failed to found all ordersBook by orderId: {}", orderId, e);
            throw new RuntimeException(e);
        }
    }

    public static OrdersBooksDao getInstance(){
        return INSTANCE;
    }
    private OrdersBooksDao() {
    }
}
