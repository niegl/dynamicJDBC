package flowdesigner.sql.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.dialect.oscar.ast.stmt.OscarSelectStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectStatement;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.Token;
import flowdesigner.sql.builder.SQLSelectBuilder;
import flowdesigner.util.raw.kit.StringKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class SQLSelectBuilderImpl extends SQLBuilderImpl implements SQLSelectBuilder {
    protected SQLSelectStatement stmt = null;

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

    protected static HashMap<String,SQLJoinTableSource.JoinType> join_type_rel = new HashMap<String,SQLJoinTableSource.JoinType>();
    static {
        join_type_rel.put("INNER JOIN", SQLJoinTableSource.JoinType.INNER_JOIN);
        join_type_rel.put("COMMA", SQLJoinTableSource.JoinType.COMMA);
        join_type_rel.put("CROSS JOIN", SQLJoinTableSource.JoinType.CROSS_JOIN);
        join_type_rel.put("LEFT JOIN", SQLJoinTableSource.JoinType.LEFT_OUTER_JOIN);
        join_type_rel.put("RIGHT JOIN", SQLJoinTableSource.JoinType.RIGHT_OUTER_JOIN);
        join_type_rel.put("FULL JOIN", SQLJoinTableSource.JoinType.FULL_OUTER_JOIN);
    }

    public SQLSelectBuilderImpl(DbType dbType) {
        this(new SQLExprBuilder(dbType), dbType);
    }
    public SQLSelectBuilderImpl(SQLExprBuilder exprBuilder, DbType dbType ) {
        super( exprBuilder, dbType);
    }

    public SQLSelectBuilderImpl(String sql, DbType dbType) {
        super(dbType);
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        if (stmtList.isEmpty()) {
            throw new IllegalArgumentException("not support empty-statement :" + sql);
        } else if (stmtList.size() > 1) {
            throw new IllegalArgumentException("not support multi-statement :" + sql);
        } else {
            this.stmt = (SQLSelectStatement)stmtList.get(0);
        }
    }

    public SQLSelectBuilderImpl(SQLSelectStatement stmt, DbType dbType) {
        super(dbType);
        this.stmt = stmt;
    }

    @Override
    public SQLSelect getSQLSelect() {
        stmt = getSQLStatement();

        if (stmt.getSelect() == null) {
            stmt.setSelect(createSelect());
        }
        return stmt.getSelect();
    }

    @Override
    public SQLSelectStatement getSQLStatement() {
        if (stmt == null) {
            stmt = createSelectStatement(dbType);
        }
        return stmt;
    }

    protected SQLSelectStatement createSelectStatement(DbType dbType) {
        return switch (dbType) {
            case oscar -> new OscarSelectStatement();
            case postgresql -> new PGSelectStatement();
            case presto -> new PrestoSelectStatement();
            default -> new SQLSelectStatement(dbType);
        };
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

        SQLExpr toSQLExpr = SQLUtils.toSQLExpr(table, dbType);

        return from(toSQLExpr, alias);
    }

    /**
     * 支持 SQLExpr 为 SQLExprTableSource、SQLJoinTableSource、SQLUnionQueryTableSource、SQLValuesTableSource
     * @param expr SQLExprTableSource、SQLJoinTableSource、SQLUnionQueryTableSource、SQLValuesTableSource
     * @param alias
     * @return
     */
    @Override
    public SQLSelectBuilder from(SQLExpr expr, String alias) {
        if (expr == null) {
            return this;
        }

        SQLSelectQueryBlock queryBlock = getQueryBlock();
        SQLExprTableSource from = new SQLExprTableSource(expr, alias);
        queryBlock.setFrom(from);

        if (!StringKit.isBlank(alias)) {
            updateAlias(expr, alias);
        }

        return this;
    }

    /**
     * select distinct
     * @return
     */
    @Override
    public SQLSelectBuilder setDistionOption() {
        SQLSelectQueryBlock queryBlock = getQueryBlock();
        boolean distinct = queryBlock.isDistinct();
        if (!distinct) {
            queryBlock.setDistinct();
        } else {
            queryBlock.setDistionOption(0);
        }

        return this;
    }

    @Override
    public void updateAlias(String table, String alias) {
        if (table == null) {
            return;
        }

        SQLExpr toSQLExpr = SQLUtils.toSQLExpr(table, dbType);
        updateAlias(toSQLExpr, alias);
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

        // 查找 stn.merged_ind IN (1,3) 中的 stn.merged_ind
        if (left instanceof SQLInListExpr inListExpr) {
            SQLExpr expr = inListExpr.getExpr();
            List<SQLExpr> targetList = inListExpr.getTargetList();

            if (expr instanceof SQLPropertyExpr propertyExpr) {
                if (propertyExpr.getOwnerName().equals(name)) {
                    return propertyExpr;
                }
            }
            for (SQLExpr target: targetList) {
                if (target instanceof SQLPropertyExpr propertyExpr) {
                    if (propertyExpr.getOwnerName().equals(name)) {
                        return propertyExpr;
                    }
                }
            }

            SQLPropertyExpr propertyExpr = null;
            if (expr instanceof SQLBinaryOpExpr binaryOpExpr1) {
                propertyExpr = getWhereCondition(binaryOpExpr1, name);
                if (propertyExpr != null) {
                    return propertyExpr;
                }
            }
            for (SQLExpr target: targetList) {
                if (target instanceof SQLBinaryOpExpr binaryOpExpr1) {
                    propertyExpr = getWhereCondition(binaryOpExpr1, name);
                    if (propertyExpr != null) {
                        return propertyExpr;
                    }
                }
            }
        }

        if (right instanceof SQLInListExpr inListExpr) {
            SQLExpr expr = inListExpr.getExpr();
            List<SQLExpr> targetList = inListExpr.getTargetList();

            if (expr instanceof SQLPropertyExpr propertyExpr) {
                if (propertyExpr.getOwnerName().equals(name)) {
                    return propertyExpr;
                }
            }
            for (SQLExpr target: targetList) {
                if (target instanceof SQLPropertyExpr propertyExpr) {
                    if (propertyExpr.getOwnerName().equals(name)) {
                        return propertyExpr;
                    }
                }
            }

            SQLPropertyExpr propertyExpr = null;
            if (expr instanceof SQLBinaryOpExpr binaryOpExpr1) {
                propertyExpr = getWhereCondition(binaryOpExpr1, name);
                if (propertyExpr != null) {
                    return propertyExpr;
                }
            }
            for (SQLExpr target: targetList) {
                if (target instanceof SQLBinaryOpExpr binaryOpExpr1) {
                    propertyExpr = getWhereCondition(binaryOpExpr1, name);
                    if (propertyExpr != null) {
                        return propertyExpr;
                    }
                }
            }
        }

        // 查找 函数中的 SQLPropertyExpr
        if (left instanceof SQLMethodInvokeExpr methodInvokeExpr) {
            SQLPropertyExpr propertyExpr = getProperty(methodInvokeExpr, name);
            if (propertyExpr != null) {
                return propertyExpr;
            }
        }
        if (right instanceof SQLMethodInvokeExpr methodInvokeExpr) {
            SQLPropertyExpr propertyExpr = getProperty(methodInvokeExpr, name);
            if (propertyExpr != null) {
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

    /**
     * 添加with Subquery 语句,支持 with查询语法: [WITH CommonTableExpression (, CommonTableExpression)*]
     * SELECT [ALL | DISTINCT] select_expr, select_expr, ...
     * @param alias Subquery别名，不能为空
     * @param select CommonTableExpression（select语句）
     * @return
     */
    @Override
    public SQLSelectBuilder addWithSubqueryClause(String alias, String select) {
        SQLSelect subQuery = null;

        if (alias == null || select == null) {
            throw new IllegalArgumentException("parameter alias||select should not be null");
        }

        SQLExpr expr = SQLUtils.toSQLExpr(select, dbType);
        if (expr instanceof SQLQueryExpr queryExpr) {
            subQuery = queryExpr.getSubQuery();
        }

        if (subQuery != null) {
            addWithSubqueryClause(alias, subQuery);
        }

        return this;
    }

    @Override
    public SQLSelectBuilder addWithSubqueryClause(String alias, SQLSelect select) {
        SQLWithSubqueryClause withSubQuery = getWithSubQuery();

        if (alias == null || select == null) {
            throw new IllegalArgumentException("parameter alias||select should not be null");
        }

        withSubQuery.addEntry(new SQLWithSubqueryClause.Entry(alias, select));

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
        SQLSelectQuery query = getQuery();

        if (!(query instanceof SQLSelectQueryBlock)) {
            throw new IllegalStateException("not support from, class : " + query.getClass().getName());
        }

        return (SQLSelectQueryBlock) query;
    }

    /**
     * select.setQuery(query)的query类型可能为：SelectQueryBlock或SQLUnionQuery。<p>
     *     （1）第一次调用getQueryBlock默认生成的 是SelectQueryBlock；<p>
     *     （2）在调用 union接口后会通过select.setQuery(SQLUnionQuery)设置为SQLUnionQuery，;
     * @return SelectQueryBlock或SQLUnionQuery
     */
    private SQLSelectQuery getQuery() {
        SQLSelect select = getSQLSelect();
        SQLSelectQuery query = select.getQuery();
        if (query == null) {
            query = createSelectQueryBlock();
            select.setQuery(query);
        }
        return query;
    }

    private SQLWithSubqueryClause getWithSubQuery() {
        SQLSelect select = getSQLSelect();
        SQLWithSubqueryClause withSubQuery = select.getWithSubQuery();
        if (withSubQuery == null) {
            withSubQuery = createWithSubqueryClause();
            select.setWithSubQuery(withSubQuery);
        }
        return withSubQuery;
    }

    protected SQLSelect createSelect() {
        return new SQLSelect();
    }

    protected SQLSelectQuery createSelectQueryBlock() {
        return SQLParserUtils.createSelectQueryBlock(dbType);
    }
    protected SQLWithSubqueryClause createWithSubqueryClause() {
        return new SQLWithSubqueryClause();
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
    public SQLSelectBuilder join(String joinType, String table, String alias,
                                   String conditionLeft, String conditionRight, String conditionOperator) {

        SQLJoinTableSource joinTableSource = null;
        SQLTableSource from = null;

        if (joinType == null || table == null) {
            return this;
        }

        // 考虑SelectQuery为 SQLSelectQueryBlock 和 SQLUnionQuery的情况
        SQLSelectQuery query = getQuery();
        if (query instanceof SQLUnionQuery unionQuery) {
            from = new SQLUnionQueryTableSource(unionQuery);
            SQLSelectQuery selectQueryBlock = createSelectQueryBlock();
            getSQLSelect().setQuery(selectQueryBlock);
        } else if (query instanceof SQLSelectQueryBlock selectQueryBlock) {
            from = selectQueryBlock.getFrom();
        }

        if (from != null) {
            if (from instanceof SQLExprTableSource
                    || from instanceof SQLJoinTableSource
                    || from instanceof SQLUnionQueryTableSource
                    || from instanceof SQLValuesTableSource) {
                joinTableSource = new SQLJoinTableSource();
                joinTableSource.setLeft(from);
            }
        }

        if (joinTableSource == null) {
            return this;
        }

        String upperCase_join = joinType.toUpperCase();
        boolean b = join_type_rel.containsKey(upperCase_join);
        if (!b) {
            return this;
        }

        SQLJoinTableSource.JoinType SQLJoinType = join_type_rel.get(upperCase_join);
        joinTableSource.setJoinType(SQLJoinType);

        SQLExpr exprTable = SQLUtils.toSQLExpr(table, dbType);
        SQLExprTableSource right = new SQLExprTableSource(exprTable, alias);
        joinTableSource.setRight(right);

        if (conditionOperator != null) {
            if (conditionOperator.equalsIgnoreCase("=")) {
                SQLBinaryOpExpr binaryOpExpr = new SQLBinaryOpExpr(dbType);
                binaryOpExpr.setLeft(new SQLIdentifierExpr(conditionLeft));
                binaryOpExpr.setRight(new SQLIdentifierExpr(conditionRight));
                binaryOpExpr.setOperator(SQLBinaryOperator.Equality);
                joinTableSource.setCondition(binaryOpExpr);
            }
        }

        SQLSelectQueryBlock queryBlock = getQueryBlock();
        queryBlock.setFrom(joinTableSource);

        updateAlias(exprTable, alias);

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

        if (from instanceof SQLJoinTableSource sqlJoinTableSource) {
            joinTableSource = sqlJoinTableSource;
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
