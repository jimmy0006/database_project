package practice.databaseProject;

import practice.databaseProject.analyze.AnalyzeResult;
import practice.databaseProject.analyze.TableAnalyzer;
import practice.databaseProject.analyze.TableAnalyzerImpl;
import practice.databaseProject.dbConnector.*;
import practice.databaseProject.editAttribute.EditAttribute;
import practice.databaseProject.entity.SQLResult;
import practice.databaseProject.entity.SQLType;
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

    public static void printSQL(SQLResult res) {
        System.out.println(Arrays.toString(res.getColNames()));
        res.rowStream().map(Arrays::toString).forEach(System.out::println);
    }

    // DB connection setting
    static final String USERNAME = "root";
    static final String PASSWORD = "root";
    static final String DB_SCOPE = "proj";
    static final String IP_ADDR = "127.0.0.1:3306/" + DB_SCOPE;
    // DB connection setting

    public static void main(String[] args) {
        try(DBConnector dbConn = new MariaConnector()) {
            dbConn.getSetting(USERNAME, PASSWORD, IP_ADDR);

            // Get tables currently loaded into DB
            String[] existingTables = dbConn.queryFor(String.format(
                    "SELECT TABLE_NAME FROM %s WHERE TABLE_SCHEMA = '%s';", SpecialTable.INFO_TABLE, DB_SCOPE
            )).getCol(0);

            // Re-generate DB
            // dropTables(dbConn, existingTables); // Drop every table
            dropTables(dbConn, SpecialTable.META_TABLE, SpecialTable.META_COL);
            createMetaInfo(dbConn);

            // Re-populate
            analyzeLoaded(dbConn, existingTables);

            //
        } catch(Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static <T> void dropTables(DBConnector dbConn, T... tables) {
        dbConn.queryExec("SET FOREIGN_KEY_CHECKS = 0;");
        for(T table : tables) {
            dbConn.queryExec(String.format("DROP TABLE IF EXISTS `%s`;", table));
        }
        dbConn.queryExec("SET FOREIGN_KEY_CHECKS = 1;");
    }

    public static void createMetaInfo(DBConnector dbConn) {
        dbConn.queryExec(String.format("CREATE TABLE IF NOT EXISTS `%s` (\n", SpecialTable.META_TABLE) +
                "  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,\n" +
                "  `name` varchar(50) NOT NULL DEFAULT '',\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  UNIQUE KEY `name` (`name`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;"
        );

        dbConn.queryExec(String.format("CREATE TABLE IF NOT EXISTS `%s` (\n", SpecialTable.META_COL) +
                "  `table_id` int(10) unsigned NOT NULL,\n" +
                "  `name` varchar(50) NOT NULL DEFAULT '',\n" +
                "  `type` varchar(50) NOT NULL DEFAULT '',\n" +
                "  `representativeAttribute` varchar(50) DEFAULT NULL,\n" +
                "  `representativeCombineKey` varchar(50) DEFAULT NULL,\n" +
                String.format("FOREIGN KEY (`table_id`) REFERENCES `%s` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,\n", SpecialTable.META_TABLE) +
                "  PRIMARY KEY (`table_id`,`name`),\n" +
                "  KEY `table_id` (`table_id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;"
        );
    }

    public static void analyzeLoaded(DBConnector dbConn, String[] existingTables) {
        EditAttribute attrEditor = new EditAttribute(dbConn);
        TableAnalyzer analyzer = new TableAnalyzerImpl(dbConn, attrEditor);

        for(String table : existingTables) {
            // Safety check
            if(SpecialTable.META_TABLE.toString().equals(table) || SpecialTable.META_COL.toString().equals(table)) continue;

            // Get column names of table
            String[] columns = dbConn.queryFor(String.format(
                    "SELECT COLUMN_NAME FROM %s WHERE TABLE_NAME = '%s';", SpecialTable.INFO_COL, table
            )).getCol(0);

            // Add to META_TABLE
            dbConn.queryExec(String.format("INSERT INTO %s(name) VALUES ('%s');", SpecialTable.META_TABLE.toString(), table));

            // Get table ID
            int tId = dbConn.queryTableId(table);

            // Add to META_COL
            String[] metaColSQL = Arrays.stream(columns)
                    .map(col -> String.format("(%s, '%s', '%s', null, null)", tId, col, SQLType.TEXT.toString()))
                    .toArray(String[]::new);
            dbConn.queryExec(String.format(
                    "INSERT INTO %s VALUES %s", SpecialTable.META_COL, String.join(", ", metaColSQL))
            );

            // Update column info
            AnalyzeResult columnInfo = analyzer.analyze(tId, columns);
            Arrays.stream(columns).forEach(column -> {
                dbConn.queryExec(String.format(
                        "UPDATE `%s` SET type = '%s' WHERE table_id='%s' and name='%s';",
                        SpecialTable.META_COL, columnInfo.getType(column), tId, column
                ));
            });

        }

    }

}
