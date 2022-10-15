package flowdesigner.jdbc.builder;

import com.alibaba.druid.DbType;

public interface SQLDropTableBuilder extends SQLBuilder {
//    SQLDropTableBuilder setType(DbType dbType);

    SQLDropTableBuilder dropTable(String table);

    SQLDropTableBuilder setIfExists(boolean setIfExists);
}
