package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import flowdesigner.jdbc.builder.SQLDropTableBuilder;

/**
 * 删除数据库表。
 * 语法：
 * 1、在 MySQL 中，其语法格式为：DROP DATABASE [ IF EXISTS ] <数据库名>
 */
public class SQLDropTableBuilderImpl implements SQLDropTableBuilder {
    private SQLDropTableStatement stmt;
    private DbType dbType;

    public SQLDropTableBuilderImpl(){}
    public SQLDropTableBuilderImpl(DbType dbType){
        this.dbType = dbType;
    }

    @Override
    public SQLDropTableBuilder setType(DbType dbType) {
        this.dbType = dbType;
        return this;
    }

    @Override
    public SQLDropTableBuilder dropTable(String table) {
        SQLDropTableStatement statement = getSQLDropTableStatement();
        statement.setName(new SQLIdentifierExpr(table));
        return this;
    }

    @Override
    public SQLDropTableBuilder setIfExists(boolean setIfExists) {
        SQLDropTableStatement statement = getSQLDropTableStatement();
        if (setIfExists) statement.setIfExists(true);
        return this;
    }

    public SQLDropTableStatement getSQLDropTableStatement() {
        if (stmt == null) {
            stmt = new SQLDropTableStatement(dbType);
        }
        return stmt;
    }

    public String toString() {
        return SQLUtils.toSQLString(stmt, dbType);
    }
}