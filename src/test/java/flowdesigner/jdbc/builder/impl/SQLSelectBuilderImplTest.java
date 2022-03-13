package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import flowdesigner.jdbc.builder.SQLBuilderFactory;
import flowdesigner.jdbc.builder.SQLSelectBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLSelectBuilderImplTest {

    @Test
    void join() {
        SQLSelectBuilder builderEx = new SQLSelectBuilderImpl(DbType.mysql);
        builderEx.select("a","b")
                .from("tablea","a");
        builderEx.join("inner join","tableb","b","a.aa","b.bb","=");
        builderEx.join("inner join","tablec","c","c.cc","b.bb","=");

        System.out.println(builderEx);

    }

    @Test
    void from() {
        SQLSelectBuilder builder = SQLBuilderFactory.createSelectSQLBuilder(DbType.mysql);
        builder.select("a", "b")
                .from("(SELECT building_t.idbuilding_t, building_t.name, building_t.floors, campus_t.idcampus_t FROM test.building_t INNER JOIN test.campus_t  ON building_t.campus_id = campus_t.idcampus_t)"
                        ,"vv");

        System.out.println(builder);
    }
}