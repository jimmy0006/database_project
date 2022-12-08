package practice.databaseProject.analyze;

import org.springframework.stereotype.Service;
import practice.databaseProject.dbConnector.DBConnector;
import practice.databaseProject.entity.SQLResult;

@Service
public class TableAnalyzerImpl implements TableAnalyzer {
    DBConnector dbConn;

    public TableAnalyzerImpl(DBConnector dbConn) {
        this.dbConn = dbConn;
    }

    @Override
    public AnalyzeResult analyze(String table, String[] columns) {
        int nEntries = Integer.parseInt(dbConn.queryFor(
            String.format("SELECT table_rows FROM INFORMATION_SCHEMA.TABLES WHERE table_name = '%s';", table)
        ).getRow(0)[0]);
        AnalyzeResult result = new AnalyzeResult();

        for(String col : columns) {
            String[] data = dbConn.queryFor(String.format("SELECT DISTINCT(%s) FROM %s;", col, table)).getCol(0);
            boolean isInteger = true, isReal = true, hasDecimal = false, hasNull = false;
            row: for(String e : data) {
                if(e == null || "null".equalsIgnoreCase(e)) {
                    hasNull = true;
                    continue;
                }
                for (int i = 0; i < e.length(); i++) {
                    char c = e.charAt(i);
                    if(c == '-' && i > 0) {
                        isInteger = isReal = false;
                        break row;
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
            result.setColumn(col, type, hasNull, data.length == nEntries);
        }

        return result;
    }
}
