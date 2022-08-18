package flowdesigner.jdbc.operators;

import com.alibaba.druid.DbType;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static flowdesigner.jdbc.operators.SQLFunctionCatalog.DatetimeFunctions;
import static org.junit.jupiter.api.Assertions.*;

class SQLOperatorUtilsTest {

    @Test
    void getFunctionSignature() {
//        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "||"));
//        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "sum"));
//        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "substr"));
//        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "PRIOR"));
//        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "map"));
//        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "case"));
//        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "explode"));
//        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "percentile_approx"));
//        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "aes_encrypt"));
//        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "mask_last_n"));
//        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "substring"));
//        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "next_day"));
//        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "cast"));
//        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "array_contains"));
//        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "shiftrightunsigned"));
//        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "AND"));
//        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "+"));
//        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "<=>"));
//        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive, "DAYOFWEEK"));
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive,DatetimeFunctions, "date_add"));
    }

    @Test
    void testGetFunctionSignature() {

        DbType dbType = DbType.mysql;
        Set<String> supportFunctions = SQLOperatorUtils.getSupportFunctions(null, dbType);
        supportFunctions.forEach( f -> {
            HashSet<SQLFunctionCatalog> catalogs = SQLOperator.getCatalog(dbType, f);
            catalogs.forEach( c -> {
                String signature = SQLOperatorUtils.getFunctionSignature(dbType,c, f);
                System.out.println(c + ":" + signature);
            });
        });
    }

    @Test
    void getSupportFunctions() {
        System.out.println(SQLOperatorUtils.getSupportFunctions(null, DbType.hive));
    }

    @Test
    void getSupportFunctionsAsString() {
    }

    @Test
    void getSupportFunctionsJson() {
        System.out.println(SQLOperatorUtils.getSupportFunctionsJson(null, DbType.mysql));
    }

    @Test
    void getEnvironmentString() {
        System.out.println(SQLOperatorUtils.getEnvironmentString(DbType.hive));
    }

    @Test
    void getVariantString() {
        System.out.println(SQLOperatorUtils.getVariantString("mysql","set global max_allowed_packet = 2*1024*1024*10;"));
    }
}