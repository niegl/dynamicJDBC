package flowdesigner.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleExportParameterVisitor;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.sql.SQLSyntaxErrorException;

public class SQLExpreTest {

    @org.junit.jupiter.api.Test
    public void testExpr(){
        DbType dbType = JdbcConstants.MYSQL;
        SQLExpr expr = SQLUtils.toSQLExpr("id=3", dbType);
        System.out.println(expr);
    }

    @org.junit.jupiter.api.Test
    public void testCHAR_LITERAL(){
        DbType dbType = JdbcConstants.HIVE;
        SQLExpr expr = SQLUtils.toSQLExpr("CHAR 'abcd'", dbType);
        System.out.println(expr);
    }

    @org.junit.jupiter.api.Test
    void testFuntions() throws SQLSyntaxErrorException {
        DbType dbType = JdbcConstants.HIVE;
        String sql = "to_date(CONCAT(SUBSTR(${TXDATE}, 1, 4), '-', SUBSTR(${TXDATE}, 5, 2), '-', SUBSTR(${TXDATE}, 7, 2)))";
        SQLExpr expr = SQLUtils.toSQLExpr(sql, dbType);
        System.out.println(expr);
    }
    @org.junit.jupiter.api.Test
    void testUnion() throws SQLSyntaxErrorException {
        DbType dbType = JdbcConstants.HIVE;
        String sql = "select A.id  from pmart.t97_input_engcspt_sum A union all select B.engcspt_rel_ordr_num  from pmart.t98_engcspt_rel_day_stat B";
        SQLExpr expr = SQLUtils.toSQLExpr(sql, dbType);
        System.out.println(expr);
    }
    @org.junit.jupiter.api.Test
    void testPARTITIONBY() throws SQLSyntaxErrorException {
        DbType dbType = JdbcConstants.HIVE;
        String sql = "PARTITIONED BY ( `data_dt` string)";
        SQLExpr expr = SQLUtils.toSQLExpr(sql, dbType);
        System.out.println(expr);
    }
    @org.junit.jupiter.api.Test
    void testSQLExtractExpr() throws SQLSyntaxErrorException {
        DbType dbType = JdbcConstants.MYSQL;
        String sql = "EXTRACT(YEAR FROM OrderDate)";
        SQLExpr expr = SQLUtils.toSQLExpr(sql, dbType);
        System.out.println(expr);
    }
    @org.junit.jupiter.api.Test
    void testSQLSelectExpr() throws SQLSyntaxErrorException {
        DbType dbType = JdbcConstants.MYSQL;
        String sql = "EXTRACT(YEAR FROM OrderDate)";
        SQLExpr expr = SQLUtils.toSQLExpr(sql, dbType);
        System.out.println(expr);
    }
    @org.junit.jupiter.api.Test
    void testSQLOverExpr() throws SQLSyntaxErrorException {
        DbType dbType = JdbcConstants.MYSQL;
        String sql = "ranger(object_id) over(partition by type order by object_id)";
        SQLExpr expr = SQLUtils.toSQLExpr(sql, dbType);
        System.out.println(expr);
    }
    @org.junit.jupiter.api.Test
    void testBinaryOperatorExpr1() throws SQLSyntaxErrorException {
        DbType dbType = JdbcConstants.HIVE;
        String sql = "<>";
        SQLBinaryOpExpr binaryOpExpr = new SQLBinaryOpExpr(new SQLIdentifierExpr("a"), SQLBinaryOperator.NotEqual, new SQLIdentifierExpr("b"));
//        SQLExpr expr = SQLUtils.toSQLExpr(sql, dbType);
        System.out.println(binaryOpExpr);
    }
    @org.junit.jupiter.api.Test
    void testUnaryOperatorExpr1() throws SQLSyntaxErrorException {
        DbType dbType = JdbcConstants.HIVE;
        String sql = "!";
        SQLExpr expr = SQLUtils.toSQLExpr(sql, dbType);
        System.out.println(expr);
    }
    @org.junit.jupiter.api.Test
    void testExprExpr3() throws SQLSyntaxErrorException {
        DbType dbType = JdbcConstants.HIVE;
        String sql = "a between a and b";
        SQLExpr expr = SQLUtils.toSQLExpr(sql, dbType);
        System.out.println(expr);
    }
    @org.junit.jupiter.api.Test
    void testAggregationExpr2() throws SQLSyntaxErrorException {
        DbType dbType = JdbcConstants.HIVE;
        String sql = "max()";
        SQLExpr expr = SQLUtils.toSQLExpr(sql, dbType);
        System.out.println(expr);
    }
    @org.junit.jupiter.api.Test
    void testMethodInvokeExpr4() throws SQLSyntaxErrorException {
        DbType dbType = JdbcConstants.HIVE;
        String sql = "floor(abc)";
        SQLExpr expr = SQLUtils.toSQLExpr(sql, dbType);
        System.out.println(expr);

        final StringBuilder out = new StringBuilder();
        final ExportParameterVisitor visitor = new OracleExportParameterVisitor(out);
        expr.accept(visitor);
        sql = out.toString();
        System.out.println("src:"+sql);
    }
    @org.junit.jupiter.api.Test
    void testExprParameter() throws SQLSyntaxErrorException {
        DbType dbType = JdbcConstants.HIVE;
        String sql = "floor(abc)";
        SQLExpr expr = SQLUtils.toSQLExpr(sql, dbType);

        final StringBuilder out = new StringBuilder();
        final ExportParameterVisitor visitor = new OracleExportParameterVisitor(out);
        expr.accept(visitor);
        sql = out.toString();
        System.out.println("src:"+sql);
    }
    @org.junit.jupiter.api.Test
    void testMap() throws SQLSyntaxErrorException {
        DbType dbType = JdbcConstants.HIVE;
        String sql = "map(key1, value1, key2, value2)";
        SQLExpr expr = SQLUtils.toSQLExpr(sql, dbType);
        System.out.println(expr);

    }
    @org.junit.jupiter.api.Test
    void testLogFunction() throws SQLSyntaxErrorException {
        DbType dbType = JdbcConstants.HIVE;
        String sql = "log(base, a)";
        SQLExpr expr = SQLUtils.toSQLExpr(sql, dbType);
        System.out.println(expr);

    }
    @org.junit.jupiter.api.Test
    void testExtractFunction() throws SQLSyntaxErrorException {
        DbType dbType = JdbcConstants.HIVE;
        String sql = "extract(month from \"2016-10-20\") ";
        SQLExpr expr = SQLUtils.toSQLExpr(sql, dbType);
        System.out.println(expr);
    }
    @org.junit.jupiter.api.Test
    void testCaseFunction() throws SQLSyntaxErrorException {
        DbType dbType = JdbcConstants.HIVE;
        String sql = "CASE a WHEN b THEN c WHEN d THEN e ELSE f END";
        SQLExpr expr = SQLUtils.toSQLExpr(sql, dbType);
        System.out.println(expr);
    }
    @org.junit.jupiter.api.Test
    void testIndentifier() throws SQLSyntaxErrorException {
        DbType dbType = JdbcConstants.HIVE;
        String sql = "123";
        SQLExpr expr = SQLUtils.toSQLExpr(sql, dbType);
        System.out.println(expr);
        String sql1 = "\"123\"";
        SQLExpr expr1 = SQLUtils.toSQLExpr(sql1, dbType);
        System.out.println(expr1);
    }

}
