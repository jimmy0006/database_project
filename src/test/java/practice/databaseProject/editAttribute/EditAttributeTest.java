package practice.databaseProject.editAttribute;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import practice.databaseProject.entity.SQLType;
import practice.databaseProject.dbConnector.MariaConnector;
import practice.databaseProject.dto.TableInfo;

import java.sql.SQLException;
import java.util.List;

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
    void editable(){
        List<TableInfo> editable = editAttribute.Editable();
        for (TableInfo tableInfo : editable) {
            System.out.println(tableInfo.getCount());
            System.out.println(tableInfo.getAttributes().toString());
        }
    }

    @Test
    void cast() throws SQLException, ClassNotFoundException {
        boolean success = editAttribute.cast(mariaConnector.queryTableId("2_physical_instructor_practice_info"), "OPERTN_YEAR", SQLType.TEXT);
        System.out.println(success);
    }
}
