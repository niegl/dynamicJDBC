package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sqlTest.SQLTest;

import java.sql.SQLSyntaxErrorException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SQLCreateTableBuilderImplTest {
    SQLCreateTableBuilderImpl tableBuilder;

    @BeforeEach
    void setUp() {

        tableBuilder = new SQLCreateTableBuilderImpl(DbType.mysql);
        tableBuilder.setName("std_line");
        tableBuilder.setSchema("std_pcode");
    }

    @Test
    void from() {
        tableBuilder.addColumn("line_id","Stirng", false, false,false);
        tableBuilder.addPartitionColumn("stat_dt","partition");
        tableBuilder.addPartitionColumn("stat_dt2","partition");
        tableBuilder.setComment("commetnst");
        tableBuilder.setIfNotExiists(true);
        tableBuilder.setShards(3);
        tableBuilder.setBuckets(4);
        tableBuilder.addOption("option1","options1");
        System.out.println(tableBuilder.toString());
    }

    @Test
    void addPrimaryKey() {
        //Id_P int NOT NULL PRIMARY KEY,
        tableBuilder.addColumn("Id_P", "int", true, false,true);
        System.out.println(tableBuilder.toString());
    }

    @Test
    void addPrimaryKey0() {
        tableBuilder.addPrimaryKey("pk_PersonID", Arrays.asList("Id_P","LastName"));
        System.out.println(tableBuilder.toString());
    }

    @Test
    void addUniqueKey() {
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
}