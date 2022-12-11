package practice.databaseProject.dto;

import lombok.Data;

@Data
public class DomainScanRequest {
    private String[] tables;
}
