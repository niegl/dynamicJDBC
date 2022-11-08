package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        tableBuilder.addColumn("line_id", "String");
        tableBuilder.setSchema("std_pcode");
    }
    static Stream<Arguments> getTypes() {
        ArrayList<Arguments> arguments = new ArrayList<>();
        for (DbType dbType : DbType.values()) {
            arguments.add(Arguments.of(dbType, "CREATE TABLE std_pcode.std_line (\n" +
                    "\tline_id String\n" +
                    ")"));
        }

        return arguments.stream();
    }

    @ParameterizedTest
    @MethodSource()
    void testBuilder(DbType dbType, String expected) throws SQLSyntaxErrorException {
            createBuilder(dbType);
            tableBuilder.addColumn("line_id", "String", false, false, false);
            tableBuilder.addPrimaryKey("PRIMARY_P", Arrays.asList("Id_P"));
            tableBuilder.addUniqueKey("uc_PersonID", Arrays.asList("Id_P","LastName"));
            tableBuilder.addForeignKey("fk_PerOrders", Arrays.asList("Id_P"),"Persons", Arrays.asList("Id_P","LastName"));
            tableBuilder.addForeignKey(null, Arrays.asList("Id_P"),"Persons", Arrays.asList("Id_P","LastName"));
            tableBuilder.addColumnAutoIncrement("Id_P","int");
            tableBuilder.setTemporary(SQLCreateTableStatement.Type.TEMPORARY);
//            tableBuilder.setSelect("select a,b from t");

            tableBuilder.addPartitionColumn("stat_dt", "String");
            tableBuilder.addPartitionColumn("stat_dt2", "String");
            tableBuilder.setComment("commetnst");
            tableBuilder.setIfNotExiists(false);
//            tableBuilder.setShards(3);
//            tableBuilder.setBuckets(4);
//            tableBuilder.addOption("option1", "options1");

            SQLTest.parser(tableBuilder.toString(), dbType  );
    }
    static Stream<Arguments> testBuilder() {
        ArrayList<Arguments> arguments = new ArrayList<>();
        for (DbType dbType : DbType.values()) {
            if (dbType == DbType.odps) {
                arguments.add(Arguments.of(dbType, "CREATE TABLE std_pcode.std_line (\n" +
                        "\tline_id INT\n" +
                        ")"));
                continue;
            }

            arguments.add(Arguments.of(dbType, "CREATE TABLE std_pcode.std_line (\n" +
                    "\tline_id int\n" +
                    ")"));

        }

        return arguments.stream();
    }

    @ParameterizedTest
    @MethodSource()
    void addColumn(DbType dbType, String expected) {
        createBuilder(dbType);
        tableBuilder.addColumn("line_id","int");
        Assertions.assertEquals(expected,tableBuilder.toString());
    }
    static Stream<Arguments> addColumn() {
        ArrayList<Arguments> arguments = new ArrayList<>();
        for (DbType dbType : DbType.values()) {
            if (dbType == DbType.odps) {
                arguments.add(Arguments.of(dbType, "CREATE TABLE std_pcode.std_line (\n" +
                        "\tline_id INT\n" +
                        ")"));
                continue;
            }

            arguments.add(Arguments.of(dbType, "CREATE TABLE std_pcode.std_line (\n" +
                    "\tline_id int\n" +
                    ")"));

        }

        return arguments.stream();
    }

    @ParameterizedTest
    @MethodSource()
    void addColumnWithComment(DbType dbType, String expected) {
        createBuilder(dbType);
        tableBuilder.addColumn("line_id","int","comment");
        Assertions.assertEquals(expected,tableBuilder.toString());
    }
    static Stream<Arguments> addColumnWithComment() {
        ArrayList<Arguments> arguments = new ArrayList<>();
        for (DbType dbType : DbType.values()) {
            if (dbType == DbType.odps) {
                arguments.add(Arguments.of(dbType, "CREATE TABLE std_pcode.std_line (\n" +
                        "\tline_id String,\n" +
                        "\tline_id int COMMENT 'comment'\n" +
                        ")"));
            } else if (dbType == DbType.jtds || dbType == DbType.sqlserver) {
                arguments.add(Arguments.of(dbType, "CREATE TABLE std_pcode.std_line (\n" +
                        "\tline_id int\n" +
                        ")"));
            } else {
                arguments.add(Arguments.of(dbType, "CREATE TABLE std_pcode.std_line (\n" +
                        "\tline_id String,\n" +
                        "\tline_id int\n" +
                        ")"));
            }
        }

        return arguments.stream();
    }

    @ParameterizedTest
    @MethodSource()
    void addClusteredByItem(DbType dbType, String expected) throws SQLSyntaxErrorException {
        SQLTest.parser(expected, dbType  );
        createBuilder(dbType);
        tableBuilder.addClusteredByItem(List.of("column1", "column2"));
        Assertions.assertEquals(expected,tableBuilder.toString());
    }
    static Stream<Arguments> addClusteredByItem() {
        ArrayList<Arguments> arguments = new ArrayList<>();
        for (DbType dbType : DbType.values()) {
            if (dbType == DbType.hive || dbType == DbType.antspark) {
                arguments.add(Arguments.of(dbType, "CREATE TABLE std_pcode.std_line (\n" +
                        "\tline_id String\n" +
                        ")\n" +
                        "CLUSTERED BY (column1,column2)\n" +
                        "INTO 2 BUCKETS"));
            } else if (dbType == DbType.odps) {
                arguments.add(Arguments.of(dbType, "CREATE TABLE std_pcode.std_line (\n" +
                        "\tline_id STRING\n" +
                        ")"));
            } else {
                arguments.add(Arguments.of(dbType, "CREATE TABLE std_pcode.std_line (\n" +
                        "\tline_id String\n" +
                        ")"));
            }

        }

        return arguments.stream();
    }


    @ParameterizedTest
    @MethodSource("getTypes")
    void setExternal(DbType dbType, String expected) throws SQLSyntaxErrorException {
        createBuilder(dbType);
        tableBuilder.setName("std_line");
        tableBuilder.setExternal(true);
        SQLStatement statement = SQLTest.parser(tableBuilder.toString(), dbType);
    }
    static Stream<Arguments> setExternal() {
        return Stream.of(Arguments.arguments(DbType.mysql, "CREATE TABLE std_pcode.std_line (\n" +
                        "\tCONSTRAINT uc_PersonID FOREIGN KEY (Id_P, LastName) REFERENCES tbl_name (col_name) ON DELETE RESTRICT ON UPDATE CASCADE\n" +
                        ")"),
                Arguments.arguments(DbType.hive, "CREATE TABLE std_pcode.std_line (\n" +
                        "\tCONSTRAINT uc_PersonID FOREIGN KEY (Id_P, LastName)\n" +
                        "\t\tREFERENCES tbl_name (col_name) DISABLE NOVALIDATE\n" +
                        ")")
        );
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

    @ParameterizedTest
    @MethodSource()
    void testAddForeignKey(DbType dbType, String expected) {
        tableBuilder = SQLBuilderFactory.createCreateTableBuilder(dbType);
        tableBuilder.setName("std_line");
        tableBuilder.setSchema("std_pcode");
        tableBuilder.addForeignKey("uc_PersonID", Arrays.asList("Id_P","LastName"), "tbl_name", Arrays.asList("col_name"));
        Assertions.assertEquals(expected,tableBuilder.toString());
    }

    static Stream<Arguments> testAddForeignKey() {
        return Stream.of(Arguments.arguments(DbType.mysql, "CREATE TABLE std_pcode.std_line (\n" +
                        "\tCONSTRAINT uc_PersonID FOREIGN KEY (Id_P, LastName) REFERENCES tbl_name (col_name) ON DELETE RESTRICT ON UPDATE CASCADE\n" +
                        ")"),
                Arguments.arguments(DbType.hive, "CREATE TABLE std_pcode.std_line (\n" +
                        "\tCONSTRAINT uc_PersonID FOREIGN KEY (Id_P, LastName)\n" +
                        "\t\tREFERENCES tbl_name (col_name) DISABLE NOVALIDATE\n" +
                        ")")
        );
    }

    @ParameterizedTest
    @MethodSource()
    void testAddPrimaryKey(DbType dbType, String expected) {
        createBuilder(dbType);
        tableBuilder.addPrimaryKey("PRIMARY_id_p", Arrays.asList("Id_P"));

        Assertions.assertEquals(expected,tableBuilder.toString());
    }
    static Stream<Arguments> testAddPrimaryKey() {
        return Stream.of(Arguments.arguments(DbType.mysql, "CREATE TABLE std_pcode.std_line (\n" +
                        "\tline_id String,\n" +
                        "\tPRIMARY KEY (Id_P)\n" +
                        ")"),
                Arguments.arguments(DbType.hive, "CREATE TABLE std_pcode.std_line (\n" +
                        "\tline_id String,\n" +
                        "\tCONSTRAINT PRIMARY_id_p PRIMARY KEY (Id_P) DISABLE NOVALIDATE\n" +
                        ")"),
                Arguments.arguments(DbType.db2, "CREATE TABLE std_pcode.std_line (\n" +
                        "\tline_id String,\n" +
                        "\tCONSTRAINT PRIMARY_id_p PRIMARY KEY (Id_P)\n" +
                        ")")
        );
    }

    @ParameterizedTest
    @MethodSource()
    void addCheckConstraint(DbType dbType, String expected) {
        createBuilder(dbType);
        tableBuilder.addCheckConstraint("PRIMARY_id_p", "c1 > c3");

        Assertions.assertEquals(expected,tableBuilder.toString());
    }

    static Stream<Arguments> addCheckConstraint() {
        return Stream.of(Arguments.arguments(DbType.mysql, "CREATE TABLE std_pcode.std_line (\n" +
                        "\tline_id String,\n" +
                        "\tCHECK (c1 > c3)\n" +
                        ")"),
                Arguments.arguments(DbType.db2, "CREATE TABLE std_pcode.std_line (\n" +
                        "\tline_id String,\n" +
                        "\tCONSTRAINT PRIMARY_id_p CHECK (c1 > c3)\n" +
                        ")"),
                Arguments.arguments(DbType.hive, "CREATE TABLE std_pcode.std_line (\n" +
                        "\tline_id String,\n" +
                        "\tCONSTRAINT PRIMARY_id_p CHECK (c1 > c3)\n" +
                        ")")
        );
    }

}