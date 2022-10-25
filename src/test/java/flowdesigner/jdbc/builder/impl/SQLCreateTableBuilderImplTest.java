package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import flowdesigner.jdbc.builder.SQLBuilderFactory;
import flowdesigner.jdbc.builder.SQLCreateTableBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import sqlTest.SQLTest;

import java.sql.SQLSyntaxErrorException;
import java.util.Arrays;
import java.util.stream.Stream;


class SQLCreateTableBuilderImplTest {
    SQLCreateTableBuilder tableBuilder;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        System.out.println(tableBuilder);
    }

    void createBuilder(DbType dbType) {
        tableBuilder = SQLBuilderFactory.createCreateTableBuilder(dbType);
        tableBuilder.setName("std_line");
        tableBuilder.setSchema("std_pcode");
        tableBuilder.setComment("comment");
    }
    @Test
    void testBuilder() {
        for (DbType dbType : DbType.values()) {
            createBuilder(dbType);
            tableBuilder.addColumn("line_id", "Stirng", false, false, false);
            tableBuilder.addPrimaryKey("PRIMARY", Arrays.asList("Id_P"));
            tableBuilder.addUniqueKey("uc_PersonID", Arrays.asList("Id_P","LastName"));
            tableBuilder.addForeignKey("fk_PerOrders", Arrays.asList("Id_P"),"Persons", Arrays.asList("Id_P","LastName"));
            tableBuilder.addForeignKey(null, Arrays.asList("Id_P"),"Persons", Arrays.asList("Id_P","LastName"));
            tableBuilder.addColumnAutoIncrement("Id_P","int");
            tableBuilder.setTemporary("dd");
            tableBuilder.setSelect("select a,b from t");

            tableBuilder.addPartitionColumn("stat_dt", "partition");
            tableBuilder.addPartitionColumn("stat_dt2", "partition");
            tableBuilder.setComment("commetnst");
            tableBuilder.setIfNotExiists(true);
            tableBuilder.setShards(3);
            tableBuilder.setBuckets(4);
            tableBuilder.addOption("option1", "options1");
            System.out.println(tableBuilder.toString());
        }
    }

    @Test
    void addColumn() {
        for (DbType dbType : DbType.values()) {
            createBuilder(dbType);
            tableBuilder.addColumn("line_id","Stirng");
            System.out.println(tableBuilder);
        }
    }

    @Test
    void from() {
        tableBuilder.addColumn("line_id", "Stirng", false, false, false);
        tableBuilder.addPartitionColumn("stat_dt", "partition");
        tableBuilder.addPartitionColumn("stat_dt2", "partition");
        tableBuilder.setComment("commetnst");
        tableBuilder.setIfNotExiists(true);
        tableBuilder.setShards(3);
        tableBuilder.setBuckets(4);
        tableBuilder.addOption("option1", "options1");
        System.out.println(tableBuilder.toString());
    }

    @Test
    void addPrimaryKey() throws SQLSyntaxErrorException {
        createBuilder(DbType.hive);
        //Id_P int NOT NULL PRIMARY KEY,
        tableBuilder.addColumn("Id_P", "int", true, false,true);
        SQLTest.parser(tableBuilder.toString(), DbType.hive);
    }

    @Test
    void addPrimaryKeyConstraint() throws SQLSyntaxErrorException {
        createBuilder(DbType.db2);
        tableBuilder.addColumn("Id_P","int");
        tableBuilder.addPrimaryKey("PRIMARY_id_p", Arrays.asList("Id_P"));
        SQLTest.parser(tableBuilder.toString(), DbType.db2);
    }

    @Test
    void addUniqueKeyMySQL() {
        tableBuilder.addUniqueKey("uc_PersonID", Arrays.asList("Id_P","LastName"));
        System.out.println(tableBuilder.toString());
    }

    @Test
    void addForeignKey() {
        tableBuilder.addForeignKey("fk_PerOrders", Arrays.asList("Id_P"),"Persons", Arrays.asList("Id_P","LastName"));
        System.out.println(tableBuilder.toString());
    }
    @Test
    void addForeignKeyNoName() {
        tableBuilder.addForeignKey(null, Arrays.asList("Id_P"),"Persons", Arrays.asList("Id_P","LastName"));
        System.out.println(tableBuilder.toString());
    }

    @Test
    void testAutoIncrease() throws SQLSyntaxErrorException {
        String dbType = "mysql";
        String sql ="ALTER DATABASE test_db CHARACTER SET = gb2312 COLLATE gb2312_chinese_ci;";
        SQLStatement statement = SQLTest.parser(sql, dbType);
        System.out.println("解析后的SQL 为 : [" + statement.toString() +"]");
    }

    @Test
    void addColumnAutoIncrement() throws SQLSyntaxErrorException {
        String dbType = "mysql";
        String sql ="CREATE TABLE tb_student(\n" +
                " id INT(4) PRIMARY KEY AUTO_INCREMENT,\n" +
                "  name VARCHAR(25) NOT NULL\n" +
                "  );";
        SQLStatement statement = SQLTest.parser(sql, dbType);
        System.out.println("解析后的SQL 为 : [" + statement.toString() +"]");

        tableBuilder.addColumnAutoIncrement("Id_P","int");
        System.out.println(tableBuilder.toString());
    }

    @Test
    void testAddColumn() {
        tableBuilder.addColumn("line_id","Stirng","commentcol");
        System.out.println(tableBuilder);
    }

    @Test
    void testTempTable0() {
        tableBuilder.addColumn("line_id","Stirng","commentcol");
        System.out.println(tableBuilder);
    }

    @Test
    void setTemporary() {
        tableBuilder.setTemporary("TEMPORARY");
        tableBuilder.addColumn("line_id","Stirng");
        System.out.println(tableBuilder);
    }

    @Test
    void setLike() {
        tableBuilder.setTemporary("dd");
        tableBuilder.setLike("abc");
        System.out.println(tableBuilder);
    }

    @Test
    void setSelect() {
        tableBuilder.setTemporary("dd");
        tableBuilder.setSelect("select a,b from t");
        System.out.println(tableBuilder);
    }

    @Test
    void setExternal() {
        tableBuilder.setExternal(true);
    }

    /**
     * methodSource不指定方法名.默认方法名与测试方法相同
     * @param dbType
     * @param expected
     */
    @ParameterizedTest
    @MethodSource()
    void createUnique(DbType dbType, String expected) {
        tableBuilder = SQLBuilderFactory.createCreateTableBuilder(dbType);
        tableBuilder.setName("std_line");
        tableBuilder.setSchema("std_pcode");
        tableBuilder.addUniqueKey("uc_PersonID", Arrays.asList("Id_P","LastName"));
        Assertions.assertEquals(expected,tableBuilder.toString());
    }

    static Stream<Arguments> createUnique() {
        return Stream.of(Arguments.arguments(DbType.mysql, "CREATE TABLE std_pcode.std_line (\n" +
                        "\tUNIQUE uc_PersonID (Id_P, LastName)\n" +
                        ")"),
                Arguments.arguments(DbType.hive, "CREATE TABLE std_pcode.std_line")
        );
    }

}