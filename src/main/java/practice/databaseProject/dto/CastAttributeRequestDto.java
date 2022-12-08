package practice.databaseProject.dto;

import lombok.Data;

@Data
public class CastAttributeRequestDto {
    private String table;
    private String column;
    private String type;
}
