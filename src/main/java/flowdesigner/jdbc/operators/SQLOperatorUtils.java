package flowdesigner.jdbc.operators;

import com.alibaba.druid.DbType;
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
                }
                break;
            case CollectionFunction:
                break;
            case TypeConversionFunction:
                break;
            case DateFunction:
                break;
            case ConditionalFunction:
                break;
            case StringFunction:
                switch (sqlOperator) {
                    case substr:
                    case substring:
                        expr = new SQLMethodInvokeExpr(name, null, new SQLIdentifierExpr("?"), new SQLIntegerExpr(0));
                        break;
                }
                break;
            case DataMaskingFunction:
                break;
            case UDAF:
                expr = new SQLAggregateExpr(name);
                ((SQLAggregateExpr) expr).addArgument(new SQLIdentifierExpr("?"));
                break;
            case UDTF:
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
            } else if (sqlOperator.isMathematicalFunction()) {
                return SQLOperatorType.MathematicalFunction;
            } else if (sqlOperator.isAggregateFunction()) {
                return SQLOperatorType.UDAF;
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
