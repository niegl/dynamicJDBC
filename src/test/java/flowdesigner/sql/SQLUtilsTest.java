package flowdesigner.sql;

import com.alibaba.druid.DbType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLUtilsTest {

    @Test
    void checkSyntax() {
        String sql = "ALTER TABLE std_operation.tbl_log ADD IF NOT EXISTS PARTITION (data_Dt = '${erg_date}');";
        String resuslt = SQLUtils.checkSyntax(sql, DbType.hive);
        System.out.println(resuslt);
    }

    @Test
    void formatSQL() {
        String sql = "SELECT * FROM (SELECT * FROM bmnc_pmart.t98_od_route_dd WHERE data_dt = '2022-03-09') C;SELECT * FROM bmnc_pmart.t98_od_route_dd WHERE data_dt = '2022-03-09';";
        String resuslt = com.alibaba.druid.sql.SQLUtils.format(sql, DbType.hive);
        System.out.println(resuslt);
    }

    @Test
    void parseContextDefinitionASString() {
        String ret = SQLUtils.parseContextDefinitionASString("hive","a=1");
        System.out.println(ret);
    }

    @Test
    void getFrom() {
        String ret = SQLUtils.getFrom("SELECT REC_SEQ, SACCCODE_ARR_DEP FROM  ${BMNC_SDATA}.cut_pi_exit_cms_filtered aa join a.bb on aa.a=bb.a", "hive");
        System.out.println(ret);
    }
}