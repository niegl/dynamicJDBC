package flowdesigner.jdbc.command.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcUtils;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.sql.ast.statement.SQLStatementType;
import flowdesigner.util.DbTypeKit;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;

import static com.alibaba.druid.util.JdbcUtils.close;


/**
 * 设计为执行SQL命令的通用类。<p>
 * 支持单/多statement运行, 如果为多statement语句仅返回最后一条statement的结果
 */
@Slf4j
public class DBExecuteImpl {

    private PreparedStatement stmt;
    private ResultSet rs;

    /**
     * SQL=脚本
     * @param conn
     * @return
     * either (1) the row count for SQL Data Manipulation Language (DML) statements <p>
     * or (2) 0 for SQL statements that return nothing <p>
     * or (3) row data for SQL select statements
     * @throws SQLException
     */
    public RunningStatus<Object> exec(@NotNull Connection conn, @NotNull String scripts) {

        log.info(scripts);

        RunningStatus<Object> runningStatus = new RunningStatus<>();

        try {
            runningStatus = execute(conn, scripts);
            runningStatus.setStatus(ExecResult.SUCCESS);
        } catch (SQLException | IllegalArgumentException e) {
            runningStatus.setStatus(ExecResult.FAILED);
            runningStatus.setResult(e.getMessage());
        }

        return runningStatus;
    }

    /**
     * 统一CURE操作接口，简化客户端操作。
     * @param conn 当前连接对象
     * @param scripts 脚本（可以为单一statement）
     * @throws SQLException 异常
     * @return
     */
    private RunningStatus<Object> execute(Connection conn, String scripts) throws SQLException {
        RunningStatus<Object> runningStatus = new RunningStatus<>();

        DbType dbType = DbTypeKit.getDbType(conn);
        if (dbType == null) {
            throw new IllegalArgumentException("dbType is not supported");
        }

        int i = 0;
        List<SQLStatement> statements = SQLUtils.parseStatements(scripts, dbType);
        for (SQLStatement stm: statements) {
            runningStatus.setStep(++i);
            runningStatus.setStatementType(SQLStatementType.getType(stm));

            if (stmt != null) {
                close(stmt);
            }
            if (rs != null) {
                close(rs);
            }

            stm.setAfterSemi(false);
            String sql = stm.toString();
            stmt = conn.prepareStatement(sql);
            if (stm instanceof SQLSelectStatement) {
                rs = stmt.executeQuery();
                List<Map<String, Object>> query = queryNext(200);
                runningStatus.setResult(query);
            } else {
                int affected = JdbcUtils.executeUpdate(conn, sql, Collections.emptyList());
                runningStatus.setResult(affected);
            }
        }

        return runningStatus;

    }

    /**
     * 获取剩余查询结果
     * @param num 获取行数
     * @return
     */
    public List<Map<String, Object>> queryNext(int num) {
        List<Map<String, Object>> rows = new ArrayList<>();

        try {
            ResultSetMetaData rsMeta = rs.getMetaData();

            int count = 0;
            while(rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();

                int i = 0;
                for(int size = rsMeta.getColumnCount(); i < size; ++i) {
                    String columName = rsMeta.getColumnLabel(i + 1);
                    Object value = rs.getObject(i + 1);
                    row.put(columName, value);
                }

                rows.add(row);

                if (++count >= num) {
                    break;
                }
            }
        } catch (SQLException e){
            close(rs);
            close((Statement)stmt);
            log.error(e.getMessage());
        }

        return rows;
    }

    public void release() {
        close(rs);
        close((Statement)stmt);
    }

    /**
     * 保存scripts的运行状态
     */
    public static class RunningStatus<T> {
        /**
         * 当前运行步骤（SQL语句）
         */
        @Setter
        @Getter
        private int step;
        /**
         * 标识当前执行的SQL语句的类型
         */
        @Setter
        @Getter
        private SQLStatementType statementType;
        /**
         * 运行结果：成功还是失败
         */
        @Setter
        @Getter
        private String status;
        /**
         * 运行结果数据内容：
         *   <li>  如果是select，保存查询结果;
         *   <li>  如果是更新等其他操作，显示影响行数
         *   <li>  如果失败，保存失败原因
         */
        @Setter
        @Getter
        private T result;
    }

}
