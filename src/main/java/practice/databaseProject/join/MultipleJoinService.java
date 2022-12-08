package practice.databaseProject.join;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.util.ArrayUtils;
import practice.databaseProject.dbConnector.DBConnector;
import practice.databaseProject.dbConnector.MariaConnector;
import practice.databaseProject.dto.JoinResult;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class MultipleJoinService {

    private final MariaConnector mariaConnector;

    private List<Boolean> isComplete;
    private String table_name, table_column, combined_column;
    private List<String> table_names, table_columns;
    int completed;

    public void multipleJoinQueryExecutor(List<String> queries) {
        IntStream.range(0, queries.size())
                .parallel()
                .peek(i -> System.out.println(queries.get(i)))
                .forEach(i -> {
                    mariaConnector.queryExec(queries.get(i));
                    isComplete.set(i, true);
                });
        System.out.println(getInfo());

    }

    public void initialize(String table_name, String table_column, List<String> table_names, List<String> table_columns, String combined_column) {
        this.table_name = table_name;
        this.table_column = table_column;
        this.table_names = table_names;
        this.table_columns = table_columns;
        this.combined_column = combined_column;

        isComplete = new ArrayList<Boolean>(Collections.nCopies(table_names.size(), false));
        Collections.fill(isComplete, false);
    }


    public static boolean contains(final int[] arr, final int key) {
        return Arrays.stream(arr).anyMatch(i -> i == key);
    }

    public List<JoinResult> getInfo() {
        Boolean val = true;
        int[] indexes =
                IntStream.range(0, isComplete.size())
                        .filter(i -> isComplete.get(i).equals(val))
                        .toArray();
        completed = Collections.frequency(isComplete, true);
        List<JoinResult> join_results = new ArrayList<>();
        for (int index = 0; index < table_names.size(); ++index) {

            String table2_name = table_names.get(index);
            String combined_name = table_name + "_" + table2_name;

            String table_count_query = "SELECT COUNT(*) FROM " + table_name;
            String table2_count_query = "SELECT COUNT(*) FROM " + table2_name;
            String combined_count_query = "SELECT COUNT(*) FROM " + combined_name;

            int table_num_records = Integer.parseInt(mariaConnector.queryFor(table_count_query).getRow(0)[0]);
            int table2_num_records = Integer.parseInt(mariaConnector.queryFor(table2_count_query).getRow(0)[0]);

            float table_success_rate, table2_success_rate;
            int combined_num_records;
            String completion;
            if (contains(indexes, index)) {
                combined_num_records = Integer.parseInt(mariaConnector.queryFor(combined_count_query).getRow(0)[0]);
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

            int num_completed = indexes.length;
            int num_to_be_joined = isComplete.size();

            JoinResult join_result = new JoinResult(table_name, table2_name, combined_name, table_num_records, table2_num_records, combined_num_records, table_column, table_columns.get(index), combined_column, table_success_rate, table2_success_rate, completion, num_to_be_joined, num_completed);
            join_results.add(join_result);

        }
        return join_results;

    }

}
