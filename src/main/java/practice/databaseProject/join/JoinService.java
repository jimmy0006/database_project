package practice.databaseProject.join;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practice.databaseProject.dbConnector.DBConnector;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JoinService {
    private final DBConnector dbConnector;
    private final MultipleJoinService multipleJoinService;
    private final SingleJoinService singleJoinService;


    // 대표 결합키가 설정이 완료된 테이블 목록
    // 테이블명 일부 또는 전체, 표준 결합키, 속성명을 입력하여 결합하고자 하는 테이블을 검색
    public void multipleInnerJoin(String table_name, String table_column, List<String> table_names, List<String> table_columns, String combined_column) {
        multipleJoinService.initialize(table_name, table_column, table_names, table_columns, combined_column);
        List<String> queries = new ArrayList<>();
        for (int i = 0; i < table_names.size(); ++i) {
            String table2_name = table_names.get(i);
            String table2_column = table_columns.get(i);
            String combined_name = table_name + "_" + table2_name;
            String join_query = "CREATE TABLE " + combined_name + " SELECT * from " + table_name + " JOIN " + table2_name + " ON " + table_name + "." + table_column + " = " + table2_name + "." +table2_column;

            queries.add(join_query);

        }
        multipleJoinService.multipleJoinQueryExecutor(queries);
    }

    public void innerJoin(String table1_name, String table1_column, String table2_name, String table2_column, String combined_column) {
        singleJoinService.initialize(table1_name, table1_column, table2_name, table2_column, combined_column);
        String combined_name = table1_name + "_" + table2_name;
        String query = "CREATE TABLE " + combined_name + " SELECT * from " + table1_name + " JOIN " + table2_name + " ON " + table1_name + "." + table1_column + " = " + table2_name + "." +table2_column;

        singleJoinService.singleJoinExecutor(query);

    }





}
