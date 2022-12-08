package practice.databaseProject.csv;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface CSVHandler {
    public void setLocalPath(String localPathIn, String localPathOut);

    public String[] columnFromCSV(Path path);

    public boolean saveFile(MultipartFile file);
    public boolean loadCSV(String filename);
    public boolean exportCSV(String tableName);
}
