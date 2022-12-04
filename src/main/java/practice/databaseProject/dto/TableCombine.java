package practice.databaseProject.dto;

import lombok.Data;

@Data
public class TableCombine {
    private String table1_name;
    private int table1_num;
    private String table1_key;
    private String table2_name;
    private int table2_num;
    private String table2_key;
    private String combine_key;

    @Override
    public String toString() {
        return "TableCombine{" +
                "\ntable1_name='" + table1_name + '\'' +
                "\ntable1_num= " + table1_num +
                "\ntable1_key= '" + table1_key + '\'' +
                "\ntable2_name='" + table2_name + '\'' +
                "\ntable2_num= " + table2_num +
                "\ntable2_key= '" + table2_key + '\'' +
                "\ncombine_key='" + combine_key + '\'' +
                "\n}";
    }

    public TableCombine(String table1_name, String table1_key, String table2_name, String table2_key,String combine_key) {
        this.table1_name = table1_name;
        this.table1_key = table1_key;
        this.table2_name = table2_name;
        this.table2_key = table2_key;
        this.combine_key = combine_key;
    }

    public TableCombine() {
    }
}
