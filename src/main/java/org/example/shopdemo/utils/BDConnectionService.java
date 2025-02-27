package org.example.shopdemo.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class BDConnectionService {
//    private static final String URL_KEY = "db.url";
    private static final String URL_KEY = (System.getenv("DB_URL") != null && !System.getenv("DB_URL").isEmpty())
        ? System.getenv("DB_URL")
        : "jdbc:postgresql://shopdemopostgres:5432/Shopdemo";
    private static final String USERNAME_KEY = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "postgres";
    private static final String PASSWORD_KEY = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "SmiSup704";

    //    private static final String USERNAME_KEY = "db.username";
//    private static final String PASSWORD_KEY = "db.password";

    static {
        loadDriver();
    }

    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection connection(){
        try {
//            return DriverManager.getConnection(PropertiesUtil.get(URL_KEY),
//                    PropertiesUtil.get(USERNAME_KEY),
//                    PropertiesUtil.get(PASSWORD_KEY));
            return DriverManager.getConnection(URL_KEY, USERNAME_KEY, PASSWORD_KEY);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private BDConnectionService() {
    }
}
