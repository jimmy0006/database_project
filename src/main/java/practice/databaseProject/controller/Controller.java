package practice.databaseProject.controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import practice.databaseProject.dbConnector.DBConnector;
import practice.databaseProject.dbConnector.MariaConnector;
import practice.databaseProject.dto.*;
import practice.databaseProject.editAttribute.EditAttribute;
import practice.databaseProject.entity.SQLType;
import practice.databaseProject.join.JoinService;
import practice.databaseProject.join.MultipleJoinService;
import practice.databaseProject.readCSV.CSVReader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class Controller {

    private final JoinService joinService;
    private final CSVReader csvReader;
    private final MultipleJoinService multipleJoinService;
    private final DBConnector mariaConnector;
    private final EditAttribute editAttribute;

    @PostMapping(value = "/dbconnect")
    public ResponseEntity<DBConnectionResponse> dbConnect(@RequestBody DBConnectionRequest dbConnectionRequest) {
        String host = dbConnectionRequest.getHost();
        String port = dbConnectionRequest.getPort();
        String database = dbConnectionRequest.getDatabase();
        String user = dbConnectionRequest.getUser();
        String password = dbConnectionRequest.getPassword();
        boolean connected = true;
        try {
            mariaConnector.setUp(user, password, host + ":" + port + "/" + database);
        } catch (Exception e) {
            connected = false;
            System.out.println(e);
        }
        DBConnectionResponse dbConnectionResponse = new DBConnectionResponse(connected);
        return ResponseEntity.ok(dbConnectionResponse);
    }
    @PostMapping(value = "/csv", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Boolean> readCSV(MultipartFile file) throws IOException {
        boolean b = csvReader.saveFile(file);
        return ResponseEntity.ok(b);
    }
    @PostMapping(value = "/jointables")
    public ResponseEntity<Void> joinTables(@RequestBody JoinTableRequest joinTableRequest) throws Exception {
        String table_name = joinTableRequest.getTable_name();
        String table_column = joinTableRequest.getTable_column();
        List<String> table_names = joinTableRequest.getTable_names();
        List<String> table_columns = joinTableRequest.getTable_columns();
        String combined_column = joinTableRequest.getTable_column();

        joinService.innerJoin(table_name, table_column, table_names, table_columns, combined_column);

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/getjoinedtable")
    public ResponseEntity<GetJoinedTableResponse> getJoinedTable() throws Exception {
        List<JoinResult> joinResults = multipleJoinService.getInfo();
        GetJoinedTableResponse getJoinedTableResponse = new GetJoinedTableResponse(joinResults);
        return ResponseEntity.ok(getJoinedTableResponse);
    }

    @GetMapping(value = "/editattribute")
    public ResponseEntity<List<TableInfo>> getTableInfo(){
        List<TableInfo> editable = editAttribute.Editable();
        return ResponseEntity.ok(editable);
    }

    @PostMapping(value = "/editattribute")
    public ResponseEntity<Boolean> updateTableInfo(@RequestBody CastAttributeRequestDto request){
        return ResponseEntity.ok(editAttribute.cast(request.getTable(), request.getColumn(), SQLType.valueOf(request.getType())));
    }

}
