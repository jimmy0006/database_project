package practice.databaseProject.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SpecialTable {
    META_TABLE("meta_table"),
    META_COL("meta_column"),
    INFO_TABLE("INFORMATION_SCHEMA.TABLES"),
    INFO_COL("INFORMATION_SCHEMA.COLUMNS");

    private final String val;

    @Override
    public String toString() {
        return val;
    }
}
