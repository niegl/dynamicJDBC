package flowdesigner.jdbc;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.util.JdbcConstants;

public class SQLExpreTest {

    @org.junit.jupiter.api.Test
    public void testExpr(){
        DbType dbType = JdbcConstants.MYSQL;
        SQLExpr expr = SQLUtils.toSQLExpr("id=3", dbType);
        System.out.println(expr);
    }

}
