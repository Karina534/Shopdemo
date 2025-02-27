package org.example.shopdemo.dao;

import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.entity.Admins;
import org.example.shopdemo.exception.DaoException;
import org.example.shopdemo.utils.BDConnectionService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class AdminsDao implements Dao<Long, Admins> {
    private final static AdminsDao INSTANCE = new AdminsDao();

    private final static String SAVE_SQL = """
    INSERT INTO admins (admin_name, surname, email, password, telephone, role, individual_num_id)
    VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

    private final static String DELETE_SQL = """
        DELETE from admins
        where admin_id = ?
        """;

    private final static String UPDATE_SQL = """
        UPDATE admins
        set admin_name = ?,
            surname = ?,
            email = ?,
            password = ?,
            telephone = ?,
            role = ?,
            individual_num_id = ?
        where admin_id = ?
        """;

    private static final String FIND_ALL_SQL = """
        SELECT *
        from admins
        """;

    private static final String FIND_BY_ID_SQL = """
        SELECT *
        from admins
        where admin_id = ?
        """;

    private static final String FIND_BY_EMAIL_AND_PASSWORD_SQL = """
        SELECT *
        from admins
        where email = ? and password = ?
        """;

    public Admins save(Admins admins){
        log.info("Saving admin: {}", admins);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, admins.getName());
            statement.setString(2, admins.getSurname());
            statement.setString(3, admins.getEmail());
            statement.setString(4, admins.getPassword());
            statement.setString(5, admins.getTelephone());
            statement.setString(6, admins.getRole());
            statement.setLong(7, admins.getIndividualNumId());
            statement.executeUpdate();

            var key = statement.getGeneratedKeys();
            if (key.next()){
                admins.setAdminId(key.getLong("admin_id"));
            }
            log.info("Admin saved with ID: {}", admins.getAdminId());
            return admins;
        } catch (SQLException e) {
            log.error("Failed to save admin: {}", admins, e);
            throw new RuntimeException(e);
        }
    }

    public boolean delete(Long id){
        log.info("Deleting admin with ID: {}", id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(DELETE_SQL)) {

            statement.setLong(1, id);
            boolean result = statement.executeUpdate() > 0;
            log.info("Admin deletion result for ID {}: {}", id, result);
            return result;
        } catch (SQLException e) {
            log.error("Failed to delete admin with ID: {}", id, e);
            throw new RuntimeException(e);
        }
    }

    public boolean update(Admins admin){
        log.info("Updating admin: {}", admin);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setString(1, admin.getName());
            statement.setString(2, admin.getSurname());
            statement.setString(3, admin.getEmail());
            statement.setString(4,admin.getPassword());
            statement.setString(5, admin.getTelephone());
            statement.setString(6, admin.getRole());
            statement.setLong(7, admin.getIndividualNumId());
            statement.setLong(8, admin.getAdminId());

            boolean result = statement.executeUpdate() > 0;
            log.info("Admin update result for ID {}: {}", admin.getAdminId(), result);
            return result;
        } catch (SQLException e) {
            log.error("Failed to update admin: {}", admin, e);
            throw new DaoException(e);
        }
    }

    public List<Admins> findAll(){
        log.info("Find all admins");
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {

            List<Admins> allAdmins = new ArrayList<>();
            var res = statement.executeQuery();
            while (res.next()){
                allAdmins.add(
                        makeAdmin(res)
                );
            }
            log.info("Found {} admins", allAdmins.size());
            return allAdmins;
        } catch (SQLException e) {
            log.error("Failed to found all admins", e);
            throw new DaoException(e);
        }
    }

    private Admins makeAdmin(ResultSet res) throws SQLException {
        return new Admins(res.getLong("admin_id"),
                res.getString("admin_name"),
                res.getString("surname"),
                res.getString("email"),
                res.getString("password"),
                res.getString("telephone"),
                res.getString("role"),
                res.getLong("individual_num_id"));
    }

    public Optional<Admins> findByEmailAndPassword(String email, String password){
        log.info("Finding admin by email: {} and password", email);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_BY_EMAIL_AND_PASSWORD_SQL)) {

            statement.setString(1, email);
            statement.setString(2, password);
            Admins admin = null;
            var res = statement.executeQuery();
            if (res.next()) admin = makeAdmin(res);

            log.info("Admin found by email {}: {}", email, admin != null);
            return Optional.ofNullable(admin);
        } catch (SQLException e) {
            log.error("Failed to find admin by email and password", e);
            throw new DaoException(e);
        }
    }

    public Optional<Admins> findById(Long admin_id){
        log.info("Finding admin by ID: {}", admin_id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setLong(1, admin_id);
            Admins admin = null;
            var res = statement.executeQuery();
            if (res.next()) admin = makeAdmin(res);

            log.info("Admin found by ID {}: {}", admin_id, admin != null);
            return Optional.ofNullable(admin);
        } catch (SQLException e) {
            log.error("Failed to find admin by ID: {}", admin_id, e);
            throw new DaoException(e);
        }
    }

    public static AdminsDao getInstance(){
        return INSTANCE;
    }
    private AdminsDao() {
    }
}


