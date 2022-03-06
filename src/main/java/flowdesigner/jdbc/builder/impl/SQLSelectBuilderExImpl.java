package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.builder.impl.SQLSelectBuilderImpl;
import flowdesigner.jdbc.builder.SQLSelectBuilderEx;

public class SQLSelectBuilderExImpl extends SQLSelectBuilderImpl implements SQLSelectBuilderEx {
    private DbType             dbType;

    public SQLSelectBuilderExImpl(){
        super(null);
    }
    public SQLSelectBuilderExImpl(DbType dbType) {
        super(dbType);
    }

    @Override
    public SQLSelectBuilderEx setType(DbType dbType) {
        this.dbType = dbType;
        return this;
    }

    /**
     * 需要支持多次join。
     * 注意：调用join之前需要先调用from接口。
     * @param joinType 支持：INNER,INNER JOIN,LEFT JOIN,FULL JOIN,CROSS JOIN
     * @param table 表名
     * @param alias 表别名
     * @param conditionLeft on左侧条件
     * @param conditionRight on右侧条件
     * @param conditionOperator 条件操作符,目前支持"="
     * @return
     */
    @Override
    public SQLSelectBuilderEx join(String joinType, String table, String alias,
                                   String conditionLeft, String conditionRight, String conditionOperator) {
        SQLSelectQueryBlock queryBlock = getQueryBlock();
        SQLTableSource from = queryBlock.getFrom();
        SQLJoinTableSource joinTableSource = null;

        if (from instanceof SQLExprTableSource || from instanceof SQLJoinTableSource) {
            joinTableSource = new SQLJoinTableSource();
            joinTableSource.setLeft(from);
        } else if (from instanceof SQLUnionQueryTableSource) {

        } else if (from instanceof SQLValuesTableSource) {

        }

        if (joinTableSource != null) {
            if(joinType.equalsIgnoreCase("INNER")) {
                joinTableSource.setJoinType(SQLJoinTableSource.JoinType.INNER_JOIN);
            } else if (joinType.equalsIgnoreCase("INNER JOIN")) {
                joinTableSource.setJoinType(SQLJoinTableSource.JoinType.INNER_JOIN);
            } else if (joinType.equalsIgnoreCase("LEFT JOIN")) {
                joinTableSource.setJoinType(SQLJoinTableSource.JoinType.LEFT_OUTER_JOIN);
            } else if (joinType.equalsIgnoreCase("RIGHT JOIN")) {
                joinTableSource.setJoinType(SQLJoinTableSource.JoinType.RIGHT_OUTER_JOIN);
            } else if (joinType.equalsIgnoreCase("FULL JOIN")) {
                joinTableSource.setJoinType(SQLJoinTableSource.JoinType.FULL_OUTER_JOIN);
            } else if (joinType.equalsIgnoreCase("CROSS JOIN")) {
                joinTableSource.setJoinType(SQLJoinTableSource.JoinType.CROSS_JOIN);
            }

            SQLExprTableSource right = new SQLExprTableSource(new SQLIdentifierExpr(table), alias);
            joinTableSource.setRight(right);

            if (conditionOperator.equalsIgnoreCase("=")) {
                SQLBinaryOpExpr binaryOpExpr = new SQLBinaryOpExpr(queryBlock.getDbType());
                binaryOpExpr.setLeft(new SQLIdentifierExpr(conditionLeft));
                binaryOpExpr.setRight(new SQLIdentifierExpr(conditionRight));
                binaryOpExpr.setOperator(SQLBinaryOperator.Equality);
                joinTableSource.setCondition(binaryOpExpr);
            }
        }

        queryBlock.setFrom(joinTableSource);

        return this;
    }
}
