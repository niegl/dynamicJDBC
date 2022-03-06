package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.builder.impl.SQLSelectBuilderImpl;
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

    @Test
    void setComment() {
        builder.setName("thisisb");
        builder.setComment("thisisb");
        System.out.println(builder);
    }
}