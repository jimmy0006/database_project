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
        int rowCount = sqlResult.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            TableInfo tableInfo = new TableInfo();
            String[] row = sqlResult.getRow(i);
            tableInfo.setName(row[1]);
            tableInfo.setCount(Integer.parseInt(dbConnector.queryFor("SELECT COUNT(*) FROM "+row[1]+";").getCol(0)[0]));
            tableInfo.setAttributes(new ArrayList(Arrays.asList(dbConnector.queryFor("SELECT name FROM meta_column where id=" + row[0] + ";").getCol(0))));
            result.add(tableInfo);
        }
        return result;
    }

    /** Column Info must be populated into meta_column */
    public boolean cast(String table, String column, SQLType type) {
        if(dbConnector.queryExec(String.format("ALTER TABLE `%s` MODIFY `%s` %s;", table, column, type))){
            String index = dbConnector.queryFor(String.format("SELECT id FROM %s WHERE `table_name`=\"%s\";", SpecialTable.META_TABLE, table)).getRow(0)[0];
            return dbConnector.queryExec("UPDATE `meta_column` SET type=\"" + type + "\" WHERE id=" + index + " and name=\"" + column + "\";");
        }
        return false;
    }
}
