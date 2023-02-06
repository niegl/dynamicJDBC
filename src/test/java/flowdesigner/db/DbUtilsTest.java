package flowdesigner.db;

import com.alibaba.druid.DbType;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DbUtilsTest {

    @Test
    void getFunctions() {
        Set<DbUtils.FunctionInfo> supportFunctions2 = DbUtils.getFunctions(DbType.postgresql);
        System.out.println(supportFunctions2);
    }

    @Test
    void getDbKeywordsAsString() {
        String dbKeywordsAsString = DbUtils.getDbKeywordsAsString(DbType.mysql);
        System.out.println(dbKeywordsAsString);
    }
}