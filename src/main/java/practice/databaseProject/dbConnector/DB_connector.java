package practice.databaseProject.dbConnector;

import org.springframework.stereotype.Service;
import practice.databaseProject.entity.SQLResult;

import java.sql.*;
import java.util.Arrays;

@Deprecated
public class DB_connector implements AutoCloseable {
    public static boolean loadDriver(String className) {
        try {
            Class.forName(className);
            return true;
        } catch(ClassNotFoundException e) {
            e.printStackTrace(System.err);
            return false;
        }
    }

    private final Connection dbConn;

    /**
    * @param address "mariadb://[host]:[port]/[db_name]"
    * */
    public DB_connector(String address, String username, String password) throws SQLException {
        dbConn = DriverManager.getConnection("jdbc:" + address, username, password);
    }

    @Override
    public void close() throws SQLException { dbConn.close(); }

    /**
    * @return false on error, otherwise true
    * */
    public boolean queryExec(String qString) {
        try(Statement stmt = dbConn.createStatement();
            ResultSet rs = stmt.executeQuery(qString)) {
            return true;
        } catch(SQLException e) {
            e.printStackTrace(System.err);
            return false;
        }
    }

    /**
     * @return null on error, otherwise SQLResult with query result
     * */
    public SQLResult queryFor(String qString) {
        try(Statement stmt = dbConn.createStatement();
            ResultSet rs = stmt.executeQuery(qString)) {
            return new SQLResult(rs);
        } catch(SQLException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    public static void main(String[] args) {
        if(!DB_connector.loadDriver("org.mariadb.jdbc.Driver")) {
            System.out.println("Failed to load MariaDB driver. Exiting...");
            System.exit(1);
        }

        try(DB_connector conn = new DB_connector("mariadb://127.0.0.1:3306/proj", "root", "root")) {
            // DB_connector는 try-with-resource로 생성된 후 exception이 발생하지 않음 -> null/false/-1 등의 값으로 오류 표시
            String[] testQueries = {
                "SELECT * FROM meta_table;",
                "SELECT * FROM meta_column;",
                "SELECT t.name, c.name FROM meta_table t, meta_column c WHERE c.isCandidate = 1 AND t.id = c.table_id;",
                "SELECT t.name AS tname, c.name AS cname FROM meta_table t, meta_column c WHERE c.isCandidate = 1 AND t.id = c.table_id;"
            };
            for(String query : testQueries) {
                System.out.println("=".repeat(50));
                System.out.printf("Executing query: %s\n\n", query);
                SQLResult res = conn.queryFor(query);
                if(res == null) {
                    System.out.println("Query Error");
                    continue;
                }

                String[] colNames = res.getColNames();      // 필요없을지도 모르지만 속성명 순서 체크
                for(int i = 0; i < colNames.length; ++i) {  // 반환 순서와 mapping 값이 일치하는지 비교
                    System.out.printf("%d) %d : %s\n", i, res.getColIndex(colNames[i]), colNames[i]);
                }
                System.out.println();
//                for(int i = 0, n = res.getRowCount(); i < n; ++i) {
//                    System.out.println(Arrays.toString(res.getRow(i)));
//                }
                res.rowStream().map(Arrays::toString).forEach(System.out::println);
            }
        } catch(SQLException e) {
            e.printStackTrace(System.err);
            System.out.println("Connection Error. Exiting...");
            System.exit(1);
        }

        System.out.println("\n\nProgram complete! Exiting safely...\n\n");

    }
}
