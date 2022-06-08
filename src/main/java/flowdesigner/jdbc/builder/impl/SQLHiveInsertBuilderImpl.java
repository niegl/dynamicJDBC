package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import flowdesigner.jdbc.builder.SQLInsertBuilder;

public class SQLHiveInsertBuilderImpl extends SQLInsertBuilderImpl{
    public SQLHiveInsertBuilderImpl(DbType dbType) {
        this(null, dbType);
    }

    public SQLHiveInsertBuilderImpl(SQLInsertStatement stmt, DbType dbType) {
        super(stmt, dbType);
    }

    @Override
    public SQLInsertBuilder setIfNotExists(boolean ifNotExists) {
        HiveInsertStatement insert = (HiveInsertStatement) getSQLInsertStatement();
        insert.setIfNotExists(ifNotExists);

        return this;
    }

}
