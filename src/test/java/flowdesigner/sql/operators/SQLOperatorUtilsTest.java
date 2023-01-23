package flowdesigner.sql.operators;

import com.alibaba.druid.DbType;
import flowdesigner.db.DbUtils;
import flowdesigner.db.operators.SQLOperatorUtils;
import flowdesigner.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static flowdesigner.db.operators.SQLFunctionCatalog.ArithmeticOperator;
import static flowdesigner.db.operators.SQLFunctionCatalog.DatetimeFunctions;

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
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive,ArithmeticOperator, "mod"));
        System.out.println(SQLOperatorUtils.getFunctionSignature(DbType.hive,ArithmeticOperator, "%"));
    }


    @Test
    void getSupportFunctionsJson() {
        System.out.println(SQLOperatorUtils.getFunctionsJson(null, DbType.mysql));
    }

    /**
     * 将函数进行格式转换.抽取函数名、签名、类型.
     */
    @Test
    void getSupportFunctionsJson1() {
        HashMap<String, ArrayList<String>> hashMap = new HashMap<>();
        var functionInfo = SQLOperatorUtils.getFunctionInfo(null, DbType.mysql);
        for (var func1 : functionInfo) {
            String type = func1.getType();
            String name = func1.getName();
            String signature = func1.getSignature();

            ArrayList<String> orDefault = hashMap.getOrDefault(type, new ArrayList<>());
            orDefault.add(name + ":" + signature);

            hashMap.putIfAbsent(type, orDefault);
        }

        for (Map.Entry<String, ArrayList<String>> entry : hashMap.entrySet() ) {
            System.out.println("[" + entry.getKey() + "]");
            entry.getValue().forEach(System.out::println );
        }

    }

    @Test
    void getEnvironmentString() {
        System.out.println(DbUtils.getContextConfigurationString(DbType.hive));
    }

    @Test
    void getVariantString() {
        System.out.println(SQLUtils.parseContextDefinitionASString("mysql","set @Var1 = 2*1024*1024*10;"));
        System.out.println(SQLUtils.parseContextDefinitionASString("mysql","set global max_allowed_packet = 2*1024*1024*10;"));

    }

}