package practice.databaseProject.analyze;

public interface TableAnalyzer {
    public AnalyzeResult analyze(String table, String[] columns);
    public boolean update(String table, AnalyzeResult info);
}
