package org.example.shopdemo.dao;

import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.entity.Consumers;
import org.example.shopdemo.entity.Orders;
import org.example.shopdemo.entity.Statuses;
import org.example.shopdemo.utils.BDConnectionService;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class OrdersDao implements Dao<Long, Orders> {
    private final static OrdersDao INSTANCE = new OrdersDao();

    private final static String SAVE_SQL = """
        INSERT INTO orders (total_price, creation_date, end_date, status_id, consumer_id, address)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

    private final static String DELETE_SQL = """
            DELETE from orders
            where order_id = ?
            """;

    private final static String UPDATE_SQL = """
            UPDATE orders
            set total_price = ?,
                creation_date = ?,
                end_date = ?,
                status_id = ?,
                consumer_id = ?,
                address = ?
            where order_id = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT o.order_id, o.total_price, o.creation_date, o.end_date, o.status_id, o.consumer_id,
                o.address, s.status_name, c.consumer_name, c.surname, c.email, c.password, c.telephone, c.role
            from orders o
            join public.consumers c on c.consumer_id = o.consumer_id
            join public.statuses s on o.status_id = s.status_id
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT o.order_id, o.total_price, o.creation_date, o.end_date, o.status_id, o.consumer_id,
                o.address, s.status_name, c.consumer_name, c.surname, c.email, c.password, c.telephone, c.role
            from orders o
            join public.consumers c on c.consumer_id = o.consumer_id
            join public.statuses s on o.status_id = s.status_id
            where o.order_id = ?
            """;

    private static final String FIND_ORDERS_BY_STATUS = """
            SELECT o.order_id, o.total_price, o.creation_date, o.end_date, o.status_id, o.consumer_id,
                o.address, s.status_name, c.consumer_name, c.surname, c.email, c.password, c.telephone, c.role
            FROM orders o
            join public.consumers c on c.consumer_id = o.consumer_id
            join public.statuses s on o.status_id = s.status_id
            WHERE o.status_id = ?
            """;

    private static final String FIND_ORDERS_BY_CONSUMER_ID = """
            SELECT o.order_id, o.total_price, o.creation_date, o.end_date, o.status_id, o.consumer_id,
                o.address, s.status_name, c.consumer_name, c.surname, c.email, c.password, c.telephone, c.role
            FROM orders o
            join public.consumers c on c.consumer_id = o.consumer_id
            join public.statuses s on o.status_id = s.status_id
            WHERE o.consumer_id = ?
            """;

    public Orders save(Orders order){
        log.info("Saving order: {}", order);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setBigDecimal(1, order.getTotalPrice());
            statement.setDate(2, Date.valueOf(order.getCreationDate()));
            statement.setDate(3, Date.valueOf(order.getEndDate()));
            statement.setLong(4, order.getStatus().getId());
            statement.setLong(5, order.getConsumer().getConsumerId());
            statement.setString(6, order.getAddress());
            statement.executeUpdate();

            var key = statement.getGeneratedKeys();
            if (key.next()){
                order.setOrderId(key.getLong("order_id"));
            }

            log.info("Order saved with ID: {}", order.getOrderId());
            return order;
        } catch (SQLException e) {
            log.error("Failed to save order: {}", order, e);
            throw new RuntimeException(e);
        }
    }

    public boolean delete(Long order_id){
        log.info("Deleting order with ID: {}", order_id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(DELETE_SQL)) {

            statement.setLong(1, order_id);
            boolean result = statement.executeUpdate() > 0;
            log.info("Order deletion result for ID {}: {}", order_id, result);
            return result;
        } catch (SQLException e) {
            log.error("Failed to delete order with ID: {}", order_id, e);
            throw new RuntimeException(e);
        }
    }

    public boolean update(Orders order){
        log.info("Updating order: {}", order);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setBigDecimal(1, order.getTotalPrice());
            statement.setDate(2, Date.valueOf(order.getCreationDate()));
            statement.setDate(3,  Date.valueOf(order.getEndDate()));
            statement.setLong(4, order.getStatus().getId());
            statement.setLong(5, order.getConsumer().getConsumerId());
            statement.setString(6, order.getAddress());
            statement.setLong(7, order.getOrderId());

            boolean result = statement.executeUpdate() > 0;
            log.info("Order update result for ID {}: {}", order.getOrderId(), result);
            return result;
        } catch (SQLException e) {
            log.error("Failed to update order: {}", order, e);
            throw new RuntimeException(e);
        }
    }

    public List<Orders> findAll(){
        log.info("Find all orders");
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {

            List<Orders> allOrders = new ArrayList<>();
            var res = statement.executeQuery();
            while (res.next()){
                allOrders.add(
                        makeOrders(res)
                );
            }

            log.info("Found {} orders", allOrders.size());
            return allOrders;
        } catch (SQLException e) {
            log.error("Failed to found all orders", e);
            throw new RuntimeException(e);
        }
    }

    public Optional<Orders> findById(Long order_id){
        log.info("Finding order by ID: {}", order_id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setLong(1, order_id);
            Orders order = null;
            var res = statement.executeQuery();
            if (res.next()){
                order = makeOrders(res);
            }

            log.info("Order found by ID {}: {}", order_id, order != null);
            return Optional.ofNullable(order);
        } catch (SQLException e) {
            log.error("Failed to find order by ID: {}", order_id, e);
            throw new RuntimeException(e);
        }
    }

    private Orders makeOrders(ResultSet res) throws SQLException {
        Statuses status = Statuses.fromId(res.getInt("status_id"));
        Consumers consumer = new Consumers(res.getLong("consumer_id"),
                res.getString("consumer_name"),
                res.getString("surname"),
                res.getString("email"),
                res.getString("password"),
                res.getString("telephone"),
                res.getString("role"));

        return new Orders(res.getLong("order_id"),
                res.getBigDecimal("total_price"),
                res.getDate("creation_date").toLocalDate(),
                res.getDate("end_date").toLocalDate(),
                status,
                consumer,
                res.getString("address"));
    }

    public List<Orders> findOrdersByStatus(Long status){
        log.info("Finding order by status: {}", status);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_ORDERS_BY_STATUS)) {

            statement.setLong(1, status);
            List<Orders> allOrders = new ArrayList<>();
            var res = statement.executeQuery();

            while (res.next()){
                allOrders.add(
                        makeOrders(res)
                );
            }

            log.info("Order found by status {}: {}", status, !allOrders.isEmpty());
            return allOrders;
        } catch (SQLException e) {
            log.error("Failed to find order by status: {}", status, e);
            throw new RuntimeException(e);
        }
    }

    public List<Orders> findOrdersByConsumerId(Long consumerId){
        log.info("Finding order by consumerId: {}", consumerId);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_ORDERS_BY_CONSUMER_ID)) {

            statement.setLong(1, consumerId);
            List<Orders> allOrders = new ArrayList<>();
            var res = statement.executeQuery();

            while (res.next()){
                allOrders.add(
                        makeOrders(res)
                );
            }

            log.info("Order found by consumerId {}: {}", consumerId, !allOrders.isEmpty());
            return allOrders;
        } catch (SQLException e) {
            log.error("Failed to find order by consumerId: {}", consumerId, e);
            throw new RuntimeException(e);
        }
    }

    public static OrdersDao getInstance(){
        return INSTANCE;
    }
    private OrdersDao() {
    }
}
