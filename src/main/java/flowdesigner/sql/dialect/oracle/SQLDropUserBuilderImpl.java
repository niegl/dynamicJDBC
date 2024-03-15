package flowdesigner.sql.dialect.oracle;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLDropDatabaseStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropUserStatement;
import flowdesigner.sql.builder.SQLDropDatabaseBuilder;
import flowdesigner.sql.builder.impl.SQLBuilderImpl;
import flowdesigner.sql.builder.impl.SQLExprBuilder;

public class SQLDropUserBuilderImpl extends SQLBuilderImpl implements SQLDropDatabaseBuilder {

    SQLDropUserStatement stmt;

    public SQLDropUserBuilderImpl(SQLExprBuilder exprBuilder, DbType dbType) {
        super(exprBuilder, dbType);
    }

    public SQLDropUserBuilderImpl(DbType dbType) {
        super(dbType);
    }

    @Override
    public SQLDropUserStatement getSQLStatement() {
        if (stmt == null) {
            stmt = new SQLDropUserStatement();
        }
        return stmt;
    }

    @Override
    public void dropDatabaseOrSchema(String database) {
        SQLDropUserStatement statement = getSQLStatement();
        statement.addUser(new SQLIdentifierExpr(database));
    }

    /**
     * 此接口Oracle支持，但目前druid未实现
     * @param cascade
     */
    @Override
    public void setCascade(boolean cascade) {
    }

    /**
     * 以下不支持
     * @param setIfExists
     */
    @Override
    public void setIfExists(boolean setIfExists) {

    }

    @Override
    public void setRestrict(boolean restrict) {

    }

    @Override
    public void setPhysical(boolean physical) {

    }

    @Override
    public boolean setServer(String server) {
        return false;
    }
}
