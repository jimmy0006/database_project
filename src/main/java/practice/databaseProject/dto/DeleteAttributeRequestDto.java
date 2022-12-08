package practice.databaseProject.dto;

import lombok.Data;

@Data
public class DeleteAttributeRequestDto {
    private String table;
    private String column;
}
