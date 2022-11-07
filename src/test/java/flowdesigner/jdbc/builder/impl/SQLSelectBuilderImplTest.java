package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import flowdesigner.jdbc.builder.SQLBuilderFactory;
import flowdesigner.jdbc.builder.SQLSelectBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLSelectBuilderImplTest {

    SQLSelectBuilder builderEx = SQLBuilderFactory.createSelectSQLBuilder( DbType.mysql);
    @AfterEach
    void tearDown() {
        System.out.println(builderEx);
    }

    @Test
    void select() {
        builderEx.select("a/b","b")
                .from("tablea","a");

        System.out.println(builderEx);
    }

    @Test
    void join() {
//        SQLSelectBuilder builderEx = new SQLSelectBuilderImpl(DbType.mysql);
        builderEx.select("a","b")
                .from("tablea","a");
        builderEx.join("inner join","tableb","b","a.aa","b.bb","=");
        builderEx.joinAnd("c","B.c","!=");
        builderEx.joinOr("d","B.d","=");
        builderEx.join("inner join","tablec","c","c.cc","b.bb","=");

        System.out.println(builderEx);

    }

    @Test
    void from() {
        SQLSelectBuilder builder = SQLBuilderFactory.createSelectSQLBuilder(DbType.mysql);
        builder//.select("a", "b")
                .from("(SELECT building_t.idbuilding_t, building_t.name, building_t.floors, campus_t.idcampus_t FROM test.building_t INNER JOIN test.campus_t  ON building_t.campus_id = campus_t.idcampus_t)"
                        ,"vv");

        System.out.println(builder);
    }
    @Test
    void from2() {
        SQLSelectBuilder builder = SQLBuilderFactory.createSelectSQLBuilder(DbType.mysql);
        builder.select("a", "b");
        builder.from("AT");
        builder.join("COMMA","BT", null,null,null,null);
        builder.join("COMMA","CT", null,null,null,null);
                System.out.println(builder);
    }
    @Test
    void union() {
        SQLSelectBuilder builder = SQLBuilderFactory.createSelectSQLBuilder(DbType.mysql);
        builder.select("a", "b")
                .from("(SELECT building_t.idbuilding_t, building_t.name, building_t.floors, campus_t.idcampus_t FROM test.building_t INNER JOIN test.campus_t  ON building_t.campus_id = campus_t.idcampus_t)"
                        ,"vv");

        SQLSelectBuilder builder2 = SQLBuilderFactory.createSelectSQLBuilder(DbType.mysql);
        builder2.select("c", "d")
                .from("test.building_t"
                        ,"tt");
        builder.union(builder2, "");

        SQLSelectBuilder builder3 = SQLBuilderFactory.createSelectSQLBuilder(DbType.mysql);
        builder3.select("e", "f")
                .from("test.building_t2"
                        ,"tt");
        builder.union(builder3, "DISTINCT");
        System.out.println(builder);
    }

    @Test
    void whereAnd() {
        SQLSelectBuilder builderEx = new SQLSelectBuilderImpl(DbType.mysql);
        builderEx.select("a","b")
                .from("tablea","a")
                .where("data_dt='2022-10'");

        builderEx.whereAnd("a=2");
        builderEx.whereOr("b=4 and aa=2");

        System.out.println(builderEx);
    }

    @Test
    void whereOr() {
    }

    @Test
    void testJoin() {
    }


    @Test
    void setBigResult() {
        builderEx.select("a","b")
                .from("tablea","a")
                .where("data_dt='2022-10'");
        builderEx.setBigResult(true);
    }

    @Test
    void setBufferResult() {
        builderEx.select("a","b")
                .from("tablea","a")
                .where("data_dt='2022-10'");
        builderEx.setBufferResult(true);
    }

    @Test
    void setCache() {
        builderEx.select("a","b")
                .from("tablea","a")
                .where("data_dt='2022-10'");
        builderEx.setCache(true);
    }

    @Test
    void setCalcFoundRows() {

        builderEx.select("a","b")
                .from("tablea","a")
                .where("data_dt='2022-10'");
        builderEx.setCalcFoundRows(true);
    }

    @Test
    void orderBy() {
        builderEx.select("a","b")
            .from("tablea","a")
            .where("data_dt='2022-10'");
        builderEx.orderBy("root_t.a asc","b");
    }
}