package practice.databaseProject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import practice.databaseProject.csv.CSVHandler;
import practice.databaseProject.dbConnector.DBConnector;
import practice.databaseProject.dictionary.StandardCombineKeyDictionary;
import practice.databaseProject.dictionary.StandardRepresentativeAttributeDictionary;
import practice.databaseProject.dto.*;
import practice.databaseProject.editAttribute.EditAttribute;
import practice.databaseProject.entity.SQLType;
import practice.databaseProject.join.JoinService;
import practice.databaseProject.join.MultipleJoinService;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class Controller {

    private final JoinService joinService;
    private final CSVHandler csvReader;
    private final MultipleJoinService multipleJoinService;
    private final DBConnector mariaConnector;
    private final EditAttribute editAttribute;
    private final StandardRepresentativeAttributeDictionary standardRepresentativeAttributeDictionary;
    private final StandardCombineKeyDictionary standardCombineKeyDictionary;

    @PostMapping(value = "/dbconnect")
    public ResponseEntity<DBConnectionResponse> dbConnect(@RequestBody DBConnectionRequest dbConnectionRequest) {
        standardRepresentativeAttributeDictionary.init();
        standardCombineKeyDictionary.init();
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
    public ResponseEntity<Boolean> readCSV(MultipartFile file) {
        Path path = csvReader.saveFile(file);
        boolean b = false;
        if(path != null) b = csvReader.loadCSV(path);
        return ResponseEntity.ok(path != null && b);
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
    public ResponseEntity<GetJoinedTableResponse> getJoinedTable() {
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

    @DeleteMapping(value = "/editattribute")
    public ResponseEntity<Boolean> deleteTableAttribute(@RequestBody DeleteAttributeRequestDto request){
        return ResponseEntity.ok(editAttribute.deleteAttribute(request.getTable(), request.getColumn()));
    }
    @PostMapping(value = "/getrepresentativeattributes")
    public ResponseEntity<List<String>> getRepresentativeAttributes() throws Exception {
        return ResponseEntity.ok(standardRepresentativeAttributeDictionary.values());
    }
    @PostMapping(value = "/getcombinekeys")
    public ResponseEntity<List<String>> getCombineKeys() throws Exception {
        return ResponseEntity.ok(standardCombineKeyDictionary.values());
    }
    @PostMapping(value = "/addrepresentativeattribute")
    public ResponseEntity<Void> addRepresentativeAttribute(@RequestBody AddRepresentativeAttributeRequest addRepresentativeAttributeRequest) throws Exception {
        standardRepresentativeAttributeDictionary.add(addRepresentativeAttributeRequest.getAttribute());
        return ResponseEntity.ok().build();
    }
    @PostMapping(value = "/addcombinekey")
    public ResponseEntity<Void> addCombineKey(@RequestBody AddCombineKeyRequest addCombineKeyRequest) throws Exception {
        standardCombineKeyDictionary.add(addCombineKeyRequest.getCombineKey());
        return ResponseEntity.ok().build();
    }
    @PostMapping(value = "/setrepresentativeattribute")
    public ResponseEntity<Void> setRepresentativeAttribute(@RequestBody SetRepresentativeAttributeRequest setRepresentativeAttributeRequest) throws Exception {
        String query = "UPDATE meta_column SET representativeAttribute = " + setRepresentativeAttributeRequest.getRepresentativeAttribute() + " WHERE table_id = " + "'"+setRepresentativeAttributeRequest.getTableId() + "'" + ", name = " + "'"+setRepresentativeAttributeRequest.getColumnName()+ "'";
        mariaConnector.queryExec(query);
        return ResponseEntity.ok().build();
    }
    @PostMapping(value = "/setrepresentativecombinekey")
    public ResponseEntity<Void> setRepresentativeCombineKey(@RequestBody SetRepresentativeCombineKeyRequest setRepresentativeCombineKeyRequest) throws Exception {
        String query = "UPDATE meta_column SET representativeCombineKey = " + setRepresentativeCombineKeyRequest.getRepresentativeCombineKey() + " WHERE table_id = " + "'"+ setRepresentativeCombineKeyRequest.getTableId() + "'" + ", name = " + "'"+setRepresentativeCombineKeyRequest.getColumnName()+ "'";
        mariaConnector.queryExec(query);
        return ResponseEntity.ok().build();
    }
}
