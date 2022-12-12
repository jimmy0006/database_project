package practice.databaseProject.dto;

import lombok.Data;

import java.util.List;

@Data
public class JoineOneTableRequest {
    private String table1Name;
    private String table1Column;
    private String table2Name;
    private String table2Column;
    private String combinedColumn;
}
