package practice.databaseProject.dto;

import lombok.Data;

import java.util.List;

@Data
public class JoinMultipleTableRequest {
    private String tableName;
    private String tableColumn;
    private List<String> tableNames;
    private List<String> tableColumns;
    private String combinedColumn;
}
