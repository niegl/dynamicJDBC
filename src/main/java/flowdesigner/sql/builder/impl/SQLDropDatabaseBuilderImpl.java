package flowdesigner.sql.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLDropDatabaseStatement;
import flowdesigner.sql.builder.SQLDropDatabaseBuilder;

public class SQLDropDatabaseBuilderImpl extends SQLBuilderImpl implements SQLDropDatabaseBuilder {

    SQLDropDatabaseStatement stmt;

    public SQLDropDatabaseBuilderImpl(SQLExprBuilder exprBuilder, DbType dbType) {
        super(exprBuilder, dbType);
    }

    public SQLDropDatabaseBuilderImpl(DbType dbType) {
        super(dbType);
    }

    @Override
    public SQLDropDatabaseStatement getSQLStatement() {
        if (stmt == null) {
            stmt = createSQLDropDatabaseStatement();
        }
        return stmt;
    }

    protected SQLDropDatabaseStatement createSQLDropDatabaseStatement() {
        return switch (dbType) {
            default -> new SQLDropDatabaseStatement(dbType);
        };
    }

    @Override
    public void dropDatabaseOrSchema(String database) {
        SQLDropDatabaseStatement statement = getSQLStatement();
        statement.setDatabase(database);
    }

    @Override
    public void setIfExists(boolean setIfExists) {
        SQLDropDatabaseStatement statement = getSQLStatement();
        statement.setIfExists(setIfExists);
    }

    @Override
    public void setRestrict(boolean restrict) {

    }

    @Override
    public void setCascade(boolean cascade) {

    }

    @Override
    public void setPhysical(boolean physical) {

    }

    @Override
    public boolean setServer(String server) {
        return false;
    }
}
