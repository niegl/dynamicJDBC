package flowdesigner.sql;

import com.alibaba.druid.DbType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLUtilsTest {

    @Test
    void checkSyntax() {
        String sql = "select name,age  test_tab1 where SELECT select;select name,age from test_tab1 where SELECT select;";
        String resuslt = SQLUtils.checkSyntax(sql, DbType.hive);
        System.out.println(resuslt);
    }
}