package sqlTest;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import org.junit.jupiter.api.Test;

public class SQLCreateTableParseTest {
    @Test
    void test() {
        SQLCreateTableParser createTableParser = new SQLCreateTableParser("create temporary table aa as select a from B");
        SQLCreateTableStatement createTableStatement = createTableParser.parseCreateTable();
        System.out.println(createTableStatement);
    }
}
