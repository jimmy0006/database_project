package practice.databaseProject.dbConnector;

import practice.databaseProject.entity.SQLResult;

import java.sql.SQLException;

public interface DBConnector extends AutoCloseable {
    @Override
    public void close() throws SQLException;

    public void setUp(String userName, String password, String address) throws ClassNotFoundException, SQLException;

    public void getSetting(String userName,String password,String address) throws ClassNotFoundException, SQLException;

    public boolean queryExec(String qString);
    public SQLResult queryFor(String qString);

    /*
    * MySQL Query Tips
    * Use backtick(`) for table/column identifiers
    * Use single quotes (') for string
    * Can use double quotes instead of single quotes but not recommended (acts like backtick in standard SQL)
    * */
}
