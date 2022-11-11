package flowdesigner.sql.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import flowdesigner.sql.builder.SQLBuilderFactory;
import flowdesigner.sql.builder.SQLSelectBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sqlTest.SQLTest;

import java.sql.SQLSyntaxErrorException;

class SQLSelectBuilderImplTest {

    SQLSelectBuilder builderEx = SQLBuilderFactory.createSelectSQLBuilder( DbType.mysql);

    @AfterEach
    void tearDown() {
        System.out.println(builderEx);
    }

    /**
     * 用于测试设置了过滤以后再设置表别名的情况下，过滤中的表别名不起作用.
     */
    @Test
    void from() throws SQLSyntaxErrorException {
        SQLStatement statement = SQLTest.parser("select A.a from tableA A join tableB B on 1=1 where tableB.a = 1 and tableB.b=2 and tableB.c=3", DbType.mysql);

        builderEx.select("a/b","b")
                .from("tablea");
        builderEx.where("tableB.a = 1").whereAnd("tablea.b=2").whereAnd("tableB.c=3");
        // 测试别名更改后是否应用于前面的where
        builderEx.from("tableB","B");

        Assertions.assertEquals(builderEx.toString(), "SELECT a / b, b\n" +
                "FROM tableB B\n" +
                "WHERE B.a = 1\n" +
                "\tAND tablea.b = 2\n" +
                "\tAND B.c = 3");
    }

    @Test
    void join() throws SQLSyntaxErrorException {
        SQLStatement statement = SQLTest.parser("SELECT a.househld_pty_id, a.motor_lighting_val, a.stat_mon, b.area, c.fac_point_id\n" +
                "\t, d.sup_househld_pty_id, d.househld_pty_nm\n" +
                "FROM pmart.t98_station_engcspt_stat_month a\n" +
                "\tINNER JOIN pdata.t01_pty_area_h b ON a.househld_pty_id = b.pty_id\n" +
                "\tINNER JOIN pdata.t02_fac_point_pty_rel_h c ON a.househld_pty_id = c.fac_point_id\n" +
                "\tINNER JOIN pdata.t01_househld_pty d ON a.househld_pty_id = d.househld_pty_id\n" +
                "WHERE t01_pty_area_h.area_typ_cd = '00'\n" +
                "\tAND t02_fac_point_pty_rel_h.fac_point_pty_rel_ctgy_cd = '06'\n" +
                "\tAND t01_pty_area_h.area != 0;\n", DbType.mysql);
//        SQLSelectBuilder builderEx = new SQLSelectBuilderImpl(DbType.mysql);
        builderEx.select("househld_pty_id","stat_mon")
                .from("pmart.t98_station_engcspt_stat_month","a");
        builderEx.where("t01_pty_area_h.area_typ_cd = '00'")
                .whereAnd("t02_fac_point_pty_rel_h.fac_point_pty_rel_ctgy_cd = '06'")
                .whereAnd("t01_pty_area_h.area != 0");
        builderEx.join("inner join","pdata.t01_pty_area_h","b","a.househld_pty_id","b.pty_id","=");
        builderEx.join("inner join","pdata.t02_fac_point_pty_rel_h","c","a.househld_pty_id","c.fac_point_id","=");
        builderEx.join("inner join","pdata.t01_househld_pty","c","a.househld_pty_id","d.househld_pty_id","=");

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