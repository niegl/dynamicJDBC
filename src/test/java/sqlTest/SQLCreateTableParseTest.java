package sqlTest;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import org.junit.jupiter.api.Test;

import java.sql.SQLSyntaxErrorException;

public class SQLCreateTableParseTest {
    @Test
    void test() {
        SQLCreateTableParser createTableParser = new SQLCreateTableParser("CREATE TEMPORARY TABLE IF NOT EXISTS `t_line_person_times_traction_engcspt_period_st` (\n" +
                "\t`househld_pty_id` varchar(8),\n" +
                "\t`line_id` varchar,\n" +
                "\t`stat_dt` string,\n" +
                "\t`stat_indx_cd` varchar,\n" +
                "\t`trc_engcst` decimal\n" +
                ");\n");
        SQLCreateTableStatement createTableStatement = createTableParser.parseCreateTable();
        System.out.println(createTableStatement);
    }
    @Test
    void test0() throws SQLSyntaxErrorException {
        SQLStatement statement = SQLTest.parser("CREATE EXTERNAL TABLE std_pcode.std_line (\n" +
                "\tline_id Stirng\n" +
                ")", DbType.mysql);
        System.out.println(statement);
    }
}
