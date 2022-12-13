package practice.databaseProject.csv;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import practice.databaseProject.analyze.AnalyzeResult;
import practice.databaseProject.analyze.TableAnalyzer;
import practice.databaseProject.dbConnector.DBConnector;
import practice.databaseProject.entity.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TableHandler implements CSVHandler {
    private final DBConnector dbConn;
    private final TableAnalyzer analyzer;

    private Path localPathIn = Path.of(".", "csv").toAbsolutePath().normalize();
    private Path localPathOut = Path.of("C:\\Program Files\\MariaDB 10.6\\data\\result\\").toAbsolutePath().normalize();

    @Override
    public Path saveFile(MultipartFile file) {
        try {
            Files.createDirectories(localPathIn);
            Path fileName = Path.of(file.getOriginalFilename()).getFileName();
            if(!file.getOriginalFilename().endsWith(".csv")) return null;    // Will also catch dot-dot attack due to no extension
            Path dest = localPathIn.resolve(fileName);
            if(Files.exists(dest)) return null;    // File already exists
            file.transferTo(dest);
            return dest;
        } catch(Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    @Override
    public String[] columnFromCSV(Path path) {
        try(BufferedReader reader = Files.newBufferedReader(path)) {
            return reader.readLine().split(",");
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    @Override
    public boolean loadCSV(Path path) {
        String fileName = path.getFileName().toString();
        String tableName = fileName.substring(0, fileName.lastIndexOf("."));
        String[] columns = columnFromCSV(path);
        if(columns == null) return false;

        String[] queryComponent = new String[columns.length];

        for (int i = 0; i < queryComponent.length; ++i) {
            queryComponent[i] = columns[i] + " TEXT";
        }
        String createQuery = String.format("CREATE TABLE `%s` (%s);", tableName, String.join(", ", queryComponent));

        String loadQuery = String.join("\n",
                String.format("LOAD DATA LOCAL INFILE '%s'", path.toString()).replace('\\','/'),
                String.format("REPLACE INTO TABLE `%s`", tableName),
                "CHARACTER SET utf8",
                "COLUMNS TERMINATED BY ','",
                "ENCLOSED BY '\"'",
                "LINES TERMINATED BY '\\n'",
                "IGNORE 1 LINES;"
        );

        String registerTableQuery = String.format("INSERT INTO %s(name) VALUES ('%s');", SpecialTable.META_TABLE, tableName);
        if(!dbConn.queryExecAll(createQuery, loadQuery, registerTableQuery)) return false;

        int tId = dbConn.queryTableId(tableName);
        AnalyzeResult analyzeData = analyzer.analyze(tId, Arrays.asList(columns));

        for(int i = 0; i < queryComponent.length; ++i) {
            SQLType type = analyzeData.getType(columns[i]);
            boolean cast = dbConn.queryExec(String.format("ALTER TABLE `%s` MODIFY `%s` %s;", tableName, columns[i], type));
            // (table id, column name, type) -> type = TEXT
            queryComponent[i] = String.format("(%s, '%s', '%s', null, null)", tId, columns[i], cast ? type : SQLType.TEXT);
        }
        String registerColumnsQuery = String.format("INSERT INTO %s VALUES %s", SpecialTable.META_COL, String.join(", ", queryComponent));
        return dbConn.queryExec(registerColumnsQuery);
    }

    public Resource exportCSV(String tableName) throws IOException {
        if(saveAsCSV(tableName)){
            Resource resource = new InputStreamResource(Files.newInputStream(localPathOut.resolve(tableName+".csv")));
            File target = new File(localPathOut.resolve(tableName+".csv").toString());
            target.delete();
            return resource;
        }

       return null;
    }

    public boolean saveAsCSV(String tableName) throws IOException {
        Files.createDirectories(localPathOut);
        Path path = localPathOut.resolve(tableName);
        int id = dbConn.queryTableId(tableName);
        SQLView sqlResult = dbConn.queryFor(String.format("SELECT name FROM %s WHERE table_id='%d'",SpecialTable.META_COL, id));
        List<String> col = sqlResult.getColumn(0).getStrings();
        String columnName = col.stream().map(s -> "'" + s + "'").collect(Collectors.joining(","));
        String exportQuery = String.join("\n",
                String.format("SELECT %s UNION ALL SELECT %s FROM %s",columnName,columnName.replace('\'',' '), tableName),
                String.format("INTO OUTFILE '%s.csv'", path.toString()).replace("\\","/"),
                "character set euckr",
                "FIELDS ENCLOSED BY '\"'",
                "TERMINATED BY ','"
        );
        return dbConn.queryExec(exportQuery);
    }
}
