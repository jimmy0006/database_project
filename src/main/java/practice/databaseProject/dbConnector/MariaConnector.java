package practice.databaseProject.dbConnector;

import org.springframework.stereotype.Component;
import practice.databaseProject.entity.SQLResult;

import java.sql.*;

@Component
public class MariaConnector implements DBConnector {
    private Connection dbConn;

    public MariaConnector(DBConfig dbConfig) throws ClassNotFoundException, SQLException {
        Class.forName("org.mariadb.jdbc.Driver");
        dbConn = DriverManager.getConnection(
                "jdbc:mariadb://" + dbConfig.getAddress(), dbConfig.getUsername(), dbConfig.getPassword()
        );
    }

    @Override
    public void close() throws SQLException {
        dbConn.close();
    }

    @Override
    public boolean queryExec(String qString) {
        try(Statement stmt = dbConn.createStatement();
            ResultSet rs = stmt.executeQuery(qString)) {
            return true;
        } catch(SQLException e) {
            e.printStackTrace(System.err);
            return false;
        }
    }

    @Override
    public SQLResult queryFor(String qString) {
        try(Statement stmt = dbConn.createStatement();
            ResultSet rs = stmt.executeQuery(qString)) {
            return new SQLResult(rs);
        } catch(SQLException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }
}
