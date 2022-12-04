package practice.databaseProject.join;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practice.databaseProject.dbConnector.DB_connector;
import practice.databaseProject.dto.Keys;
import practice.databaseProject.dto.TableCombine;
import practice.databaseProject.dto.TableCombineResult;
import practice.databaseProject.entity.SQLResult;

import java.sql.SQLException;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final DB_connector db_connector;

    public static void main(String[] args){
        //테스트
        TableCombine tableCombine = beforeJoin("1_fitness_measurement", "PHONE_NUM", "2_physical_instructor_practice_info", "TEL_NO", "phone");
        System.out.println(tableCombine);
        System.out.println(afterJoin("1_fitness_measurement","PHONE_NUM", tableCombine.getTable1_num(), "2_physical_instructor_practice_info","TEL_NO",tableCombine.getTable2_num()));
    }

    //쿼리문 날리기 이전 해당 테이블의 레코드 수 조사
    public static TableCombine beforeJoin(String table1, String column1, String table2, String column2,String combine_key){
        TableCombine tableCombine = new TableCombine(table1,column1,table2,column2,combine_key);
        try(DB_connector conn = new DB_connector("mariadb://127.0.0.1:3306/proj", "root", "root")) {
            String[] testQueries = {
                    "SELECT COUNT(*) as COUNT1 FROM "+table1+";",
                    "SELECT COUNT(*) as COUNT2 FROM "+table2+";"
            };
            int[] queryResult=new int[2];
            int i=0;
            for(String query : testQueries) {
                SQLResult res = conn.queryFor(query);
                if(res == null) {
                    System.out.println("Query Error");
                    continue;
                }
                queryResult[i++]=Integer.parseInt(res.getRow(0)[0]);
            }
            tableCombine.setTable1_num(queryResult[0]);
            tableCombine.setTable2_num(queryResult[1]);
        } catch(SQLException e) {
            e.printStackTrace(System.err);
            System.out.println("Connection Error. Exiting...");
            System.exit(1);
        }
        return tableCombine;
    }

    //join문 작성 후 실행, 결과 출력
    public static TableCombineResult afterJoin(String table1, String column1,int table1_num, String table2, String column2,int table2_num){
        TableCombineResult tableCombineResult = new TableCombineResult();
        try(DB_connector conn = new DB_connector("mariadb://127.0.0.1:3306/proj", "root", "root")) {
            // DB_connector는 try-with-resource로 생성된 후 exception이 발생하지 않음 -> null/false/-1 등의 값으로 오류 표시
            String testQuery = "SELECT * from `proj`.`"+table1+"` inner join `proj`.`"+table2+"` on "+table1+"."+column1+" = "+table2+"."+column2;
            SQLResult res = conn.queryFor(testQuery);
            if(res == null) {
                System.out.println("Query Error");
            }
            tableCombineResult.setResult_num(res.getRowCount());
            tableCombineResult.setTable1_result((float)res.getRowCount()/table1_num);
            tableCombineResult.setTable2_result((float)res.getRowCount()/table2_num);
        }catch(SQLException e) {
            e.printStackTrace(System.err);
            System.out.println("Connection Error. Exiting...");
            System.exit(1);
        }
        return tableCombineResult;
    }

}