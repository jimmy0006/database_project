package practice.databaseProject.domainScanner;

public interface DomainScanner {
    DomainScan scan(int tableId, String[] columns);
}
