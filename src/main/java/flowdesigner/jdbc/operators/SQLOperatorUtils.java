package flowdesigner.jdbc.operators;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLDescribeStatement;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.util.Utils;
import com.google.gson.Gson;
import flowdesigner.jdbc.command.CommandKey;
import flowdesigner.jdbc.command.CommandManager;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.util.sql.kit.DBTypeKit;
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
    private static class FunctionInfo {
        String name;
        String type;
        String signature;

        public FunctionInfo(String name, String type, String signature) {
            this.name = name;
            this.type = type;
            this.signature = signature;
        }
    }

    public SQLOperatorUtils() {
    }

    public static String getSupportFunctionsJson(Connection connection, DbType dbType) {

        Collection<String> stringList = getSupportFunctions(connection, dbType);
        Collection<FunctionInfo> functionInfos = stringList.stream().map(f -> {
            SQLOperatorType type = getFunctionType(f);
            String signature = getFunctionSignature(dbType, f);
            return new FunctionInfo(f, type.name(), signature);
        }).collect(Collectors.toList());

        Gson gson = new Gson();
        return gson.toJson(functionInfos);
    }

    /**
     * 获取当前数据库支持的函数列表。为了尽量保证函数完整，返回的函数为两部分组成：配置函数+获取函数（如果获取不到就默认）
     * @param connection 当前数据库连接
     * @return 函数列表
     */
    public static Set<String> getSupportFunctions(Connection connection, DbType dbType) {
        Set<String> functions = new HashSet<>();

//        if (connection != null) {
//            ExecResult<Set<String>> execResult = CommandManager.exeCommand(connection, CommandKey.CMD_DBReverseGetFunctionsImpl, new HashMap<>());
//            if (execResult.getStatus().equalsIgnoreCase(ExecResult.SUCCESS)) {
//                functions.addAll(execResult.getBody());
//            }
//        }

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

        DbType dbType = null;
        try {
            dbType = DbType.valueOf(DBTypeKit.getDBTypeStr(connection));
        } catch (SQLException e) {
            e.printStackTrace();
            return ret;
        }

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
     * @param name 函数名称
     * @return 处理后的函数签名
     */
    public static String getFunctionSignature(DbType dbType, String name) {

        SQLExpr expr = new SQLIdentifierExpr(name);

        SQLOperator sqlOperator = SQLOperator.of(name);
        if (sqlOperator == null) {
            return expr.toString();
        }

        SQLOperatorType functionType = getFunctionType(name);
        switch (functionType) {

            case RelationalOperator:
            case ArithmeticOperator:
            case LogicalOperator:
            case BinaryOperator:
                SQLBinaryOperator binaryOperator = of(sqlOperator.name(), SQLBinaryOperator.values());
                if (binaryOperator != null) {
                    expr = new SQLBinaryOpExpr(new SQLIdentifierExpr("?"), binaryOperator, new SQLIdentifierExpr("?"), dbType);
                    return expr.toString();
                }
            case UnaryOperator:
                SQLUnaryOperator unaryOperator = of(sqlOperator.name(), SQLUnaryOperator.values());
                if (unaryOperator != null) {
                    expr = new SQLUnaryExpr(unaryOperator, new SQLIdentifierExpr("?"));
                    return expr.toString();
                }
            case ComplexTypeConstructor:
                switch (sqlOperator) {
                    case Map:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("key1"), new SQLIdentifierExpr("value1"), new SQLIdentifierExpr("key2"), new SQLIdentifierExpr("value2"), new SQLIdentifierExpr("..."));
                        return expr.toString();
                    case Struct:
                    case Array:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("value1"), new SQLIdentifierExpr("value2"), new SQLIdentifierExpr("..."));
                        return expr.toString();
                    case Named_struct:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("name1"), new SQLIdentifierExpr("value1"), new SQLIdentifierExpr("name2"), new SQLIdentifierExpr("value2"), new SQLIdentifierExpr("..."));
                        return expr.toString();
                    case Create_union:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("tag"),new SQLIdentifierExpr("value1"), new SQLIdentifierExpr("value2"), new SQLIdentifierExpr("..."));
                        return expr.toString();
                }
                break;
            case MathematicalFunction:
                switch (sqlOperator) {
                    case rand:
                        expr = new SQLMethodInvokeExpr(name,null, new SQLIdentifierExpr("base"),new SQLIdentifierExpr("?"));
                        return expr.toString();
                    case POW:
                        expr = new SQLMethodInvokeExpr(name,null, new SQLIdentifierExpr("?"),new SQLIdentifierExpr("2"));
                        return expr.toString();
                    case conv:
                        expr = new SQLMethodInvokeExpr(name,null, new SQLIdentifierExpr("?"),new SQLIdentifierExpr("10"),new SQLIdentifierExpr("2"));
                        return expr.toString();
                    case pmod:
                        expr = new SQLMethodInvokeExpr(name,null, new SQLIdentifierExpr("?"),new SQLIdentifierExpr("?"));
                        return expr.toString();
                    case e:
                    case PI:
                        expr = new SQLMethodInvokeExpr(name);
                        return expr.toString();
                    case shiftleft:
                    case shiftright:
                    case shiftrightunsigned:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("1"));
                        return expr.toString();
                    case GREATEST:
                    case LEAST:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("?"), new SQLIdentifierExpr("..."));
                        return expr.toString();
                    default:
                        expr = new SQLMethodInvokeExpr(name,  new SQLIdentifierExpr("?"));
                        return expr.toString();
                }
            case CollectionFunction:
                switch (sqlOperator) {
                    case array_contains:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("value1"));
                        return expr.toString();
                    default:
                        expr = new SQLMethodInvokeExpr(name,  new SQLIdentifierExpr("?"));
                        return expr.toString();
                }
            case TypeConversionFunction:
                switch (sqlOperator) {
                    case cast:
                        expr = new SQLCastExpr(new SQLIdentifierExpr("?"), new SQLDataTypeImpl("<type>"));
                        return expr.toString();
                    case CONVERT:
                        if (dbType.equals(DbType.sqlserver)) {
                            expr = new SQLMethodInvokeExpr(name,new SQLIdentifierExpr("VARCHAR(19),?"));
                        } else if (dbType.equals(DbType.mysql)) {
                            expr = new SQLMethodInvokeExpr(name,new SQLIdentifierExpr("'ABC' USING gbk"));
                        }
                        else {
                            expr = new SQLMethodInvokeExpr(name,new SQLIdentifierExpr("?,d_chset"));
                        }
                        return expr.toString();
                    default:
                        expr = new SQLMethodInvokeExpr(name,  new SQLIdentifierExpr("?"));
                        return expr.toString();
                }
            case DateFunction:
                switch (sqlOperator) {
                    case from_unixtime:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("[, string format]"));
                        return expr.toString();
                    case unix_timestamp:
                        expr = new SQLMethodInvokeExpr(name);
                        return expr.toString();
                    case TO_DATE:
                    case YEAR:
                    case        quarter:
                    case MONTH:
                    case DAY:
                    case        dayofmonth:
                    case        hour:
                    case       minute:
                    case        second:
                    case MICROSECOND:
                    case WEEKOFYEAR:
                    case         last_day:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"));
                        return expr.toString();
                    case  extract:
                        SQLMethodInvokeExpr methodExpr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("<month/hour/dayofweek/month/minute>"));
                        methodExpr.setFrom(new SQLCharExpr("?"));
                        expr = methodExpr;
                        return expr.toString();
                    case DATEDIFF:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("('2009-03-01', '2009-02-27')"));
                        return expr.toString();
                    case        date_add:
                    case        date_sub:
                    case       add_months:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIntegerExpr(1));
                        return expr.toString();
                    case        from_utc_timestamp:
                    case        to_utc_timestamp:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("PST"));
                        return expr.toString();
                    case        next_day:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("'FRIDAY"));
                        return expr.toString();
                    case        trunc:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("format"));
                        return expr.toString();
                    case        months_between:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("'1997-02-28 10:30:00'"), new SQLIdentifierExpr("'1996-10-30'"));
                        return expr.toString();
                    case        date_format:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("'fmt'"));
                        return expr.toString();
                    case TIMESTAMP:
                        if (dbType.equals(DbType.mysql)) {
                            expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("expression"), new SQLIdentifierExpr("'interval'"));
                            return expr.toString();
                        }
                        break;
                    case TIMESTAMPDIFF:
                        if (dbType.equals(DbType.mysql)) {
                            expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("DAY/MONTH/YEAR,?,?"));
                            return expr.toString();
                        }
                        break;
                    case        current_date:
                    case CURRENT_TIMESTAMP:
                    default:
                        expr = new SQLMethodInvokeExpr(name);
                        return expr.toString();
                }
            case ConditionalFunction:
                switch (sqlOperator) {
                    case  nvl:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("default_value"));
                        return expr.toString();
                    case  COALESCE:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("val1"), new SQLIdentifierExpr("val2"), new SQLIdentifierExpr("..."));
                        return expr.toString();
                    case NULLIF:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("?"));
                        return expr.toString();
                    case CASE:
                        SQLCaseExpr caseExpr = new SQLCaseExpr();
                        caseExpr.setValueExpr(new SQLIdentifierExpr("?"));
                        caseExpr.addItem(new SQLIdentifierExpr("condition1"), new SQLIdentifierExpr("value1"));
                        caseExpr.addItem(new SQLIdentifierExpr("condition2"), new SQLIdentifierExpr("value2"));
                        caseExpr.setElseExpr(new SQLIdentifierExpr("elsevalue"));
                        expr = caseExpr;
                        return expr.toString();
                    case IF:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("testCondition"), new SQLIdentifierExpr("valueTrue"), new SQLIdentifierExpr("valueFalseOrNull"));
                        return expr.toString();
                    case ISNULL:
                    case ISNOTNULL:
                    case  assert_true:
                    default:
                        expr = new SQLMethodInvokeExpr(name,  new SQLIdentifierExpr("?"));
                        return expr.toString();
                }
            case StringFunction:
                switch (sqlOperator) {
                    case SPACE:
                        expr = new SQLMethodInvokeExpr(name,  new SQLIdentifierExpr("1"));
                        return expr.toString();
                    // 一个参数
                    case ASCII:
                    case base64:
                    case character_length:
                    case CHR:
                    case LENGTH:
                    case LOWER:
                    case LCASE:
                    case UPPER:
                    case ucase:
                    case ltrim:
                    case RTRIM:
                    case trim:
                    case OCTET_LENGTH:
                    case quote:
                    case REVERSE:
                    case sentences:
                    case unbase64:
                    case INITCAP:
                    case SOUNDEX:
                        expr = new SQLMethodInvokeExpr(name,  new SQLIdentifierExpr("?"));
                        return expr.toString();
                    // 两个常量
                    case levenshtein:
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("A"), new SQLIdentifierExpr("B"));
                        return expr.toString();
                    // 多个常量
                    case concat:
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("A"), new SQLIdentifierExpr("B..."));
                        return expr.toString();
                    // 参数 + 常量
                    case encode:
                    case decode:
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("'UTF-8'"));
                        return expr.toString();
                    case elt://elt(N int,str1 string,str2 string,str3 string,...)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("N"), new SQLIdentifierExpr("str1"), new SQLIdentifierExpr("str2"), new SQLIdentifierExpr("..."));
                        return expr.toString();
                    case field://field(val T,val1 T,val2 T,val3 T,...)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("val"), new SQLIdentifierExpr("val1"), new SQLIdentifierExpr("val2"), new SQLIdentifierExpr("..."));
                        return expr.toString();
                    case find_in_set://find_in_set(string str, string strList)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("strList"));
                        return expr.toString();
                    case format_number:// format_number(number x, int d)
                    case REPEAT://repeat(string str, int n)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLNumberExpr(2));
                        return expr.toString();
                    case in_file://in_file(string str, string filename)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("filename"));
                        return expr.toString();
                    case instr://instr(string str, string substr)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("str"), new SQLIdentifierExpr("?"));
                        return expr.toString();
                    case LOCATE://locate(string substr, string str[, int pos])
                        if (dbType.equals(DbType.db2)) {
                            expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("substr"));
                        } else {
                            expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("substr"), new SQLIdentifierExpr("?"));
                        }
                        return expr.toString();
                    case lpad://lpad(string str, int len, string pad)
                    case rpad:
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLNumberExpr(8), new SQLIdentifierExpr("0"));
                        return expr.toString();
                    case parse_url://parse_url(string urlString, string partToExtract [, string keyToExtract])
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLCharExpr("HOST"));
                        return expr.toString();
                    case printf://printf(String format, Obj... args)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("format"), new SQLIdentifierExpr("?"));
                        return expr.toString();
                    case regexp_extract://regexp_extract(string subject, string pattern, int index)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("pattern"),new SQLNumberExpr(1));
                        return expr.toString();
                    case regexp_replace://regexp_replace(string INITIAL_STRING, string PATTERN, string REPLACEMENT)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("pattern"),new SQLIdentifierExpr("REPLACEMENT"));
                        return expr.toString();
                    case REPLACE://replace(string A, string OLD, string NEW)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("OLD"),new SQLIdentifierExpr("NEW"));
                        return expr.toString();
                    case split://split(string str, string pat)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLCharExpr(","));
                        return expr.toString();
                    case str_to_map://str_to_map(text[, delimiter1, delimiter2])
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLCharExpr(","), new SQLCharExpr(":"));
                        return expr.toString();
                    case SUBSTR:
                    case SUBSTRING:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIntegerExpr(0));
                        return expr.toString();
                    case substring_index://substring_index(string A, string delim, int count)
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"),new SQLCharExpr("."), new SQLNumberExpr(2));
                        return expr.toString();
                    case TRANSLATE://translate(string|char|varchar input, string|char|varchar from, string|char|varchar to)
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"),new SQLCharExpr("from"), new SQLCharExpr("to"));
                        return expr.toString();
                    // 全模式
                    case context_ngrams://context_ngrams(array<array<string>>, array<string>, int K, int pf)
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"),new SQLIdentifierExpr("array<string>"), new SQLNumberExpr(3));
                        return expr.toString();
                    case CONCAT_WS://concat_ws(string SEP, string A, string B...)
                        expr = new SQLMethodInvokeExpr(name, null, new SQLCharExpr("','"),new SQLIdentifierExpr("stringA"),new SQLIdentifierExpr("stringB"),new SQLIdentifierExpr("..."));
                        return expr.toString();
                    case get_json_object://get_json_object(string json_string, string path)
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("json_string"), new SQLIdentifierExpr("path"));
                        return expr.toString();
                    case ngrams://ngrams(array<array<string>>, int N, int K, int pf)
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLNumberExpr(1), new SQLNumberExpr(2));
                        return expr.toString();
                    case POSITION:
                        if (dbType.equals(DbType.db2)) {
                            expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("EXP2"));
                            return expr.toString();
                        }
                }
                break;
            case DataMaskingFunction:
                switch (sqlOperator) {
                    case mask: // mask(string str[, string upper[, string lower[, string number]]])
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLCharExpr("U"), new SQLIdentifierExpr("L"), new SQLCharExpr("#"));
                        return expr.toString();
                    case mask_first_n:
                    case mask_last_n:
                    case mask_show_first_n:
                    case mask_show_last_n:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLNumberExpr(4));
                        return expr.toString();
                    case mask_hash:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"));
                        return expr.toString();
                }
                break;
            case MiscFunctions:
                switch (sqlOperator) {
                    case CURRENT_USER:
                    case logged_in_user:
                    case current_database:
                    case version:
                    case surrogate_key:
                        expr = new SQLMethodInvokeExpr(name);
                        return expr.toString();
                        // 一个参数
                    case hash:
                    case MD5:
                    case sha1:
                    case sha:
                    case crc32:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"));
                        return expr.toString();
                        // 一个参数+ int
                    case sha2:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("224|256|384|512|0"));
                        return expr.toString();
                        // 一个参数+ string
                    case aes_encrypt:
                    case aes_decrypt:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("key"));
                        return expr.toString();
                        // 函数
                    case java_method:
                    case reflect://java_method(class, method[, arg1[, arg2..]])
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("class"), new SQLIdentifierExpr("method")
                                , new SQLIdentifierExpr("[, arg1[, arg2..]]"));
                        return expr.toString();
                }
            case UDAF:
                switch (sqlOperator) {
                    case AVG:
                    case COUNT:
                    case MAX:
                    case MIN:
                    case STDDEV:
                    case SUM:
                    case variance:
                    case VAR_POP:
                    case var_samp:
                    case stddev_pop:
                    case stddev_samp:
                    case collect_set:
                    case collect_list:
                        expr = new SQLAggregateExpr(name, null, new SQLIdentifierExpr("?"));
                        return expr.toString();
                        // 两个参数
                    case covar_pop:
                    case covar_samp:
                    case corr:
                        expr = new SQLAggregateExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("?"));
                        return expr.toString();
                        // 一个参数+ 一个小数
                    case percentile:
                    case percentile_approx:
                        expr = new SQLAggregateExpr(name, null, new SQLIdentifierExpr("?"), new SQLNumberExpr(0.1));
                        return expr.toString();
                        // 一个参数+一个int
                    case histogram_numeric:
                    case ntile:
                        expr = new SQLAggregateExpr(name, null, new SQLIdentifierExpr("?"), new SQLNumberExpr(2));
                        return expr.toString();
                        // 全匹配模式
                    case regr_avgx://regr_sxx(independent, dependent)
                    case regr_avgy:
                    case regr_count:
                    case regr_intercept:
                    case regr_r2:
                    case regr_slope:
                    case regr_sxx:
                    case regr_sxy:
                    case regr_syy:
                        expr = new SQLAggregateExpr(name, null, new SQLIdentifierExpr("independent"), new SQLIdentifierExpr("dependent"));
                        return expr.toString();
                }

            case UDTF:
                switch (sqlOperator) {
                    case explode:
                    case posexplode:
                    case inline:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"));
                        return expr.toString();
                    case stack:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("r,T1V1,...,Tn/rVn)"));
                        return expr.toString();
                    case json_tuple://json_tuple(string jsonStr,string k1,...,string kn)
                    case parse_url_tuple:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("k1,...,kn"));
                        return expr.toString();
                }
                break;
            case GROUPingAndSORTing:
                return expr.toString();
            case UtilityFunction:
                break;
            case UDF:
                break;
            case Others:
                break;
        }

        return getFunctionSignature2(dbType, name);
    }

    /**
     * 获取函数类型：UnaryOperator=一元运算符；BinaryOperator=二元运算符；MethodInvoke=函数；AggregateFunction=聚合函数;0=其他函数
     * @param name 函数名称
     * @return 1=一元运算符；2=二元运算符；0=其他函数
     */
    public static SQLOperatorType getFunctionType(String name) {
        SQLOperator sqlOperator = null;

        try {
            sqlOperator = SQLOperator.of(name);
        } catch (IllegalArgumentException ignored) {
        }

        if (sqlOperator != null) {
            if (sqlOperator.isLockingFunctions()) {
                return SQLOperatorType.LockFunctions;
            } else if (sqlOperator.isENCRYPT_DECRYPT()) {
                return SQLOperatorType.ENCRYPT_DECRYPT;
            } else if (sqlOperator.isBuildIn()) {
                return SQLOperatorType.BuiltIn;
            } else if (sqlOperator.isArithmetic()){
                return SQLOperatorType.ArithmeticOperator;
            } else if (sqlOperator.isLogical()) {
                return SQLOperatorType.LogicalOperator;
            } else if (sqlOperator.isRelational()) {
                return SQLOperatorType.RelationalOperator;
            } else if (sqlOperator.isStringFunction()) {
                return SQLOperatorType.StringFunction;
            } else if (sqlOperator.isDataMaskingFunctions()) {
                return SQLOperatorType.DataMaskingFunction;
            } else if (sqlOperator.isMiscFunctions()) {
                return SQLOperatorType.MiscFunctions;
            } else if (sqlOperator.isInformationFunctions()) {
                return SQLOperatorType.InformationFunctions;
            } else if (sqlOperator.isMathematicalFunction()) {
                return SQLOperatorType.MathematicalFunction;
            } else if (sqlOperator.isCollectionFunction()) {
                return SQLOperatorType.CollectionFunction;
            } else if (sqlOperator.isComplexTypeConstructor()) {
                return SQLOperatorType.ComplexTypeConstructor;
            } else if (sqlOperator.isTypeConversionFunction()) {
                return SQLOperatorType.TypeConversionFunction;
            } else if (sqlOperator.isDateFunction()) {
                return SQLOperatorType.DateFunction;
            } else if (sqlOperator.isConditionalFunction()) {
                return SQLOperatorType.ConditionalFunction;
            } else if (sqlOperator.isAggregateFunction()) {
                return SQLOperatorType.UDAF;
            } else if (sqlOperator.isUDTF()) {
                return SQLOperatorType.UDTF;
            } else if (sqlOperator.isXPathUDF()) {
                return SQLOperatorType.XPathUDF;
            } else {
                if (of(sqlOperator.name(), SQLBinaryOperator.values()) != null) {
                    return SQLOperatorType.BinaryOperator;
                } else if (of(sqlOperator.name(), SQLUnaryOperator.values()) != null) {
                    return SQLOperatorType.UnaryOperator;
                }
            }
        }

        return SQLOperatorType.Others;
    }

    private static String getFunctionSignature2(DbType dbType, String name) {

        SQLExpr expr = new SQLIdentifierExpr(name);

        SQLOperator sqlOperator = SQLOperator.of(name);
        if (sqlOperator == null) {
            return expr.toString();
        }

        return sqlOperator.usage;
    }

    /**
     * 返回指定数据库的可用环境参数设置
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
    public static List<String> getEnvironmentList(DbType dbType) {
        ArrayList<String> list = new ArrayList<>();

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
                list.add("key_buffer_size=int");
                list.add("max_allowed_packet=int");
            }
            case mariadb -> {
            }
            case derby -> {
            }
            case hive -> {
                list.add("ngmr.furion.pool=string");
                list.add("hive.support.concurrency=bool");
                list.add("hive.groupby.position.alias = bool");
                list.add("hive.auto.convert.join=bool");
                list.add("hive.fetch.task.conversion=none|more|minimal");
                list.add("mapred.max.split.size=bool");
                list.add("hive.exec.reducers.bytes.per.reducer=int");
                list.add("hive.exec.reducers.max=int");
                list.add("hive.groupby.skewindata = bool");
                list.add("hive.exec.parallel=bool");
                list.add("hive.exec.parallel.thread.number=int");
                list.add("hive.exec.dynamic.partition=bool");
                list.add("hive.exec.dynamic.partition.mode=nonstrict|strict");
                list.add("hive.enforce.bucketing=bool");
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
        return list;
    }

    public static String getEnvironmentString(DbType dbType) {
        Collection<String> list = getEnvironmentList(dbType);
        return StringUtils.join(list,',');
    }
    /**
     * 获取用户定义变量
     * @param dbType 数据库类型
     * @param sql set SQL语句
     * @return 变量列表
     */
    public static List<String> getVariantList(String dbType, String sql) {
        List<SQLStatement> list = SQLUtils.parseStatements(sql, dbType);

        return list.stream().filter(s -> s instanceof SQLSetStatement)
                .flatMap(s -> {
                    Collection<SQLAssignItem> items = ((SQLSetStatement) s).getItems();
                    items.removeIf(i -> i.getTarget() instanceof SQLPropertyExpr);
                    return items.stream().map(i -> i.getTarget().toString());
                }).toList();
    }

    public static String getVariantString(String dbType, String sql) {
        Collection<String> list = getVariantList(dbType, sql);
        return StringUtils.join(list,',');
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
