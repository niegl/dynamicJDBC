package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import flowdesigner.jdbc.builder.SQLDropTableBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 删除数据库表。
 * 语法：
 * 1、在 MySQL 中，其语法格式为：DROP DATABASE [ IF EXISTS ] <数据库名>
 */
public class SQLDropTableBuilderImpl extends SQLBuilderImpl implements SQLDropTableBuilder {
    private SQLDropTableStatement stmt;

    public SQLDropTableBuilderImpl(@NotNull DbType dbType){
        this(new SQLExprBuilder(dbType), dbType);
    }
    public SQLDropTableBuilderImpl(SQLExprBuilder exprBuilder, DbType dbType ) {
        super( exprBuilder, dbType);
    }

    public SQLDropTableBuilderImpl(String sql, DbType dbType) {
        super(dbType);
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        if (stmtList.isEmpty()) {
            throw new IllegalArgumentException("not support empty-statement :" + sql);
        } else if (stmtList.size() > 1) {
            throw new IllegalArgumentException("not support multi-statement :" + sql);
        } else {
            SQLDropTableStatement stmt = (SQLDropTableStatement)stmtList.get(0);
            this.stmt = stmt;
        }
    }
    public SQLDropTableBuilderImpl(@Nullable SQLDropTableStatement stmt, @NotNull DbType dbType){
        super(dbType);
        this.stmt = stmt;
    }

//    @Override
//    public SQLDropTableBuilder setType(DbType dbType) {
//        this.dbType = dbType;
//        return this;
//    }

    @Override
    public SQLDropTableBuilder dropTable(String table) {
        SQLDropTableStatement statement = getSQLStatement();
        statement.setName(new SQLIdentifierExpr(table));
        return this;
    }

    @Override
    public SQLDropTableBuilder setIfExists(boolean setIfExists) {
        SQLDropTableStatement statement = getSQLStatement();
        if (setIfExists) statement.setIfExists(true);
        return this;
    }

    public SQLDropTableStatement getSQLStatement() {
        if (stmt == null) {
            stmt = createSQLDropTableStatement();
        }
        return stmt;
    }

    protected SQLDropTableStatement createSQLDropTableStatement() {
        return new SQLDropTableStatement(dbType);
    }

//    public String toString() {
//        return SQLUtils.toSQLString(stmt, dbType);
//    }
}