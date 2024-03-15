package flowdesigner.sql.dialect.pg.builder;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLDropDatabaseStatement;
import com.alibaba.druid.sql.dialect.oscar.ast.stmt.OscarDropSchemaStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGDropSchemaStatement;
import flowdesigner.sql.builder.SQLDropDatabaseBuilder;
import flowdesigner.sql.builder.impl.SQLBuilderImpl;
import flowdesigner.sql.builder.impl.SQLExprBuilder;

public class PGDropDatabaseBuilderImpl extends SQLBuilderImpl implements SQLDropDatabaseBuilder {

    PGDropSchemaStatement stmt;

    public PGDropDatabaseBuilderImpl(SQLExprBuilder exprBuilder, DbType dbType) {
        super(exprBuilder, dbType);
    }

    public PGDropDatabaseBuilderImpl(DbType dbType) {
        super(dbType);
    }

    @Override
    public PGDropSchemaStatement getSQLStatement() {
        if (stmt == null) {
            stmt = createSQLDropDatabaseStatement();
        }
        return stmt;
    }

    protected PGDropSchemaStatement createSQLDropDatabaseStatement() {
        return new PGDropSchemaStatement();
    }

    @Override
    public void dropDatabaseOrSchema(String database) {
        PGDropSchemaStatement statement = getSQLStatement();
        statement.setSchemaName(new SQLIdentifierExpr(database));
    }

    @Override
    public void setIfExists(boolean setIfExists) {
        PGDropSchemaStatement statement = getSQLStatement();
        statement.setIfExists(setIfExists);
    }

    @Override
    public void setRestrict(boolean restrict) {
        PGDropSchemaStatement statement = getSQLStatement();
        statement.setRestrict(restrict);
    }

    @Override
    public void setCascade(boolean cascade) {
        PGDropSchemaStatement statement = getSQLStatement();
        statement.setCascade(cascade);
    }

    /**
     * 以下不支持
     * @param physical
     */
    @Override
    public void setPhysical(boolean physical) {

    }

    @Override
    public boolean setServer(String server) {
        return false;
    }
}
