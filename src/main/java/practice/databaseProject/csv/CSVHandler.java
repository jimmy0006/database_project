package practice.databaseProject.csv;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface CSVHandler {
    public void setLocalPath(String localPathIn, String localPathOut);

    public String[] columnFromCSV(Path path);

    public Path saveFile(MultipartFile file);
    public boolean loadCSV(Path path);
    public Path exportCSV(String tableName);
}
