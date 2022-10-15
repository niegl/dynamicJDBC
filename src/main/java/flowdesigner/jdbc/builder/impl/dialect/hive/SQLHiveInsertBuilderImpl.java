package flowdesigner.jdbc.builder.impl.dialect.hive;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import flowdesigner.jdbc.builder.SQLInsertBuilder;
import flowdesigner.jdbc.builder.impl.SQLInsertBuilderImpl;

public class SQLHiveInsertBuilderImpl extends SQLInsertBuilderImpl {
    public SQLHiveInsertBuilderImpl(DbType dbType) {
        super(dbType);
    }

    public SQLHiveInsertBuilderImpl(String sql, DbType dbType) {
        super(sql, dbType);
    }

    @Override
    public SQLInsertBuilder setIfNotExists(boolean ifNotExists) {
        HiveInsertStatement insert = (HiveInsertStatement) getSQLStatement();
        insert.setIfNotExists(ifNotExists);

        return this;
    }

}
