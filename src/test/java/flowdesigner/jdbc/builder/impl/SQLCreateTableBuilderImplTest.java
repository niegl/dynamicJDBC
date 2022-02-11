package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLCreateTableBuilderImplTest {
    SQLCreateTableBuilderImpl tableBuilder;

    @BeforeEach
    void setUp() {

        tableBuilder = new SQLCreateTableBuilderImpl(DbType.hive);
    }

    @Test
    void from() {
        tableBuilder.setName("std_line");
        tableBuilder.setSchema("std_pcode");
        tableBuilder.addColumn("line_id","Stirng");
        tableBuilder.addPartitionColumn("stat_dt","partition");
        tableBuilder.addPartitionColumn("stat_dt2","partition");
        tableBuilder.setComment("commetnst");
        tableBuilder.setIfNotExiists(true);
        tableBuilder.setShards(3);
        tableBuilder.setBuckets(4);
        tableBuilder.addOption("option1","options1");
        System.out.println(tableBuilder.toString());
    }
}