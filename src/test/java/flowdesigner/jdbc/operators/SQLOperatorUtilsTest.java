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
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "case"));
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "explode"));
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "percentile_approx"));
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "aes_encrypt"));
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "mask_last_n"));
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "substring"));
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "next_day"));
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "cast"));
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "array_contains"));
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "shiftrightunsigned"));
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "AND"));
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "+"));
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "<=>"));
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "DAYOFWEEK"));

    }

    @Test
    void testGetFunctionSignature() {

    }

    @Test
    void getSupportFunctions() {
        System.out.println(SQLOperatorUtils.getSupportFunctions(null, DbType.hive));
    }
}