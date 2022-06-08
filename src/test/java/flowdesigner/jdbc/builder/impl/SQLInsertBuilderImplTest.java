package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLInsertBuilderImplTest {
    SQLInsertBuilderImpl insertBuilder;

    @BeforeEach
    void setUp() {

        insertBuilder = new SQLInsertBuilderImpl(DbType.hive);
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
}