package flowdesigner.jdbc.command.impl;

import com.alibaba.druid.util.JdbcUtils;
import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class DBExecuteUpdateCommandImpl implements Command<ExecResult> {

    public ExecResult exec(Connection conn, Map<String, String> params) {
        String SQL = params.getOrDefault("SQL",null);

        ExecResult ret = new ExecResult();
        //获取连接正常的情况下，进入下一步
        try {
            int cc = JdbcUtils.executeUpdate(conn,SQL, new ArrayList<>());
            ret.setStatus(ExecResult.SUCCESS);
            ret.setBody(cc);
        } catch (SQLException e) {
            ret.setStatus(ExecResult.FAILED);
            ret.setBody(e.getMessage());
            logger.severe( e.getMessage());
        } finally {
        }

        return ret;
    }
}
