package practice.databaseProject;

import practice.databaseProject.analyze.AnalyzeResult;
import practice.databaseProject.analyze.TableAnalyzer;
import practice.databaseProject.analyze.TableAnalyzerImpl;
import practice.databaseProject.dbConnector.*;
import practice.databaseProject.editAttribute.EditAttribute;
import practice.databaseProject.entity.SpecialTable;

import java.util.Arrays;
import java.util.List;

public class ResetDB {
    static final List<String> sampleTables = Arrays.asList("1_fitness_measurement",
            "2_physical_instructor_practice_info",
            "3_physical_instructor_writing_info",
            "4_census_income",
            "5_bank_marketing"
    );

    // DB connection setting
    static final String USERNAME = "root";
    static final String PASSWORD = "root";
    static final String DB_SCOPE = "proj";
    static final String IP_ADDR = "127.0.0.1:3306/" + DB_SCOPE;
    // DB connection setting

    public static void main(String[] args) {
        List<String> existingTables = null;

        try(DBConnector dbConn = new MariaConnector()) {
            dbConn.connect(USERNAME, PASSWORD, IP_ADDR);

            // Get tables currently loaded into DB
            existingTables = dbConn.queryFor(String.format(
                    "SELECT TABLE_NAME FROM %s WHERE TABLE_SCHEMA = '%s';", SpecialTable.INFO_TABLE, DB_SCOPE
            )).getColumn(0).getStrings();

            // Re-generate DB
            // dropTables(dbConn, existingTables); // Drop every table
            dropTables(dbConn, SpecialTable.META_TABLE, SpecialTable.META_COL);
        } catch(Exception e) {
            e.printStackTrace(System.err);
        }

        try(DBConnector dbConn = new MariaConnector()) {
            dbConn.setUp(USERNAME, PASSWORD, IP_ADDR);

            // Re-populate
            if(existingTables != null)
                analyzeLoaded(dbConn, existingTables);
            else
                System.err.println("Error...");

            //
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void dropTables(DBConnector dbConn, Object... tables) {
        String[] queries = new String[tables.length + 2];
        queries[0] = "SET FOREIGN_KEY_CHECKS = 0;";
        queries[queries.length - 1] = "SET FOREIGN_KEY_CHECKS = 1;";
        for (int i = 0; i < tables.length; i++) {
            queries[i + 1] = String.format("DROP TABLE IF EXISTS `%s`;", tables[i]);
        }

        dbConn.queryExecAll(queries);
    }

    public static void analyzeLoaded(DBConnector dbConn, List<String> existingTables) {
        EditAttribute attrEditor = new EditAttribute(dbConn);
        TableAnalyzer analyzer = new TableAnalyzerImpl(dbConn, attrEditor);

        for(String table : existingTables) {
            // Safety check
            if(SpecialTable.META_TABLE.toString().equals(table) || SpecialTable.META_COL.toString().equals(table)) continue;

            // Get column names of table
            List<String> columns = dbConn.queryFor(String.format(
                    "SELECT COLUMN_NAME FROM %s WHERE TABLE_NAME = '%s';", SpecialTable.INFO_COL, table
            )).getColumn(0).getStrings();

            // Add to META_TABLE
            dbConn.queryExec(String.format("INSERT INTO %s(name) VALUES ('%s', null);", SpecialTable.META_TABLE, table));

            // Get table ID
            int tId = dbConn.queryTableId(table);

            // Update column info
            AnalyzeResult columnInfo = analyzer.analyze(tId, columns);
            String[] metaColSQL = columns.stream()
                    .map(col -> String.format("(%s, '%s', '%s', null, null)", tId, col, columnInfo.getType(col)))
                    .toArray(String[]::new);
            dbConn.queryExec(String.format("INSERT INTO %s VALUES %s", SpecialTable.META_COL, String.join(", ", metaColSQL)));

        }

    }

}
