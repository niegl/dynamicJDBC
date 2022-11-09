package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import sqlTest.SQLTest;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SQLAlterDatabaseBuilderImplTest {

    SQLAlterDatabaseBuilderImpl sqlAlterDatabaseBuilder;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        System.out.println(sqlAlterDatabaseBuilder);
    }

    @ParameterizedTest
    @MethodSource()
    void test(DbType dbType, String expected) {
        sqlAlterDatabaseBuilder = new SQLAlterDatabaseBuilderImpl(dbType);
        sqlAlterDatabaseBuilder.setName("db_name");
        sqlAlterDatabaseBuilder.alter("db_name",new LinkedHashMap<String,String>(){{
            put("CHARACTER SET","charset_name");
            put("COLLATE","collation_name");
        }});


    }
    static Stream<Arguments> test() {
        ArrayList<Arguments> arguments = new ArrayList<>();
        for (DbType dbType : DbType.values()) {
            String syntax =  switch (dbType) {
                case mysql, elastic_search, mariadb, drds -> "CREATE DATABASE IF NOT EXISTS db_name";
                case h2 -> "CREATE SCHEMA IF NOT EXISTS db_name";
                default -> "CREATE DATABASE IF NOT EXISTS db_name COMMENT comments";
            };
            arguments.add(Arguments.of(dbType, syntax));
        }

        return arguments.stream();
    }


    @Test
    void testAlterDb() throws SQLSyntaxErrorException {
        String dbType = "mysql";
        String sql ="ALTER DATABASE test_db CHARACTER SET = gb2312 COLLATE gb2312_chinese_ci;";
        SQLStatement statement = SQLTest.parser(sql, dbType);
        System.out.println("解析后的SQL 为 : [" + statement.toString() +"]");
    }
}