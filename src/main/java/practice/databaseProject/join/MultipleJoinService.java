package practice.databaseProject.join;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import practice.databaseProject.dbConnector.DBConnector;
import practice.databaseProject.dto.JoinResult;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class MultipleJoinService {

    private final DBConnector dbConnector;

    private List<Boolean> isComplete;
    private String table_name, table_column, combined_column;
    private List<String> table_names, table_columns;
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
            isComplete.set(index,true);
        }
    }

    public void multipleJoinQueryExecutor(List<String> queries) {

        for(int i=0;i<queries.size();++i) {
            CustomThread temp= new CustomThread(queries.get(i),i);
            temp.start();
        }

        System.out.println(isComplete.get(0));

    }


    public void initialize(String table_name, String table_column, List<String> table_names, List<String> table_columns, String combined_column) {
        this.table_name = table_name;
        this.table_column = table_column;
        this.table_names = table_names;
        this.table_columns = table_columns;
        this.combined_column = combined_column;

        Boolean[] tmp = new Boolean[table_names.size()];
        Arrays.fill(tmp, false);
        isComplete = Arrays.asList(tmp);
    }

    public List<JoinResult> getInfo() {
        Set<Integer> indexes = IntStream.range(0, isComplete.size())
                .filter(isComplete::get)
                .boxed()
                .collect(Collectors.toCollection(HashSet::new));
        completed = Collections.frequency(isComplete, true);
        List<JoinResult> join_results = new ArrayList<>();
        for (int index = 0; index < table_names.size(); ++index) {

            String table2_name = table_names.get(index);
            String combined_name = table_name + "_" + table2_name;

            String table_count_query = "SELECT COUNT(*) FROM " + table_name;
            String table2_count_query = "SELECT COUNT(*) FROM " + table2_name;
            String combined_count_query = "SELECT COUNT(*) FROM " + combined_name;

            int table_num_records = dbConnector.queryFor(table_count_query).getColumn(0).getIntegers().get(0);
            int table2_num_records = dbConnector.queryFor(table2_count_query).getColumn(0).getIntegers().get(0);

            float table_success_rate, table2_success_rate;
            int combined_num_records;
            String completion;
            if (indexes.contains(index)) {
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

            int num_completed = indexes.size();
            int num_to_be_joined = isComplete.size();

            JoinResult join_result = new JoinResult(table_name, table2_name, combined_name, table_num_records, table2_num_records, combined_num_records, table_column, table_columns.get(index), combined_column, table_success_rate, table2_success_rate, completion, num_to_be_joined, num_completed);
            join_results.add(join_result);

        }
        return join_results;

    }

}
