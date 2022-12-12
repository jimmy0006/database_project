package practice.databaseProject.csv;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface CSVHandler {

    String[] columnFromCSV(Path path);

    Path saveFile(MultipartFile file);
    boolean loadCSV(Path path);
    Resource exportCSV(String tableName) throws IOException;
    boolean saveAsCSV(String tableName) throws IOException;
}
