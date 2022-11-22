package flowdesigner.db.operators;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.fastjson2.JSON;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import static flowdesigner.db.DbUtils.*;

public class SQLOperatorUtils {

    /**
     * 用于向客户端输出JSON格式
     */
    @Data
    static class FunctionInfo {
        String name;
        String type;
        String signature;

        public FunctionInfo(String name, String type, String signature) {
            this.name = name;
            this.type = type;
            this.signature = signature;
        }
    }

    public static String getFunctionsJson(Connection connection, DbType dbType) {
        ArrayList<FunctionInfo> functionInfos = new ArrayList<>();

        Collection<String> functions = getFunctions(connection, dbType);
        for (String function: functions) {
            ArrayList<SQLOperator> operators = SQLOperator.of(dbType, function);
            if (operators.isEmpty()) {
                continue;
            }
            operators.forEach(sqlOperator -> {
                functionInfos.add(new FunctionInfo(sqlOperator.name, sqlOperator.catalog.toString(), sqlOperator.usage));
            });
        }

        return JSON.toJSONString(functionInfos);
    }

    /**
     * 获取函数签名- 处理后的函数签名。处理过程：常数填充默认值，字段填充?号; 对于有多个签名的函数，取最简洁的那一个。
     * 客户端使用时只需要把?号换成选择字段即可，多个?号的剩余客户自己改写。
     * @param dbType 数据库类型
     * @param catalog 函数类别
     * @param name 函数名称
     * @return 处理后的函数签名
     */
    public static String getFunctionSignature(DbType dbType, SQLFunctionCatalog catalog, String name) {

        SQLExpr expr = new SQLIdentifierExpr(name);

        SQLOperator sqlOperator = SQLOperator.of(dbType,catalog,name);
        if (sqlOperator == null) {
            return expr.toString();
        }

        return sqlOperator.usage;
    }

    /**
     * 获取函数类型：UnaryOperator=一元运算符；BinaryOperator=二元运算符；MethodInvoke=函数；AggregateFunction=聚合函数;0=其他函数
     * @param name 函数名称
     * @return 1=一元运算符；2=二元运算符；0=其他函数
     */
    public static String getFunctionType(DbType dbType, String name) {
        HashSet<SQLFunctionCatalog> catalogs = null;

        try {
            catalogs = SQLOperator.getCatalog(dbType, name);
        } catch (IllegalArgumentException ignored) {
        }

        return StringUtils.join(catalogs,',');
    }

    /**
     * 解析脚本中的需要用户输入的参数。比如hive中以${}为标志的变量
     * @param dbType 数据库类型
     * @param sql 脚本
     * @return 参数列表(带有${}的参数列表,方便客户端直接替换)
     */
    public static String getParameters(String dbType, String sql) {

        return dbType;
    }



    private static <T extends Enum<T>> T of(String name, T[] values) {
        for (T value : values) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }

        return null;
    }

}
