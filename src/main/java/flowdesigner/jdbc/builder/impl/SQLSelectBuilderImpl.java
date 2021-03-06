package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.Token;
import flowdesigner.jdbc.builder.SQLSelectBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SQLSelectBuilderImpl implements SQLSelectBuilder {
    private SQLSelectStatement stmt;
    private DbType             dbType;

    protected static List<String> supportMethods = new ArrayList<>();
    static {
        supportMethods.add("select");
        supportMethods.add("selectWithAlias");
        supportMethods.add("from");
        supportMethods.add("orderBy");
        supportMethods.add("groupBy");
        supportMethods.add("having");
        supportMethods.add("into");
        supportMethods.add("where");
        supportMethods.add("whereAnd");
        supportMethods.add("whereOr");
        supportMethods.add("limit");
        supportMethods.add("union");
        supportMethods.add("join");
        supportMethods.add("joinAnd");
        supportMethods.add("joinOr");
        supportMethods.add("join");
    }

    public SQLSelectBuilderImpl(DbType dbType){
        this(new SQLSelectStatement(), dbType);
    }

    public SQLSelectBuilderImpl(String sql, DbType dbType) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        if (stmtList.isEmpty()) {
            throw new IllegalArgumentException("not support empty-statement :" + sql);
        } else if (stmtList.size() > 1) {
            throw new IllegalArgumentException("not support multi-statement :" + sql);
        } else {
            SQLSelectStatement stmt = (SQLSelectStatement)stmtList.get(0);
            this.stmt = stmt;
            this.dbType = dbType;
        }
    }

    public SQLSelectBuilderImpl(SQLSelectStatement stmt, DbType dbType){
        this.stmt = stmt;
        this.dbType = dbType;
    }

    @Override
    public SQLSelect getSQLSelect() {
        if (stmt.getSelect() == null) {
            stmt.setSelect(createSelect());
        }
        return stmt.getSelect();
    }

    @Override
    public SQLSelectStatement getSQLSelectStatement() {
        return stmt;
    }

    /**
     * ???????????????????????????????????????
     * @return
     */
    @Override
    public List<String> getSupportMethods() {
        return supportMethods;
    }

    @Override
    public String getSupportMethodsAsString() {
        return StringUtils.join(supportMethods,",");
    }

    @Override
    public SQLSelectBuilder select(String... columns) {
        SQLSelectQueryBlock queryBlock = getQueryBlock();

        for (String column : columns) {
            SQLSelectItem selectItem = SQLUtils.toSelectItem(column, dbType);
            queryBlock.addSelectItem(selectItem);
        }

        return this;
    }

    @Override
    public SQLSelectBuilder selectWithAlias(String column, String alias) {
        SQLSelectQueryBlock queryBlock = getQueryBlock();

        SQLExpr columnExpr = SQLUtils.toSQLExpr(column, dbType);
        SQLSelectItem selectItem = new SQLSelectItem(columnExpr, alias);
        queryBlock.addSelectItem(selectItem);

        return this;
    }

    @Override
    public SQLSelectBuilder from(String table) {
        return from(table, null);
    }

    @Override
    public SQLSelectBuilder from(String table, String alias) {
        SQLSelectQueryBlock queryBlock = getQueryBlock();
        SQLExprTableSource from = new SQLExprTableSource(new SQLIdentifierExpr(table), alias);
        queryBlock.setFrom(from);

        return this;
    }

    @Override
    public SQLSelectBuilder orderBy(String... columns) {
        SQLSelect select = this.getSQLSelect();

        SQLOrderBy orderBy = select.getOrderBy();
        if (orderBy == null) {
            orderBy = createOrderBy();
            select.setOrderBy(orderBy);
        }

        for (String column : columns) {
            SQLSelectOrderByItem orderByItem = SQLUtils.toOrderByItem(column, dbType);
            orderBy.addItem(orderByItem);
        }

        return this;
    }

    @Override
    public SQLSelectBuilder groupBy(String expr) {
        SQLSelectQueryBlock queryBlock = getQueryBlock();

        SQLSelectGroupByClause groupBy = queryBlock.getGroupBy();
        if (groupBy == null) {
            groupBy = createGroupBy();
            queryBlock.setGroupBy(groupBy);
        }

        SQLExpr exprObj = SQLUtils.toSQLExpr(expr, dbType);
        groupBy.addItem(exprObj);

        return this;
    }

    @Override
    public SQLSelectBuilder having(String expr) {
        SQLSelectQueryBlock queryBlock = getQueryBlock();

        SQLSelectGroupByClause groupBy = queryBlock.getGroupBy();
        if (groupBy == null) {
            groupBy = createGroupBy();
            queryBlock.setGroupBy(groupBy);
        }

        SQLExpr exprObj = SQLUtils.toSQLExpr(expr, dbType);
        groupBy.setHaving(exprObj);

        return this;
    }

    @Override
    public SQLSelectBuilder into(String expr) {
        SQLSelectQueryBlock queryBlock = getQueryBlock();

        SQLExpr exprObj = SQLUtils.toSQLExpr(expr, dbType);
        queryBlock.setInto(exprObj);

        return this;
    }

    @Override
    public SQLSelectBuilder where(String expr) {
        return where(SQLUtils.toSQLExpr(expr, dbType));
    }

    @Override
    public SQLSelectBuilder where(SQLExpr expr) {
        SQLSelectQueryBlock queryBlock = getQueryBlock();
        queryBlock.setWhere(expr);
        return this;
    }

    @Override
    public SQLSelectBuilder whereAnd(String expr) {
        SQLSelectQueryBlock queryBlock = getQueryBlock();
        queryBlock.addWhere(SQLUtils.toSQLExpr(expr, dbType));

        return this;
    }

    @Override
    public SQLSelectBuilder whereOr(String expr) {
        SQLSelectQueryBlock queryBlock = getQueryBlock();

        SQLExpr exprObj = SQLUtils.toSQLExpr(expr, dbType);
        if (exprObj instanceof SQLBinaryOpExpr) {
            ((SQLBinaryOpExpr) exprObj).setParenthesized(true);
        }
        SQLExpr newCondition = SQLUtils.buildCondition(SQLBinaryOperator.BooleanOr, exprObj, false,
                queryBlock.getWhere());

        queryBlock.setWhere(newCondition);

        return this;
    }

    @Override
    public SQLSelectBuilder limit(int rowCount) {
        return limit(rowCount, 0);
    }

    @Override
    public SQLSelectBuilder limit(int rowCount, int offset) {
        getQueryBlock()
                .limit(rowCount, offset);
        return this;
    }

    protected SQLSelectQueryBlock getQueryBlock() {
        SQLSelect select = getSQLSelect();
        SQLSelectQuery query = select.getQuery();
        if (query == null) {
            query = SQLParserUtils.createSelectQueryBlock(dbType);
            select.setQuery(query);
        }

        if (!(query instanceof SQLSelectQueryBlock)) {
            throw new IllegalStateException("not support from, class : " + query.getClass().getName());
        }

        SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
        return queryBlock;
    }

    protected SQLSelect createSelect() {
        return new SQLSelect();
    }

    protected SQLSelectQuery createSelectQueryBlock() {
        return SQLParserUtils.createSelectQueryBlock(dbType);
    }

    protected SQLOrderBy createOrderBy() {
        return new SQLOrderBy();
    }

    protected SQLSelectGroupByClause createGroupBy() {
        return new SQLSelectGroupByClause();
    }

    /**
     * UNION ??????,????????????union???
     * @param selectBuilder union????????????
     * @param unionType union?????????ALL???DISTINCT
     * @return ??????????????????
     */
    @Override
    public SQLSelectBuilder union(SQLSelectBuilder selectBuilder, String unionType) {
        SQLSelectQuery left = getSQLSelect().getQuery();
        SQLSelectQuery right = selectBuilder.getSQLSelect().getQuery();

        if (left == null || right == null) {
            return this;
        }

        SQLUnionQuery unionQuery = new SQLUnionQuery(dbType);
        unionQuery.setLeft(left);

        if ( unionType.equalsIgnoreCase(Token.ALL.name) ) {
            unionQuery.setOperator(SQLUnionOperator.UNION_ALL);
        } else if (unionType.equalsIgnoreCase(Token.DISTINCT.name)) {
            unionQuery.setOperator(SQLUnionOperator.DISTINCT);
        }

        // ????????????????????????????????? union????????????????????????????????????????????????union???order by ?????????union???????????????
        boolean paren = false;
        if (right instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock rightQuery = (SQLSelectQueryBlock) right;
            SQLOrderBy orderBy = rightQuery.getOrderBy();
            if (orderBy != null) {
                paren = true;
            }

            SQLLimit limit = rightQuery.getLimit();
            if (limit != null) {
                paren = true;
            }
        } else if (right instanceof SQLUnionQuery) {
            SQLUnionQuery rightUnion = (SQLUnionQuery) right;
            final SQLOrderBy orderBy = rightUnion.getOrderBy();
            if (orderBy != null) {
                paren = true;
            }

            SQLLimit limit = rightUnion.getLimit();
            if (limit != null) {
                paren = true;
            }
        }

        if (paren) {
            right.setParenthesized(true);
        }

//        boolean paren = lexer.token == Token.LPAREN;
//        SQLSelectQuery right = this.query(paren ? null : union, false);
        unionQuery.setRight(right);
        getSQLSelect().setQuery(unionQuery);

        return this;
    }
    /**
     * ??????????????????join???
     * ???????????????join?????????????????????from?????????
     * @param joinType ?????????INNER,INNER JOIN,LEFT JOIN,FULL JOIN,CROSS JOIN
     * @param table ??????
     * @param alias ?????????
     * @param conditionLeft on????????????
     * @param conditionRight on????????????
     * @param conditionOperator ???????????????,????????????"="
     * @return
     */
    @Override
    public SQLSelectBuilder join(String joinType, String table, String alias,
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

    @Override
    public SQLSelectBuilder joinAnd(String conditionLeft, String conditionRight, String conditionOperator) {
        joinRest(SQLBinaryOperator.BooleanAnd, conditionLeft, conditionRight, conditionOperator);
        return this;
    }

    @Override
    public SQLSelectBuilder joinOr(String conditionLeft, String conditionRight, String conditionOperator) {
        joinRest(SQLBinaryOperator.BooleanOr, conditionLeft, conditionRight, conditionOperator);
        return this;
    }

    /**
     * ?????? A JOIN B on A.a = B.b And ????????????
     * @param AndOr
     * @param conditionLeft
     * @param conditionRight
     * @param conditionOperator
     */
    private void joinRest(SQLBinaryOperator AndOr, String conditionLeft, String conditionRight, String conditionOperator) {
        SQLSelectQueryBlock queryBlock = getQueryBlock();
        SQLTableSource from = queryBlock.getFrom();
        SQLJoinTableSource joinTableSource = null;

        if (from instanceof SQLExprTableSource || from instanceof SQLJoinTableSource) {
            joinTableSource = (SQLJoinTableSource) from;
        }
        if (joinTableSource != null) {
            SQLExpr left = joinTableSource.getCondition();
            SQLBinaryOpExpr right = new SQLBinaryOpExpr(queryBlock.getDbType());
            right.setLeft(new SQLIdentifierExpr(conditionLeft));
            right.setRight(new SQLIdentifierExpr(conditionRight));

            SQLBinaryOperator binaryOperator = null;
            for (SQLBinaryOperator operator: SQLBinaryOperator.values()) {
                if (operator.getName().equalsIgnoreCase(conditionOperator)) {
                    binaryOperator = operator;
                }
            }
            if (binaryOperator == null) return;

            right.setOperator(binaryOperator);
            SQLBinaryOpExpr newCondition = new SQLBinaryOpExpr(left, AndOr, right, dbType);
            joinTableSource.setCondition(newCondition);
        }
    }

    public String toString() {
        return SQLUtils.toSQLString(stmt, dbType);
    }

    /**
     * ?????????MYSQL????????????
     */
    @Override
    public void setBigResult(boolean bigResult) {

    }
    @Override
    public void setBufferResult(boolean bufferResult) {

    }
    @Override
    public void setCache(Boolean cache) {

    }
    @Override
    public void setCalcFoundRows(boolean calcFoundRows) {

    }

    /**
     * ?????????DB2????????????
     */
    @Override
    public void setIsolation(String isolation) {

    }
    @Override
    public void setIsolation(DB2SelectQueryBlock.Isolation isolation) {

    }
    @Override
    public void setForReadOnly(boolean forReadOnly) {

    }
    @Override
    public void setOptimizeFor(SQLExpr optimizeFor) {

    }
}
