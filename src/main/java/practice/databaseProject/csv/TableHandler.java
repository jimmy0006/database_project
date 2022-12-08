package practice.databaseProject.csv;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import practice.databaseProject.dbConnector.DBConnector;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class TableHandler implements CSVHandler {
    private final DBConnector dbConn;

    private Path localPathIn;
    private Path localPathOut;

    @Override
    public void setLocalPath(String localPathIn, String localPathOut) {
        this.localPathIn = Path.of(localPathIn).toAbsolutePath().normalize();
        this.localPathOut = Path.of(localPathOut).toAbsolutePath().normalize();
    }

    @Override
    public boolean saveFile(MultipartFile file) {
        try {
            Files.createDirectories(localPathIn);
            Path fileName = Path.of(file.getOriginalFilename()).getFileName();
            if(!fileName.endsWith(".csv")) return false;    // Will also catch dot-dot attack due to no extension

            Path dest = localPathIn.resolve(fileName);
            if(Files.exists(dest)) return false;    // File already exists
            file.transferTo(dest);
            return true;
        } catch(Exception e) {
            e.printStackTrace(System.err);
            return false;
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
    public boolean loadCSV(String filename) {
        Path path = Path.of(filename);
        String tableName = path.getFileName().toString();
        String[] columns = columnFromCSV(path);
        if(columns == null) return false;

        String createQuery = String.format("CREATE TABLE %s(%s);",
                tableName,
                String.join(", ", Collections.nCopies(columns.length, "TEXT")
        ));
        boolean createRes = dbConn.queryExec(createQuery);
        if(!createRes) return false;

        String loadQuery = String.join("\n",
                String.format("LOAD DATA LOCAL INFILE `%s`", localPathIn.resolve(path)),
                String.format("REPLACE INTO TABLE `%s`", tableName),
                "CHARACTER SET utf8",
                "COLUMNS TERMINATED BY ','",
                "ENCLOSED BY '\"'",
                "LINES TERMINATED BY '\\n'",
                "IGNORE 1 LINES;"
        );
        return dbConn.queryExec(loadQuery);
    }

    @Override
    public boolean exportCSV(String tableName) {
        String exportQuery = String.join("\n",
                String.format("SELECT * FROM %s", tableName),
                String.format("INTO OUTFILE '%s.csv'", localPathOut.resolve(tableName)),
                "FIELDS ENCLOSED BY '\"'",
                "TERMINATED BY ';'",
                "ESCAPED BY '\"'",
                "LINES TERMINATED BY '\\r\\n';"
        );
        return dbConn.queryExec(exportQuery);
    }
}
