package practice.databaseProject.analyze;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practice.databaseProject.dbConnector.DBConnector;
import practice.databaseProject.editAttribute.EditAttribute;
import practice.databaseProject.entity.SQLType;
import practice.databaseProject.entity.SpecialTable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TableAnalyzerImpl implements TableAnalyzer {
    private final DBConnector dbConn;
    private final EditAttribute tableEditor;

    /** Expects @param columns to be in order of table schema */
    @Override
    public AnalyzeResult analyze(int tableId, Iterable<String> columns) {
        AnalyzeResult result = new AnalyzeResult();

        for(String col : columns) {
            // data may be null if SQLException but is not handled... Could lead to NullPointerException
            List<String> data = dbConn.queryFor(String.format("SELECT DISTINCT(%s) FROM %s;", col, dbConn.getTableName(tableId)))
                    .getColumn(0).getStrings();

            boolean isInteger = true, isReal = true, hasDecimal = false;
            int nullCount = 0;
            row: for(String e : data) {
                if(e == null || "null".equalsIgnoreCase(e)) {
                    nullCount += 1;
                    continue;
                }
                for (int i = 0; i < e.length(); i++) {
                    char c = e.charAt(i);
                    if(c == '-') {
                        if(i > 0) {
                            isInteger = isReal = false;
                            break row;
                        }
                    } else if(c == '.') {
                        if(hasDecimal) {
                            isInteger = isReal = false;
                            break row;
                        } else if(i > 0 && Character.isDigit(e.charAt(i - 1))) {
                            isInteger = false;
                            hasDecimal = true;
                        } else {
                            isInteger = isReal = false;
                            break row;
                        }
                    } else if(!Character.isDigit(c)) {
                        isInteger = isReal = false;
                        break row;
                    }
                }
            }

            SQLType type = isInteger ? SQLType.INTEGER : isReal ? SQLType.DOUBLE : SQLType.TEXT;
            result.setColumn(col, type, nullCount, data.size());
        }

        return result;
    }

    @Override
    public boolean update(int tableId, AnalyzeResult info) {
        for(String column : info.getColumns()) {
            if(!tableEditor.cast(tableId, column, info.getType(column))) return false;

            if(!dbConn.queryExec(String.format(
                    "UPDATE %s SET %s = '%s' WHERE table_id = '%s' and name = '%s';",
                    SpecialTable.META_COL, "type", info.getType(column), tableId, column
            ))) return false;
        }
        return true;
    }
}
