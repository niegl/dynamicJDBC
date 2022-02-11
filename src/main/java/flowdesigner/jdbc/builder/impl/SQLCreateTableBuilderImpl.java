package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2CreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsCreateTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import flowdesigner.jdbc.builder.SQLCreateTableBuilder;

import java.util.List;

public class SQLCreateTableBuilderImpl implements SQLCreateTableBuilder {

    private SQLCreateTableStatement  stmt;
    private DbType             dbType;

    public SQLCreateTableBuilderImpl(DbType dbType){
        this.dbType = dbType;
    }

    public SQLCreateTableBuilderImpl(String sql, DbType dbType){
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        if (stmtList.size() == 0) {
            throw new IllegalArgumentException("not support empty-statement :" + sql);
        }

        if (stmtList.size() > 1) {
            throw new IllegalArgumentException("not support multi-statement :" + sql);
        }

        SQLCreateTableStatement stmt = (SQLCreateTableStatement) stmtList.get(0);
        this.stmt = stmt;
        this.dbType = dbType;
    }

    public SQLCreateTableBuilderImpl(SQLCreateTableStatement stmt, DbType dbType){
        this.stmt = stmt;
        this.dbType = dbType;
    }


    @Override
    public SQLCreateTableBuilder setName(String name) {
        SQLCreateTableStatement create = getSQLCreateTableStatement();
        create.setTableName(name);
        return this;
    }

    @Override
    public SQLCreateTableBuilder setSchema(String name) {
        SQLCreateTableStatement create = getSQLCreateTableStatement();
        create.setSchema(name);
        return this;
    }

    @Override
    public SQLCreateTableBuilder setIfNotExiists(boolean ifNotExists) {
        SQLCreateTableStatement create = getSQLCreateTableStatement();
        create.setIfNotExiists(ifNotExists);

        return this;
    }


    @Override
    public SQLCreateTableBuilder setBuckets(int buckets) {
        SQLCreateTableStatement create = getSQLCreateTableStatement();
        create.setBuckets(buckets);

        return this;
    }

    @Override
    public SQLCreateTableBuilder setShards(int shards) {
        SQLCreateTableStatement create = getSQLCreateTableStatement();
        create.setShards(shards);

        return this;
    }

    @Override
    public SQLCreateTableBuilder setComment(String comment) {
        SQLCharExpr expr;
        if (comment == null) {
            expr = null;
        } else {
            expr = new SQLCharExpr(comment);
        }
        setComment(expr);

        return this;
    }

    @Override
    public SQLCreateTableBuilder setComment(SQLExpr comment) {
        if (comment == null) {
            throw new IllegalArgumentException();
        }
        SQLCreateTableStatement create = getSQLCreateTableStatement();
        create.setComment(comment);

        return this;
    }

    @Override
    public SQLCreateTableBuilder addColumn(String columnName, String dataType) {

        SQLColumnDefinition column = new SQLColumnDefinition();
        column.setName(columnName);
        column.setDataType(
                SQLParserUtils.createExprParser(dataType, dbType).parseDataType()
        );
        addColumn(column);
        return this;
    }

    @Override
    public SQLCreateTableBuilder addColumn(SQLColumnDefinition column) {
        if (column == null) {
            throw new IllegalArgumentException();
        }
        SQLCreateTableStatement create = getSQLCreateTableStatement();
        create.addColumn(column);

        return this;
    }

    @Override
    public SQLCreateTableBuilder addPartitionColumn(String columnName) {
        SQLColumnDefinition column = new SQLColumnDefinition();
        column.setName(columnName);
        addPartitionColumn(column);

        return this;
    }

    @Override
    public SQLCreateTableBuilder addPartitionColumn(String columnName, String columnComment) {
        SQLColumnDefinition column = new SQLColumnDefinition();
        column.setName(columnName);
        column.setComment(columnComment);
        addPartitionColumn(column);

        return this;
    }

    @Override
    public SQLCreateTableBuilder addPartitionColumn(SQLColumnDefinition column) {
        if (column == null) {
            throw new IllegalArgumentException();
        }
        SQLCreateTableStatement create = getSQLCreateTableStatement();
        create.addPartitionColumn(column);

        return this;
    }

    @Override
    public SQLCreateTableBuilder addOption(String name, String value) {
        SQLCharExpr expr = new SQLCharExpr(value);
        addOption(name,expr);

        return this;
    }

    @Override
    public SQLCreateTableBuilder addOption(String name, SQLExpr value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        SQLCreateTableStatement create = getSQLCreateTableStatement();
        create.addOption(name,value);

        return this;
    }

    public SQLCreateTableStatement getSQLCreateTableStatement() {
        if (stmt == null) {
            stmt = createSQLCreateTableStatement();
        }
        return stmt;
    }

    public SQLCreateTableStatement createSQLCreateTableStatement() {
        switch (dbType) {
            case oracle:
                return new OracleCreateTableStatement();
            case mysql:
                return new MySqlCreateTableStatement();
            case db2:
                return new DB2CreateTableStatement();
            case hive:
                return new HiveCreateTableStatement();
            case odps:
                return new OdpsCreateTableStatement();
            default:
                return new SQLCreateTableStatement();
        }
    }

    public String toString() {
        return SQLUtils.toSQLString(stmt, dbType);
    }
}
