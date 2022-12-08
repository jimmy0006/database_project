package practice.databaseProject.readCSV;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import practice.databaseProject.dbConnector.DBConnector;
import practice.databaseProject.dbConnector.MariaConnector;
import practice.databaseProject.entity.SQLResult;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CSVReader {
    private final MariaConnector dbConnector;

    private static final String localPath = "C:/Users/jinmi/OneDrive/바탕 화면/개발자노트/git/database_project/csv/";
    private static final String localPathOut = "C:\\\\Users\\\\jinmi\\\\OneDrive\\\\바탕 화면\\\\개발자노트\\\\git\\\\database_project\\\\csv_output\\\\";

    public boolean DB_saver(String fileName) throws SQLException, ClassNotFoundException {
        dbConnector.setUp("root", "1234", "localhost:3305/test");
        List<String> lists = readCSV(fileName);
        String tableName = fileName.substring(0,fileName.lastIndexOf("."));
        String query = "CREATE TABLE `test`.`" + tableName+"`(";
        for (String s : lists) {
            query+=s+" VARCHAR(100),";
        }
        query = query.substring(0, query.length() - 1)+");";
        dbConnector.queryFor(query);
        return dbConnector.queryExec("LOAD DATA LOCAL INFILE '" + localPath + fileName+"'\n" +
                "REPLACE\n" +
                "INTO TABLE `test`.`"+tableName+"`\n" +
                "CHARACTER SET utf8\n" +
                "COLUMNS TERMINATED BY ','\n" +
                "ENCLOSED BY '\"'\n" +
                "LINES TERMINATED BY '\\n'\n"+
                "IGNORE 1 LINES;"
        );
    }

    public boolean DBtoCSV(String tableName) throws SQLException, ClassNotFoundException {
        dbConnector.setUp("root", "1234", "localhost:3305/test");
        System.out.println(localPathOut);
        dbConnector.queryExec("SELECT * FROM test."+tableName+"\n" +
                "INTO OUTFILE '"+localPathOut+tableName+".csv'\n" +
                "FIELDS ENCLOSED BY '\"'\n" +
                "TERMINATED BY ';'\n" +
                "ESCAPED BY '\"'\n" +
                "LINES TERMINATED BY '\\r\\n';");
        return true;
    }

    //column name만 읽어오는 함수
    public List<String> readCSV(String fileName) {
        List<String> csvList = new ArrayList<String>();
        File csv = new File("./csv/"+fileName);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(csv,Charset.forName("utf-8")));
            String[] lineArr = br.readLine().split(","); // 파일의 한 줄을 ,로 나누어 배열에 저장 후 리스트로 변환한다.
            csvList = Arrays.asList(lineArr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close(); // 사용 후 BufferedReader를 닫아준다.
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        return csvList;
    }

    public boolean saveFile(MultipartFile file) {
        // parent directory를 찾는다.

        try{
            Path directory = Paths.get("C:\\Users\\jinmi\\OneDrive\\바탕 화면\\개발자노트\\git\\database_project\\csv").toAbsolutePath().normalize();

            // directory 해당 경로까지 디렉토리를 모두 만든다.

            Files.createDirectories(directory);

            // 파일명을 바르게 수정한다.
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
            if(!ext.equals("csv")){
                return false;
            }
//        file.transferTo(new File("C:\\Users\\jinmi\\OneDrive\\바탕 화면\\개발자노트\\git\\database_project\\csv\\"+file.getOriginalFilename()));
            // 파일명에 '..' 문자가 들어 있다면 오류를 발생하고 아니라면 진행(해킹및 오류방지)
            Assert.state(!fileName.contains(".."), "Name of file cannot contain '..'");
//        // 파일을 저장할 경로를 Path 객체로 받는다.
            Path targetPath = directory.resolve(fileName).normalize();
//
//        // 파일이 이미 존재하는지 확인하여 존재한다면 오류를 발생하고 없다면 저장한다.
            Assert.state(!Files.exists(targetPath), fileName + " File alerdy exists.");
            file.transferTo(targetPath);
            DB_saver(fileName);
            return true;
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }

    }
}