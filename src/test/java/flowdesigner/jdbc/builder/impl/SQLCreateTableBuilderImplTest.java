package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}