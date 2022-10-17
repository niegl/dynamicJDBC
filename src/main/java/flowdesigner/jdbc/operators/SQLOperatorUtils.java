package flowdesigner.jdbc.operators;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLDescribeStatement;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.util.Utils;
import com.alibaba.fastjson2.JSON;
import flowdesigner.jdbc.command.CommandKey;
import flowdesigner.jdbc.command.CommandManager;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.util.sql.DbTypeKit;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * 系统变量
     */
    @Data
    private static class VariableInfo {
        /**
         * 系统变量名
         */
        String name;
        /**
         * 默认值
         */
        String defaultValue;

        public VariableInfo(String name, String defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }
    }

    public SQLOperatorUtils() {
    }


    public static String getSupportFunctionsJson(Connection connection, DbType dbType) {
        ArrayList<FunctionInfo> functionInfos = new ArrayList<>();

        Collection<String> functions = getSupportFunctions(connection, dbType);
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
     * 获取当前数据库支持的函数列表。为了尽量保证函数完整，返回的函数为两部分组成：配置函数+获取函数（如果获取不到就默认）
     * @param connection 当前数据库连接
     * @return 函数列表
     */
    public static Set<String> getSupportFunctions(Connection connection, DbType dbType) {
        Set<String> functions = new HashSet<>();

        // 实际数据库中可能获取的不全，需要配置补充
        if (dbType.equals(DbType.oracle)) {
            Utils.loadFromFile("META-INF/druid/parser/oracle/builtin_functions", functions);
        } else if (dbType.equals(DbType.mysql)) {
            Utils.loadFromFile("META-INF/druid/parser/mysql/builtin_functions", functions);
        } else if (dbType.equals(DbType.hive)) {
            Utils.loadFromFile("META-INF/druid/parser/hive/builtin_functions", functions);
        } else if (dbType.equals(DbType.postgresql)) {
            Utils.loadFromFile("META-INF/druid/parser/postgresql/builtin_functions", functions);
        } else if (dbType.equals(DbType.db2)) {
            Utils.loadFromFile("META-INF/druid/parser/db2/builtin_functions", functions);
        } else if (dbType.equals(DbType.mariadb)) {
            Utils.loadFromFile("META-INF/druid/parser/maria/builtin_functions", functions);
        } else if (dbType.equals(DbType.sqlserver)) {
            Utils.loadFromFile("META-INF/druid/parser/sqlserver/builtin_functions", functions);
        } else if (dbType.equals(DbType.sybase)) {
            Utils.loadFromFile("META-INF/druid/parser/sybase/builtin_functions", functions);
        } else if (dbType.equals(DbType.derby)) {
            Utils.loadFromFile("META-INF/druid/parser/derby/builtin_functions", functions);
        } else if (dbType.equals(DbType.h2)) {
            Utils.loadFromFile("META-INF/druid/parser/h2/builtin_functions", functions);
        } else if (dbType.equals(DbType.dm)) {
            Utils.loadFromFile("META-INF/druid/parser/dm/builtin_functions", functions);
        } else if (dbType.equals(DbType.kingbase)) {
            Utils.loadFromFile("META-INF/druid/parser/kingbase/builtin_functions", functions);
        } else if (dbType.equals(DbType.gbase)) {
            Utils.loadFromFile("META-INF/druid/parser/gbase/builtin_functions", functions);
        } else if (dbType.equals(DbType.oceanbase)) {
            Utils.loadFromFile("META-INF/druid/parser/oceanbase/builtin_functions", functions);
        } else if (dbType.equals(DbType.informix)) {
            Utils.loadFromFile("META-INF/druid/parser/Informix/builtin_functions", functions);
        }

        // 如果没有获取到任何函数也没有配置函数，那么返回默认
        if (functions.isEmpty()) {
            functions.addAll(Arrays.asList("AVG", "COUNT", "MAX", "MIN", "SUM"));
            functions.addAll(Arrays.asList("ABS",
                    "ACOS",
                    "ASIN",
                    "ATAN",
                    "ATAN2",
                    "BIT_COUNT",
                    "CEIL",
                    "CEILING",
                    "CONV",
                    "COS",
                    "COT",
                    "CRC32",
                    "DEGREES",
                    "EXP",
                    "FLOOR",
                    "LN",
                    "LOG",
                    "LOG10",
                    "LOG2",
                    "MOD",
                    "NEG",
                    "PI",
                    "POW",
                    "POWER",
                    "RADIANS",
                    "RAND",
                    "ROUND",
                    "SIGN",
                    "SIN",
                    "SQRT",
                    "TAN",
                    "TRUNCATE"));
        }

        return functions;
    }

    /**
     * 获取函数的使用说明
     * @param connection 当前数据库连接
     * @param name 通过getSupportFunctions接口获取到的函数名称
     * @return 函数描述
     */
    public static String getFunctionDescription(Connection connection, String name) {
        String ret = "";

        DbType dbType = DbTypeKit.getDbType(connection);

        SQLDescribeStatement stmt = new SQLDescribeStatement();
        stmt.setDbType(dbType);
        stmt.setObject(new SQLIdentifierExpr("function"));
        stmt.setColumn(new SQLIdentifierExpr(name));

        ExecResult<ResultSet> execResult = CommandManager.exeCommand(connection, CommandKey.CMD_DBExecuteCommandImpl, new HashMap<String,String>(){{
            put("SQL",stmt.toString());
        }});

        if (execResult.getStatus().equalsIgnoreCase(ExecResult.FAILED)) {
            return ret;
        }

        ResultSet rs = execResult.getBody();
        while (true) {
            try {
                if (!rs.next()) break;
                ret = (rs.getString("tab_name"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return ret;
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
     * 返回用于定义数据库执行上下文的属性(配置参数)，可覆盖覆盖hive-site.xml（hive-default.xml）中的参数值
     * @param dbType 数据库类型
     * @return 可用环境，如：
     * set ngmr.furion.pool=DEFAULT;
     * set hive.support.concurrency=false;
     * set hive.groupby.position.alias = true;
     * set hive.auto.convert.join=true;
     *
     * set hive.fetch.task.conversion=more;
     * set mapred.max.split.size=50000000; --50M指的是数据的最大分割单元大小；max的默认值是256MB
     * set hive.exec.reducers.bytes.per.reducer=50000000; --（50M（每个reduce任务处理的数据量，默认为1000^3=1G））
     * set hive.exec.reducers.max=999;  --（每个任务最大的reduce数，默认为999）
     * set hive.groupby.skewindata = true;
     *
     * set hive.exec.parallel=true; --可以开启并发执行
     * set hive.exec.parallel.thread.number=16; --同一个sql允许最大并行度，默认为8
     * set hive.exec.dynamic.partition=true;
     * set hive.exec.dynamic.partition.mode=nonstrict;
     * set hive.enforce.bucketing=true;
     */
    public static List<VariableInfo> getContextConfiguration(DbType dbType) {
        Set<String> variants = new HashSet<>();

        if (dbType.equals(DbType.oracle)) {
            //Utils.loadFromFile("META-INF/druid/parser/oracle/builtin_functions", functions);
        } else if (dbType.equals(DbType.mysql)) {
            Utils.loadFromFile("META-INF/druid/parser/mysql/builtin_variables", variants);
        } else if (dbType.equals(DbType.hive)) {
            Utils.loadFromFile("META-INF/druid/parser/hive/builtin_variables", variants);
        }
//        } else if (dbType.equals(DbType.postgresql)) {
//            //Utils.loadFromFile("META-INF/druid/parser/postgresql/builtin_functions", variants);
//        } else if (dbType.equals(DbType.db2)) {
//            Utils.loadFromFile("META-INF/druid/parser/db2/builtin_functions", variants);
//        } else if (dbType.equals(DbType.mariadb)) {
//            Utils.loadFromFile("META-INF/druid/parser/maria/builtin_functions", functions);
//        } else if (dbType.equals(DbType.sqlserver)) {
//            Utils.loadFromFile("META-INF/druid/parser/sqlserver/builtin_functions", functions);
//        } else if (dbType.equals(DbType.sybase)) {
//            Utils.loadFromFile("META-INF/druid/parser/sybase/builtin_functions", functions);
//        } else if (dbType.equals(DbType.derby)) {
//            Utils.loadFromFile("META-INF/druid/parser/derby/builtin_functions", functions);
//        } else if (dbType.equals(DbType.h2)) {
//            Utils.loadFromFile("META-INF/druid/parser/h2/builtin_functions", functions);
//        } else if (dbType.equals(DbType.dm)) {
//            Utils.loadFromFile("META-INF/druid/parser/dm/builtin_functions", functions);
//        } else if (dbType.equals(DbType.kingbase)) {
//            Utils.loadFromFile("META-INF/druid/parser/kingbase/builtin_functions", functions);
//        } else if (dbType.equals(DbType.gbase)) {
//            Utils.loadFromFile("META-INF/druid/parser/gbase/builtin_functions", functions);
//        } else if (dbType.equals(DbType.oceanbase)) {
//            Utils.loadFromFile("META-INF/druid/parser/oceanbase/builtin_functions", functions);
//        } else if (dbType.equals(DbType.informix)) {
//            Utils.loadFromFile("META-INF/druid/parser/Informix/builtin_functions", functions);
//        }

        return variants.stream().map(f -> {
            String[] split = f.split("=");
            String defaultValue = split.length == 2? split[1]:"";
            return new VariableInfo(split[0], defaultValue);
        }).collect(Collectors.toList());
    }

    public static String getContextConfigurationString(DbType dbType) {
        List<VariableInfo> list = getContextConfiguration(dbType);
        return JSON.toJSONString(list);
    }

    /**
     * 获取用户定义变量
     * @param dbType 数据库类型
     * @param sql set SQL语句
     * @return 变量列表
     */
    public static List<String> parseContextDefinition(String dbType, String sql) {
        List<SQLStatement> list = SQLUtils.parseStatements(sql, dbType);

        return list.stream()
                .filter(s -> s instanceof SQLSetStatement)
                .flatMap(s -> {
                    Collection<SQLAssignItem> items = ((SQLSetStatement) s).getItems();
                    items.removeIf(i -> i.getTarget() instanceof SQLPropertyExpr);
                    return items.stream().map(i -> i.getTarget().toString().replaceAll("@","") + "=" + i.getValue());
                })
                .toList();
    }

    public static String parseContextDefinitionASString(String dbType, String sql) {
        Collection<String> list = parseContextDefinition(dbType, sql);
        return StringUtils.join(list,',');
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

    /**
     * 上下文配置的设置语法
     * @param dbType 数据库类型
     * @return 语法格式，变量用?代替
     */
    public static String getContextSetGrammar(DbType dbType) {
        String grammar = "SET ?= ?;";

        switch (dbType) {

            case other -> {
            }
            case jtds -> {
            }
            case hsql -> {
            }
            case db2 -> {
            }
            case postgresql -> {
            }
            case sqlserver -> {
            }
            case oracle -> {
            }
            case mysql -> {
                grammar = "SET @?= ?;";
            }
            case mariadb -> {
            }
            case derby -> {
            }
            case hive -> {
                grammar = "SET ?= ?;";
            }
            case h2 -> {
            }
            case dm -> {
            }
            case kingbase -> {
            }
            case gbase -> {
            }
            case oceanbase -> {
            }
            case informix -> {
            }
            case odps -> {
            }
            case teradata -> {
            }
            case phoenix -> {
            }
            case edb -> {
            }
            case kylin -> {
            }
            case sqlite -> {
            }
            case ads -> {
            }
            case presto -> {
            }
            case elastic_search -> {
            }
            case hbase -> {
            }
            case drds -> {
            }
            case clickhouse -> {
            }
            case blink -> {
            }
            case antspark -> {
            }
            case oceanbase_oracle -> {
            }
            case polardb -> {
            }
            case ali_oracle -> {
            }
            case mock -> {
            }
            case sybase -> {
            }
            case highgo -> {
            }
            case greenplum -> {
            }
            case gaussdb -> {
            }
            case trino -> {
            }
            case oscar -> {
            }
            case tidb -> {
            }
            case tydb -> {
            }
            case ingres -> {
            }
            case cloudscape -> {
            }
            case timesten -> {
            }
            case as400 -> {
            }
            case sapdb -> {
            }
            case kdb -> {
            }
            case log4jdbc -> {
            }
            case xugu -> {
            }
            case firebirdsql -> {
            }
            case JSQLConnect -> {
            }
            case JTurbo -> {
            }
            case interbase -> {
            }
            case pointbase -> {
            }
            case edbc -> {
            }
            case mimer -> {
            }
        }

        return grammar;
    }
    /**
     * 环境变量的使用语法
     * @param dbType 数据库类型
     * @return 语法格式，变量用?代替
     */
    public static String getContextUseGrammar(DbType dbType) {
        String grammar = "${?}";
        switch (dbType) {

            case other -> {
            }
            case jtds -> {
            }
            case hsql -> {
            }
            case db2 -> {
            }
            case postgresql -> {
            }
            case sqlserver -> {
            }
            case oracle -> {
            }
            case mysql -> {
                grammar = "@?";
            }
            case mariadb -> {
            }
            case derby -> {
            }
            case hive -> {
                grammar = "${hiveconf:?}";
            }
            case h2 -> {
            }
            case dm -> {
            }
            case kingbase -> {
            }
            case gbase -> {
            }
            case oceanbase -> {
            }
            case informix -> {
            }
            case odps -> {
            }
            case teradata -> {
            }
            case phoenix -> {
            }
            case edb -> {
            }
            case kylin -> {
            }
            case sqlite -> {
            }
            case ads -> {
            }
            case presto -> {
            }
            case elastic_search -> {
            }
            case hbase -> {
            }
            case drds -> {
            }
            case clickhouse -> {
            }
            case blink -> {
            }
            case antspark -> {
            }
            case oceanbase_oracle -> {
            }
            case polardb -> {
            }
            case ali_oracle -> {
            }
            case mock -> {
            }
            case sybase -> {
            }
            case highgo -> {
            }
            case greenplum -> {
            }
            case gaussdb -> {
            }
            case trino -> {
            }
            case oscar -> {
            }
            case tidb -> {
            }
            case tydb -> {
            }
            case ingres -> {
            }
            case cloudscape -> {
            }
            case timesten -> {
            }
            case as400 -> {
            }
            case sapdb -> {
            }
            case kdb -> {
            }
            case log4jdbc -> {
            }
            case xugu -> {
            }
            case firebirdsql -> {
            }
            case JSQLConnect -> {
            }
            case JTurbo -> {
            }
            case interbase -> {
            }
            case pointbase -> {
            }
            case edbc -> {
            }
            case mimer -> {
            }
        }

        return  grammar;
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
