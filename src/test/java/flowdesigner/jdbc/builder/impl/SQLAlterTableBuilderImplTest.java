package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import flowdesigner.jdbc.builder.impl.dialect.mysql.MySQLAlterTableBuilderImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sqlTest.SQLTest;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SQLAlterTableBuilderImplTest {
    SQLAlterTableBuilderImpl alterTableBuilder;
    @BeforeEach
    void setUp() {
        alterTableBuilder = new SQLAlterTableBuilderImpl(DbType.hive);
        alterTableBuilder.setName("tb_emp2");
    }

    @Test
    void alterColumn() {
        alterTableBuilder.alterColumn("srccolumn","dstcolumn","string","comment thisi s",null,true);
        System.out.println(alterTableBuilder);
    }

    @org.junit.jupiter.api.Test
    void testPrimary() throws SQLSyntaxErrorException {
        String dbType = "hive";
        String sql ="ALTER TABLE 数据表名 ADD PRIMARY KEY(字段名);";
        SQLStatement statement = SQLTest.parser(sql, dbType);
        System.out.println("解析后的SQL 为 : [" + statement.toString() +"]");
    }

    @Test
    void addPrimaryKey() {
        alterTableBuilder.addPrimaryKey("primary", true, null);
        System.out.println(alterTableBuilder);
    }

    @Test
    void addUniqueKey() {
        alterTableBuilder.addUniqueKey("primary", true, null);
        System.out.println(alterTableBuilder);
        alterTableBuilder.addUniqueIndex("primary", true, null);
        System.out.println(alterTableBuilder);
    }

    @Test
    void addUniqueIndex() {
        alterTableBuilder.addUniqueIndex("primary", true, null);
        System.out.println(alterTableBuilder);
    }

    @Test
    void dropDropForeignKey() {
        alterTableBuilder.dropForeignKey("primary");
        System.out.println(alterTableBuilder);
    }

    @Test
    void addForeignKey() throws SQLSyntaxErrorException {
        String dbType = "hive";
        String sql ="ALTER TABLE tb_emp2\n" +
                " ADD CONSTRAINT fk_tb_dept1\n" +
                " FOREIGN KEY(deptId)\n" +
                " REFERENCES tb_dept1(id);";
        SQLStatement statement = SQLTest.parser(sql, dbType);
        System.out.println("解析后的SQL 为 : [" + statement.toString() +"]");
        ArrayList<String> strings = new ArrayList<>();
        strings.add("deptId");
        alterTableBuilder.addForeignKey(true,"fk_tb_dept1","index_name", strings,
                "tb_dept1",Arrays.asList("id"));
        System.out.println(alterTableBuilder);
    }
}