package practice.databaseProject;

import practice.databaseProject.analyze.AnalyzeResult;
import practice.databaseProject.analyze.TableAnalyzerImpl;
import practice.databaseProject.dbConnector.DBConnector;
import practice.databaseProject.dbConnector.MariaConnector;
import practice.databaseProject.entity.SQLResult;

import java.util.Arrays;

public class Main {
    public static void printSQL(SQLResult res) {
        System.out.println(Arrays.toString(res.getColNames()));
        res.rowStream().map(Arrays::toString).forEach(System.out::println);
    }

    public static void main(String[] args) {
        try(MariaConnector dbConn = new MariaConnector()) {
            dbConn.setUp("root", "root", "127.0.0.1:3306/proj");
            // test2(dbConn);
        } catch(Exception e) {
            e.printStackTrace(System.err);
        }
    }
    /*
    public static void test1(MariaConnector dbConn) {
        printSQL(dbConn.queryFor("SELECT DISTINCT(PRCTTQ_PSEXAM_FLAG_NM) FROM 2_physical_instructor_practice_info;"));
    }

    public static void test2(MariaConnector dbConn) {
        TableAnalyzerImpl analyzer = new TableAnalyzerImpl(dbConn);
        String[] columns = dbConn.queryFor("SELECT column_name FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '3_physical_instructor_writing_info'").getCol(0);
        AnalyzeResult res = analyzer.analyze("3_physical_instructor_writing_info", columns);
        System.out.printf("%20s: %10s,\t%s\t%s\n", "Column Name", "type", "nullable", "unique");
        for(String col : columns) {
            System.out.printf("%20s: %10s,\t%b,\t\t%b\n", col, res.getType(col), res.isNullable(col), res.isDistinct(col));
        }
    }
    */
}
