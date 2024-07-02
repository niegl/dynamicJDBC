package flowdesigner.sql.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLCommentStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import flowdesigner.sql.builder.SQLCommentBuilder;

public class SQLCommentBuilderImpl extends SQLBuilderImpl implements SQLCommentBuilder {

    protected SQLCommentStatement stmt;

    public SQLCommentBuilderImpl(DbType dbType) {
        super(dbType);
    }

    @Override
    public SQLCommentBuilderImpl setComment(String comment) {
        SQLCommentStatement statement = getSQLStatement();
        statement.setComment(SQLUtils.toSQLExpr(comment, dbType));

        return this;
    }

    @Override
    public void setType(SQLCommentStatement.Type type) {
        SQLCommentStatement statement = getSQLStatement();
        statement.setType(type);
    }

    @Override
    public void setType(String onType) {
        SQLCommentStatement.Type type = switch (onType) {
            case "TABLE" -> SQLCommentStatement.Type.TABLE;
            case "VIEW" -> SQLCommentStatement.Type.VIEW;
            case "COLUMN" -> SQLCommentStatement.Type.COLUMN;
            case "INDEX" -> SQLCommentStatement.Type.INDEX;
            default -> null;
        };

        if (type != null) {
            this.setType(type);
        }
    }

    public void setOn(SQLExprTableSource on) {
        if (on != null) {
            on.setParent(stmt);
        }

        SQLCommentStatement statement = getSQLStatement();
        statement.setOn(on);
    }

    @Override
    public void setOn(String on) {
        this.setOn(new SQLExprTableSource((SQLName)(SQLUtils.toSQLExpr(on, dbType))));
    }

    @Override
    public SQLCommentStatement getSQLStatement() {
        if (stmt == null) {
            stmt = new SQLCommentStatement();
        }
        return stmt;
    }

}
