package flowdesigner.db;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLDescribeStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.fastjson2.JSON;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.command.impl.DBExecuteImpl;
import flowdesigner.util.DbTypeKit;
import flowdesigner.util.Utils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据库级别相关的数据对象、操作方法等
 */
public class DbUtils {

    /**
     * 返回数据库关键字
     * @param dbType 数据库类型
     * @return 以逗号分隔的关键字列表
     */
    public static String getDbKeywordsAsString(DbType dbType) {

        Set<String> keywords = new HashSet<>();

        switch (dbType) {
            case ads -> Utils.loadFromFile("META-INF/druid/parser/ads/builtin_keywords", keywords);
            case dm -> Utils.loadFromFile("META-INF/druid/parser/dm/builtin_keywords", keywords);
            case kingbase -> Utils.loadFromFile("META-INF/druid/parser/kingbase/builtin_keywords", keywords);
            case mysql -> Utils.loadFromFile("META-INF/druid/parser/mysql/builtin_keywords", keywords);
            case oracle -> Utils.loadFromFile("META-INF/druid/parser/oracle/builtin_keywords", keywords);
            case hive -> Utils.loadFromFile("META-INF/druid/parser/hive/builtin_keywords", keywords);
            case postgresql -> Utils.loadFromFile("META-INF/druid/parser/postgresql/builtin_keywords", keywords);
            case db2 -> Utils.loadFromFile("META-INF/druid/parser/db2/builtin_keywords", keywords);
            case mariadb -> Utils.loadFromFile("META-INF/druid/parser/mariadb/builtin_keywords", keywords);
            case presto -> Utils.loadFromFile("META-INF/druid/parser/presto/builtin_keywords", keywords);
            case sqlserver -> Utils.loadFromFile("META-INF/druid/parser/sqlserver/builtin_keywords", keywords);
            case sapdb -> Utils.loadFromFile("META-INF/druid/parser/sapdb/builtin_keywords", keywords);
            case derby -> Utils.loadFromFile("META-INF/druid/parser/derby/builtin_keywords", keywords);
            case elastic_search -> Utils.loadFromFile("META-INF/druid/parser/elastic_search/builtin_keywords", keywords);
            case h2 -> Utils.loadFromFile("META-INF/druid/parser/h2/builtin_keywords", keywords);
            case hsql -> Utils.loadFromFile("META-INF/druid/parser/hsql/builtin_keywords", keywords);
            case blink -> Utils.loadFromFile("META-INF/druid/parser/blink/builtin_keywords", keywords);
            case odps -> Utils.loadFromFile("META-INF/druid/parser/maxcompute/builtin_keywords", keywords);
            default -> {}
        }

        if (keywords.isEmpty()) {
            Lexer lexer = SQLParserUtils.createLexer("select *", dbType);
            keywords = lexer.getKeywords().getKeywords().keySet();
        }

        return StringUtils.join(keywords, ",");
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
        List<String> variants = new ArrayList<>();

        if (dbType.equals(DbType.oracle)) {
            //Utils.loadFromFile("META-INF/druid/parser/oracle/builtin_variables", variants);
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
     * 上下文配置的设置语法
     * @param dbType 数据库类型
     * @return 语法格式，变量用?代替
     */
    public static String getContextSetGrammar(DbType dbType) {
        String grammar = "SET ?= ?;";

//        switch (dbType) {
//            case oracle -> {
//                grammar = " ?:= ?;";
//            }
//            case mysql -> {
//                grammar = "SET @?= ?;";
//            }
//        }

        return grammar;
    }

    /**
     * Dynamic Parameter Bindings
     * You can use dynamic parameters and variables in your SQL queries. The parameter format is :name.
     * Also, variables syntax could be used as ${varname}. When you execute a query that contains dynamic parameters,
     * DBeaver displays a dialog box in which you can fill the parameter values.
     * @param dbType 数据库类型
     * @return 语法格式，变量用?代替
     */
    public static String getContextUseGrammar(DbType dbType) {
        String grammar = "${?}";

//        switch (dbType) {
//            case mysql -> grammar = "@?";
//            /**
//             * There are three namespaces for variables – hiveconf, system, and env.
//             * (Custom variables can also be created in a separate namespace with the define or hivevar option in Hive 0.8.0 and later releases.)
//             * hiveconf 是默认的namespace
//             */
//            case hive -> grammar = "${hiveconf:?}";
//
//        }

        return  grammar;
    }

    /**
     * 获取当前数据库支持的函数列表。为了尽量保证函数完整，返回的函数为两部分组成：配置函数+获取函数（如果获取不到就默认）
     * @return 函数列表
     */
    public static Set<FunctionInfo> getFunctions( DbType dbType) {
        List<String> functions = new ArrayList<>();

        // 实际数据库中可能获取的不全，需要配置补充
        switch (dbType) {
            case oracle ->Utils.loadFromFile("META-INF/druid/parser/oracle/builtin_functions", functions);
            case mysql ->Utils.loadFromFile("META-INF/druid/parser/mysql/builtin_functions", functions);
            case hive ->Utils.loadFromFile("META-INF/druid/parser/hive/builtin_functions", functions);
            case postgresql ->Utils.loadFromFile("META-INF/druid/parser/postgresql/builtin_functions", functions);
            case db2 ->Utils.loadFromFile("META-INF/druid/parser/db2/builtin_functions", functions);
            case mariadb ->Utils.loadFromFile("META-INF/druid/parser/maria/builtin_functions", functions);
            case sqlserver ->Utils.loadFromFile("META-INF/druid/parser/sqlserver/builtin_functions", functions);
            case sapdb ->Utils.loadFromFile("META-INF/druid/parser/sapdb/builtin_functions", functions);
            case derby ->Utils.loadFromFile("META-INF/druid/parser/derby/builtin_functions", functions);
            case h2 ->Utils.loadFromFile("META-INF/druid/parser/h2/builtin_functions", functions);
            case dm ->Utils.loadFromFile("META-INF/druid/parser/dm/builtin_functions", functions);
            case kingbase ->Utils.loadFromFile("META-INF/druid/parser/kingbase/builtin_functions", functions);
            case gbase ->Utils.loadFromFile("META-INF/druid/parser/gbase/builtin_functions", functions);
            case oceanbase ->Utils.loadFromFile("META-INF/druid/parser/oceanbase/builtin_functions", functions);
        }

        /**
         * 有些数据库没有配置相应的函数时，加载基本的+-*\/
         */
        if (functions.isEmpty()) {
            functions.addAll(Arrays.asList("*:? * ? : Multiplication operator",
                    "+:? + ?: Addition operator",
                    "-:? - ? :Minus operator",
                    "-: - ? : Change the sign of the argument",
                    "/:? / ? : Division operator"));
        }

        Set<FunctionInfo> functionInfos = new HashSet<>();

        String subCatalog = "";
        String catalog = "";
        for (String function: functions) {
            if (function.startsWith("[-->") && function.endsWith("]")) {
                subCatalog = catalog + function.substring(1, function.lastIndexOf("]"));
                continue;
            } else if (function.startsWith("[") && function.endsWith("]")) {
                catalog = function.substring(1, function.lastIndexOf("]"));
                subCatalog = catalog;
                continue;
            }

            String separator = ":";
            if (function.contentEquals(":=")) {
                separator = ":=";
            }

            // 对内容中存在的':'或“:”做替换处理。以免拆分错误。
            if (function.contains("':'")) {
                function = function.replaceAll("':'","'：'");
            }  else if (function.contains("\":\"")) {
                function = function.replaceAll("\":\"","\"：\"");
            }

            String[] split = function.split(separator);
            if (split.length < 2) {
                continue;
            }

            // 替换后恢复
            List<String> segments = Arrays.stream(split).map(c -> c.replaceAll("：", ":")).toList();

            String name = segments.get(0).trim();
            String signature = segments.get(1).trim();
            String description = segments.size() == 3? segments.get(2).trim():"" ;

            functionInfos.add(new FunctionInfo(name, subCatalog, signature,description));
        }

        return functionInfos;
    }

    /**
     * 获取数据库离线数据类型
     * @param dbType 数据库类型
     * @return 数据类型列表
     */
    public static Set<String> getDbTypes(DbType dbType) {
        HashSet<String> types = new HashSet<String>();

        switch (dbType) {
            case oracle -> Utils.loadFromFile("META-INF/druid/parser/oracle/builtin_datatypes", types);
            case mysql -> Utils.loadFromFile("META-INF/druid/parser/mysql/builtin_datatypes", types);
            case hive -> Utils.loadFromFile("META-INF/druid/parser/hive/builtin_datatypes", types);
            case postgresql -> Utils.loadFromFile("META-INF/druid/parser/postgresql/builtin_datatypes", types);
            case db2 -> Utils.loadFromFile("META-INF/druid/parser/db2/builtin_datatypes", types);
            case mariadb -> Utils.loadFromFile("META-INF/druid/parser/maria/builtin_datatypes", types);
            case sqlserver -> Utils.loadFromFile("META-INF/druid/parser/sqlserver/builtin_datatypes", types);
            case sapdb -> Utils.loadFromFile("META-INF/druid/parser/sapdb/builtin_datatypes", types);
            case derby -> Utils.loadFromFile("META-INF/druid/parser/derby/builtin_datatypes", types);
            case h2 -> Utils.loadFromFile("META-INF/druid/parser/h2/builtin_datatypes", types);
            case blink -> Utils.loadFromFile("META-INF/druid/parser/blink/builtin_datatypes", types);
            case dm -> Utils.loadFromFile("META-INF/druid/parser/dm/builtin_datatypes", types);
            case kingbase -> Utils.loadFromFile("META-INF/druid/parser/kingbase/builtin_datatypes", types);
            case gbase -> Utils.loadFromFile("META-INF/druid/parser/gbase/builtin_datatypes", types);
            case oceanbase -> Utils.loadFromFile("META-INF/druid/parser/oceanbase/builtin_datatypes", types);
            case edb -> Utils.loadFromFile("META-INF/druid/parser/edb/builtin_datatypes", types);
            case elastic_search -> Utils.loadFromFile("META-INF/druid/parser/elastic_search/builtin_datatypes", types);
            case firebirdsql -> Utils.loadFromFile("META-INF/druid/parser/firebirdsql/builtin_datatypes", types);
            case hbase -> Utils.loadFromFile("META-INF/druid/parser/hbase/builtin_datatypes", types);
            case hsql -> Utils.loadFromFile("META-INF/druid/parser/hsql/builtin_datatypes", types);
            case informix -> Utils.loadFromFile("META-INF/druid/parser/Informix/builtin_datatypes", types);
            case ingres -> Utils.loadFromFile("META-INF/druid/parser/ingres/builtin_datatypes", types);
            case interbase -> Utils.loadFromFile("META-INF/druid/parser/interbase/builtin_datatypes", types);
            case mimer -> Utils.loadFromFile("META-INF/druid/parser/mimer/builtin_datatypes", types);
            case sqlite -> Utils.loadFromFile("META-INF/druid/parser/sqlite/builtin_datatypes", types);
            case teradata -> Utils.loadFromFile("META-INF/druid/parser/teradata/builtin_datatypes", types);
            case odps -> Utils.loadFromFile("META-INF/druid/parser/maxcompute/builtin_datatypes", types);
        }

        return types;
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

    /**
     * 用于向客户端输出JSON格式
     */
    @Data
    public static class FunctionInfo {
        String name;
        String type;
        String signature;
        String description;

        public FunctionInfo(String name, String type, String signature) {
            this(name, type, signature, "");
        }
        public FunctionInfo(String name, String type, String signature, String description) {
            this.name = name;
            this.type = type;
            this.signature = signature;
            this.description = description;
        }
    }

}
