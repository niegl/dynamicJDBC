package flowdesigner.jdbc.command.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcUtils;
import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.util.sql.DbTypeKit;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * 设计为执行SQL命令的通用类。<p>
 * 支持脚本运行、脚本状态查询（当前运行步骤）、取消脚本运行、单statement运行.
 */
@Slf4j
public class DBExecuteImpl implements Command<ExecResult<RunningStatus>> {
    /**
     * SQL=脚本
     * @param conn
     * @param params 传递SQL脚本.
     * @return
     * either (1) the row count for SQL Data Manipulation Language (DML) statements <p>
     * or (2) 0 for SQL statements that return nothing <p>
     * or (3) row data for SQL select statements
     * @throws SQLException
     */
    public ExecResult<RunningStatus> exec(Connection conn, Map<String, String> params) throws SQLException {
        ExecResult<RunningStatus> ret = new ExecResult<>();

        String scripts = params.getOrDefault("SQL",null);
        if (scripts == null) {
            throw new IllegalArgumentException("parameter [SQL] not specified");
        }

        RunningStatus runningStatus = execute(conn, scripts);

        ret.setStatus(ExecResult.SUCCESS);
        ret.setBody(runningStatus);

        return ret;
    }

    /**
     * 统一CURE操作接口，简化客户端操作。
     * @param conn 当前连接对象
     * @param scripts 脚本（可以为单一statement）
     * @throws SQLException 异常
     * @return
     */
    private RunningStatus execute(@NotNull Connection conn, @NotNull String scripts) throws SQLException {
        RunningStatus runningStatus = null;

        DbType dbType = DbTypeKit.getDbType(conn);
        if (dbType == null) {
            log.info("dbType = null");
            return null;
        }

        List<SQLStatement> statements = SQLUtils.parseStatements(scripts, dbType);
        for (SQLStatement stm: statements) {
            String sql = stm.toString();
            runningStatus = new RunningStatus(sql);

            if (stm instanceof SQLSelectStatement) {
                List<Map<String, Object>> query = JdbcUtils.executeQuery(conn, sql, Collections.emptyList());
                runningStatus.setResult(query);
            } else {
                int affected = JdbcUtils.executeUpdate(conn, sql, Collections.emptyList());
                runningStatus.setAffected(affected);
            }
        }

        return runningStatus;

    }

}

/**
 * 保存scripts的运行状态
 */
class RunningStatus {
    /**
     * 当前运行步骤（SQL语句）
     */
    @Getter private final String step;
    @Setter
    @Getter
    private int affected = -1;
    /**
     * 如果是select，保存查询结果
     */
    @Setter
    @Getter
    private List<Map<String, Object>> result;

    RunningStatus(String step) {
        this.step = step;
    }
}