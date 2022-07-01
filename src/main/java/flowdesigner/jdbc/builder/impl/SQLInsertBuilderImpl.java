package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerInsertStatement;
import flowdesigner.jdbc.builder.SQLInsertBuilder;
import org.jetbrains.annotations.Nullable;

public class SQLInsertBuilderImpl implements SQLInsertBuilder {

    private SQLInsertStatement stmt = null;
    private DbType dbType;

    public SQLInsertBuilderImpl(DbType dbType){
        this.dbType = dbType;
    }

    public SQLInsertBuilderImpl(SQLInsertStatement stmt, DbType dbType){
        this.stmt = stmt;
        this.dbType = dbType;
    }

    /**
     * 设置插入的表名称
     * @param tableName 表名
     */
    @Override
    public SQLInsertBuilder setTableSource(String tableName) {
        SQLInsertStatement statement = getSQLInsertStatement();
        statement.setTableSource(new SQLIdentifierExpr(tableName));
        return this;
    }

    @Override
    public SQLInsertBuilder setInsertColumn(String column) {
        return setInsertColumns(column);
    }

    @Override
    public SQLInsertBuilder setInsertColumns(String... columns) {
        SQLInsertStatement insert = getSQLInsertStatement();
        for (String column : columns) {
            SQLIdentifierExpr expr = new SQLIdentifierExpr(column);
            insert.getColumns().add(expr);
        }
        return this;
    }

    /**
     * 非通用接口：子类实现
     * @param ifNotExists
     * @return
     */
    @Override
    public SQLInsertBuilder setIfNotExists(boolean ifNotExists) {
        return this;
    }

    /**
     * 添加分区
     * @param target 分区名称
     * @param value 分区值，可为null(即动态分区)
     */
    @Override
    public SQLInsertBuilder addPartition(String target, @Nullable String value) {
        SQLAssignItem ptExpr = new SQLAssignItem();
        ptExpr.setTarget(new SQLIdentifierExpr(target));
        if (value != null && !value.isEmpty()) {
            ptExpr.setValue(new SQLCharExpr(value));
        }

        addPartition(ptExpr);

        return this;
    }

    @Override
    public SQLInsertBuilder addPartition(SQLAssignItem partition) {
        SQLInsertStatement statement = getSQLInsertStatement();
        statement.addPartition(partition);
        return this;
    }

    @Override
    public SQLInsertBuilder setQuery(String query) {
        SQLStatement statement = SQLUtils.parseSingleStatement(query, this.dbType, true);
        if (statement instanceof SQLSelectStatement ) {
            SQLSelectStatement selectStatement = (SQLSelectStatement) statement;
            setQuery(selectStatement.getSelect());
        }

        return this;
    }

    @Override
    public SQLInsertBuilder setQuery(SQLSelectQuery query) {
        this.setQuery(new SQLSelect(query));
        return this;
    }

    @Override
    public SQLInsertBuilder setQuery(SQLSelect query) {
        SQLInsertStatement statement = getSQLInsertStatement();
        if (query != null) {
            query.setParent(statement);
        }
        statement.setQuery(query);

        return this;
    }

    @Override
    public SQLInsertStatement getSQLInsertStatement() {
        if (stmt == null) {
            stmt = createSQLInsertStatement();
            stmt.setDbType(dbType);
        }
        return stmt;
    }

    private SQLInsertStatement createSQLInsertStatement() {
        switch (dbType) {
            case postgresql:
                return new PGInsertStatement();
            case sqlserver:
                return new SQLServerInsertStatement();
            case oracle:
                return new OracleInsertStatement();
            case mysql:
                return new MySqlInsertStatement();
            case hive:
                return new HiveInsertStatement();
        }

        return new SQLInsertStatement();
    }

    public String toString() {
        return SQLUtils.toSQLString(stmt, dbType);
    }

}
