package flowdesigner.jdbc.builder;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import flowdesigner.jdbc.builder.impl.SQLSelectBuilderImpl;

public interface SQLSelectBuilder {

    SQLSelect getSQLSelect();

    SQLSelectStatement getSQLSelectStatement();

    SQLSelectBuilder select(String... columns);

    SQLSelectBuilder selectWithAlias(String column, String alias);

    SQLSelectBuilder from(String table);

    SQLSelectBuilder from(String table, String alias);

    SQLSelectBuilder orderBy(String... columns);

    SQLSelectBuilder groupBy(String expr);

    SQLSelectBuilder having(String expr);

    SQLSelectBuilder into(String expr);

    SQLSelectBuilder where(String expr);

    SQLSelectBuilder whereAnd(String expr);

    SQLSelectBuilder whereOr(String expr);

    SQLSelectBuilder limit(int rowCount);

    SQLSelectBuilder limit(int rowCount, int offset);

    SQLSelectBuilder union(SQLSelectBuilder selectBuilder, String unionType);

    SQLSelectBuilder join(String joinType, String table, String alias,
                          String exprLeft, String exprRight, String operator);

    SQLSelectBuilder joinAnd(String conditionLeft, String conditionRight, String conditionOperator);

    SQLSelectBuilder joinOr(String conditionLeft, String conditionRight, String conditionOperator);

    void setBigResult(boolean bigResult);

    void setBufferResult(boolean bufferResult);

    void setCache(Boolean cache);

    void setCalcFoundRows(boolean calcFoundRows);

    void setIsolation(String isolation);

    void setIsolation(DB2SelectQueryBlock.Isolation isolation);

    void setForReadOnly(boolean forReadOnly);

    void setOptimizeFor(SQLExpr optimizeFor);
}
