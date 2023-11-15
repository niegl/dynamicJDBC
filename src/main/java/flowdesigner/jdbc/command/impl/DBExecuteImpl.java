package flowdesigner.jdbc.command.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLType;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.util.DbTypeKit;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.alibaba.druid.util.JdbcUtils.close;


/**
 * 设计为执行SQL命令的通用类。<p>
 * 支持单/多statement运行, 如果为多statement语句仅返回最后一条statement的结果
 */
@Slf4j
public class DBExecuteImpl {
    /**
     * 原子计算。用来保证每次查询获取到不同的ID值
     */
    private static final AtomicInteger count = new AtomicInteger(0);
    private PreparedStatement stmt;
    private ResultSet rs;

    static {
        String dlljvmpath = System.getProperty("dlljvmpath");
        System.load(dlljvmpath);
        log.info("load dll success, path:" + dlljvmpath);
    }

    // 执行查询完成后回调
    private native int nativeCallback(int appId, int queryId, String result);

    /**
     * SQL=脚本
     * @param conn
     * @return
     * either (1) the row count for SQL Data Manipulation Language (DML) statements <p>
     * or (2) 0 for SQL statements that return nothing <p>
     * or (3) row data for SQL select statements
     * @throws SQLException
     */
    public RunningStatus<Object> exec(int appId, @NotNull Connection conn, @NotNull String scripts) {

        log.info(scripts);

        int queryId = count.incrementAndGet();

        final RunningStatus<Object>[] runningStatus = new RunningStatus[]{new RunningStatus<>()};
        runningStatus[0].setStatus(ExecResult.SUCCESS);
        runningStatus[0].setQueryId(queryId);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                     execute(conn, scripts, runningStatus[0]);
                } catch (ParserException e) {
                    runningStatus[0].setStatus(ExecResult.FAILED);
                    runningStatus[0].setResult("SQL ParserException :" + e.getMessage());
                } catch (SQLException | IllegalArgumentException e) {
                    runningStatus[0].setStatus(ExecResult.FAILED);
                    runningStatus[0].setResult(e.getMessage());
                }

