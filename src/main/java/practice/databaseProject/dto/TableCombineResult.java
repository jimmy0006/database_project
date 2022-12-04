package practice.databaseProject.dto;

import lombok.Data;

@Data
public class TableCombineResult {
    private int result_num;
    private float table1_result;
    private float table2_result;

    @Override
    public String toString() {
        return "TableCombineResult{" +
                "\nresult_num=   " + result_num +
                "\ntable1_result=" + table1_result +
                "\ntable2_result=" + table2_result +
                "\n}";
    }

}
