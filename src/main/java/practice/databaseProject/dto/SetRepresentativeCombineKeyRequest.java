package practice.databaseProject.dto;

import lombok.Data;

@Data
public class SetRepresentativeCombineKeyRequest {
    private String tableId;
    private String columnName;
    private String representativeCombineKey;
}
