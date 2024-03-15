package flowdesigner.jdbc.command.impl;

import com.alibaba.druid.DbType;
import flowdesigner.db.DbUtils;
import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.dialect.DBDialect;
import flowdesigner.jdbc.dialect.DBDialectMatcher;
import flowdesigner.jdbc.command.model.DataTypeEntity;
import flowdesigner.util.DbTypeKit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@Slf4j
public class DBReverseGetTypeInfoImpl implements Command<ExecResult<List<DataTypeEntity>>> {

    /**
     * 获取数据库数据类型清单
     * @param conn 数据库连接实例，可以为null。当为null时返回配置的数据库类型
     * @param params dbType：数据库类型名称
     * @return 返回连接的数据库类型。当连接为空或异常时返回配置的数据库类型
     * @throws SQLException
     */
    public ExecResult<List<DataTypeEntity>> exec(Connection conn, Map<String, String> params) throws SQLException {
        ExecResult<List<DataTypeEntity>> ret = new ExecResult<>();
        List<DataTypeEntity> tableEntities = null;

        // 如果已连接数据库，那么获取在线数据类型
        if (conn != null) {
            try {
                tableEntities = getTypeInfo(conn);
            } catch (SQLException ignored) {
            }
        }

        // 如果在线获取异常 或 离线状态，获取配置的数据类型
        if (tableEntities == null) {
            String dbType = params.getOrDefault("dbType", null);
            if (dbType == null) {
                throw new RuntimeException("[parameter] dbType should not be null.");
            }

            Set<String> dbTypes = DbUtils.getDbTypes(DbType.of(dbType));
            tableEntities = new ArrayList<>();
            for (String TYPE_NAME : dbTypes) {
                Integer DATA_TYPE = type_int_map.getOrDefault(TYPE_NAME, 1111);
                DataTypeEntity typeInfoEntity = new DataTypeEntity(TYPE_NAME,DATA_TYPE);
                tableEntities.add(typeInfoEntity);
            }
        }

        ret.setStatus(ExecResult.SUCCESS);
        ret.setBody(tableEntities);

        return ret;
    }

    /**
     * 获取所有数据表列表
     * @return
     */
    private List<DataTypeEntity> getTypeInfo(Connection conn) throws SQLException {
        List<DataTypeEntity> infoEntities;
        try {
            DbType dbType = DbTypeKit.getDbType(conn);
            DBDialect dbDialect = DBDialectMatcher.getDBDialect(dbType);
            infoEntities = dbDialect.getDataType(conn);
        } catch (SQLException e) {
            log.error("读取数据库类型出错", e);
            throw new RuntimeException(e);
        }

        return infoEntities;
    }


    private static final Map<String, Integer> type_int_map = Collections.unmodifiableMap(new HashMap<>() {{
        // java.sql.Types 中的类型定义
        put("BIT", -7);
        put("TINYINT", -6);
        put("SMALLINT", 5);
        put("BIGINT", -5);
        put("FLOAT", 6);
        put("REAL", 7);
        put("DOUBLE", 8);
        put("NUMERIC", 2);
        put("DECIMAL", 3);
        put("CHAR", 1);
        put("VARCHAR", 12);
        put("LONG VARCHAR", -1);
        put("DATE", 91);
        put("TIME", 92);
        put("TIMESTAMP", 93);
        put("BINARY", -2);
        put("VARBINARY", -3);
        put("LONG VARBINARY", -4);
        put("NULL", 0);
        put("VOID", 0);
        put("OTHER", 1111);
        put("JAVA_OBJECT", 2000);
        put("DISTINCT", 2001);
        put("STRUCT", 2002);
        put("ARRAY", 2003);
        put("BLOB", 2004);
        put("CLOB", 2005);
        put("REF", 2006);
        put("DATALINK", 70);
        put("BOOLEAN", 16);
        put("ROWID", -8);
        put("NCHAR", -15);
        put("NVARCHAR", -9);
        put("LONGNVARCHAR", -16);
        put("NCLOB", 2011);
        put("SQLXML", 2009);
        put("REF_CURSOR", 2012);
        put("TIME_WITH_TIMEZONE", 2013);
        put("TIMESTAMP_WITH_TIMEZONE", 2014);

        // 各数据库中特有的数据类型对应的int值
        put("INT", 4);
        put("STRING", 12);
        put("INTERVAL_YEAR_MONTH", 1111);
        put("INTERVAL_DAY_TIME", 1111);
        put("MAP", 2000);
        put("UNIONTYPE", 1111);
        put("BOOL", 16);
        put("TINYINT UNSIGNED", -6);
        put("BIGINT UNSIGNED", -5);
        put("MEDIUMBLOB", -4);
        put("LONGBLOB", -4);
        put("TINYBLOB", -3);
        put("MEDIUMTEXT", -1);
        put("LONGTEXT", -1);
        put("TEXT", -1);
        put("ENUM", 1);
        put("SET", 1);
        put("INTEGER UNSIGNED", 4);
        put("INT UNSIGNED", 4);
        put("MEDIUMINT", 4);
        put("MEDIUMINT UNSIGNED", 4);
        put("SMALLINT UNSIGNED", 5);
        put("DOUBLE PRECISION", 8);
        put("TINYTEXT", 12);
        put("YEAR", 91);
        put("DATETIME", 93);
    }});


}


