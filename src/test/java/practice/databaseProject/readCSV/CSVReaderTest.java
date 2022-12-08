package practice.databaseProject.readCSV;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import practice.databaseProject.dbConnector.MariaConnector;
import practice.databaseProject.join.JoinService;
import practice.databaseProject.join.MultipleJoinService;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CSVReaderTest {

    @Autowired
    CSVReader csvReader;
    @Autowired
    MariaConnector mariaConnector;

    @BeforeEach
    void beforeEach() throws SQLException, ClassNotFoundException {
        mariaConnector.setUp("root", "1234", "localhost:3305/test");
    }
    @Test
    void readCSV() throws SQLException, ClassNotFoundException {
        List<String> strings = csvReader.readCSV("1_Fitness_Measurement.csv");
        System.out.println(strings);
        csvReader.DB_saver("1_Fitness_Measurement.csv" );
    }

    @Test
    void saveFile() {
    }
}