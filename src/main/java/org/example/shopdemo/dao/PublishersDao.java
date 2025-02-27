package org.example.shopdemo.dao;

import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.entity.Publishers;
import org.example.shopdemo.utils.BDConnectionService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class PublishersDao implements Dao<Long, Publishers> {
    private final static PublishersDao INSTANCE = new PublishersDao();

    private final static String SAVE_SQL = """
            INSERT INTO publishers (publisher_name)
            values (?)
            """;

    private final static String DELETE_SQL = """
            DELETE from publishers
            where publisher_id = ?
            """;

    private final static String FIND_ALL_SQL = """
            SELECT *
            from publishers
            """;

    private final static String FIND_BY_ID_SQL = """
            SELECT *
            from publishers
            where publisher_id = ?
            """;

    private final static String UPDATE_SQL = """
            UPDATE publishers
            set publisher_name = ?
            where publisher_id = ?
            """;

    public Publishers save(Publishers publisher){
        log.info("Saving publisher: {}", publisher);
        try (var connection = BDConnectionService.connection();
        var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)){

            statement.setString(1, publisher.getName());
            statement.executeUpdate();

            var key = statement.getGeneratedKeys();
            if (key.next()){
                publisher.setPublisherId(key.getLong("publisher_id"));
            }

            log.info("Publisher saved with ID: {}", publisher.getPublisherId());
            return publisher;
        } catch (SQLException e) {
            log.error("Failed to save publisher: {}", publisher, e);
            throw new RuntimeException(e);
        }
    }

    public boolean delete(Long publisher_id){
        log.info("Deleting publisher with ID: {}", publisher_id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(DELETE_SQL)){

            statement.setLong(1, publisher_id);
            statement.executeUpdate();

            boolean result = statement.executeUpdate() > 0;
            log.info("Publisher deletion result for ID {}: {}", publisher_id, result);
            return result;
        } catch (SQLException e) {
            log.error("Failed to delete publisher with ID: {}", publisher_id, e);
            throw new RuntimeException(e);
        }
    }

    public boolean update(Publishers publisher){
        log.info("Updating publisher: {}", publisher);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(UPDATE_SQL)){

            statement.setString(1, publisher.getName());
            statement.setLong(2, publisher.getPublisherId());
            statement.executeUpdate();

            boolean result = statement.executeUpdate() > 0;
            log.info("Publisher update result for ID {}: {}", publisher.getPublisherId(), result);
            return result;
        } catch (SQLException e) {
            log.error("Failed to update publisher: {}", publisher, e);
            throw new RuntimeException(e);
        }
    }

    public List<Publishers> findAll(){
        log.info("Find all publishers");
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_ALL_SQL)){

            List<Publishers> allPublishers = new ArrayList<>();
            var res = statement.executeQuery();
            while (res.next()){
                allPublishers.add(
                        makePublisher(res)
                );
            }

            log.info("Found {} publishers", allPublishers.size());
            return allPublishers;
        } catch (SQLException e) {
            log.error("Failed to found all publisher", e);
            throw new RuntimeException(e);
        }
    }

    public Optional<Publishers> findById(Long publisher_id){
        log.info("Finding publisher by ID: {}", publisher_id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_ALL_SQL)){

            Publishers publisher = null;
            var res = statement.executeQuery();
            if (res.next()){
                publisher = makePublisher(res);
            }

            log.info("Publisher found by ID {}: {}", publisher_id, publisher != null);
            return Optional.ofNullable(publisher);
        } catch (SQLException e) {
            log.error("Failed to find publisher by ID: {}", publisher_id, e);
            throw new RuntimeException(e);
        }
    }

    private Publishers makePublisher(ResultSet res) throws SQLException {
        return new Publishers(
                res.getLong("publisher_id"),
                res.getString("publisher_name")
        );
    }

    public static PublishersDao getInstance(){
        return INSTANCE;
    }
    private PublishersDao() {
    }
}
