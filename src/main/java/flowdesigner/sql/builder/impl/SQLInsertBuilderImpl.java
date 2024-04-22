package flowdesigner.sql.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import com.alibaba.druid.sql.dialect.oscar.ast.stmt.OscarInsertStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerInsertStatement;
import flowdesigner.sql.builder.SQLInsertBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SQLInsertBuilderImpl extends SQLBuilderImpl implements SQLInsertBuilder {

    protected @Nullable SQLInsertStatement stmt = null;

    public SQLInsertBuilderImpl(@NotNull DbType dbType) {
        this(new SQLExprBuilder(dbType), dbType);
    }
    public SQLInsertBuilderImpl(SQLExprBuilder exprBuilder, DbType dbType ) {
        super( exprBuilder, dbType);
    }
    public SQLInsertBuilderImpl(String sql, @NotNull DbType dbType) {
        super(dbType);
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        if (stmtList.isEmpty()) {
            throw new IllegalArgumentException("not support empty-statement :" + sql);
        } else if (stmtList.size() > 1) {
            throw new IllegalArgumentException("not support multi-statement :" + sql);
        } else {
            SQLInsertStatement stmt = (SQLInsertStatement)stmtList.get(0);
            this.stmt = stmt;
        }
    }
    public SQLInsertBuilderImpl(@Nullable SQLInsertStatement stmt, @NotNull DbType dbType){
        super(dbType);
        this.stmt = stmt;
    }

    /**
     * 设置插入的表名称
     * @param tableName 表名
     */
    @Override
    public SQLInsertBuilder setTableSource(String tableName) {
        SQLInsertStatement statement = getSQLStatement();
        statement.setTableSource(new SQLIdentifierExpr(tableName));
        return this;
    }

    @Override
    public SQLInsertBuilder setInsertColumn(String column) {
        return setInsertColumns(column);
    }

    @Override
    public SQLInsertBuilder setInsertColumns(String... columns) {
        SQLInsertStatement insert = getSQLStatement();
        for (String column : columns) {
            SQLIdentifierExpr expr = new SQLIdentifierExpr(column);
            insert.getColumns().add(expr);
        }
        return this;
    }

    @Override
    public SQLInsertBuilder setOverwrite(boolean overwrite) {
        SQLInsertStatement insert = getSQLStatement();
        insert.setOverwrite(overwrite);
        return this;
    }

    @Override
    public void addValueClause(ArrayList<String> columnValues) {

        SQLInsertStatement statement = getSQLStatement();
        List<SQLInsertStatement.ValuesClause> valueClauseList = statement.getValuesList();

        List valueExprList = new ArrayList(columnValues.size());
        SQLInsertStatement.ValuesClause values = new SQLInsertStatement.ValuesClause(valueExprList, statement);
        for (String column : columnValues) {
            SQLIdentifierExpr expr = new SQLIdentifierExpr(column);
            expr.setParent(values);
            valueExprList.add(expr);
        }

        valueClauseList.add(values);
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
        SQLInsertStatement statement = getSQLStatement();
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
        SQLInsertStatement statement = getSQLStatement();
        if (query != null) {
            query.setParent(statement);
        }
        statement.setQuery(query);

        return this;
    }



    @Override
    public SQLInsertStatement getSQLStatement() {
        if (stmt == null) {
            stmt = createSQLInsertStatement();
            stmt.setDbType(dbType);
        }
        return stmt;
    }

    protected SQLInsertStatement createSQLInsertStatement() {
        return switch (dbType) {
            case oscar -> new OscarInsertStatement();
            case postgresql -> new PGInsertStatement();
            case sqlserver -> new SQLServerInsertStatement();
            case oracle -> new OracleInsertStatement();
            case mysql -> new MySqlInsertStatement();
            case hive -> new HiveInsertStatement();
            default -> new SQLInsertStatement();
        };

    }

}
