package practice.databaseProject.domainScanner;

public interface DomainScanner {
    public DomainScan scan(String table, String[] columns);
}
