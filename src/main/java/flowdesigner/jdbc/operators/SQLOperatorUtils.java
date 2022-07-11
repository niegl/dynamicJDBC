package flowdesigner.jdbc.operators;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLDescribeStatement;
import com.alibaba.druid.sql.ast.statement.SQLShowFunctionsStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionOperator;
import flowdesigner.jdbc.command.CommandKey;
import flowdesigner.jdbc.command.CommandManager;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.util.sql.kit.DBTypeKit;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SQLOperatorUtils {

    public SQLOperatorUtils() {
    }

    public static String getSupportFunctionsAsString(Connection connection) {
        Collection<String> stringList = getSupportFunctions(connection);
        return StringUtils.join(stringList,",");
    }
    /**
     * 获取当前数据库支持的函数列表
     * @param connection 当前数据库连接
     * @return 函数列表
     */
    public static List<String> getSupportFunctions(Connection connection) {
        ArrayList<String> ret = new ArrayList<>();

        // 处理离线状态下的函数获取
        if (connection == null) {
            for (SQLUnaryOperator operator:SQLUnaryOperator.values()) {
                ret.add(operator.name);
            }
            for (SQLBinaryOperator operator:SQLBinaryOperator.values()) {
                ret.add(operator.name);
            }
            ret.addAll(Arrays.asList("AVG", "COUNT", "MAX", "MIN", "STDDEV", "SUM"));
            return ret;
        }

        SQLShowFunctionsStatement functionsStatement = new SQLShowFunctionsStatement();
        ExecResult<ResultSet> execResult = CommandManager.exeCommand(connection, CommandKey.CMD_DBExecuteCommandImpl, new HashMap<String,String>(){{
            put("SQL",functionsStatement.toString());
        }});

        if (execResult.getStatus().equalsIgnoreCase(ExecResult.FAILED)) {
            return ret;
        }

        ResultSet rs = execResult.getBody();
        while (true) {
            try {
                if (!rs.next()) break;
                ret.add(rs.getString("tab_name"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return ret;
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
                expr = new SQLBinaryOpExpr(new SQLIdentifierExpr("?"), of(sqlOperator.name(), SQLBinaryOperator.values()), new SQLIdentifierExpr("?"), dbType);
                break;
            case UnaryOperator:
                expr = new SQLUnaryExpr(of(sqlOperator.name(), SQLUnaryOperator.values()), new SQLIdentifierExpr("?"));
                break;
            case ComplexTypeConstructor:
                switch (sqlOperator) {
                    case Map:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("key1"), new SQLIdentifierExpr("value1"), new SQLIdentifierExpr("key2"), new SQLIdentifierExpr("value2"), new SQLIdentifierExpr("..."));
                        break;
                    case Struct:
                    case Array:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("value1"), new SQLIdentifierExpr("value2"), new SQLIdentifierExpr("..."));
                        break;
                    case Named_struct:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("name1"), new SQLIdentifierExpr("value1"), new SQLIdentifierExpr("name2"), new SQLIdentifierExpr("value2"), new SQLIdentifierExpr("..."));
                        break;
                    case Create_union:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("tag"),new SQLIdentifierExpr("value1"), new SQLIdentifierExpr("value2"), new SQLIdentifierExpr("..."));
                        break;
                }
                break;
            case MathematicalFunction:
                switch (sqlOperator) {
                    case rand:
                        expr = new SQLMethodInvokeExpr(name,null, new SQLIdentifierExpr("base"),new SQLIdentifierExpr("?"));
                        break;
                    case pow:
                        expr = new SQLMethodInvokeExpr(name,null, new SQLIdentifierExpr("?"),new SQLIdentifierExpr("2"));
                        break;
                    case conv:
                        expr = new SQLMethodInvokeExpr(name,null, new SQLIdentifierExpr("?"),new SQLIdentifierExpr("10"),new SQLIdentifierExpr("2"));
                        break;
                    case pmod:
                        expr = new SQLMethodInvokeExpr(name,null, new SQLIdentifierExpr("?"),new SQLIdentifierExpr("?"));
                        break;
                    case e:
                    case pi:
                        expr = new SQLMethodInvokeExpr(name);
                        break;
                    case shiftleft:
                    case shiftright:
                    case shiftrightunsigned:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("1"));
                        break;
                    case greatest:
                    case least:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("?"), new SQLIdentifierExpr("..."));
                        break;
                    default:
                        expr = new SQLMethodInvokeExpr(name,  new SQLIdentifierExpr("?"));
                        break;
                }
                break;
            case CollectionFunction:
                switch (sqlOperator) {
                    case array_contains:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("value1"));
                        break;
                    default:
                        expr = new SQLMethodInvokeExpr(name,  new SQLIdentifierExpr("?"));
                        break;
                }
                break;
            case TypeConversionFunction:
                switch (sqlOperator) {
                    case cast:
                        expr = new SQLCastExpr(new SQLIdentifierExpr("?"), new SQLDataTypeImpl("<type>"));
                        break;
                    default:
                        expr = new SQLMethodInvokeExpr(name,  new SQLIdentifierExpr("?"));
                        break;
                }
                break;
            case DateFunction:
                switch (sqlOperator) {
                    case from_unixtime:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("[, string format]"));
                        break;
                    case         unix_timestamp:
                        expr = new SQLMethodInvokeExpr(name);
                        break;
                    case       to_date:
                    case         year:
                    case        quarter:
                    case        month:
                    case         day:
                    case        dayofmonth:
                    case        hour:
                    case       minute:
                    case        second:
                    case        weekofyear:
                    case         last_day:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"));
                        break;
                    case  extract:
                        SQLMethodInvokeExpr methodExpr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("<month/hour/dayofweek/month/minute>"));
                        methodExpr.setFrom(new SQLCharExpr("?"));
                        expr = methodExpr;
                        break;
                    case  datediff:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("('2009-03-01', '2009-02-27')"));
                        break;
                    case        date_add:
                    case        date_sub:
                    case       add_months:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIntegerExpr(1));
                        break;
                    case        from_utc_timestamp:
                    case        to_utc_timestamp:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("PST"));
                        break;
                    case        next_day:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("'FRIDAY"));
                        break;
                    case        trunc:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("format"));
                        break;
                    case        months_between:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("'1997-02-28 10:30:00'"), new SQLIdentifierExpr("'1996-10-30'"));
                        break;
                    case        date_format:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("'fmt'"));
                        break;
                    case        current_date:
                    case        current_timestamp:
                    default:
                        expr = new SQLMethodInvokeExpr(name);
                        break;
                }
                break;
            case ConditionalFunction:
                switch (sqlOperator) {
                    case  nvl:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("default_value"));
                        break;
                    case  COALESCE:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("val1"), new SQLIdentifierExpr("val2"), new SQLIdentifierExpr("..."));
                        break;
                    case  nullif:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("?"));
                        break;
                    case CASE:
                        SQLCaseExpr caseExpr = new SQLCaseExpr();
                        caseExpr.setValueExpr(new SQLIdentifierExpr("?"));
                        caseExpr.addItem(new SQLIdentifierExpr("condition1"), new SQLIdentifierExpr("value1"));
                        caseExpr.addItem(new SQLIdentifierExpr("condition2"), new SQLIdentifierExpr("value2"));
                        caseExpr.setElseExpr(new SQLIdentifierExpr("elsevalue"));
                        expr = caseExpr;
                        break;
                    case IF:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("testCondition"), new SQLIdentifierExpr("valueTrue"), new SQLIdentifierExpr("valueFalseOrNull"));
                        break;
                    case  isnull:
                    case  isnotnull:
                    case  assert_true:
                    default:
                        expr = new SQLMethodInvokeExpr(name,  new SQLIdentifierExpr("?"));
                        break;
                }
                break;
            case StringFunction:
                switch (sqlOperator) {
                    case space:
                        expr = new SQLMethodInvokeExpr(name,  new SQLIdentifierExpr("1"));
                        break;
                    // 一个参数
                    case ascii:
                    case base64:
                    case character_length:
                    case chr:
                    case length:
                    case lower:
                    case lcase:
                    case upper:
                    case ucase:
                    case ltrim:
                    case rtrim:
                    case trim:
                    case octet_length:
                    case quote:
                    case reverse:
                    case sentences:
                    case unbase64:
                    case initcap:
                    case soundex:
                        expr = new SQLMethodInvokeExpr(name,  new SQLIdentifierExpr("?"));
                        break;
                    // 两个常量
                    case levenshtein:
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("A"), new SQLIdentifierExpr("B"));
                        break;
                    // 多个常量
                    case concat:
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("A"), new SQLIdentifierExpr("B..."));
                        break;
                    // 参数 + 常量
                    case encode:
                    case decode:
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("'UTF-8'"));
                        break;
                    case elt://elt(N int,str1 string,str2 string,str3 string,...)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("N"), new SQLIdentifierExpr("str1"), new SQLIdentifierExpr("str2"), new SQLIdentifierExpr("..."));
                        break;
                    case field://field(val T,val1 T,val2 T,val3 T,...)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("val"), new SQLIdentifierExpr("val1"), new SQLIdentifierExpr("val2"), new SQLIdentifierExpr("..."));
                        break;
                    case find_in_set://find_in_set(string str, string strList)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("strList"));
                        break;
                    case format_number:// format_number(number x, int d)
                    case repeat://repeat(string str, int n)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLNumberExpr(2));
                        break;
                    case in_file://in_file(string str, string filename)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("filename"));
                        break;
                    case instr://instr(string str, string substr)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("str"), new SQLIdentifierExpr("?"));
                        break;
                    case locate://locate(string substr, string str[, int pos])
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("str"));
                        break;
                    case lpad://lpad(string str, int len, string pad)
                    case rpad:
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLNumberExpr(8), new SQLIdentifierExpr("0"));
                        break;
                    case parse_url://parse_url(string urlString, string partToExtract [, string keyToExtract])
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLCharExpr("HOST"));
                        break;
                    case printf://printf(String format, Obj... args)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("format"), new SQLIdentifierExpr("?"));
                        break;
                    case regexp_extract://regexp_extract(string subject, string pattern, int index)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("pattern"),new SQLNumberExpr(1));
                        break;
                    case regexp_replace://regexp_replace(string INITIAL_STRING, string PATTERN, string REPLACEMENT)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("pattern"),new SQLIdentifierExpr("REPLACEMENT"));
                        break;
                    case replace://replace(string A, string OLD, string NEW)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("OLD"),new SQLIdentifierExpr("NEW"));
                        break;
                    case split://split(string str, string pat)
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLCharExpr(","));
                        break;
                    case str_to_map://str_to_map(text[, delimiter1, delimiter2])
                        expr = new SQLMethodInvokeExpr(name, new SQLIdentifierExpr("?"), new SQLCharExpr(","), new SQLCharExpr(":"));
                        break;
                    case substr:
                    case substring:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIntegerExpr(0));
                        break;
                    case substring_index://substring_index(string A, string delim, int count)
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"),new SQLCharExpr("."), new SQLNumberExpr(2));
                        break;
                    case translate://translate(string|char|varchar input, string|char|varchar from, string|char|varchar to)
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"),new SQLCharExpr("from"), new SQLCharExpr("to"));
                        break;
                    // 全模式
                    case context_ngrams://context_ngrams(array<array<string>>, array<string>, int K, int pf)
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"),new SQLIdentifierExpr("array<string>"), new SQLNumberExpr(3));
                        break;
                    case concat_ws://concat_ws(string SEP, string A, string B...)
                        expr = new SQLMethodInvokeExpr(name, null, new SQLCharExpr(","),new SQLIdentifierExpr("stringA"),new SQLIdentifierExpr("stringB"),new SQLIdentifierExpr("..."));
                        break;
                    case get_json_object://get_json_object(string json_string, string path)
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("json_string"), new SQLIdentifierExpr("path"));
                        break;
                    case ngrams://ngrams(array<array<string>>, int N, int K, int pf)
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLNumberExpr(1), new SQLNumberExpr(2));
                        break;
                }
                break;
            case DataMaskingFunction:
                switch (sqlOperator) {
                    case mask: // mask(string str[, string upper[, string lower[, string number]]])
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLCharExpr("U"), new SQLIdentifierExpr("L"), new SQLCharExpr("#"));
                        break;
                    case mask_first_n:
                    case mask_last_n:
                    case mask_show_first_n:
                    case mask_show_last_n:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLNumberExpr(4));
                        break;
                    case mask_hash:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"));
                        break;
                }
                break;
            case MiscFunctions:
                switch (sqlOperator) {
                    case current_user:
                    case logged_in_user:
                    case current_database:
                    case version:
                    case surrogate_key:
                        expr = new SQLMethodInvokeExpr(name);
                        break;
                        // 一个参数
                    case hash:
                    case md5:
                    case sha1:
                    case sha:
                    case crc32:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"));
                        break;
                        // 一个参数+ int
                    case sha2:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("224|256|384|512|0"));
                        break;
                        // 一个参数+ string
                    case aes_encrypt:
                    case aes_decrypt:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("key"));
                        break;
                        // 函数
                    case java_method:
                    case reflect://java_method(class, method[, arg1[, arg2..]])
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("class"), new SQLIdentifierExpr("method")
                                , new SQLIdentifierExpr("[, arg1[, arg2..]]"));
                        break;
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
                    case var_pop:
                    case var_samp:
                    case stddev_pop:
                    case stddev_samp:
                    case collect_set:
                    case collect_list:
                        expr = new SQLAggregateExpr(name, null, new SQLIdentifierExpr("?"));
                        break;
                        // 两个参数
                    case covar_pop:
                    case covar_samp:
                    case corr:
                        expr = new SQLAggregateExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("?"));
                        break;
                        // 一个参数+ 一个小数
                    case percentile:
                    case percentile_approx:
                        expr = new SQLAggregateExpr(name, null, new SQLIdentifierExpr("?"), new SQLNumberExpr(0.1));
                        break;
                        // 一个参数+一个int
                    case histogram_numeric:
                    case ntile:
                        expr = new SQLAggregateExpr(name, null, new SQLIdentifierExpr("?"), new SQLNumberExpr(2));
                        break;
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
                        break;
                }

            case UDTF:
                switch (sqlOperator) {
                    case explode:
                    case posexplode:
                    case inline:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"));
                        break;
                    case stack:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("r,T1V1,...,Tn/rVn)"));
                        break;
                    case json_tuple://json_tuple(string jsonStr,string k1,...,string kn)
                    case parse_url_tuple:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIdentifierExpr("k1,...,kn"));
                        break;
                }
                break;
            case GROUPingAndSORTing:
                break;
            case UtilityFunction:
                break;
            case UDF:
                break;
            case Others:
                break;
        }

        return expr.toString();
    }

    public static String getFunctionTypeAsString(String name) {
        return getFunctionType(name).name();
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
            if (sqlOperator.isArithmetic()){
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

    private static <T extends Enum<T>> T of(String name, T[] values) {
        for (T value : values) {
            if (value.name().equals(name)) {
                return value;
            }
        }

        return null;
    }


}
