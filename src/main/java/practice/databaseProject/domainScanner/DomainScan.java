package practice.databaseProject.domainScanner;

import lombok.RequiredArgsConstructor;
import practice.databaseProject.entity.SQLType;

import java.util.*;

public class DomainScan {
    // column :
    private final Map<String, ColumnStat> columnStats;

    public DomainScan() {
        columnStats = new LinkedHashMap<>();
    }

    public void setColumn(String column, int totalCount, int notNullCount, int distinctCount,
                          int normalCount, int zeroCount, String min, String max) {
        columnStats.put(column, new ColumnStat(totalCount, notNullCount, distinctCount, normalCount, zeroCount, min, max));
    }

    public int getTotalCount(String column) {return columnStats.get(column).totalCount;}
    public int getNotNullCount(String column) {return columnStats.get(column).notNullCount;}
    public int getDistinctCount(String column) {return columnStats.get(column).distinctCount;}
    public int getNormalCount(String column) {return columnStats.get(column).normalCount;}
    public int getZeroCount(String column) {return columnStats.get(column).zeroCount;}
    public String getMin(String column) {return columnStats.get(column).min;}
    public String getMax(String column) {return columnStats.get(column).max;}

}

@RequiredArgsConstructor
class ColumnStat {
    protected final int totalCount;
    protected final int notNullCount;
    protected final int distinctCount;
    protected final int normalCount;    // 한글, 영어, 숫자
    protected final int zeroCount;
    protected final String min;
    protected final String max;
}
