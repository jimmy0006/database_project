package practice.databaseProject.dbConnector;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import practice.databaseProject.entity.SQLResult;

import java.sql.*;

@Component
public class MariaConnector implements DBConnector {
    private Connection dbConn;

    @Override
    public void setUp(String userName, String password, String address) throws ClassNotFoundException, SQLException {
        Class.forName("org.mariadb.jdbc.Driver");
        dbConn = DriverManager.getConnection(
                "jdbc:mariadb://" + address, userName, password
        );
        queryExec("CREATE TABLE IF NOT EXISTS `meta_table` (\n" +
                "  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,\n" +
                "  `name` varchar(50) NOT NULL DEFAULT '',\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  UNIQUE KEY `name` (`name`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;");
        queryExec("CREATE TABLE IF NOT EXISTS `meta_column` (\n" +
                "  `table_id` int(10) unsigned NOT NULL,\n" +
                "  `name` varchar(50) NOT NULL DEFAULT '',\n" +
                "  `type` varchar(50) NOT NULL DEFAULT '',\n" +
                "  `representativeAttribute` varchar(50) DEFAULT NULL,\n" +
                "  `representativeCombineKey` varchar(50) DEFAULT NULL,\n" +
                "  PRIMARY KEY (`table_id`,`name`),\n" +
                "  KEY `table_id` (`table_id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;");
    }

    public void getSetting(String userName,String password,String address) throws ClassNotFoundException, SQLException{
        Class.forName("org.mariadb.jdbc.Driver");
        dbConn = DriverManager.getConnection(
                "jdbc:mariadb://" +address, userName,password
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
