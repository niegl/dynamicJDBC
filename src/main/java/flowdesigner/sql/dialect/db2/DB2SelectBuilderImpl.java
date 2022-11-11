package flowdesigner.sql.dialect.db2;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import flowdesigner.sql.builder.impl.SQLSelectBuilderImpl;

public class DB2SelectBuilderImpl extends SQLSelectBuilderImpl {
    static {
        supportMethods.add("setIsolation");
        supportMethods.add("setForReadOnly");
        supportMethods.add("setOptimizeFor");
    }

    public DB2SelectBuilderImpl(DbType dbType) {
        super(dbType);
    }
    public DB2SelectBuilderImpl(String sql, DbType dbType) {
        super(sql, dbType);
    }
    public DB2SelectBuilderImpl(SQLSelectStatement stmt, DbType dbType) {
        super(stmt, dbType);
    }

    @Override
    protected DB2SelectQueryBlock getQueryBlock() {
        return (DB2SelectQueryBlock) super.getQueryBlock();
    }

    @Override
    public void setIsolation(String isolation) {
        setIsolation(DB2SelectQueryBlock.Isolation.valueOf(isolation));
    }
    @Override
    public void setIsolation(DB2SelectQueryBlock.Isolation isolation) {
        getQueryBlock().setIsolation(isolation);
    }
    @Override
    public void setForReadOnly(boolean forReadOnly) {
        getQueryBlock().setForReadOnly(forReadOnly);
    }

    @Override
    public void setOptimizeFor(SQLExpr optimizeFor) {
        getQueryBlock().setOptimizeFor(optimizeFor);
    }


}
