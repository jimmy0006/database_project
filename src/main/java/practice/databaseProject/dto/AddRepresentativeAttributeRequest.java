package practice.databaseProject.dto;

import lombok.Data;

@Data
public class AddRepresentativeAttributeRequest {
    private String table;
    private String column;
    private String attribute;
}
