package practice.databaseProject.analyze;

public interface TableAnalyzer {
    AnalyzeResult analyze(int tableId, Iterable<String> columns);
}
