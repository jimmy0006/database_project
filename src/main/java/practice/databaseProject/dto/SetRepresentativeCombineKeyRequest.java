package practice.databaseProject.dto;

import lombok.Data;

@Data
public class SetRepresentativeCombineKeyRequest {
    private String table;
    private String column;
    private String representativeCombineKey;
}
