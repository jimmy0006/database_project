package practice.databaseProject;

public class Test {
    public static void main(String[] args) {
        String tableName = "someTableA";
        String[] columns = {"col1", "col2", "col3", "col4"};

        String createQuery = "CREATE TABLE `" + tableName+"`(";
        for (String s : columns) {
            createQuery+=s+" TEXT,";
        }
        createQuery = createQuery.substring(0, createQuery.length() - 1)+");";

        String[] queryComponent = new String[columns.length];
        for (int i = 0; i < queryComponent.length; i++) {
            queryComponent[i] = columns[i] + " TEXT";
        }
        String createQuery1 = String.format("CREATE TABLE `%s`(%s);", tableName, String.join(",", queryComponent));

        System.out.println(createQuery);
        System.out.println(createQuery1);
    }
}
