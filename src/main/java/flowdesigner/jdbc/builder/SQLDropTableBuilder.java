package flowdesigner.jdbc.builder;

public interface SQLDropTableBuilder {
    SQLDropTableBuilder dropTable(String table);

    SQLDropTableBuilder setIfExists(boolean setIfExists);
}
