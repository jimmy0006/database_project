package practice.databaseProject.joinTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import practice.databaseProject.join.JoinService;

import java.sql.SQLException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JoinTest{

    @Autowired
    JoinService joinService;

    @Test
    public void joinTest() throws ClassNotFoundException, SQLException {
        joinService.innerJoin("1_fitness_measurement", "PHONE_NUM", "2_physical_instructor_practice_info", "TEL_NO", "전화번호");
    }

}
