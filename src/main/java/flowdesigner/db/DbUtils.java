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

    public static Set<String> getDbKeywords(DbType dbType) {
        Lexer lexer = SQLParserUtils.createLexer("select *", dbType);
        return lexer.getKeywords().getKeywords().keySet();
    }

    /**
     * 返回数据库关键字
     * @param dbType 数据库类型
     * @return 以逗号分隔的关键字列表
     */
    public static String getDbKeywordsAsString(DbType dbType) {
        Set<String> keywords = getDbKeywords(dbType);
        return StringUtils.join(keywords,",");
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

    /**
     * 获取当前数据库支持的函数列表。为了尽量保证函数完整，返回的函数为两部分组成：配置函数+获取函数（如果获取不到就默认）
     * @return 函数列表
     */
    public static Set<FunctionInfo> getFunctions( DbType dbType) {
        List<String> functions = new ArrayList<>();

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

        String catalog = "";
        for (String function: functions) {
            if (function.startsWith("[") && function.endsWith("]")) {
                catalog = function.substring(1, function.lastIndexOf("]"));
                continue;
            }

            String separator = ":";
            if (function.contentEquals(":=")) {
                separator = ":=";
            }

            String[] split = function.split(separator);
            if (split.length < 2) {
                continue;
            }

            String name = split[0].trim();
            String signature = split[1].trim();
            String description = split.length == 3? split[2].trim():"" ;

            functionInfos.add(new FunctionInfo(name, catalog, signature));
        }

        return functionInfos;
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

        DBExecuteImpl dbExecute = new DBExecuteImpl();
        var exec = dbExecute.exec(connection, stmt.toString());

        if (exec.getStatus().equalsIgnoreCase(ExecResult.FAILED)) {
            return ret;
        }

//        ResultSet rs = exec.getBody();
//        while (true) {
//            try {
//                if (!rs.next()) break;
//                ret = (rs.getString("tab_name"));
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }

        return ret;
    }

    /**
     * 获取数据库离线数据类型
     * @param dbType 数据库类型
     * @return 数据类型列表
     */
    public static Set<String> getDbTypes(DbType dbType) {
        HashSet<String> types = new HashSet<String>();

        if (dbType.equals(DbType.oracle)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/oracle/builtin_datatypes", types);
        } else if (dbType.equals(DbType.mysql)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/mysql/builtin_datatypes", types);
        } else if (dbType.equals(DbType.hive)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/hive/builtin_datatypes", types);
        } else if (dbType.equals(DbType.postgresql)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/postgresql/builtin_datatypes", types);
        } else if (dbType.equals(DbType.db2)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/db2/builtin_datatypes", types);
        } else if (dbType.equals(DbType.mariadb)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/maria/builtin_datatypes", types);
        } else if (dbType.equals(DbType.sqlserver)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/sqlserver/builtin_datatypes", types);
        } else if (dbType.equals(DbType.sybase)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/sybase/builtin_datatypes", types);
        } else if (dbType.equals(DbType.derby)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/derby/builtin_datatypes", types);
        } else if (dbType.equals(DbType.h2)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/h2/builtin_datatypes", types);
        } else if (dbType.equals(DbType.dm)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/dm/builtin_datatypes", types);
        } else if (dbType.equals(DbType.kingbase)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/kingbase/builtin_datatypes", types);
        } else if (dbType.equals(DbType.gbase)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/gbase/builtin_datatypes", types);
        } else if (dbType.equals(DbType.oceanbase)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/oceanbase/builtin_datatypes", types);
        } else if (dbType.equals(DbType.edb)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/edb/builtin_datatypes", types);
        } else if (dbType.equals(DbType.elastic_search)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/elastic_search/builtin_datatypes", types);
        } else if (dbType.equals(DbType.firebirdsql)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/firebirdsql/builtin_datatypes", types);
        } else if (dbType.equals(DbType.hbase)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/hbase/builtin_datatypes", types);
        } else if (dbType.equals(DbType.hsql)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/hsql/builtin_datatypes", types);
        } else if (dbType.equals(DbType.informix)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/Informix/builtin_datatypes", types);
        } else if (dbType.equals(DbType.ingres)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/ingres/builtin_datatypes", types);
        } else if (dbType.equals(DbType.interbase)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/interbase/builtin_datatypes", types);
        } else if (dbType.equals(DbType.mimer)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/mimer/builtin_datatypes", types);
        } else if (dbType.equals(DbType.sqlite)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/sqlite/builtin_datatypes", types);
        } else if (dbType.equals(DbType.teradata)) {
            com.alibaba.druid.util.Utils.loadFromFile("META-INF/druid/parser/teradata/builtin_datatypes", types);
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

        public FunctionInfo(String name, String type, String signature) {
            this.name = name;
            this.type = type;
            this.signature = signature;
        }
    }

}
