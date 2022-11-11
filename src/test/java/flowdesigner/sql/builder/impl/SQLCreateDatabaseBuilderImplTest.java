package flowdesigner.sql.builder.impl;

import com.alibaba.druid.DbType;
import flowdesigner.sql.builder.SQLBuilderFactory;
import flowdesigner.sql.builder.SQLCreateDatabaseBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import sqlTest.SQLTest;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * H2由于druid解析问题，其他应该都pass
 */
class SQLCreateDatabaseBuilderImplTest {
    SQLCreateDatabaseBuilder builder;

    @AfterEach
    void tearDown() {
        System.out.println(builder);
    }

    @ParameterizedTest
    @MethodSource()
    void createDatabase(DbType dbType, String expected) throws SQLSyntaxErrorException {
        SQLTest.parser(expected, dbType);
        builder = SQLBuilderFactory.createCreateDatabaseBuilder(dbType);
        builder.createDatabase(true,"db_name", new HashMap<String,String>());
        builder.setComment("comments");
        Assertions.assertEquals(expected, builder.toString());
    }
    static Stream<Arguments> createDatabase() {
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

}