package flowdesigner.sql.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.parser.Token;
import flowdesigner.sql.SQLUtils;
import flowdesigner.sql.builder.SQLBuilder;
import org.jetbrains.annotations.Nullable;

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

    /**
     * ALTER TABLE table_name DROP CONSTRAINT constraint_name;(对应parseAlterDrop)
     * @param stmt
     * @param constraintName
     */
    public void buildAlterDropConstraint(SQLAlterTableStatement stmt, SQLName constraintName,
                                         Token token) {

        if (stmt == null || constraintName == null) {
            return;
        }

        SQLAlterTableDropConstraint item = new SQLAlterTableDropConstraint();
        item.setConstraintName(constraintName);

        if (token == Token.RESTRICT) {
            item.setRestrict(true);
        } else if (token == Token.CASCADE) {
            item.setCascade(true);
        }

        stmt.addItem(item);
    }

    /**
     * 从表达式中获取特定表名称的Property，即 station.station_id样式的SQL，没有返回null
     * @param methodInvokeExpr 表达式
     * @param name 特定表名称
     * @return
     */
    protected SQLPropertyExpr getProperty(SQLMethodInvokeExpr methodInvokeExpr, String name) {
        SQLExpr expr = null;
        List<SQLExpr> arguments = methodInvokeExpr.getArguments();

        for (SQLExpr argument: arguments) {
            if (argument instanceof SQLPropertyExpr propertyExpr) {
                if (propertyExpr.getOwnerName().equals(name)) {
                    return propertyExpr;
                }
            }
        }
        SQLPropertyExpr propertyExpr = null;
        for (SQLExpr argument: arguments) {
            if (argument instanceof SQLMethodInvokeExpr methodInvokeExpr1) {
                propertyExpr = getProperty(methodInvokeExpr1, name);
                if (propertyExpr != null) {
                    return propertyExpr;
                }
            }
        }

        return null;
    }

    @Nullable
    protected String getTableName(SQLExpr exprTable) {
        String name = null;
        if (exprTable instanceof SQLIdentifierExpr identifierExpr) {
            name = identifierExpr.getName();
        } else if (exprTable instanceof SQLPropertyExpr propertyExpr) {
            name = propertyExpr.getName();
        }
        return name;
    }

}
