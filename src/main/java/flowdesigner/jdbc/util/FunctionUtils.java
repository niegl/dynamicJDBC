package flowdesigner.jdbc.util;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryOperator;
import com.alibaba.druid.sql.ast.statement.SQLDescribeStatement;
import com.alibaba.druid.sql.ast.statement.SQLShowFunctionsStatement;
import com.google.gson.Gson;
import flowdesigner.jdbc.command.CommandKey;
import flowdesigner.jdbc.command.CommandManager;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.util.sql.core.DBType;
import flowdesigner.jdbc.util.sql.kit.DBTypeKit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FunctionUtils {

    public FunctionUtils() {
    }

    /**
     * 获取当前数据库支持的函数列表
     * @param connection 当前数据库连接
     * @return 函数列表
     */
    public static List<String> getSupportFunctions(Connection connection) {
        ArrayList<String> ret = new ArrayList<>();

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
    public static String getFunctionUsage(Connection connection, String name) {
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
     * 获取函数类型：1=一元运算符；2=二元运算符；0=其他函数
     * @param name 函数名称
     * @return 1=一元运算符；2=二元运算符；0=其他函数
     */
    public static int getFunctionType(String name) {
        for (SQLUnaryOperator type : SQLUnaryOperator.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return 1;
            }
        }
        for (SQLBinaryOperator type : SQLBinaryOperator.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return 2;
            }
        }

        return 0;
    }


}
