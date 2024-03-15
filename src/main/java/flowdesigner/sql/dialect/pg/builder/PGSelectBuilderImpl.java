package flowdesigner.sql.dialect.pg.builder;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock;
import flowdesigner.sql.builder.impl.SQLSelectBuilderImpl;

import java.util.List;

public class PGSelectBuilderImpl extends SQLSelectBuilderImpl {
    static {
        supportMethods.add("setDistinctOn");
        supportMethods.add("setFetch");
        supportMethods.add("setForClause");
        supportMethods.add("setIntoOption");
    }

    public PGSelectBuilderImpl(DbType dbType) {
        super(dbType);
    }
    public PGSelectBuilderImpl(String sql, DbType dbType) {
        super(sql, dbType);
    }
    public PGSelectBuilderImpl(SQLSelectStatement stmt, DbType dbType) {
        super(stmt, dbType);
    }

    @Override
    protected PGSelectQueryBlock getQueryBlock() {
        return (PGSelectQueryBlock) super.getQueryBlock();
    }

    /**
     * 以下方法目前暂不暴露给接口
     * @param fetch
     */
    public void setFetch(PGSelectQueryBlock.FetchClause fetch) {
        getQueryBlock().setFetch(fetch);
    }

    public void setForClause(PGSelectQueryBlock.ForClause forClause) {
        getQueryBlock().setForClause(forClause);
    }

    public void setDistinctOn(List<SQLExpr> distinctOn) {
        getQueryBlock().setDistinctOn(distinctOn);
    }

}
