package practice.databaseProject.entity;

import java.util.*;
import java.sql.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Parse and save result of SQL query as a 2D-like data structure
 *
 * Provide row-index and column-index accessors with column-name-to-column-index mapping
 * Also provide data as Stream of row-or-column
 * */
public class SQLResult {
    private final Map<String, Integer> colMappings;
    private final List<String[]> data;
    private int colCount;

    public SQLResult(ResultSet queryRes) throws SQLException {
        ResultSetMetaData queryMeta = queryRes.getMetaData();
        colCount = queryMeta.getColumnCount();

        // Generate column to index mappings where index is converted from 1-index to 0-index
        colMappings = new LinkedHashMap<>();    // Use LinkedHashMap to retain column iteration order
        for(int i = 0; i < colCount; ++i) {
            // getColumnName() uses column name of table -> Name collision if multiple tables share the same column name
            // getColumnLabel() respects aliasing in the query string -> Use aliasing to prevent name collision
            String colName = queryMeta.getColumnLabel(i + 1);
            if(colMappings.containsKey(colName)) {
                throw new SQLException("Column name collision. Use column alias in your SQL query!");
            } else {
                colMappings.put(colName, i);
            }
        }

        // Save query result
        data = new ArrayList<>();
        while(queryRes.next()) {
            String[] row = new String[colCount];
            for(int i = 0; i < colCount; ++i) {
                // Remember SQL API uses 1-index
                row[i] = queryRes.getString(i + 1);
            }
            data.add(row);
        }
    }

    public int getColCount() { return colCount; }
    public int getRowCount() { return data.size(); }

    public int getColIndex(String colName) { return colMappings.getOrDefault(colName, -1); }
    public String[] getColNames() { return colMappings.keySet().toArray(String[]::new); }

    public String[] getRow(int index) { return (index < 0 || index >= data.size()) ? null : data.get(index); }
    public String[] getCol(int index) {
        return (index < 0 || index >= colCount) ? null : data.stream()
                .map(row -> row[index])
                .toArray(String[]::new);
    }

    public Stream<String[]> rowStream() { return data.stream(); }
    public Stream<String[]> colStream() { return IntStream.range(0, colCount).mapToObj(this::getCol); }
}
