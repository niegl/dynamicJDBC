package flowdesigner.db;

import com.alibaba.druid.DbType;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DbUtilsTest {

    @Test
    void getFunctions() {
        ArrayList<DbUtils.FunctionInfo> supportFunctions2 = DbUtils.getFunctions(DbType.mysql);
        System.out.println(supportFunctions2);
    }

}