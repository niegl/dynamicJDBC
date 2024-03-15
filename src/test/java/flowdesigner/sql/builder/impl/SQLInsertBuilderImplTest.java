package flowdesigner.sql.builder.impl;

import com.alibaba.druid.DbType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class SQLInsertBuilderImplTest {
    SQLInsertBuilderImpl insertBuilder;

    @BeforeEach
    void setUp() {

        insertBuilder = new SQLInsertBuilderImpl(DbType.mysql);
        insertBuilder.setTableSource("tableA");
    }

    @Test
    void setIfNotExists() {
        insertBuilder.setIfNotExists(true);
    }

    @Test
    void setInsertColumns() {
        insertBuilder.setInsertColumns("a","b");
        System.out.println(insertBuilder);
    }

    @Test
    void setQuery() {
        insertBuilder.setInsertColumns("a","b");
        insertBuilder.addPartition("data_dt","2021-10");
        insertBuilder.setQuery("SELECT column1, column2, columnN FROM second_table_name WHERE column1=2;");
        System.out.println(insertBuilder);
    }
    @Test
    void addDynamicPartition() {
        insertBuilder.setInsertColumns("a","b");
        insertBuilder.addPartition("data_dt",null);
        insertBuilder.setQuery("SELECT column1, column2, columnN FROM second_table_name WHERE column1=2;");
        System.out.println(insertBuilder);
    }

    @Test
    void addPartition() {
        insertBuilder.setInsertColumns("a","b");
        insertBuilder.addPartition("data_dt",null);
        insertBuilder.setQuery("SELECT column1, column2, columnN FROM second_table_name WHERE column1=2;");
        System.out.println(insertBuilder);
    }

    @Test
    void parseValueClause() {
        insertBuilder.setInsertColumns("a","b");
//        insertBuilder.addValueClause(new String[]{"columnvalue1", " columnvalue2"});
//        insertBuilder.addValueClause(List.of("columnvalue11"," columnvalue21"));
        System.out.println(insertBuilder);
    }

    @Test
    void addValueClause() {
        insertBuilder.setInsertColumns("a","b");
        ArrayList<String> strings = new ArrayList<>();
        strings.add("value1");
        strings.add("value2");
        insertBuilder.addValueClause(strings);
        System.out.println(insertBuilder);
    }
}