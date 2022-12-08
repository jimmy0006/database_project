package practice.databaseProject.analyze;

import practice.databaseProject.entity.SQLType;

import java.util.*;

public class AnalyzeResult {
    // col : type, nullCount, distinctCount
    private final Map<String, Object[]> metaData;

    public AnalyzeResult() {
        this.metaData = new LinkedHashMap<>();
    }

    public void setColumn(String column, SQLType type, int nullCount, int distinctCount) {
        metaData.put(column, new Object[]{type, nullCount, distinctCount});
    }

    public boolean contains(String column) {return metaData.containsKey(column);}

    public String[] getColumns() {return metaData.keySet().toArray(String[]::new);}

    public SQLType getType(String column) {return (SQLType) metaData.get(column)[0];}

    public int getNullCount(String column) {return (int) metaData.get(column)[1];}

    public int getDistinctCount(String column) {return (int) metaData.get(column)[2];}
}
