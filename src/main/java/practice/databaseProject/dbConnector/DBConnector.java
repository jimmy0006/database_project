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
}
