package practice.databaseProject.editAttribute;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practice.databaseProject.dbConnector.DBConnector;
import practice.databaseProject.entity.SQLType;
import practice.databaseProject.dto.TableInfo;
import practice.databaseProject.entity.SQLResult;
import practice.databaseProject.entity.SpecialTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EditAttribute {
    private final DBConnector dbConnector;

    public List<TableInfo> Editable() {
        List<TableInfo> result = new ArrayList<>();
        SQLResult sqlResult = dbConnector.queryFor(String.format("SELECT id,name FROM %s;", SpecialTable.META_TABLE));
        int rowCount = sqlResult.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            TableInfo tableInfo = new TableInfo();
            String[] row = sqlResult.getRow(i);
            tableInfo.setName(row[1]);
            tableInfo.setCount(Integer.parseInt(dbConnector.queryFor(String.format("SELECT COUNT(*) FROM %s;", row[1])).getRow(0)[0]));
            tableInfo.setAttributes(Arrays.asList(dbConnector.queryFor(String.format("SELECT name FROM %s where table_id='%s';", SpecialTable.META_COL, row[0])).getCol(0)));
            result.add(tableInfo);
        }
        return result;
    }

    /** Column Info must be populated into meta_column */
    public boolean cast(String table, String column, SQLType type) {
        if(dbConnector.queryExec(String.format("ALTER TABLE `%s` MODIFY `%s` %s;", table, column, type))){
            String index = dbConnector.queryFor(String.format("SELECT id FROM %s WHERE name='%s';", SpecialTable.META_TABLE, table)).getRow(0)[0];
            return dbConnector.queryExec(String.format("UPDATE `%s` SET type = '%s' WHERE table_id='%s' and name='%s';", SpecialTable.META_COL, type, index, column));
        }
        return false;
    }

    public boolean deleteAttribute(String table, String column){
        if(dbConnector.queryExec(String.format("ALTER TABLE `%s` DROP COLUMN %s;", table, column))){
            String id = dbConnector.queryFor(String.format("SELECT id FROM `%s` WHERE name='%s'", SpecialTable.META_TABLE, table)).getRow(0)[0];
            return dbConnector.queryExec(String.format("DELETE FROM `%s` WHERE table_id='%s' AND name='%s';",SpecialTable.META_COL,id,column));
        }
        return false;
    }
}
