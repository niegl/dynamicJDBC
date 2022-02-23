package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;
import sqlTest.SQLTest;

import java.sql.SQLSyntaxErrorException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class SQLAlterDatabaseBuilderImplTest {

    @Test
    void alter() {
        SQLAlterDatabaseBuilderImpl sqlAlterDatabaseBuilder = new SQLAlterDatabaseBuilderImpl(DbType.mysql);
        sqlAlterDatabaseBuilder.setName("db_name");
        sqlAlterDatabaseBuilder.alter("db_name",new LinkedHashMap<String,String>(){{
            put("CHARACTER SET","charset_name");
            put("COLLATE","collation_name");
        }});

        System.out.println(sqlAlterDatabaseBuilder);
    }

    @Test
    void testAlterDb() throws SQLSyntaxErrorException {
        String dbType = "mysql";
        String sql ="ALTER DATABASE test_db CHARACTER SET = gb2312 COLLATE gb2312_chinese_ci;";
        SQLStatement statement = SQLTest.parser(sql, dbType);
        System.out.println("解析后的SQL 为 : [" + statement.toString() +"]");
    }
}