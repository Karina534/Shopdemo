package org.example.shopdemo.dao;

import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.entity.Baskets;
import org.example.shopdemo.entity.Consumers;
import org.example.shopdemo.exception.DaoException;
import org.example.shopdemo.utils.BDConnectionService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class BasketsDao implements Dao<Long, Baskets> {
    private static final BasketsDao INSTANCE = new BasketsDao();

    private final static String SAVE_SQL = """
        INSERT INTO baskets (consumer_id)
        VALUES (?)
        """;

    private final static String DELETE_SQL = """
            DELETE from baskets
            where basket_id = ?
            """;

    private final static String UPDATE_SQL = """
            UPDATE baskets
            set consumer_id = ?
            where basket_id = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT b.basket_id, b.consumer_id, c.consumer_name, c.surname, c.email, c.password, c.telephone, c.role
            from baskets b
            join public.consumers c on c.consumer_id = b.consumer_id
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT b.basket_id, b.consumer_id, c.consumer_name, c.surname, c.email, c.password, c.telephone, c.role
            from baskets b
            join public.consumers c on c.consumer_id = b.consumer_id
            where basket_id = ?
            """;

    private static final String FIND_BY_CONSUMER_ID_SQL = """
            SELECT b.basket_id, b.consumer_id, c.consumer_name, c.surname, c.email, c.password, c.telephone, c.role
            from baskets b
            join public.consumers c on c.consumer_id = b.consumer_id
            where b.consumer_id = ?
            """;

    public Baskets save(Baskets baskets){
        log.info("Saving baskets: {}", baskets);
        try(var connection = BDConnectionService.connection();
            var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, baskets.getConsumer().getConsumerId());

            statement.executeUpdate();

            var key = statement.getGeneratedKeys();
            if (key.next()){
                baskets.setBasketId(key.getLong("basket_id"));
            }

            log.info("Baskets saved with ID: {}", baskets.getBasketId());
            return baskets;
        } catch (SQLException e) {
            log.error("Failed to save baskets: {}", baskets, e);
            throw new DaoException(e);
        }
    }

    public boolean delete(Long basket_id){
        log.info("Deleting baskets with ID: {}", basket_id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(DELETE_SQL)) {

            statement.setLong(1, basket_id);
            boolean result = statement.executeUpdate() > 0;
            log.info("Baskets deletion result for ID {}: {}", basket_id, result);
            return result;
        } catch (SQLException e) {
            log.error("Failed to delete baskets with ID: {}", basket_id, e);
            throw new DaoException(e);
        }
    }

    public boolean update(Baskets baskets){
        log.info("Updating baskets: {}", baskets);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setLong(1, baskets.getConsumer().getConsumerId());
            statement.setLong(2, baskets.getBasketId());

            boolean result = statement.executeUpdate() > 0;
            log.info("Baskets update result for ID {}: {}", baskets.getBasketId(), result);
            return result;
        } catch (SQLException e) {
            log.error("Failed to update baskets: {}", baskets, e);
            throw new DaoException(e);
        }
    }

    public List<Baskets> findAll(){
        log.info("Find all Baskets");
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {

            List<Baskets> allBaskets = new ArrayList<>();
            var res = statement.executeQuery();
            while (res.next()){
                allBaskets.add(
                        makeBasket(res)
                );
            }
            log.info("Found {} Baskets", allBaskets.size());
            return allBaskets;
        } catch (SQLException e) {
            log.error("Failed to found all Baskets", e);
            throw new DaoException(e);
        }
    }

    private Baskets makeBasket(ResultSet res) throws SQLException {
        Consumers consumer = new Consumers(res.getLong("consumer_id"),
                res.getString("consumer_name"),
                res.getString("surname"),
                res.getString("email"),
                res.getString("password"),
                res.getString("telephone"),
                res.getString("role"));

        return new Baskets(res.getLong("basket_id"),
                consumer);
    }

    public Optional<Baskets> findById(Long basket_id){
        log.info("Finding Baskets by ID: {}", basket_id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setLong(1, basket_id);
            Baskets basket = null;
            var res = statement.executeQuery();
            if (res.next()) basket = makeBasket(res);

            log.info("Baskets found by ID {}: {}", basket_id, basket != null);
            return Optional.ofNullable(basket);
        } catch (SQLException e) {
            log.error("Failed to find Baskets by ID: {}", basket_id, e);
            throw new DaoException(e);
        }
    }

    public Optional<Baskets> findByConsumerId(Long id){
        log.info("Finding Baskets by Consumer ID: {}", id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_BY_CONSUMER_ID_SQL)) {

            statement.setLong(1, id);
            Baskets basket = null;
            var res = statement.executeQuery();
            if (res.next()) basket = makeBasket(res);

            log.info("Baskets found by Consumer ID {}: {}", id, basket != null);
            return Optional.ofNullable(basket);
        } catch (SQLException e) {
            log.error("Failed to find Baskets by Consumer ID: {}", id, e);
            throw new DaoException(e);
        }
    }

    public static BasketsDao getInstance(){
        return INSTANCE;
    }

    public BasketsDao() {
    }
}
