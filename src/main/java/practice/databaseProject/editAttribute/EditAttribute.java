package practice.databaseProject.editAttribute;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import practice.databaseProject.entity.SQLType;
import practice.databaseProject.dbConnector.MariaConnector;
import practice.databaseProject.dto.TableInfo;
import practice.databaseProject.entity.SQLResult;
import practice.databaseProject.entity.SpecialTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EditAttribute {
    @Autowired
    private final MariaConnector dbConnector;

    public List<TableInfo> Editable() {
        List<TableInfo> result = new ArrayList<>();
        SQLResult sqlResult = dbConnector.queryFor("SELECT id,table_name FROM meta_table;");
        List<List<String>> temp = new ArrayList<>();
        int rowCount = sqlResult.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            temp.add(Arrays.asList(sqlResult.getRow(i)));
        }
//        ArrayList<String> name = new ArrayList(Arrays.asList(sqlResult.getCol(1)));

        for (List<String> tableName : temp) {
            TableInfo tableInfo = new TableInfo();
//            editableTable.setCount(dbConnector.queryFor("SELECT COUNT(*) FROM test."+tableName.get(1)+";")[0][0]);
            System.out.println(new ArrayList(Arrays.asList(dbConnector.queryFor("SELECT COUNT(*) FROM "+tableName.get(1)+";").getCol(0))));
            System.out.println(new ArrayList(Arrays.asList(dbConnector.queryFor("SELECT name FROM meta_column where id="+tableName.get(0)+";").getCol(0))));
        }
        SQLResult records = dbConnector.queryFor("SELECT id,table_name FROM meta_table;");

        return result;
    }

    /** Column Info must be populated into meta_column */
    public boolean cast(String table, String column, SQLType type) {
        if(dbConnector.queryExec(String.format("ALTER TABLE `%s` MODIFY `%s` %s;", table, column, type))){
            String index = dbConnector.queryFor(String.format("SELECT id FROM %s WHERE `table_name`=`%s`;", SpecialTable.META_TABLE, table)).getRow(0)[0];
            return dbConnector.queryExec("UPDATE `meta_column` SET type=\"" + type + "\" WHERE id=" + index + " and name=\"" + column + "\";");
        }
        return false;
    }
}
