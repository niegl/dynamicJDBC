package flowdesigner.sql.dialect.oscar.builder;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLDropDatabaseStatement;
import com.alibaba.druid.sql.dialect.oscar.ast.stmt.OscarDropSchemaStatement;
import flowdesigner.sql.builder.SQLDropDatabaseBuilder;
import flowdesigner.sql.builder.impl.SQLBuilderImpl;
import flowdesigner.sql.builder.impl.SQLExprBuilder;

public class OscarDropDatabaseBuilderImpl extends SQLBuilderImpl implements SQLDropDatabaseBuilder {

    OscarDropSchemaStatement stmt;

    public OscarDropDatabaseBuilderImpl(SQLExprBuilder exprBuilder, DbType dbType) {
        super(exprBuilder, dbType);
    }

    public OscarDropDatabaseBuilderImpl(DbType dbType) {
        super(dbType);
    }

    @Override
    public OscarDropSchemaStatement getSQLStatement() {
        if (stmt == null) {
            stmt = createSQLDropDatabaseStatement();
        }
        return stmt;
    }

    protected OscarDropSchemaStatement createSQLDropDatabaseStatement() {
        return new OscarDropSchemaStatement();
    }

    @Override
    public void dropDatabaseOrSchema(String database) {
        OscarDropSchemaStatement statement = getSQLStatement();
        statement.setSchemaName(new SQLIdentifierExpr(database));
    }

    @Override
    public void setIfExists(boolean setIfExists) {
        OscarDropSchemaStatement statement = getSQLStatement();
        statement.setIfExists(setIfExists);
    }

    @Override
    public void setRestrict(boolean restrict) {
        OscarDropSchemaStatement statement = getSQLStatement();
        statement.setRestrict(restrict);
    }

    @Override
    public void setCascade(boolean cascade) {
        OscarDropSchemaStatement statement = getSQLStatement();
        statement.setCascade(cascade);
    }

    /**
     * 以下方法不支持
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
