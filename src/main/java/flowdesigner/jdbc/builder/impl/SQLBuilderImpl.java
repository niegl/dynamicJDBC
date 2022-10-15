package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import flowdesigner.jdbc.builder.SQLBuilder;

public abstract class SQLBuilderImpl implements SQLBuilder {
    protected DbType dbType;

    public SQLBuilderImpl(DbType dbType) {
        this.dbType = dbType;
    }

    protected abstract SQLStatement getSQLStatement();

    @Override
    public String toString() {
        return SQLUtils.toSQLString(getSQLStatement(), dbType);
    }

    @Override
    public DbType getDbType() {
        return dbType;
    }

    @Override
    public final SQLBuilder setType(DbType dbType) {
        this.dbType = dbType;
        return this;
    }

}
