package flowdesigner.sql.builder;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;

import java.util.List;

/**
 * 将statement的共有接口进行提取
 */
public interface SQLBuilder {
    DbType getDbType();

    SQLBuilder setType(DbType dbType);

    SQLStatement getSQLStatement();

    void addBeforeComment(String var1);
    void addBeforeComment(List<String> var1);
    void addAfterComment(String var1);
    void addAfterComment(List<String> var1);

}
