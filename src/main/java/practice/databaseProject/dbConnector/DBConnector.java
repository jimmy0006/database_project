package practice.databaseProject.dbConnector;

import practice.databaseProject.entity.SQLResult;

import java.sql.SQLException;

public interface DBConnector extends AutoCloseable {
    @Override
    public void close() throws SQLException;

    public boolean queryExec(String qString);
    public SQLResult queryFor(String qString);
}
