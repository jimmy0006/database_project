package practice.databaseProject.entity;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.sql.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.IntStream;

public final class SQLView {
    private static final List<Integer> INTEGER_TYPES = Arrays.asList(
            Types.INTEGER, Types.TINYINT, Types.TINYINT, Types.BIGINT // BIGINT is long
    );
    private static final List<Integer> STRING_TYPES = Arrays.asList(
            Types.VARCHAR, Types.NVARCHAR, Types.CHAR, Types.NCHAR, Types.LONGVARCHAR, Types.LONGNVARCHAR
    );
    private static final List<Integer> REAL_TYPES = Arrays.asList(
            Types.FLOAT, Types.DOUBLE, Types.DECIMAL
    );

    private final Map<String, ColumnView> data;
    private int records;

    @SuppressWarnings("unchecked")
    @AllArgsConstructor
    public class ColumnView {
        public final String name;
        public final SQLType type;
        List<Object> data;

        @Override
        public String toString() {return String.format("(%s) %s = %s", type, name, data);}
        public int size() {return data.size();}

        public List<Object> get() {return Collections.unmodifiableList(data);}
        public List<Integer> getIntegers() {
            return type == SQLType.INTEGER ? Collections.unmodifiableList((List<Integer>) (List) data) : Collections.emptyList();
        }
        public List<Double> getDoubles() {
            return type == SQLType.DOUBLE ? Collections.unmodifiableList((List<Double>) (List) data) : Collections.emptyList();
        }
        public List<String> getStrings() {
            return type == SQLType.TEXT ? Collections.unmodifiableList((List<String>) (List) data) : Collections.emptyList();
        }
    }

    @RequiredArgsConstructor
    public class RowView {
        final int row;

        public Object get(String column) {
            if(data.containsKey(column)) return data.get(column).data.get(row);
            return null;
        }
        public int getInt(String column) {
            Object o = this.get(column);
            return o instanceof Integer ? (Integer) o : Integer.MAX_VALUE;
        }
        public double getDouble(String column) {
            Object o = this.get(column);
            return o instanceof Double ? (Double) o : Double.NaN;
        }
        public String getString(String column) {
            Object o = this.get(column);
            return o instanceof String ? (String) o : null;
        }

        @Override
        public String toString() {
            Object[] tmp = new Object[data.size()];
            int i = 0;
            for(ColumnView column : data.values()) {
                tmp[i] = column.data.get(row);
                ++i;
            }
            return Arrays.toString(tmp);
        }
    }

    public SQLView(ResultSet queryRes) throws SQLException {
        data = new LinkedHashMap<>();

        // Extract column info
        ResultSetMetaData queryMeta = queryRes.getMetaData();
        int columnCount = queryMeta.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {    // SQL is 1-indexed
            String column = queryMeta.getColumnLabel(i);
            if(data.containsKey(column)) {
                throw new SQLException("Column name collision. Use column alias in your SQL query!");
            }

            int typeConstant = queryMeta.getColumnType(i);
            SQLType type;
            if(INTEGER_TYPES.contains(typeConstant)) type = SQLType.INTEGER;
            else if(STRING_TYPES.contains(typeConstant)) type = SQLType.TEXT;
            else if(REAL_TYPES.contains(typeConstant)) type = SQLType.DOUBLE;
            else throw new SQLException(String.format(
                "Unsupported column type for `%s`: %s - Refer to %s\n", column, typeConstant,
                "https://docs.oracle.com/en/java/javase/11/docs/api/constant-values.html#java.sql.Types"
            ));
            data.put(column, new ColumnView(column, type, new ArrayList<>()));
        }

        // Save query result
        records = 0;
        while(queryRes.next()) {
            records += 1;
            for(ColumnView view : data.values()) {
                Object o;
                switch(view.type) {
                    case INTEGER: o = queryRes.getInt(view.name); break;
                    case DOUBLE: o = queryRes.getDouble(view.name); break;
                    default: o = queryRes.getString(view.name);
                }
                view.data.add(o);
            }
        }

    }

    public int getRowCount() {return records;}
    public int getColumnCount() {return data.size();}

    public RowView getRow(int index) {return new RowView(index);}
    public ColumnView getColumn(String column) {return data.getOrDefault(column, null);}

    public Map<String, ColumnView> getColumns() {return Collections.unmodifiableMap(data);}
    public Stream<RowView> rowStream() {return IntStream.range(0, records).mapToObj(RowView::new);}

    @Override
    public String toString() {return data.toString();}
}
