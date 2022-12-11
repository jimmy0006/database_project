package practice.databaseProject.dto;

import lombok.Data;

@Data
public class ColumnInfo {
    String name;
    String type;
    String min;
    String max;
    int nullCount;
    float nullRatio;
    int distinctCount;
    int specialCount;
    float specialRatio;
    int zeroCount;
    float zeroRatio;
}
