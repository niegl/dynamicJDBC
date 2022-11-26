package flowdesigner.sql.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.parser.Token;
import flowdesigner.sql.SQLUtils;
import flowdesigner.sql.builder.SQLBuilder;

import java.util.List;
import java.util.Map;

import static com.alibaba.druid.sql.SQLUtils.toSQLExpr;

/**
 * 此类对应解析过程中的 SQLStatementParser
 */
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

    protected void buildAssignItems(List<? super SQLAssignItem> items, SQLObject parent, boolean variant,
                                 Map<String, String> targetValues) {

        for (Map.Entry<String,String> entry : targetValues.entrySet()) {
            SQLExpr target = toSQLExpr(entry.getKey(), dbType);
            SQLExpr value = toSQLExpr(entry.getValue(), dbType);

            SQLAssignItem item = this.exprBuilder.buildAssignItem(variant, parent, target, value);
            item.setParent(parent);
            items.add(item);
        }

    }

}
