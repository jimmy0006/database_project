package practice.databaseProject.dto;

import lombok.Data;

import java.util.List;

@Data
public class JoinTableRequest {
    private String table_name;
    private String table_column;
    private List<String> table_names;
    private List<String> table_columns;
    private String combined_column;
}
