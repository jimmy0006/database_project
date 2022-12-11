package practice.databaseProject.dbConnector;

import practice.databaseProject.entity.SQLResult;

import java.sql.SQLException;

public interface DBConnector extends AutoCloseable {
    @Override
    void close() throws SQLException;

    void setUp(String userName, String password, String address) throws ClassNotFoundException, SQLException;

    void getSetting(String userName,String password,String address) throws ClassNotFoundException, SQLException;

    boolean queryExec(String qString);
    SQLResult queryFor(String qString);
    boolean queryExecBatch(String... qStrings);

    int queryTableId(String tableName);
    String getTableName(int tableId);

    /*
    * MySQL Query Tips
    * Use backtick(`) for table/column identifiers
    * Use single quotes (') for string
    * Can use double quotes instead of single quotes but not recommended (acts like backtick in standard SQL)
    * */
}
