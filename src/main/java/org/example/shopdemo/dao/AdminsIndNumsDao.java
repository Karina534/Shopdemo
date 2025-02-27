package org.example.shopdemo.dao;

import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.entity.AdminsIndNums;
import org.example.shopdemo.exception.DaoException;
import org.example.shopdemo.utils.BDConnectionService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class AdminsIndNumsDao implements Dao<Long, AdminsIndNums> {
    private final static AdminsIndNumsDao INSTANCE = new AdminsIndNumsDao();
    private final static String SAVE_SQL = """
    INSERT INTO admins_ind_nums (ind_num)
    VALUES (?)
    """;

    private final static String DELETE_SQL = """
        DELETE from admins_ind_nums
        where admins_ind_nums_id = ?
        """;

    private final static String UPDATE_SQL = """
        UPDATE admins_ind_nums
        set ind_num = ?
        where admins_ind_nums_id = ?
        """;

    private static final String FIND_ALL_SQL = """
        SELECT *
        from admins_ind_nums
        """;

    private static final String FIND_BY_ID_SQL = """
        SELECT *
        from admins_ind_nums
        where admins_ind_nums_id = ?
        """;

    private static final String FIND_BY_IND_NUM_SQL = """
        SELECT *
        from admins_ind_nums
        where ind_num = ?
        """;

    @Override
    public AdminsIndNums save(AdminsIndNums adminsIndNums) {
        log.info("Saving adminsIndNums: {}", adminsIndNums);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, adminsIndNums.getIndNum());

            statement.executeUpdate();

            var key = statement.getGeneratedKeys();
            if (key.next()){
                adminsIndNums.setAdminsIndNumsId(key.getLong("admins_ind_nums_id"));
            }

            log.info("AdminsIndNums saved with ID: {}", adminsIndNums.getAdminsIndNumsId());
            return adminsIndNums;
        } catch (SQLException e) {
            log.error("Failed to save adminsIndNums: {}", adminsIndNums, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(Long id) {
        log.info("Deleting adminsIndNums with ID: {}", id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(DELETE_SQL)) {

            statement.setLong(1, id);
            boolean result = statement.executeUpdate() > 0;
            log.info("AdminsIndNums deletion result for ID {}: {}", id, result);
            return result;
        } catch (SQLException e) {
            log.error("Failed to delete adminsIndNums with ID: {}", id, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(AdminsIndNums adminsIndNums) {
        log.info("Updating adminsIndNums: {}", adminsIndNums);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setLong(1, adminsIndNums.getIndNum());

            statement.setLong(2, adminsIndNums.getAdminsIndNumsId());

            boolean result = statement.executeUpdate() > 0;
            log.info("AdminsIndNums update result for ID {}: {}", adminsIndNums.getAdminsIndNumsId(), result);
            return result;
        } catch (SQLException e) {
            log.error("Failed to update adminsIndNums: {}", adminsIndNums, e);
            throw new DaoException(e);
        }
    }

    @Override
    public List<AdminsIndNums> findAll() {
        log.info("Find all AdminsIndNums");
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {

            List<AdminsIndNums> allAdminsIndNums = new ArrayList<>();
            var res = statement.executeQuery();
            while (res.next()){
                allAdminsIndNums.add(
                        makeAdminIndNum(res)
                );
            }
            log.info("Found {} AdminsIndNums", allAdminsIndNums.size());
            return allAdminsIndNums;
        } catch (SQLException e) {
            log.error("Failed to found all AdminsIndNums", e);
            throw new DaoException(e);
        }
    }

    private AdminsIndNums makeAdminIndNum(ResultSet res) throws SQLException {
        return new AdminsIndNums(
                res.getLong("admins_ind_nums_id"),
                res.getLong("ind_num"));
    }

    @Override
    public Optional<AdminsIndNums> findById(Long id) {
        log.info("Finding AdminsIndNums by ID: {}", id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setLong(1, id);
            AdminsIndNums adminsIndNums = null;
            var res = statement.executeQuery();
            if (res.next()) adminsIndNums = makeAdminIndNum(res);

            log.info("AdminsIndNums found by ID {}: {}", id, adminsIndNums != null);
            return Optional.ofNullable(adminsIndNums);
        } catch (SQLException e) {
            log.error("Failed to find AdminsIndNums by ID: {}", id, e);
            throw new DaoException(e);
        }
    }

    public Optional<AdminsIndNums> findByIndNum(Long indNum) {
        log.info("Finding AdminsIndNums by indNum: {}", indNum);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_BY_IND_NUM_SQL)) {

            statement.setLong(1, indNum);
            AdminsIndNums adminsIndNums = null;
            var res = statement.executeQuery();
            if (res.next()) adminsIndNums = makeAdminIndNum(res);

            log.info("AdminsIndNums found by indNum {}: {}", indNum, adminsIndNums != null);
            return Optional.ofNullable(adminsIndNums);
        } catch (SQLException e) {
            log.error("Failed to find AdminsIndNums by indNum: {}", indNum, e);
            throw new DaoException(e);
        }
    }

    public static AdminsIndNumsDao getInstance(){
        return INSTANCE;
    }
    private AdminsIndNumsDao() {
    }
}
