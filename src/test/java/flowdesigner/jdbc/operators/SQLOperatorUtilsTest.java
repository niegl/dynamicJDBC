package flowdesigner.jdbc.operators;

import com.alibaba.druid.DbType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLOperatorUtilsTest {

    @Test
    void getFunctionSignature() {
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "||"));
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "sum"));
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "substr"));
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "PRIOR"));
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "map"));
    }

    @Test
    void testGetFunctionSignature() {

    }

    @Test
    void getSupportFunctions() {
        System.out.println(SQLOperatorUtils.getSupportFunctions(null));
    }
}