package practice.databaseProject.dto;

import lombok.Data;

@Data
public class SetRepresentativeCombineKeyRequest {
    private String columnName;
    private String tableId;
    private String representativeCombineKey;
}
