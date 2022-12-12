package practice.databaseProject.dto;

import lombok.Data;

@Data
public class TableInfo {
    private String name;
    private int count;      // record count
    private ColumnInfo[] columns;
}
