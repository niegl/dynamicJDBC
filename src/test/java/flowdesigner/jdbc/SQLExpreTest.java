package flowdesigner.jdbc;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
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
    void testFuntions() throws SQLSyntaxErrorException {
        DbType dbType = JdbcConstants.HIVE;
        String sql = "to_date(CONCAT(SUBSTR(${TXDATE}, 1, 4), '-', SUBSTR(${TXDATE}, 5, 2), '-', SUBSTR(${TXDATE}, 7, 2)))";
        SQLExpr expr = SQLUtils.toSQLExpr(sql, dbType);
        System.out.println(expr);
    }

    @org.junit.jupiter.api.Test
    void testPARTITIONBY() throws SQLSyntaxErrorException {
        DbType dbType = JdbcConstants.HIVE;
        String sql = "PARTITION BY Customercity";
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

}
