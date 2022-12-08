package practice.databaseProject.analyze;

import practice.databaseProject.entity.SQLType;

import java.util.*;

public class AnalyzeResult {
    private static final int INTEGER = 1;
    private static final int DOUBLE = 2;
    private static final int TEXT = 3;

    private static final int IS_NOTNULL = 0;
    private static final int IS_NULLABLE = 1;

    private static final int IS_NOTDISTINCT = 0;
    private static final int IS_DISTINCT = 1;

    // col : type, nullable, unique
    private final Map<String, Object[]> metaData;

    public AnalyzeResult() {
        this.metaData = new LinkedHashMap<>();
    }

    public void setColumn(String column, SQLType type, boolean nullable, boolean unique) {
        metaData.put(column, new Object[]{type, nullable, unique});
    }

    public boolean contains(String column) {return metaData.containsKey(column);}

    public String[] getColumns() {return metaData.keySet().toArray(String[]::new);}

    public SQLType getType(String column) {return (SQLType) metaData.get(column)[0];}

    public boolean isNullable(String column) {return (boolean) metaData.get(column)[1];}

    public boolean isDistinct(String column) {return (boolean) metaData.get(column)[2];}
}
