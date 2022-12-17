package practice.databaseProject.editAttribute;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practice.databaseProject.dbConnector.DBConnector;
import practice.databaseProject.dto.ColumnInfo;
import practice.databaseProject.dto.TableInfo;
import practice.databaseProject.entity.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EditAttribute {
    // Pattern for KR/ENG/Digit
    private static final String PAT_NORMAL = "^[ㄱ-ㅎ가-힣0-9a-zA-Z]*$";

    private final DBConnector dbConnector;

    /*
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
    */

    /** columns[i] paired with columnTypes[i] */
    public TableInfo scanTable(String table, List<String> columns, List<String> columnTypes) {
        TableInfo result = new TableInfo();
        result.setName(table);
        // 1 time info: get row count
        String tableInfoQuery = String.format("SELECT COUNT(*) FROM `%s`;", table);
        SQLView tInfoRes = dbConnector.queryFor(tableInfoQuery);
        if(tInfoRes == null) {
            result.setCount(-1);
            result.setColumns(null);
            return result;
        }
        int rows = tInfoRes.getColumn(0).getInt(0);
        result.setCount(rows);

        ColumnInfo[] columnInfos = new ColumnInfo[columns.size()];
        for(int i = 0; i < columnInfos.length; ++i) {
            String normalSQL = String.format("SELECT COUNT(*) FROM `%s` WHERE `%s` RLIKE '%s'", table, columns.get(i), PAT_NORMAL);
            String zeroSQL = String.format("SELECT COUNT(*) FROM `%s` WHERE `%s` = '0'", table, columns.get(i));    // Automatic type coercion - evil, just like JS
            // Add IS NOT NULL condition to fix MIN/MAX value not being computed correctly - other SELECT values should remain the same... should...
            String columnSQL = String.format(
                "SELECT (%s) `Normal`, (%s) Zero, COUNT(`%s`) `NotNull`, COUNT(DISTINCT `%s`) `Distinct`, MIN(`%s`) `Min`, MAX(`%s`) `Max` FROM `%s`;",
                normalSQL, zeroSQL, columns.get(i), columns.get(i), columns.get(i), columns.get(i), table
            );
            SQLView cInfoRes = dbConnector.queryFor(columnSQL);
            if(cInfoRes == null) {
                columnInfos[i] = null;
                continue;
            }
            // String[] cInfo = cInfoRes.getRow(0);
            ColumnInfo columnInfo = new ColumnInfo();
            int normal = cInfoRes.getColumn("Normal").getInt(0);
            int zero = cInfoRes.getColumn("Zero").getInt(0);
            int notNull = cInfoRes.getColumn("NotNull").getInt(0);
            int distinct = cInfoRes.getColumn("Distinct").getInt(0);
            String min = cInfoRes.getColumn("Min").get(0).toString();
            String max = cInfoRes.getColumn("Max").get(0).toString();

            columnInfo.setName(columns.get(i));
            columnInfo.setType(columnTypes.get(i));
            columnInfo.setMax(max);
            columnInfo.setMin(min);
            columnInfo.setDistinctCount(distinct);
            columnInfo.setNullCount(rows - notNull);
            columnInfo.setNullRatio((float) (rows - notNull) / rows);
            columnInfo.setSpecialCount(rows - normal);
            columnInfo.setSpecialRatio((float) (rows - normal) / rows);
            columnInfo.setZeroCount(zero);
            columnInfo.setZeroRatio((float) zero / rows);

            columnInfos[i] = columnInfo;
        }
        result.setColumns(columnInfos);
        return result;
    }

    /** Column Info must be populated into meta_column */
    public boolean cast(int tableId, String column, SQLType type) {
        String castSql = String.format("ALTER TABLE `%s` MODIFY `%s` %s;", dbConnector.getTableName(tableId), column, type);
        String metaInfoSql = String.format("UPDATE `%s` SET type = '%s' WHERE table_id='%s' and name='%s';", SpecialTable.META_COL, type, tableId, column);
        return dbConnector.queryExecAll(castSql, metaInfoSql);
    }

    public boolean deleteAttribute(int tableId, String column){
        String dropSql = String.format("ALTER TABLE `%s` DROP COLUMN %s;", dbConnector.getTableName(tableId), column);
        String metaInfoSql = String.format("DELETE FROM `%s` WHERE table_id='%s' AND name='%s';", SpecialTable.META_COL, tableId, column);
        return dbConnector.queryExecAll(dropSql, metaInfoSql);
    }
}
