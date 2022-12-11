package practice.databaseProject.analyze;

public interface TableAnalyzer {
    public AnalyzeResult analyze(int tableId, String[] columns);
    public boolean update(int tableId, AnalyzeResult info);
}
