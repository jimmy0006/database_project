package practice.databaseProject.domainScanner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practice.databaseProject.dbConnector.DBConnector;

@Service
@RequiredArgsConstructor
public class DomainScannerImpl implements DomainScanner {
    // Pattern for KR/ENG/Digit
    private static final String PAT_NORMAL = "^[ㄱ-ㅎ가-힣0-9a-zA-Z]*$";

    private final DBConnector dbConn;

    @Override
    public DomainScan scan(String table, String[] columns) {
        DomainScan result = new DomainScan();
        for(String col : columns) {
            String[] qRes = dbConn.queryFor(String.format(
                    "SELECT COUNT(*) Total, COUNT(%s) NotNull, COUNT(DISTINCT %s) Distinct,(SELECT COUNT(*) FROM %s WHERE %s RLIKE '%s') Normal, (SELECT COUNT(*) FROM %s WHERE %s = 0) Zero, Min(%s) Min, Max(%s) Max",
                    col, col, table, col, PAT_NORMAL, table, col, col, col)).getRow(0);
            int[] counts = new int[5];
            for(int i = 0; i < counts.length; ++i) {
                counts[i] = Integer.parseInt(qRes[i]);
            }
            result.setColumn(col, counts[0], counts[1], counts[2], counts[3], counts[4], qRes[5], qRes[6]);
        }
        return result;
    }
}
