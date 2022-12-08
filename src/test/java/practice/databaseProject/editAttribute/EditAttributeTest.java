package practice.databaseProject.editAttribute;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import practice.databaseProject.analyze.SQLType;
import practice.databaseProject.dbConnector.MariaConnector;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EditAttributeTest {

    @Autowired
    EditAttribute editAttribute;

    @Autowired
    MariaConnector mariaConnector;

    @BeforeEach
    void beforeEach() throws SQLException, ClassNotFoundException {
        mariaConnector.setUp("root", "1234", "localhost:3305/test");
    }

    @Test
    void editable() throws SQLException, ClassNotFoundException {
        editAttribute.Editable();
    }

    @Test
    void cast() throws SQLException, ClassNotFoundException {
        boolean success = editAttribute.cast("2_physical_instructor_practice_info", "OPERTN_YEAR", SQLType.TEXT);
        System.out.println(success);
    }
}