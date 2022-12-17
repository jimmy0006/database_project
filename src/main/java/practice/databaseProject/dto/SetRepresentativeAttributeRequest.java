package practice.databaseProject.dto;

import lombok.Data;

@Data
public class SetRepresentativeAttributeRequest {
    private String table;
    private String column;
}
