package flowdesigner.jdbc.builder;

import com.alibaba.druid.DbType;

import java.util.Map;

/**
 * 修改数据库操作.
 * 如mysql语法：ALTER DATABASE test_db
 *     DEFAULT CHARACTER SET gb2312
 *     DEFAULT COLLATE gb2312_chinese_ci;
 */
public interface SQLAlterDatabaseBuilder extends SQLBuilder {
//    SQLAlterDatabaseBuilder setType(DbType dbType);

    SQLAlterDatabaseBuilder setName(String db_name);

    SQLAlterDatabaseBuilder alter(String databaseName, Map<String, String> alterOption);
}
