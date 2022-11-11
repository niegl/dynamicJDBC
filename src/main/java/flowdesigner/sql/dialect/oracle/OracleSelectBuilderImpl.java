package flowdesigner.sql.dialect.oracle;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import flowdesigner.sql.builder.SQLSelectBuilder;
import flowdesigner.sql.builder.impl.SQLSelectBuilderImpl;

public class OracleSelectBuilderImpl extends SQLSelectBuilderImpl {

    public OracleSelectBuilderImpl(DbType dbType) {
        super(dbType);
    }
    public OracleSelectBuilderImpl(String sql, DbType dbType) {
        super(sql, dbType);
    }
    public OracleSelectBuilderImpl(SQLSelectStatement stmt, DbType dbType) {
        super(stmt, dbType);
    }

    @Override
    protected OracleSelectQueryBlock getQueryBlock() {
        return (OracleSelectQueryBlock) super.getQueryBlock();
    }

    @Override
    public SQLSelectBuilder from(String tableName) {
        OracleSelectTableReference from;
        if (tableName != null && tableName.length() != 0) {
            from = new OracleSelectTableReference(new SQLIdentifierExpr(tableName));
        } else {
            from = null;
        }

        getQueryBlock().setFrom(from);

        return this;
    }

}
