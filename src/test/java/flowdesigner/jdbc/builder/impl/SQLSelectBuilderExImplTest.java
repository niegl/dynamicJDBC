package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import flowdesigner.jdbc.builder.SQLSelectBuilderEx;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLSelectBuilderExImplTest {

    @Test
    void join() {
        SQLSelectBuilderEx builderEx = new SQLSelectBuilderExImpl(DbType.mysql);
        builderEx.select("a","b")
                .from("tablea","a");
        builderEx.join("inner join","tableb","b","a.aa","b.bb","=");
        builderEx.join("inner join","tablec","c","c.cc","b.bb","=");

        System.out.println(builderEx);

    }
}