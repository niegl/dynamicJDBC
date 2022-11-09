package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import flowdesigner.jdbc.builder.SQLBuilder;

import java.util.List;

public abstract class SQLBuilderImpl implements SQLBuilder {
    protected DbType dbType;
    /**
     * exprBuilder用于expr的生成
     */
    protected SQLExprBuilder     exprBuilder;

    public SQLBuilderImpl(DbType dbType) {
        this.dbType = dbType;
        exprBuilder = new SQLExprBuilder(dbType);
    }
    public SQLBuilderImpl(SQLExprBuilder exprBuilder, DbType dbType ) {
        this.dbType = dbType;
        this.exprBuilder = exprBuilder;
    }

    @Override
    public String toString() {
        return SQLUtils.toSQLString(getSQLStatement(), dbType);
    }

    @Override
    public DbType getDbType() {
        return dbType;
    }

    @Override
    public final SQLBuilder setType(DbType dbType) {
        this.dbType = dbType;
        return this;
    }

    protected SQLExprBuilder getExprBuilder() {
        return exprBuilder;
    }

    @Override
    public void addBeforeComment(String comment) {
        addBeforeComment(List.of(comment));
    }

    @Override
    public void addBeforeComment(List<String> comments) {
        SQLStatement statement = getSQLStatement();
        if (statement == null) {
            return;
        }
        for (String comment : comments) {
            String comment1 = comment;
            if (!comment.startsWith("--")) {
                comment1 = "-- " + comment;
            }
            statement.addBeforeComment(comment1);
        }
    }

    @Override
    public void addAfterComment(String comment) {
        addAfterComment(List.of(comment));
    }

    @Override
    public void addAfterComment(List<String> comments) {
        SQLStatement statement = getSQLStatement();
        if (statement != null) {
            statement.addAfterComment(comments);
        }
    }
}