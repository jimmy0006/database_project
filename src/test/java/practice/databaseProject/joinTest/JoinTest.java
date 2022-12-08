package practice.databaseProject.joinTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import practice.databaseProject.join.JoinService;
import practice.databaseProject.join.MultipleJoinService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JoinTest{

    @Autowired
    JoinService joinService;

    @Autowired
    MultipleJoinService multipleJoinService;

    @Test
    public void joinTest() throws ClassNotFoundException, SQLException {
        List<String> table_names = new ArrayList<>();
        table_names.add("2_physical_instructor_practice_info");
        List<String> table_columns = new ArrayList<>();
        table_columns.add("TEL_NO");

        joinService.innerJoin("1_fitness_measurement", "PHONE_NUM", table_names, table_columns, "전화번호");
        //for(int i=0;i<100;++i) System.out.println(multipleJoinService.getInfo());
    }

}