                String jsonString = JSON.toJSONString(runningStatus[0]);
                nativeCallback(appId, queryId, jsonString);
            }
        }).start();

        return runningStatus[0];
    }

    /**
     * 统一CURE操作接口，简化客户端操作。
     * @param conn 当前连接对象
     * @param scripts 脚本（可以为单一statement）
     * @throws SQLException 异常
     * @return
     */
    private void execute(Connection conn, String scripts,RunningStatus<Object> runningStatus) throws ParserException,SQLException {

        if (conn == null || conn.isClosed()) {
            throw new IllegalArgumentException("please connect to database first!");
        }
        DbType dbType = DbTypeKit.getDbType(conn);
        if (dbType == null) {
            throw new IllegalArgumentException("database is not supported");
        }

        int i = 0;
        List<SQLStatement> statements = SQLUtils.parseStatements(scripts, dbType);
        for (SQLStatement statement: statements) {
            statement.setAfterSemi(false);
            String sql = statement.toString();
            runningStatus.setStep(++i);

            SQLType sqlType = SQLParserUtils.getSQLTypeV2(sql, dbType);
            if (sqlType == SQLType.UNKNOWN || sqlType == SQLType.ERROR ) {
                throw new SQLException(sql + ": SQLType is UNKNOWN or ERROR");
            }

            runningStatus.setStatementType(sqlType.name());

            if (stmt != null) {
                close(stmt);
            }
            if (rs != null) {
                close(rs);
            }

            stmt = conn.prepareStatement(sql);
            switch (sqlType) {

                case SELECT:
                case ANALYZE :
                case EXPLAIN :
                case SHOW :
                case SHOW_TABLES :
                case SHOW_USERS :
                case SHOW_PARTITIONS :
                case SHOW_CATALOGS :
                case SHOW_FUNCTIONS :
                case SHOW_ROLE :
                case SHOW_ROLES :
                case SHOW_PACKAGE :
                case SHOW_PACKAGES :
                case SHOW_CHANGELOGS :
                case SHOW_ACL :
                case SHOW_RECYCLEBIN :
                case SHOW_VARIABLES :
                case SHOW_HISTORY :
                case SHOW_GRANT :
                case SHOW_GRANTS :
                case SHOW_CREATE_TABLE :
                case SHOW_STATISTIC :
                case SHOW_STATISTIC_LIST :
                case SHOW_LABEL :
                case DESC :
                case DUMP_DATA :
                case LIST :
                case LIST_USERS :
                case LIST_TABLES :
                case LIST_ROLES :
                case LIST_TENANT_ROLES :
                case LIST_TRUSTEDPROJECTS :
                case LIST_ACCOUNTPROVIDERS :
                case LIST_TEMPORARY_OUTPUT :
                case WHO :
                case WHOAMI :
                    rs = stmt.executeQuery();
                    QueryData data = queryNext(200);
                    runningStatus.setResult(data);
                    runningStatus.setHasQueryData(true);
                    break;
                case UPDATE :
                case INSERT_SELECT :
                case INSERT_INTO_SELECT :
                case INSERT_OVERWRITE_SELECT :
                case INSERT_VALUES :
                case INSERT_INTO_VALUES :
                case INSERT_OVERWRITE_VALUES :
                case INSERT :
                case INSERT_INTO :
                case INSERT_OVERWRITE :
                case INSERT_MULTI :
                case DELETE :
                case MERGE :
                case CREATE :
                case ALTER :
                case DROP :
                case TRUNCATE :
                case REPLACE :
                case SET :
                case SET_PROJECT :
                case SET_LABEL :
                case GRANT :
                case REVOKE :
                case COMMIT :
                case ROLLBACK :
                case USE :
                case KILL :
                case MSCK :
                case ADD_USER :
                case REMOVE_USER :
                case REMOVE_RESOURCE :
                case CREATE_USER :
                case CREATE_TABLE :
                case CREATE_TABLE_AS_SELECT :
                case CREATE_VIEW :
                case CREATE_FUNCTION :
                case CREATE_ROLE :
                case CREATE_PACKAGE :
                case DROP_USER :
                case DROP_TABLE :
                case DROP_VIEW :
                case DROP_MATERIALIZED_VIEW :
                case DROP_FUNCTION :
                case DROP_ROLE :
                case DROP_RESOURCE :
                case ALTER_USER :
                case ALTER_TABLE :
                case ALTER_VIEW :
                case READ :
                case ADD_TABLE :
                case ADD_FUNCTION :
                case ADD_RESOURCE :
                case ADD_TRUSTEDPROJECT :
                case ADD_VOLUME :
                case ADD_STATISTIC :
                case ADD_ACCOUNTPROVIDER :
                case TUNNEL_DOWNLOAD :
                case UPLOAD :
                case SCRIPT :
                case COUNT :
                case ADD :
                case CLONE :
                case LOAD :
                case INSTALL :
                case UNLOAD :
                case ALLOW :
                case PURGE :
                case RESTORE :
                case EXSTORE :
                case UNDO :
                case REMOVE :
                case EMPTY :
                case ALTER_TABLE_ADD_PARTITION :
                case ALTER_TABLE_MERGE_PARTITION :
                case ALTER_TABLE_DROP_PARTITION :
                case ALTER_TABLE_RENAME_PARTITION :
                case ALTER_TABLE_SET_LIFECYCLE :
                case ALTER_TABLE_ENABLE_LIFECYCLE :
                case ALTER_TABLE_DISABLE_LIFECYCLE :
                case ALTER_TABLE_RENAME :
                case ALTER_TABLE_ADD_COLUMN :
                case ALTER_TABLE_RENAME_COLUMN :
                case ALTER_TABLE_ALTER_COLUMN :
                case ALTER_TABLE_SET_TBLPROPERTIES :
                case ALTER_TABLE_SET_COMMENT :
                case ALTER_TABLE_TOUCH :
                case ALTER_TABLE_CHANGE_OWNER :
                case MULTI :
                case WITH :
                    int affected = JdbcUtils.executeUpdate(conn, sql, Collections.emptyList());
                    runningStatus.setResult(affected);
                    break;
                case SET_UNKNOWN :
                    break;
            }
        }

    }

    /**
     * 获取剩余查询结果
     * @param num 获取行数
     * @return
     */
    public QueryData queryNext(int num) {

        List<String> header = new ArrayList<>();
        List<String> type = new ArrayList<>();
        List<List<Object>> rows = new ArrayList<>();

        try {
            int i = 0;

            // 获取元数据表头
            ResultSetMetaData rsMeta = rs.getMetaData();
            for(int size = rsMeta.getColumnCount(); i < size; ++i) {
                String columName = rsMeta.getColumnLabel(i + 1);
                String columTypeName = rsMeta.getColumnTypeName(i + 1);
                header.add(columName);
                type.add(columTypeName);
            }

            // 获取数据
            int count = 0;
            while(rs.next()) {
                ArrayList<Object> row = new ArrayList<>();

                for (String columnName : header) {
                    Object value = rs.getObject(columnName);
                    row.add(value);
                }

                rows.add(row);

                if (++count >= num) {
                    break;
                }
            }
        } catch (SQLException e) {
            close(rs);
            close((Statement)stmt);
            log.error(e.getMessage());
        }

        return new QueryData(header, type, rows);
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
        @JSONField(ordinal = 1)
        private int step;
        /**
         * 标识当前执行的SQL语句的类型
         */
        @Setter
        @Getter
        @JSONField(ordinal = 2)
        private String statementType;
        /**
         * 运行结果：成功还是失败
         */
        @Setter
        @Getter
        @JSONField(ordinal = 3)
        private String status;
        @Setter
        @Getter
        @JSONField(ordinal = 4)
        private boolean hasQueryData = false;
        /**
         * sql query id用来匹配回调函数中的执行结果查询
         */
        @Setter
        @Getter
        private int queryId;
        /**
         * 运行结果数据内容：
         *   <li>  如果是select，保存查询结果;
         *   <li>  如果是更新等其他操作，显示影响行数
         *   <li>  如果失败，保存失败原因
         */
        @Setter
        @Getter
        @JSONField(ordinal = 5)
        private T result;
    }

    /**
     * 用于将查询出来的数据进行header和data数据分离保存
     */
    public static class QueryData {
        public QueryData(List<String> head, List<String> type,List<List<Object>> data) {
            this.head = head;
            this.type = type;
            this.data = data;
        }

        @Getter
        List<String> head = new ArrayList<>();
        @Getter
        List<String> type = new ArrayList<>();
        @Getter
        List<List<Object>> data = new ArrayList<>();
    }

}
