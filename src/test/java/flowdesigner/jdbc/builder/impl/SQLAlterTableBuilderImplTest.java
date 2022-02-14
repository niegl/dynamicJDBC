package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLAlterTableBuilderImplTest {
    SQLAlterTableBuilderImpl alterTableBuilder;
    @BeforeEach
    void setUp() {
        alterTableBuilder = new SQLAlterTableBuilderImpl(DbType.hive);
        alterTableBuilder.setName("std_line");
    }

    @Test
    void alterColumn() {
        alterTableBuilder.alterColumn("srccolumn","dstcolumn","string","comment thisi s",null,true);
        System.out.println(alterTableBuilder);
    }
}