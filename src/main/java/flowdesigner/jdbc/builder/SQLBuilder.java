package flowdesigner.jdbc.builder;

import com.alibaba.druid.DbType;

/**
 * 将statement的共有接口进行提取
 */
public interface SQLBuilder {
    DbType getDbType();

    SQLBuilder setType(DbType dbType);
}
