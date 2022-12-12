package practice.databaseProject.dbConnector;

import org.springframework.stereotype.Component;
import practice.databaseProject.entity.SQLView;
import practice.databaseProject.entity.SpecialTable;
import java.util.*;

import java.sql.*;

@Component
public class MariaConnector implements DBConnector {
    private Connection dbConn;
    private final Map<Integer, String> tableKeys;

    public MariaConnector() {
        tableKeys = new HashMap<>();
    }

    @Override
    public void setUp(String userName, String password, String address) throws ClassNotFoundException, SQLException {
        if(dbConn != null) {
            throw new RuntimeException("MariaConnector::setUp was called when a connection already exists.");
        }

        Class.forName("org.mariadb.jdbc.Driver");
        dbConn = DriverManager.getConnection(
                "jdbc:mariadb://" + address, userName, password
        );

        String metaTable = String.format("CREATE TABLE IF NOT EXISTS `%s` (\n", SpecialTable.META_TABLE) +
                "  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,\n" +
                "  `name` varchar(50) NOT NULL DEFAULT '',\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  UNIQUE KEY `name` (`name`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;";

        String metaColumn = String.format("CREATE TABLE IF NOT EXISTS `%s` (\n", SpecialTable.META_COL) +
                "  `table_id` int(10) unsigned NOT NULL,\n" +
                "  `name` varchar(50) NOT NULL DEFAULT '',\n" +
                "  `type` varchar(50) NOT NULL DEFAULT '',\n" +
                "  `representativeAttribute` varchar(50) DEFAULT NULL,\n" +
                "  `representativeCombineKey` varchar(50) DEFAULT NULL,\n" +
                String.format("FOREIGN KEY (`table_id`) REFERENCES `%s` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,\n", SpecialTable.META_TABLE) +
                "  PRIMARY KEY (`table_id`,`name`),\n" +
                "  KEY `table_id` (`table_id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;";

        queryExecAll(metaTable, metaColumn);
    }

    @Override
    public void getSetting(String userName,String password,String address) throws ClassNotFoundException, SQLException{
        if(dbConn != null) {
            throw new RuntimeException("MariaConnector::getSetting was called when a connection already exists.");
        }

        Class.forName("org.mariadb.jdbc.Driver");
        dbConn = DriverManager.getConnection(
                "jdbc:mariadb://" +address, userName,password
        );
    }

    @Override
    public void close() throws SQLException {
        dbConn.close();
        dbConn = null;
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
    public SQLView queryFor(String qString) {
        try(Statement stmt = dbConn.createStatement();
            ResultSet rs = stmt.executeQuery(qString)) {
            return new SQLView(rs);
        } catch(SQLException e) {
            System.err.println(qString);
            e.printStackTrace(System.err);
            return null;
        }
    }

    @Override
    public boolean queryExecAll(String... qStrings) {
        for(String query : qStrings) {
            if(!queryExec(query)) return false;
        }
        return true;
    }

    @Override
    public int queryTableId(String tableName) {
        if(tableName == null) return -1;

        SQLView r = queryFor(String.format("SELECT id FROM %s WHERE name='%s';", SpecialTable.META_TABLE, tableName));
        if (r == null) return -1;

        int id = r.getColumn("id").getIntegers().get(0);
        tableKeys.put(id, tableName);
        return id;
    }

    @Override
    public int[] queryAllTableId() {
        SQLView r = queryFor(String.format("SELECT id, name FROM `%s`;", SpecialTable.META_TABLE));
        if(r == null) return new int[]{};
        int[] ids = new int[r.getRowCount()];

        List<Integer> tableIds = r.getColumn("id").getIntegers();
        List<String> tableNames = r.getColumn("name").getStrings();
        for (int i = 0; i < ids.length; i++) {
            ids[i] = tableIds.get(i);
            tableKeys.put(ids[i], tableNames.get(i));
        }
        return ids;
    }

    /** Non-negative tableId that did not come from queryTableId will return null */
    @Override
    public String getTableName(int tableId) {
        if(tableId < 0) return null;
        return tableKeys.getOrDefault(tableId, null);
    }
}
