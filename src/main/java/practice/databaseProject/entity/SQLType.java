package practice.databaseProject.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SQLType {
    ERROR("ERROR"),
    INTEGER("INTEGER"),
    DOUBLE("DOUBLE"),
    TEXT("TEXT");

    private final String val;

    @Override
    public String toString() {return val;}
}
