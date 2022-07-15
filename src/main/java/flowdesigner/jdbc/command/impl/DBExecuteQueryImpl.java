package flowdesigner.jdbc.command.impl;

import com.alibaba.druid.util.JdbcUtils;
import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 设计为执行SQL命令的通用类。
 */
public class DBExecuteQueryImpl implements Command<ExecResult<List<Map<String, Object>>>> {

    public ExecResult<List<Map<String, Object>>> exec(Connection conn, Map<String, String> params) throws SQLException {
        String SQL = params.getOrDefault("SQL",null);

        ExecResult<List<Map<String, Object>>> ret = new ExecResult<>();
        List<Map<String, Object>> query = JdbcUtils.executeQuery(conn, SQL, new ArrayList<>());
        ret.setStatus(ExecResult.SUCCESS);
        ret.setBody(query);

        return ret;
    }
}
