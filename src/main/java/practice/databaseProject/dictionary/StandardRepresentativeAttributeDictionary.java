package practice.databaseProject.dictionary;

import org.springframework.stereotype.Service;
import practice.databaseProject.dbConnector.DBConnector;
import practice.databaseProject.entity.SQLView;
import practice.databaseProject.entity.SpecialTable;

import java.util.*;

@Service
public class StandardRepresentativeAttributeDictionary {
    DBConnector dbConn;

//    public void init() {
//        dict = new HashSet<>();
//        dict.add("학업정보");
//        dict.add("금융정보");
//        dict.add("회원정보");
//        dict.add("건강정보");
//    }

//    public List<String> values() {
//        return Arrays.asList(dict.toArray(String[]::new));
//    }
//    public void add(String attribute) {
//        dict.add(attribute);
//    }

    public boolean addToDict(int tableId, String column, String category) {
        String query = String.format("UPDATE `%s` SET representativeAttributeDict = '%s' WHERE table_id = %s AND name = '%s';",
                SpecialTable.META_COL, category, tableId, column);
        return dbConn.queryExec(query);
    }

    /** Return {table1: [[column1, category1], [column2, category2], ...], table2: [[...], ...], ...} */
    public Map<String, List<String[]>> getAllCategories() {
        int[] tables = dbConn.queryAllTableId();
        Map<String, List<String[]>> result = new HashMap<>();
        SQLView queryRet = dbConn.queryFor(String.format("SELECT id, name, representativeAttributeDict AS dict FROM `%s`", SpecialTable.META_COL));
        if(queryRet != null) {
            queryRet.rowStream().forEach(rowView -> {
                String table = dbConn.getTableName(rowView.getInt("id"));
                result.computeIfAbsent(table, k -> new ArrayList<String[]>())
                        .add(new String[]{rowView.getString("name"), rowView.getString("dict")});
            });
        }
        return result;
    }

}
