package flowdesigner.jdbc.command.impl;

import com.alibaba.druid.util.JdbcUtils;
import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class DBExecuteUpdateCommandImpl implements Command<ExecResult<Integer>> {

    public ExecResult<Integer> exec(Connection conn, Map<String, String> params) throws SQLException {
        String SQL = params.getOrDefault("SQL",null);

        ExecResult<Integer> ret = new ExecResult<Integer>();
        int cc = JdbcUtils.executeUpdate(conn,SQL, new ArrayList<>());
        ret.setStatus(ExecResult.SUCCESS);
        ret.setBody(cc);

        return ret;
    }
}
