package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class SQLCreateDatabaseBuilderImplTest {
    SQLCreateDatabaseBuilderImpl builder;

    @BeforeEach
    void setUp() {
        builder = new SQLCreateDatabaseBuilderImpl(DbType.mysql);
    }

    @Test
    void createDatabase() {
        builder.createDatabase(true,"db_name", new HashMap<String,String>(){{
            put("CHARACTER SET","charset_name");
            put("COLLATE","collation_name");
        }});
        System.out.println(builder);
    }
}