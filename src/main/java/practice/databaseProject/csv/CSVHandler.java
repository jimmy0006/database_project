package practice.databaseProject.csv;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface CSVHandler {

    public String[] columnFromCSV(Path path);

    public Path saveFile(MultipartFile file);
    public boolean loadCSV(Path path);
    public File exportCSV(String tableName) throws IOException;
    public boolean saveAsCSV(String tableName) throws IOException;
}
