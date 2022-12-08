package practice.databaseProject.dto;

import lombok.Data;

@Data
public class SetRepresentativeAttributeRequest {
    private String tableId;
    private String columnName;
    private String representativeAttribute;
}
