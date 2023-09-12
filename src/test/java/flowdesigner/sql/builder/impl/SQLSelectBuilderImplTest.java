package flowdesigner.sql.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import flowdesigner.sql.builder.SQLBuilderFactory;
import flowdesigner.sql.builder.SQLSelectBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import sqlTest.SQLTest;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.stream.Stream;

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
                .from("tableB");
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
    void from2() {
        SQLSelectBuilder builder = SQLBuilderFactory.createSelectSQLBuilder(DbType.mysql);
        builder.select("a", "b");
        builder.from("AT");
        builder.join("COMMA","BT", null,null,null,null);
        builder.join("COMMA","CT", null,null,null,null);
                System.out.println(builder);
    }
    @Test
    void test_select_hive() {
        SQLSelectBuilder builder = SQLBuilderFactory.createSelectSQLBuilder(DbType.hive);
        builder.select("t3.gregorian_date");

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
    void testUnionJoin() throws SQLSyntaxErrorException {
        SQLStatement statement = SQLTest.parser(" SELECT *\n" +
                "    FROM (VALUES ('p','q'),('x','y')) AS TableLiteral(Col1, Col2)", DbType.mysql);
        SQLStatement statement0 = SQLTest.parser("select A.id  from pmart.t97_input_engcspt_sum A union all select B.engcspt_rel_ordr_num  from pmart.t98_engcspt_rel_day_stat B ORDER BY id", DbType.mysql);
        SQLStatement statement1 = SQLTest.parser("SELECT * FROM (\n" +
                "select A.id  from pmart.t97_input_engcspt_sum A union all select B.engcspt_rel_ordr_num  from pmart.t98_engcspt_rel_day_stat B ) C ORDER BY C.id", DbType.mysql);
        SQLStatement statement2 = SQLTest.parser("SELECT  * from (select A.id  from pmart.t97_input_engcspt_sum A union all select B.engcspt_rel_ordr_num  from pmart.t98_engcspt_rel_day_stat B) J join pmart.t98_engcspt_rel_mth_stat C on J.id = C.engcspt_rel_ordr_num ", DbType.mysql);

        SQLSelectBuilder builder2 = SQLBuilderFactory.createSelectSQLBuilder(DbType.mysql);
        builder2.select("c", "d")
                .from("test.building_t"
                        ,"tt");

        SQLSelectBuilder builder3 = SQLBuilderFactory.createSelectSQLBuilder(DbType.mysql);
        builder3.select("e", "f")
                .from("test.building_t2"
                        ,"tt");
        builder2.union(builder3, "DISTINCT");

        builder2.join("inner join","pdata.t01_househld_pty","c","a.househld_pty_id","d.househld_pty_id","=");

        builder2.select("*");

        SQLTest.parser(builder2.toString(),DbType.mysql);

    }

    @Test
    void testUnionWithWhere() throws SQLSyntaxErrorException {

        SQLSelectBuilder builder2 = SQLBuilderFactory.createSelectSQLBuilder(DbType.mysql);
        builder2.select("c", "d")
                .from("test.building_t" ,"tt")
                .where("a != 1");

        SQLSelectBuilder builder3 = SQLBuilderFactory.createSelectSQLBuilder(DbType.mysql);
        builder3.select("e", "f")
                .from("test.building_t2","tt")
                .where("b != 1");

        builder2.union(builder3, "DISTINCT");

        SQLTest.parser(builder2.toString(),DbType.mysql);

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
    void withSubqueryClause() throws SQLSyntaxErrorException {
        builderEx.select("a","b");
        builderEx.from("LL");

        SQLExpr expr = SQLUtils.toSQLExpr("select * from std_pcode.t98_std_line");
        if (expr instanceof SQLQueryExpr queryExpr) {
            SQLSelect subQuery = queryExpr.getSubQuery();
            if (subQuery != null) {
                builderEx.addWithSubqueryClause("LL",subQuery);
            }
        }

    }

    @ParameterizedTest
    @MethodSource()
    void union(DbType dbType, String expected) throws SQLSyntaxErrorException {
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

        SQLTest.parser(builder.toString(),dbType);
        Assertions.assertEquals(expected, builder.toString());
    }
    static Stream<Arguments> union() {
        ArrayList<Arguments> arguments = new ArrayList<>();
        for (DbType dbType : DbType.values()) {
            String syntax =  switch (dbType) {
                default -> "SELECT a, b\n" +
                        "FROM (\n" +
                        "\tSELECT building_t.idbuilding_t, building_t.name, building_t.floors, campus_t.idcampus_t\n" +
                        "\tFROM test.building_t\n" +
                        "\t\tINNER JOIN test.campus_t ON building_t.campus_id = campus_t.idcampus_t\n" +
                        ") vv\n" +
                        "UNION\n" +
                        "SELECT c, d\n" +
                        "FROM test.building_t tt\n" +
                        "UNION DISTINCT\n" +
                        "SELECT e, f\n" +
                        "FROM test.building_t2 tt";
            };
            arguments.add(Arguments.of(dbType, syntax));
        }

        return arguments.stream();
    }

    @ParameterizedTest
    @MethodSource()
    void join(DbType dbType, String expected) throws SQLSyntaxErrorException {
        builderEx = SQLBuilderFactory.createSelectSQLBuilder( dbType);
        builderEx.select("househld_pty_id","stat_mon")
                .from("pmart.t98_station_engcspt_stat_month","a");
        builderEx.where("t01_pty_area_h.area_typ_cd = '00'")
                .whereAnd("t02_fac_point_pty_rel_h.fac_point_pty_rel_ctgy_cd = '06'")
                .whereAnd("t01_pty_area_h.area != 0");
        builderEx.join("left join","pdata.t01_pty_area_h","b","a.househld_pty_id","b.pty_id","=");
        builderEx.join("right join","pdata.t02_fac_point_pty_rel_h","c","a.househld_pty_id","c.fac_point_id","=");
        builderEx.join("inner join","pdata.t01_househld_pty","c","a.househld_pty_id","d.househld_pty_id","=");

        SQLTest.parser(builderEx.toString(),dbType);
        Assertions.assertEquals(expected, builderEx.toString());
    }
    static Stream<Arguments> join() {
        ArrayList<Arguments> arguments = new ArrayList<>();
        for (DbType dbType : DbType.values()) {
            String syntax =  switch (dbType) {
                case odps -> "SELECT househld_pty_id, stat_mon\n" +
                        "FROM pmart.t98_station_engcspt_stat_month a\n" +
                        "INNER JOIN pdata.t01_pty_area_h b\n" +
                        "ON a.househld_pty_id = b.pty_id\n" +
                        "INNER JOIN pdata.t02_fac_point_pty_rel_h c\n" +
                        "ON a.househld_pty_id = c.fac_point_id\n" +
                        "INNER JOIN pdata.t01_househld_pty c\n" +
                        "ON a.househld_pty_id = d.househld_pty_id\n" +
                        "WHERE b.area_typ_cd = '00'\n" +
                        "\tAND c.fac_point_pty_rel_ctgy_cd = '06'\n" +
                        "\tAND b.area != 0";
                default -> "SELECT househld_pty_id, stat_mon\n" +
                        "FROM pmart.t98_station_engcspt_stat_month a\n" +
                        "\tINNER JOIN pdata.t01_pty_area_h b ON a.househld_pty_id = b.pty_id\n" +
                        "\tINNER JOIN pdata.t02_fac_point_pty_rel_h c ON a.househld_pty_id = c.fac_point_id\n" +
                        "\tINNER JOIN pdata.t01_househld_pty c ON a.househld_pty_id = d.househld_pty_id\n" +
                        "WHERE b.area_typ_cd = '00'\n" +
                        "\tAND c.fac_point_pty_rel_ctgy_cd = '06'\n" +
                        "\tAND b.area != 0";
            };
            arguments.add(Arguments.of(dbType, syntax));
        }

        return arguments.stream();
    }

    @ParameterizedTest
    @MethodSource()
    void orderBy(DbType dbType, String expected) throws SQLSyntaxErrorException {
        builderEx = SQLBuilderFactory.createSelectSQLBuilder( dbType);
        builderEx.select("a","b")
            .from("tablea","a")
            .where("data_dt='2022-10'");
        builderEx.orderBy("root_t.a asc","b");
        SQLTest.parser(expected,dbType);
        Assertions.assertEquals(expected, builderEx.toString());
    }
    static Stream<Arguments> orderBy() {
        ArrayList<Arguments> arguments = new ArrayList<>();
        for (DbType dbType : DbType.values()) {
            String syntax =  switch (dbType) {
                case odps -> "SELECT a, b\n" +
                        "FROM tablea a\n" +
                        "WHERE data_dt = '2022-10'\n" +
                        "ORDER BY root_t.a ASC, \n" +
                        "\tb";
                default -> "SELECT a, b\n" +
                        "FROM tablea a\n" +
                        "WHERE data_dt = '2022-10'\n" +
                        "ORDER BY root_t.a ASC, b";
            };
            arguments.add(Arguments.of(dbType, syntax));
        }

        return arguments.stream();
    }

    @ParameterizedTest
    @MethodSource()
    void where(DbType dbType, String expected) throws SQLSyntaxErrorException {
        builderEx = SQLBuilderFactory.createSelectSQLBuilder( dbType);
        builderEx.select("a","b")
                .from("tablea","a")
                .where("data_dt='2022-10'");
        SQLTest.parser(expected,dbType);
        Assertions.assertEquals(expected, builderEx.toString());
    }
    static Stream<Arguments> where() {
        ArrayList<Arguments> arguments = new ArrayList<>();
        for (DbType dbType : DbType.values()) {
            String syntax =  switch (dbType) {
                default -> "SELECT a, b\n" +
                        "FROM tablea a\n" +
                        "WHERE data_dt = '2022-10'";
            };
            arguments.add(Arguments.of(dbType, syntax));
        }

        return arguments.stream();
    }

    @Test
    void testJoinWhere() {
        builderEx.select("a/b","b")
                .from("tablea");
        builderEx.where("tableB.a = 1").whereAnd("tablea.b=2").whereAnd("tableB.c=3");
        // 测试别名更改后是否应用于前面的where
        builderEx.join("COMMA","tableB","B",null,null,null);
    }

    @Test
    void setDistionOption() {
        builderEx.setDistionOption();
        builderEx.select("a/b","b")
                .from("tablea");
        System.out.println(builderEx);
        builderEx.setDistionOption();
        System.out.println(builderEx);
    }
}