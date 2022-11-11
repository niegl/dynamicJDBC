package flowdesigner.sql.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.Token;
import com.github.houbb.auto.log.annotation.AutoLog;
import flowdesigner.sql.builder.SQLSelectBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class SQLSelectBuilderImpl extends SQLBuilderImpl implements SQLSelectBuilder {
    protected SQLSelectStatement stmt;

    /**
     * 别名，用于替换输入的全名,包括select部分、where部分
     */
    protected HashMap<String,String> aliasMap = new HashMap<>();

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

    public SQLSelectBuilderImpl(DbType dbType) {
        this(new SQLExprBuilder(dbType), dbType);
    }
    public SQLSelectBuilderImpl(SQLExprBuilder exprBuilder, DbType dbType ) {
        super( exprBuilder, dbType);
        this.stmt = new SQLSelectStatement(dbType);
    }

    public SQLSelectBuilderImpl(String sql, DbType dbType) {
        super(dbType);
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        if (stmtList.isEmpty()) {
            throw new IllegalArgumentException("not support empty-statement :" + sql);
        } else if (stmtList.size() > 1) {
            throw new IllegalArgumentException("not support multi-statement :" + sql);
        } else {
            SQLSelectStatement stmt = (SQLSelectStatement)stmtList.get(0);
            this.stmt = stmt;
        }
    }

    public SQLSelectBuilderImpl(SQLSelectStatement stmt, DbType dbType) {
        super(dbType);
        this.stmt = stmt;
    }

    @Override
    public SQLSelect getSQLSelect() {
        if (stmt.getSelect() == null) {
            stmt.setSelect(createSelect());
        }
        return stmt.getSelect();
    }

    @Override
    public SQLSelectStatement getSQLStatement() {
        if (stmt == null) {
            stmt = new SQLSelectStatement(dbType);
        }
        return stmt;
    }

    /**
     * 返回当前数据库支持的方法。
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
        if (table == null) {
            return this;
        }
        SQLSelectQueryBlock queryBlock = getQueryBlock();

        SQLExpr toSQLExpr = SQLUtils.toSQLExpr(table, dbType);
        SQLExprTableSource from = new SQLExprTableSource(toSQLExpr, alias);
        queryBlock.setFrom(from);

        if (alias != null) {
            updateAlias(toSQLExpr, alias);
            aliasMap.put(table, alias);
        }

        return this;
    }

    /**
     *  设置别名后对where中的条件进行别名的替换
      */
    private void updateAlias(SQLExpr exprTable, String alias) {

        if (alias == null) {
            return;
        }

        String name = getTableName(exprTable);

        SQLSelectQueryBlock queryBlock = getQueryBlock();

        // 更新where部分别名
        SQLExpr where = queryBlock.getWhere();
        if (where instanceof SQLBinaryOpExpr binaryOpExpr) {
            SQLPropertyExpr whereCondition = null;
            do {
                whereCondition = getWhereCondition(binaryOpExpr, name);
                if (whereCondition != null) {
                    whereCondition.setOwner(alias);
                }
            } while (whereCondition != null);

        }

    }

    @Nullable
    private String getTableName(SQLExpr exprTable) {
        String name = null;
        if (exprTable instanceof SQLIdentifierExpr identifierExpr) {
            name = identifierExpr.getName();
        } else if (exprTable instanceof SQLPropertyExpr propertyExpr) {
            name = propertyExpr.getName();
        }
        return name;
    }

    private SQLPropertyExpr getWhereCondition(SQLBinaryOpExpr binaryOpExpr, String name) {
        SQLExpr left = binaryOpExpr.getLeft();
        SQLExpr right = binaryOpExpr.getRight();

        if (left instanceof SQLPropertyExpr propertyExpr) {
            if (propertyExpr.getOwnerName().equals(name)) {
                return propertyExpr;
            }
        }
        if (right instanceof SQLPropertyExpr propertyExpr) {
            if (propertyExpr.getOwnerName().equals(name)) {
                return propertyExpr;
            }
        }

        SQLPropertyExpr expr = null;
        if (left instanceof SQLBinaryOpExpr binaryOpExpr1) {
            expr = getWhereCondition(binaryOpExpr1, name);
            if (expr != null) {
                return expr;
            }
        }
        if (right instanceof SQLBinaryOpExpr binaryOpExpr1) {
            expr = getWhereCondition(binaryOpExpr1, name);
            if (expr != null) {
                return expr;
            }
        }

        return null;
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

        return (SQLSelectQueryBlock) query;
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
     * UNION 操作,支持多次union。
     * @param selectBuilder union后半部分
     * @param unionType union类型：ALL、DISTINCT
     * @return 前半部分对象
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

        // 需要判断是否需要括号将 union后半部分括起来，以免本来属于整个union的order by 分配到union后半部分的
        boolean paren = false;
        if (right instanceof SQLSelectQueryBlock rightQuery) {
            SQLOrderBy orderBy = rightQuery.getOrderBy();
            if (orderBy != null) {
                paren = true;
            }

            SQLLimit limit = rightQuery.getLimit();
            if (limit != null) {
                paren = true;
            }
        } else if (right instanceof SQLUnionQuery rightUnion) {
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
    @AutoLog
    public SQLSelectBuilder join(String joinType, String table, String alias,
                                   String conditionLeft, String conditionRight, String conditionOperator) {

        SQLJoinTableSource joinTableSource = null;
        SQLTableSource from = null;

        if (joinType == null || table == null) {
            return this;
        }

        SQLSelectQueryBlock queryBlock = getQueryBlock();
        if (queryBlock != null) {
            from = queryBlock.getFrom();
        }

        if (from != null) {
            if (from instanceof SQLExprTableSource || from instanceof SQLJoinTableSource) {
                joinTableSource = new SQLJoinTableSource();
                joinTableSource.setLeft(from);
            } else if (from instanceof SQLUnionQueryTableSource) {

            } else if (from instanceof SQLValuesTableSource) {

            }
        }

        if (joinTableSource == null) {
            return this;
        }

        try {
            String joinType1 = joinType.replaceAll(" ", "_").toUpperCase();
            SQLJoinTableSource.JoinType SQLJoinType = SQLJoinTableSource.JoinType.valueOf(joinType1);
            joinTableSource.setJoinType(SQLJoinType);

            SQLExpr exprTable = SQLUtils.toSQLExpr(table, dbType);
            SQLExprTableSource right = new SQLExprTableSource(exprTable, alias);
            joinTableSource.setRight(right);

            if (conditionOperator != null) {
                if (conditionOperator.equalsIgnoreCase("=")) {
                    SQLBinaryOpExpr binaryOpExpr = new SQLBinaryOpExpr(queryBlock.getDbType());
                    binaryOpExpr.setLeft(new SQLIdentifierExpr(conditionLeft));
                    binaryOpExpr.setRight(new SQLIdentifierExpr(conditionRight));
                    binaryOpExpr.setOperator(SQLBinaryOperator.Equality);
                    joinTableSource.setCondition(binaryOpExpr);
                }
            }

            queryBlock.setFrom(joinTableSource);

            updateAlias(exprTable, alias);
        } catch (IllegalArgumentException exception) {
            log.error(exception.toString());
        }

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
     * 添加 A JOIN B on A.a = B.b And 后面部分
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

//    public String toString() {
//        return SQLUtils.toSQLString(stmt, dbType);
//    }

    /**
     * 以下为MYSQL适配接口
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
     * 以下为DB2适配接口
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
