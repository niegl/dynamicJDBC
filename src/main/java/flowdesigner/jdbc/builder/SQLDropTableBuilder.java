package flowdesigner.jdbc.builder;

import com.alibaba.druid.DbType;

public interface SQLDropTableBuilder {
    SQLDropTableBuilder setType(DbType dbType);

    SQLDropTableBuilder dropTable(String table);

    SQLDropTableBuilder setIfExists(boolean setIfExists);
}
