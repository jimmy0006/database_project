package practice.databaseProject.dto;

public class JoinResult {
    private String table1_name, table2_name, combined_name;
    private int table1_num_records, table2_num_records, combined_num_records;
    private String table1_combine_key, table2_combine_key, combine_key;
    private float table1_success_rate, table2_success_rate;
    private String completion;


    public JoinResult(String table1_name, String table2_name, String combined_name, int table1_num_records, int table2_num_records, int combined_num_records, String table1_combine_key, String table2_combine_key, String combine_key, float table1_success_rate, float table2_success_rate, String completion) {
        this.table1_name = table1_name;
        this.table2_name = table2_name;
        this.combined_name = combined_name;
        this.table1_num_records = table1_num_records;
        this.table2_num_records = table2_num_records;
        this.combined_num_records = combined_num_records;
        this.table1_combine_key = table1_combine_key;
        this.table2_combine_key = table2_combine_key;
        this.combine_key = combine_key;
        this.table1_success_rate = table1_success_rate;
        this.table2_success_rate = table2_success_rate;
        this.completion = completion;
    }


}
