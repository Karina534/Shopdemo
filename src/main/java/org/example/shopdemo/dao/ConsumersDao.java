package org.example.shopdemo.dao;

import lombok.extern.slf4j.Slf4j;
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
public class ConsumersDao implements Dao<Long, Consumers> {
    private final static ConsumersDao INSTANCE = new ConsumersDao();

    private final static String SAVE_SQL = """
        INSERT INTO consumers (consumer_name, surname, email, password, telephone, role)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

    private final static String DELETE_SQL = """
            DELETE from consumers
            where consumer_id = ?
            """;

    private final static String UPDATE_SQL = """
            UPDATE consumers
            set consumer_name = ?,
                surname = ?,
                email = ?,
                password = ?,
                telephone = ?,
                role = ?
            where consumer_id = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT *
            from consumers
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT *
            from consumers
            where consumer_id = ?
            """;

    private static final String FIND_BY_EMAIL_AND_PASSWORD_SQL = """
            SELECT *
            from consumers
            where email = ? and password = ?
            """;

    public Consumers save(Consumers consumers){
        log.info("Saving consumer: {}", consumers);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, consumers.getName());
            statement.setString(2, consumers.getSurname());
            statement.setString(3, consumers.getEmail());
            statement.setString(4, consumers.getPassword());
            statement.setString(5, consumers.getTelephone());
            statement.setString(6, consumers.getRole());

            statement.executeUpdate();

            var key = statement.getGeneratedKeys();
            if (key.next()){
                consumers.setConsumerId(key.getLong("consumer_id"));
            }

            log.info("Consumer saved with ID: {}", consumers.getConsumerId());
            return consumers;
        } catch (SQLException e) {
            log.error("Failed to save consumer: {}", consumers, e);
            throw new RuntimeException(e);
        }
    }

    public boolean delete(Long consumer_id){
        log.info("Deleting consumer with ID: {}", consumer_id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(DELETE_SQL)) {

            statement.setLong(1, consumer_id);
            boolean result = statement.executeUpdate() > 0;
            log.info("Consumer deletion result for ID {}: {}", consumer_id, result);
            return result;
        } catch (SQLException e) {
            log.error("Failed to delete consumer with ID: {}", consumer_id, e);
            throw new RuntimeException(e);
        }
    }

    public boolean update(Consumers consumer){
        log.info("Updating consumer: {}", consumer);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setString(1, consumer.getName());
            statement.setString(2, consumer.getSurname());
            statement.setString(3, consumer.getEmail());
            statement.setString(4,consumer.getPassword());
            statement.setString(5, consumer.getTelephone());
            statement.setString(6, consumer.getRole());

            statement.setLong(8, consumer.getConsumerId());

            boolean result = statement.executeUpdate() > 0;
            log.info("Consumer update result for ID {}: {}", consumer.getConsumerId(), result);
            return result;
        } catch (SQLException e) {
            log.error("Failed to consumer admin: {}", consumer, e);
            throw new DaoException(e);
        }
    }

    public List<Consumers> findAll(){
        log.info("Find all Consumers");
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {

            List<Consumers> allConsumers = new ArrayList<>();
            var res = statement.executeQuery();
            while (res.next()){
                allConsumers.add(
                        makeConsumer(res)
                );
            }

            log.info("Found {} Consumers", allConsumers.size());
            return allConsumers;
        } catch (SQLException e) {
            log.error("Failed to found all Consumers", e);
            throw new DaoException(e);
        }
    }

    private Consumers makeConsumer(ResultSet res) throws SQLException {
        return new Consumers(res.getLong("consumer_id"),
                res.getString("consumer_name"),
                res.getString("surname"),
                res.getString("email"),
                res.getString("password"),
                res.getString("telephone"),
                res.getString("role"));
    }

    public Optional<Consumers> findByEmailAndPassword(String email, String password){
        log.info("Finding consumer by email: {} and password", email);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_BY_EMAIL_AND_PASSWORD_SQL)) {

            statement.setString(1, email);
            statement.setString(2, password);
            Consumers consumer = null;
            var res = statement.executeQuery();
            if (res.next()) consumer = makeConsumer(res);

            log.info("Consumer found by email {}: {}", email, consumer != null);
            return Optional.ofNullable(consumer);
        } catch (SQLException e) {
            log.error("Failed to find consumer by email and password", e);
            throw new DaoException(e);
        }
    }

    public Optional<Consumers> findById(Long consumer_id){
        log.info("Finding consumer by ID: {}", consumer_id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setLong(1, consumer_id);
            Consumers consumer = null;
            var res = statement.executeQuery();
            if (res.next()) consumer = makeConsumer(res);

            log.info("Consumer found by ID {}: {}", consumer_id, consumer != null);
            return Optional.ofNullable(consumer);
        } catch (SQLException e) {
            log.error("Failed to find consumer by ID: {}", consumer_id, e);
            throw new DaoException(e);
        }
    }

    public static ConsumersDao getInstance(){
        return INSTANCE;
    }
    private ConsumersDao() {
    }
}
