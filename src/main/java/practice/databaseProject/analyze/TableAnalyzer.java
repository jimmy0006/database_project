package practice.databaseProject.analyze;

public interface TableAnalyzer {
    AnalyzeResult analyze(int tableId, String[] columns);
    boolean update(int tableId, AnalyzeResult info);
}
