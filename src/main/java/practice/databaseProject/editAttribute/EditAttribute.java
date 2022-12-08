package practice.databaseProject.editAttribute;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import practice.databaseProject.dbConnector.MariaConnector;
import practice.databaseProject.dto.TableInfo;
import practice.databaseProject.entity.SQLResult;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EditAttribute {
    @Autowired
    private final MariaConnector dbConnector;

    public List<TableInfo> Editable() throws SQLException, ClassNotFoundException {
        dbConnector.setUp("root", "1234", "localhost:3305/test");
        List<TableInfo> result = new ArrayList<>();
        SQLResult sqlResult = dbConnector.queryFor("SELECT id,table_name FROM test.meta_table;");
        List<List<String>> temp = new ArrayList<>();
        int rowCount = sqlResult.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            temp.add(Arrays.asList(sqlResult.getRow(i)));
        }
//        ArrayList<String> name = new ArrayList(Arrays.asList(sqlResult.getCol(1)));

        for (List<String> tableName : temp) {
            TableInfo tableInfo = new TableInfo();
//            editableTable.setCount(dbConnector.queryFor("SELECT COUNT(*) FROM test."+tableName.get(1)+";")[0][0]);
            System.out.println(new ArrayList(Arrays.asList(dbConnector.queryFor("SELECT COUNT(*) FROM test."+tableName.get(1)+";").getCol(0))));
            System.out.println(new ArrayList(Arrays.asList(dbConnector.queryFor("SELECT name FROM test.meta_column where id="+tableName.get(0)+";").getCol(0))));
        }
        SQLResult records = dbConnector.queryFor("SELECT id,table_name FROM test.meta_table;");

        return result;
    }

}
