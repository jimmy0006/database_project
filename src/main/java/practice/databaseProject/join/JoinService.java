package practice.databaseProject.join;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practice.databaseProject.dbConnector.MariaConnector;
import practice.databaseProject.dto.JoinResult;
import practice.databaseProject.entity.SQLResult;

import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class JoinService {
    private final MariaConnector mariaConnector;


    // 대표 결합키가 설정이 완료된 테이블 목록
    // 테이블명 일부 또는 전체, 표준 결합키, 속성명을 입력하여 결합하고자 하는 테이블을 검색
    public JoinResult innerJoin(String table1_name, String table1_column, String table2_name, String table2_column, String combined_column) throws ClassNotFoundException, SQLException {
        String combined_name = table1_name + "_" + table2_name;
        String join_query = "CREATE TABLE " + combined_name + " SELECT * from " + table1_name + " JOIN " + table2_name + " ON " + table1_name + "." + table1_column + " = " + table2_name + "." +table2_column;
        mariaConnector.setUp("root", "1234", "localhost:3306/test");
        mariaConnector.queryExec(join_query);

        String table1_count_query = "SELECT COUNT(*) FROM " + table1_name;
        String table2_count_query = "SELECT COUNT(*) FROM " + table2_name;
        String combined_count_query = "SELECT COUNT(*) FROM " + combined_name;

        int table1_num_records = Integer.parseInt(mariaConnector.queryFor(table1_count_query).getRow(0)[0]);
        int table2_num_records = Integer.parseInt(mariaConnector.queryFor(table2_count_query).getRow(0)[0]);
        int combined_num_records = Integer.parseInt(mariaConnector.queryFor(combined_count_query).getRow(0)[0]);

        float table1_success_rate = (float) table1_num_records/combined_num_records;
        float table2_success_rate = (float) table2_num_records/combined_num_records;

        JoinResult joinResult = new JoinResult(table1_name, table2_name, combined_name, table1_num_records, table2_num_records, combined_num_records, table1_column, table2_column, combined_column, table1_success_rate, table2_success_rate, "완료");

        return joinResult;
    }



}