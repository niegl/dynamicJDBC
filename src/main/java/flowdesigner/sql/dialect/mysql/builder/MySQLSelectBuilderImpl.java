package flowdesigner.sql.dialect.mysql.builder;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import flowdesigner.sql.builder.impl.SQLSelectBuilderImpl;

public class MySQLSelectBuilderImpl extends SQLSelectBuilderImpl {

    static {
        supportMethods.add("setBigResult");
        supportMethods.add("setBufferResult");
        supportMethods.add("setCache");
        supportMethods.add("setCalcFoundRows");
    }

    public MySQLSelectBuilderImpl(DbType dbType) {
        super(dbType);
    }
    public MySQLSelectBuilderImpl(String sql, DbType dbType) {
        super(sql, dbType);
    }
    public MySQLSelectBuilderImpl(SQLSelectStatement stmt, DbType dbType) {
        super(stmt, dbType);
    }

    @Override
    protected MySqlSelectQueryBlock getQueryBlock() {
        return (MySqlSelectQueryBlock) super.getQueryBlock();
    }

    @Override
    public void setBigResult(boolean bigResult) {
        MySqlSelectQueryBlock queryBlock = getQueryBlock();
        queryBlock.setBigResult(bigResult);
    }

    @Override
    public void setBufferResult(boolean bufferResult) {
        MySqlSelectQueryBlock queryBlock = getQueryBlock();
        queryBlock.setBufferResult(bufferResult);
    }

    @Override
    public void setCache(Boolean cache) {
        MySqlSelectQueryBlock queryBlock = getQueryBlock();
        queryBlock.setCache(cache);
    }

    @Override
    public void setCalcFoundRows(boolean calcFoundRows) {
        MySqlSelectQueryBlock queryBlock = getQueryBlock();
        queryBlock.setCalcFoundRows(calcFoundRows);
    }

}
