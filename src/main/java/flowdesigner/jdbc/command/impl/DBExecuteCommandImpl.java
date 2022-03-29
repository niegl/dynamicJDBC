package flowdesigner.jdbc.command.impl;

import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.command.dialect.DBDialect;
import flowdesigner.jdbc.command.dialect.DBDialectMatcher;
import flowdesigner.jdbc.util.sql.core.DBType;
import flowdesigner.jdbc.util.sql.kit.DBTypeKit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * 设计为执行SQL命令的通用类。
 */
public class DBExecuteCommandImpl implements Command<ExecResult<ResultSet>> {

    public ExecResult<ResultSet> exec(Connection conn, Map<String, String> params) throws SQLException {
        String SQL = params.getOrDefault("SQL",null);

        ExecResult<ResultSet> ret = new ExecResult<>();
        ResultSet resultSet = fetchExecuteResult(conn, SQL);
        ret.setStatus(ExecResult.SUCCESS);
        ret.setBody(resultSet);

        return ret;
    }

    /**
     * 获取SQL执行结果
     * @return 结果集
     */
    protected ResultSet fetchExecuteResult(Connection conn, String SQL) throws SQLException {
        ResultSet ret = null;

        DBType dbType = DBTypeKit.getDBType(conn);
        DBDialect dbDialect = DBDialectMatcher.getDBDialect(dbType);
        if (conn != null) {
            Statement stmt = conn.createStatement();
            ret = stmt.executeQuery(SQL);
        }

        return ret;
    }
}
