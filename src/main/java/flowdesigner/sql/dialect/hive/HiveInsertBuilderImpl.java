package flowdesigner.sql.dialect.hive;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import flowdesigner.sql.builder.SQLInsertBuilder;
import flowdesigner.sql.builder.impl.SQLInsertBuilderImpl;

public class HiveInsertBuilderImpl extends SQLInsertBuilderImpl {
    public HiveInsertBuilderImpl(DbType dbType) {
        super(dbType);
    }

    public HiveInsertBuilderImpl(String sql, DbType dbType) {
        super(sql, dbType);
    }

    @Override
    public SQLInsertBuilder setIfNotExists(boolean ifNotExists) {
        HiveInsertStatement insert = (HiveInsertStatement) getSQLStatement();
        insert.setIfNotExists(ifNotExists);

        return this;
    }

    @Override
    protected HiveInsertStatement createSQLInsertStatement() {
        return new HiveInsertStatement();
    }
}
