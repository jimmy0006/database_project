package practice.databaseProject.dto;

import lombok.Data;

@Data
public class DomainScanResponse {
    private TableInfo[] tableInfos;
}
