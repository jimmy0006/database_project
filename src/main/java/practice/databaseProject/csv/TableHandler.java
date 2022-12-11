package practice.databaseProject.csv;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import practice.databaseProject.dbConnector.DBConnector;
import practice.databaseProject.entity.SQLType;
import practice.databaseProject.entity.SpecialTable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class TableHandler implements CSVHandler {
    private final DBConnector dbConn;

    private Path localPathIn = Path.of(".\\csv\\").toAbsolutePath().normalize();
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
        if(!dbConn.queryExec(createQuery)) return false;

        String loadQuery = String.join("\n",
                String.format("LOAD DATA LOCAL INFILE '%s'", path.toString()).replace('\\','/'),
                String.format("REPLACE INTO TABLE `%s`", tableName),
                "CHARACTER SET utf8",
                "COLUMNS TERMINATED BY ','",
                "ENCLOSED BY '\"'",
                "LINES TERMINATED BY '\\n'",
                "IGNORE 1 LINES;"
        );
        if(!dbConn.queryExec(loadQuery)) return false;

        String registerTableQuery = String.format("INSERT INTO %s(name) VALUES ('%s');", SpecialTable.META_TABLE.toString(), tableName);
        if(!dbConn.queryExec(registerTableQuery)) return false;

        for(int i = 0; i < queryComponent.length; ++i) {
            // (table id, column name, type) -> type = TEXT
            queryComponent[i] = String.format("(%s, '%s', '%s', null, null)", dbConn.queryTableId(tableName), columns[i], SQLType.TEXT.toString());
        }
        String registerColumnsQuery = String.format("INSERT INTO %s VALUES %s", SpecialTable.META_COL, String.join(", ", queryComponent));
        return dbConn.queryExec(registerColumnsQuery);
    }

    public File exportCSV(String tableName) throws IOException {
       if(saveAsCSV(tableName)) {
           return new File(localPathOut.resolve(tableName+".csv").toString());
       }
       return null;
    }

    public boolean saveAsCSV(String tableName) throws IOException {
        Files.createDirectories(localPathOut);
        Path path = localPathOut.resolve(tableName);
        String exportQuery = String.join("\n",
                String.format("SELECT * FROM %s", tableName),
                String.format("INTO OUTFILE '%s.csv'", path.toString()).replace("\\","/"),
                "FIELDS ENCLOSED BY '\"'",
                "TERMINATED BY ';'",
                "ESCAPED BY '\"'",
                "LINES TERMINATED BY '\\r\\n';"
        );
        return dbConn.queryExec(exportQuery);
    }
}
