package flowdesigner.sql.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLShowCreateTableStatement;
import flowdesigner.sql.builder.SQLShowCreateTableBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 删除数据库表。
 * 语法：
 * 1、在 MySQL 中，其语法格式为：DROP DATABASE [ IF EXISTS ] <数据库名>
 */
public class SQLShowCreateTableBuilderImpl extends SQLBuilderImpl implements SQLShowCreateTableBuilder {
    private SQLShowCreateTableStatement stmt;;

    public SQLShowCreateTableBuilderImpl(@NotNull DbType dbType){
        super(dbType);
    }
    public SQLShowCreateTableBuilderImpl(SQLExprBuilder exprBuilder, DbType dbType ) {
        super( exprBuilder, dbType);
    }

    public SQLShowCreateTableBuilderImpl(@Nullable SQLShowCreateTableStatement stmt, @NotNull DbType dbType) {
        super(dbType);
        this.stmt = stmt;
    }

    @Override
    public void setName(String table) {
        SQLShowCreateTableStatement statement = getSQLStatement();
        statement.setName(new SQLIdentifierExpr(table));
    }

    public SQLShowCreateTableStatement getSQLStatement() {
        if (stmt == null) {
            stmt = createStatement();
        }
        return stmt;
    }

    protected SQLShowCreateTableStatement createStatement() {
        return new SQLShowCreateTableStatement();
    }

}