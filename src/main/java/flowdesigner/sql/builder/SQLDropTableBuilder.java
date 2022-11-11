package flowdesigner.sql.builder;

public interface SQLDropTableBuilder extends SQLBuilder {
//    SQLDropTableBuilder setType(DbType dbType);

    SQLDropTableBuilder dropTable(String table);

    SQLDropTableBuilder setIfExists(boolean setIfExists);
}
