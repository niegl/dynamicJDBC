package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sqlTest.SQLTest;

import java.sql.SQLSyntaxErrorException;

import static org.junit.jupiter.api.Assertions.*;

class SQLAlterTableBuilderImplTest {
    SQLAlterTableBuilderImpl alterTableBuilder;
    @BeforeEach
    void setUp() {
        alterTableBuilder = new SQLAlterTableBuilderImpl(DbType.mysql);
        alterTableBuilder.setName("std_line");
    }

    @Test
    void alterColumn() {
        alterTableBuilder.alterColumn("srccolumn","dstcolumn","string","comment thisi s",null,true);
        System.out.println(alterTableBuilder);
    }

    @org.junit.jupiter.api.Test
    void testPrimary() throws SQLSyntaxErrorException {
        String dbType = "mysql";
        String sql ="ALTER TABLE 数据表名 ADD PRIMARY KEY(字段名);";
        SQLStatement statement = SQLTest.parser(sql, dbType);
        System.out.println("解析后的SQL 为 : [" + statement.toString() +"]");
    }
}