package classi.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbConnection {
    private Connection conn;
    protected static final String URL = "jdbc:mysql://localhost:3306/organigrammadb";
    protected static final String USER = "root";
    protected static final String PASSWORD = "root";
    protected static final String HOST = "localhost";
    protected static final int PORT = 3306;
    protected static final String dbName = "organigrammadb";
    protected static final String SAVEPATH = "C:/Users/Utente/IdeaProjects/Organigramma Aziendale 219993/src/OrgSaving.sql";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
