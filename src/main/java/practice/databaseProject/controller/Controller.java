package practice.databaseProject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import practice.databaseProject.csv.CSVHandler;
import practice.databaseProject.dbConnector.DBConnector;
import practice.databaseProject.dictionary.*;
import practice.databaseProject.dto.*;
import practice.databaseProject.editAttribute.EditAttribute;
import practice.databaseProject.entity.*;
import practice.databaseProject.join.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class Controller {

    private final JoinService joinService;
    private final CSVHandler csvReader;
    private final MultipleJoinService multipleJoinService;
    private final DBConnector dbConn;
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
            dbConn.setUp(user, password, host + ":" + port + "/" + database);
        } catch (Exception e) {
            connected = false;
            System.out.println(e);
        }
        DBConnectionResponse dbConnectionResponse = new DBConnectionResponse(connected);
        return ResponseEntity.ok(dbConnectionResponse);
    }
    @PostMapping(value = "/csv", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Boolean> readCSV(MultipartFile file) throws IOException {
        Path path = csvReader.saveFile(file);
        boolean b = false;
        if(path != null) b = csvReader.loadCSV(path);
        return ResponseEntity.ok(path != null && b);
    }

//    @GetMapping(value="/csv")
//    public ResponseEntity<File> exportCSV() throws IOException {
//        File file = csvReader.exportCSV("1_fitness_measurement");
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(file.getName()).build());
//        return new ResponseEntity<Object>(resource, headers, HttpStatus.OK);
//    }

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
    public ResponseEntity<DomainScanResponse> getTableInfo(@RequestBody DomainScanRequest request) {
        String[] tables = request.getTables();
        DomainScanResponse response = new DomainScanResponse();
        TableInfo[] scanResults = new TableInfo[tables.length];
        for(int i = 0; i < tables.length; ++i) {
            int tableId = dbConn.queryTableId(tables[i]);
            SQLResult colInfo = dbConn.queryFor(String.format("SELECT name, type FROM `%s` WHERE table_id = `%s`;", SpecialTable.META_COL, tableId));
            String[] columns = colInfo.getCol(colInfo.getColIndex("name"));
            String[] types = colInfo.getCol(colInfo.getColIndex("type"));
            scanResults[i] = editAttribute.scanTable(tables[i], columns, types);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/editattribute")
    public ResponseEntity<Boolean> updateTableInfo(@RequestBody CastAttributeRequestDto request){
        return ResponseEntity.ok(editAttribute.cast(dbConn.queryTableId(request.getTable()), request.getColumn(), SQLType.valueOf(request.getType())));
    }

    @DeleteMapping(value = "/editattribute")
    public ResponseEntity<Boolean> deleteTableAttribute(@RequestBody DeleteAttributeRequestDto request){
        return ResponseEntity.ok(editAttribute.deleteAttribute(dbConn.queryTableId(request.getTable()), request.getColumn()));
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
        String query = String.format("UPDATE %s SET representativeAttribute = %s WHERE table_id = '%s', name = '%s';",
                SpecialTable.META_COL.toString(), setRepresentativeAttributeRequest.getRepresentativeAttribute(),
                setRepresentativeAttributeRequest.getTableId(), setRepresentativeAttributeRequest.getColumnName()
        );
        dbConn.queryExec(query);
        return ResponseEntity.ok().build();
    }
    @PostMapping(value = "/setrepresentativecombinekey")
    public ResponseEntity<Void> setRepresentativeCombineKey(@RequestBody SetRepresentativeCombineKeyRequest setRepresentativeCombineKeyRequest) throws Exception {
        String query = String.format("UPDATE %s SET representativeCombineKey = %s WHERE table_id = '%s', name = '%s'",
                SpecialTable.META_COL.toString(), setRepresentativeCombineKeyRequest.getRepresentativeCombineKey(),
                setRepresentativeCombineKeyRequest.getTableId(), setRepresentativeCombineKeyRequest.getColumnName()
        );
        dbConn.queryExec(query);
        return ResponseEntity.ok().build();
    }
}
