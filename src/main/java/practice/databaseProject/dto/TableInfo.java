package practice.databaseProject.dto;

import lombok.Data;

import java.util.List;

@Data
public class TableInfo {
    private String name;
    private int count;
    private List<String> attributes;
}
