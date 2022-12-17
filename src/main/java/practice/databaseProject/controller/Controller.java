package practice.databaseProject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
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
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class Controller {

    private final JoinService joinService;
    private final CSVHandler csvReader;
    private final MultipleJoinService multipleJoinService;
    private final SingleJoinService singleJoinService;
    private final DBConnector dbConn;
    private final EditAttribute editAttribute;
    private final StandardRepresentativeAttributeDictionary standardRepresentativeAttributeDictionary;
    private final StandardCombineKeyDictionary standardCombineKeyDictionary;

    @PostMapping(value = "/dbconnect")
    public ResponseEntity<DBConnectionResponse> dbConnect(@RequestBody DBConnectionRequest dbConnectionRequest) {
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

    @GetMapping(value="/csv")
    public ResponseEntity<Resource> exportCSV(@ModelAttribute GetCSVfileRequest request) throws IOException {
        Resource resource = csvReader.exportCSV(request.getFilename());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(request.getFilename()+".csv").build());
        headers.add(HttpHeaders.CONTENT_TYPE,"text/csv");
        return new ResponseEntity<>(resource,headers, HttpStatus.OK);
    }

    @PostMapping(value = "/joinonetable")
    public ResponseEntity<Void> joinOneTable(@RequestBody JoineOneTableRequest joineOneTableRequest) throws Exception {
        String table1_name = joineOneTableRequest.getTable1Name();
        String table1_column = joineOneTableRequest.getTable1Column();
        String table2_name = joineOneTableRequest.getTable2Name();
        String table2_column = joineOneTableRequest.getTable2Column();
        String combined_column = joineOneTableRequest.getCombinedColumn();

        joinService.innerJoin(table1_name, table1_column, table2_name, table2_column, combined_column);

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/joinmultipletable")
    public ResponseEntity<Void> joinMultipleTable(@RequestBody JoinMultipleTableRequest joinMultipleTableRequest) throws Exception {
        String table_name = joinMultipleTableRequest.getTableName();
        String table_column = joinMultipleTableRequest.getTableColumn();
        List<String> table_names = joinMultipleTableRequest.getTableNames();
        List<String> table_columns = joinMultipleTableRequest.getTableColumns();
        String combined_column = joinMultipleTableRequest.getCombinedColumn();

        joinService.multipleInnerJoin(table_name, table_column, table_names, table_columns, combined_column);

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/getmultiplejoinedtable")
    public ResponseEntity<GetMultipleJoinedTableResponse> getMultipleJoinedTable() {
        List<JoinResult> joinResults = multipleJoinService.getInfo();
        GetMultipleJoinedTableResponse getMultipleJoinedTableResponse = new GetMultipleJoinedTableResponse(joinResults);
        return ResponseEntity.ok(getMultipleJoinedTableResponse);
    }
    @PostMapping(value = "/getonejoinedtable")
    public ResponseEntity<GetOneJoinedTableResponse> getOneJoinedTable() {
        JoinResult joinResult = singleJoinService.getInfo();
        GetOneJoinedTableResponse getOneJoinedTableResponse = new GetOneJoinedTableResponse(joinResult);
        return ResponseEntity.ok(getOneJoinedTableResponse);
    }

    @GetMapping(value = "/editattribute")
    public ResponseEntity<DomainScanResponse> getTableInfo() {
        int[] tables = dbConn.queryAllTableId();
        DomainScanResponse response = new DomainScanResponse();
        TableInfo[] scanResults = new TableInfo[tables.length];
        for(int i = 0; i < tables.length; ++i) {
            int tableId = tables[i];
            SQLView colInfo = dbConn.queryFor(String.format("SELECT name, type FROM `%s` WHERE table_id = '%s';", SpecialTable.META_COL, tableId));
            List<String> columns = colInfo.getColumn("name").getStrings();
            List<String> types = colInfo.getColumn("type").getStrings();
            scanResults[i] = editAttribute.scanTable(dbConn.getTableName(tables[i]), columns, types);
        }
        response.setTableInfos(scanResults);
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
    @GetMapping(value = "/getrepresentativeattributes")
    public ResponseEntity<Map<String, List<String[]>>> getRepresentativeAttributes() throws Exception {
        return ResponseEntity.ok(standardRepresentativeAttributeDictionary.getAllCategories());
    }
    @PostMapping(value = "/getcombinekeys")
    public ResponseEntity<List<String>> getCombineKeys() throws Exception {
        return ResponseEntity.ok(standardCombineKeyDictionary.values());
    }
    @PostMapping(value = "/addrepresentativeattribute")
    public ResponseEntity<Boolean> addRepresentativeAttribute(@RequestBody AddRepresentativeAttributeRequest addReprAttrReq) throws Exception {
        int tableId = dbConn.queryTableId(addReprAttrReq.getTable());
        return ResponseEntity.ok(standardRepresentativeAttributeDictionary.addToDict(tableId, addReprAttrReq.getColumn(), addReprAttrReq.getAttribute()));
    }
    @PostMapping(value = "/addcombinekey")
    public ResponseEntity<Void> addCombineKey(@RequestBody AddCombineKeyRequest addCombineKeyRequest) throws Exception {
        standardCombineKeyDictionary.add(addCombineKeyRequest.getCombineKey());
        return ResponseEntity.ok().build();
    }
    @PostMapping(value = "/setrepresentativeattribute")
    public ResponseEntity<Void> setRepresentativeAttribute(@RequestBody SetRepresentativeAttributeRequest setRepresentativeAttributeRequest) throws Exception {
        String query = String.format("UPDATE %s SET representativeAttribute = '%s' WHERE name = '%s';",
                SpecialTable.META_TABLE, setRepresentativeAttributeRequest.getColumnName(),
                setRepresentativeAttributeRequest.getTableName()
        );
        dbConn.queryExec(query);
        return ResponseEntity.ok().build();
    }
    @PostMapping(value = "/setrepresentativecombinekey")
    public ResponseEntity<Void> setRepresentativeCombineKey(@RequestBody SetRepresentativeCombineKeyRequest setRepresentativeCombineKeyRequest) throws Exception {
        String query = String.format("UPDATE %s SET representativeCombineKey = %s WHERE table_id = '%s', name = '%s'",
                SpecialTable.META_COL, setRepresentativeCombineKeyRequest.getRepresentativeCombineKey(),
                setRepresentativeCombineKeyRequest.getTableId(), setRepresentativeCombineKeyRequest.getColumnName()
        );
        dbConn.queryExec(query);
        return ResponseEntity.ok().build();
    }
}
