package practice.databaseProject.join;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import practice.databaseProject.dbConnector.DBConnector;
import practice.databaseProject.dto.JoinResult;

@Component
@RequiredArgsConstructor
public class SingleJoinService {
    private final DBConnector dbConnector;
    private Boolean isComplete;
    private String table1_name, table1_column, table2_name, table2_column, combined_column;
    int completed;
    class CustomThread extends Thread {

        String query;
        int index;

        public CustomThread (String query, int index) {
            this.query=query;
            this.index=index;
        }

        public void run() {
            dbConnector.queryExec(query);
            isComplete = true;
        }
    }
    public void initialize(String table1_name, String table1_column, String table2_name, String table2_column, String combined_column) {
        this.table1_name = table1_name;
        this.table1_column = table1_column;
        this.table2_name = table2_name;
        this.table2_column = table2_column;
        this.combined_column = combined_column;

        isComplete = false;
    }
    public void singleJoinExecutor(String query) {
        SingleJoinService.CustomThread temp= new SingleJoinService.CustomThread(query, 0);
        temp.start();
        System.out.println(isComplete);
    }
    public JoinResult getInfo() {

        String combined_name = table1_name + "_" + table2_name;

        String table1_count_query = "SELECT COUNT(*) FROM " + table1_name;
        String table2_count_query = "SELECT COUNT(*) FROM " + table2_name;
        String combined_count_query = "SELECT COUNT(*) FROM " + combined_name;

        int table_num_records = dbConnector.queryFor(table1_count_query).getColumn(0).getIntegers().get(0);
        int table2_num_records = dbConnector.queryFor(table2_count_query).getColumn(0).getIntegers().get(0);

        float table_success_rate, table2_success_rate;
        int combined_num_records;
        String completion;
        if (isComplete) {
            combined_num_records = dbConnector.queryFor(combined_count_query).getColumn(0).getIntegers().get(0);
            table_success_rate = (float) combined_num_records/table_num_records;
            table2_success_rate = (float) combined_num_records/table2_num_records;
            completion = "완료";
        }
        else {
            combined_num_records = -1;
            table_success_rate = -1;
            table2_success_rate = -1;
            completion = "진행 중";
        }

        JoinResult join_result = new JoinResult(table1_name, table2_name, combined_name, table_num_records, table2_num_records, combined_num_records, table1_column, table2_column, combined_column, table_success_rate, table2_success_rate, completion, 0, 1);

        return join_result;

    }
}
