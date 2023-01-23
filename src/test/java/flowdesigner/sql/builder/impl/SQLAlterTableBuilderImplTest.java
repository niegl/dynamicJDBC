package flowdesigner.sql.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import flowdesigner.sql.builder.SQLAlterTableBuilder;
import flowdesigner.sql.builder.SQLBuilderFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import sqlTest.SQLTest;

import java.awt.*;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

class SQLAlterTableBuilderImplTest {
    SQLAlterTableBuilder alterTableBuilder;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        System.out.println(alterTableBuilder);
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

    @ParameterizedTest
    @MethodSource()
    void dropDropForeignKey(DbType dbType, String expected) throws SQLSyntaxErrorException {

        alterTableBuilder = SQLBuilderFactory.createAlterTableBuilder(dbType);
        alterTableBuilder.setName("table_name");
        alterTableBuilder.dropForeignKey("constraint_name");

        SQLStatement statement = SQLTest.parser(alterTableBuilder.toString(), dbType);
    }

    static Stream<Arguments> dropDropForeignKey() {
        ArrayList<Arguments> arguments = new ArrayList<>();
        for (DbType dbType : DbType.values()) {
            String syntax =  switch (dbType) {
                default -> "ALTER TABLE table_name DROP CONSTRAINT constraint_name;";
            };
            arguments.add(Arguments.of(dbType, syntax));
        }

        return arguments.stream();
    }

    @Test
    void addForeignKey() throws SQLSyntaxErrorException {
        String dbType = "hive";
        String sql ="ALTER TABLE tb_emp2\n" +
                " ADD CONSTRAINT fk_tb_dept1\n" +
                " FOREIGN KEY(deptId)\n" +
                " REFERENCES tb_dept1(id) DISABLE NOVALIDATE;";
        SQLStatement statement = SQLTest.parser(sql, dbType);
        System.out.println("解析后的SQL 为 : [" + statement.toString() +"]");
        ArrayList<String> strings = new ArrayList<>();
        strings.add("deptId");
        alterTableBuilder.addForeignKey(true,"fk_tb_dept1","index_name", strings,
                "tb_dept1",Arrays.asList("id"));
        System.out.println(alterTableBuilder);
    }

    @ParameterizedTest
    @MethodSource()
    void dropPartition(DbType dbType, String expected) throws SQLSyntaxErrorException {

        alterTableBuilder = SQLBuilderFactory.createAlterTableBuilder(dbType);
        alterTableBuilder.setName("table_name");
        alterTableBuilder.dropPartition(new HashMap<String,String>(){{
            put("partCol","'value1'");
        }});

        SQLStatement statement = SQLTest.parser(alterTableBuilder.toString(), dbType);
//        Assertions.assertEquals(expected, alterTableBuilder.toString());
    }
    static Stream<Arguments> dropPartition() {
        ArrayList<Arguments> arguments = new ArrayList<>();
        for (DbType dbType : DbType.values()) {
            String syntax =  switch (dbType) {
                default -> "ALTER TABLE table_name DROP PARTITION (partCol = 'value1')";
            };
            arguments.add(Arguments.of(dbType, syntax));
        }

        return arguments.stream();
    }

    @ParameterizedTest
    @MethodSource()
    void addPartition(DbType dbType, String expected) throws SQLSyntaxErrorException {

        alterTableBuilder = SQLBuilderFactory.createAlterTableBuilder(dbType);
        alterTableBuilder.setName("table_name");
        alterTableBuilder.addPartition(new HashMap<String,String>(){{
            put("partCol","'value1'");
        }}, false, "'loc1'");

        SQLStatement statement = SQLTest.parser(alterTableBuilder.toString(), dbType);
//        Assertions.assertEquals(expected, alterTableBuilder.toString());
    }
    static Stream<Arguments> addPartition() {
        ArrayList<Arguments> arguments = new ArrayList<>();
        for (DbType dbType : DbType.values()) {
            String syntax =  switch (dbType) {
                case hive -> "ALTER TABLE db_name\n" +
                        "\tADD COLUMNS (line_id int)";
                case odps -> "ALTER TABLE db_name\n" +
                        "\tADD COLUMNS (line_id INT)";
                case oracle, oceanbase_oracle, h2 -> "ALTER TABLE db_name\n" +
                        "\tADD (line_id int)";
                case db2,mysql,mariadb,postgresql,ads,presto,clickhouse, oscar, tidb -> "ALTER TABLE db_name\n" +
                        "\tADD COLUMN line_id int";
                default -> "ALTER TABLE table_name ADD PARTITION (partCol = 'value1') location 'loc1';";
            };
            arguments.add(Arguments.of(dbType, syntax));
        }

        return arguments.stream();
    }

    @ParameterizedTest
    @MethodSource()
    void addColumn(DbType dbType, String expected) throws SQLSyntaxErrorException {
        SQLStatement statement = SQLTest.parser(expected, dbType);

        alterTableBuilder = SQLBuilderFactory.createAlterTableBuilder(dbType);
        alterTableBuilder.setName("db_name");
        alterTableBuilder.addColumn("line_id","int");

        Assertions.assertEquals(expected, alterTableBuilder.toString());
    }
    static Stream<Arguments> addColumn() {
        ArrayList<Arguments> arguments = new ArrayList<>();
        for (DbType dbType : DbType.values()) {
            String syntax =  switch (dbType) {
                case hive -> "ALTER TABLE db_name\n" +
                        "\tADD COLUMNS (line_id int)";
                case odps -> "ALTER TABLE db_name\n" +
                        "\tADD COLUMNS (line_id INT)";
                case oracle, oceanbase_oracle, h2 -> "ALTER TABLE db_name\n" +
                        "\tADD (line_id int)";
                case db2,mysql,mariadb,postgresql,ads,presto,clickhouse, oscar, tidb -> "ALTER TABLE db_name\n" +
                        "\tADD COLUMN line_id int";
                default -> "ALTER TABLE db_name\n" +
                        "\tADD line_id int";
            };
            arguments.add(Arguments.of(dbType, syntax));
        }

        return arguments.stream();
    }

    @ParameterizedTest
    @MethodSource()
    void dropColomn(DbType dbType, String expected) throws SQLSyntaxErrorException {

        alterTableBuilder = SQLBuilderFactory.createAlterTableBuilder(dbType);
        alterTableBuilder.setName("db_name");
        if (dbType == DbType.hive) {
            HashMap<String, String> stringStringHashMap = new HashMap<>();
            stringStringHashMap.put("line_id","int");
            stringStringHashMap.put("b","int");
            alterTableBuilder.replaceColumn(stringStringHashMap);
        } else
        alterTableBuilder.dropColumn("line_id");

        SQLTest.parser(expected, dbType);
        Assertions.assertEquals(expected, alterTableBuilder.toString());
    }
    static Stream<Arguments> dropColomn() {
        ArrayList<Arguments> arguments = new ArrayList<>();
        for (DbType dbType : DbType.values()) {
            String syntax =  switch (dbType) {
                case hive -> "ALTER TABLE db_name\n" +
                        "\tREPLACE COLUMNS (b int, line_id int)";
                default -> "ALTER TABLE db_name\n" +
                        "\tDROP COLUMN line_id";
            };
            arguments.add(Arguments.of(dbType, syntax));
        }

        return arguments.stream();
    }

}