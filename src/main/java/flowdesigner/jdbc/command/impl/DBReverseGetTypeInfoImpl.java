package flowdesigner.jdbc.command.impl;

import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.command.dialect.DBDialect;
import flowdesigner.jdbc.command.dialect.DBDialectMatcher;
import flowdesigner.jdbc.command.model.DataTypeEntity;
import flowdesigner.jdbc.util.sql.core.DBType;
import flowdesigner.jdbc.util.sql.kit.DBTypeKit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@Slf4j
public class DBReverseGetTypeInfoImpl implements Command<ExecResult<List<DataTypeEntity>>> {

    public ExecResult<List<DataTypeEntity>> exec(Connection conn, Map<String, String> params) throws SQLException {
        ExecResult<List<DataTypeEntity>> ret = new ExecResult<>();

        List<DataTypeEntity> tableEntities = null;
        if (conn == null) {
            String dbType = params.get("dbType");
            if (dbType != null) {
                tableEntities = TypeInfoConstant.get_items().getOrDefault(dbType,new ArrayList<>());
            }
        } else {
            tableEntities = getTypeInfo(conn);
        }
        ret.setStatus(ExecResult.SUCCESS);
        ret.setBody(tableEntities);

        return ret;
    }

    /**
     * 获取所有数据表列表
     * @return
     */
    protected List<DataTypeEntity> getTypeInfo(Connection conn) throws SQLException {
        List<DataTypeEntity> infoEntities;
        try {
            DBType dbType = DBTypeKit.getDBType(conn);
            DBDialect dbDialect = DBDialectMatcher.getDBDialect(dbType);
            infoEntities = dbDialect.getDataType(conn);
        } catch (SQLException e) {
            log.error("读取表清单出错", e);
            throw new RuntimeException(e);
        }

        return infoEntities;
    }

    private static class TypeInfoConstant {
        @Getter
        private static final Map<String, List<DataTypeEntity>> _items = new HashMap<String, List<DataTypeEntity>>(){{
            put("hive", Arrays.asList(
                    DataTypeEntity.of("VOID",0),
                    DataTypeEntity.of("BOOLEAN",16),
                    DataTypeEntity.of("TINYINT",-6),
                    DataTypeEntity.of("SMALLINT",5),
                    DataTypeEntity.of("INT",4),
                    DataTypeEntity.of("BIGINT",-5),
                    DataTypeEntity.of("FLOAT",6),
                    DataTypeEntity.of("DOUBLE",8),
                    DataTypeEntity.of("STRING",12),
                    DataTypeEntity.of("CHAR",1),
                    DataTypeEntity.of("VARCHAR",12),
                    DataTypeEntity.of("DATE",91),
                    DataTypeEntity.of("TIMESTAMP",93),
                    DataTypeEntity.of("INTERVAL_YEAR_MONTH",1111),
                    DataTypeEntity.of("INTERVAL_DAY_TIME",1111),
                    DataTypeEntity.of("BINARY",-2),
                    DataTypeEntity.of("DECIMAL",3),
                    DataTypeEntity.of("ARRAY",2003),
                    DataTypeEntity.of("MAP",2000),
                    DataTypeEntity.of("STRUCT",2002),
                    DataTypeEntity.of("UNIONTYPE",1111),
                    DataTypeEntity.of("USER_DEFINED",1111)));
            put("mysql", Arrays.asList(
                    DataTypeEntity.of("BIT",-7),
                    DataTypeEntity.of("BOOL",16),
                    DataTypeEntity.of("TINYINT",-6),
                    DataTypeEntity.of("TINYINT UNSIGNED",-6),
                    DataTypeEntity.of("BIGINT",-5),
                    DataTypeEntity.of("BIGINT UNSIGNED",-5),
                    DataTypeEntity.of("LONG VARBINARY",-4),
                    DataTypeEntity.of("MEDIUMBLOB",-4),
                    DataTypeEntity.of("LONGBLOB",-4),
                    DataTypeEntity.of("BLOB",-4),
                    DataTypeEntity.of("VARBINARY",-3),
                    DataTypeEntity.of("TINYBLOB",-3),
                    DataTypeEntity.of("BINARY",-2),
                    DataTypeEntity.of("LONG VARCHAR",-1),
                    DataTypeEntity.of("MEDIUMTEXT",-1),
                    DataTypeEntity.of("LONGTEXT",-1),
                    DataTypeEntity.of("TEXT",-1),
                    DataTypeEntity.of("CHAR",1),
                    DataTypeEntity.of("ENUM",1),
                    DataTypeEntity.of("SET",1),
                    DataTypeEntity.of("DECIMAL",3),
                    DataTypeEntity.of("NUMERIC",3),
                    DataTypeEntity.of("INTEGER",4),
                    DataTypeEntity.of("INTEGER UNSIGNED",4),
                    DataTypeEntity.of("INT",4),
                    DataTypeEntity.of("INT UNSIGNED",4),
                    DataTypeEntity.of("MEDIUMINT",4),
                    DataTypeEntity.of("MEDIUMINT UNSIGNED",4),
                    DataTypeEntity.of("SMALLINT",5),
                    DataTypeEntity.of("SMALLINT UNSIGNED",5),
                    DataTypeEntity.of("FLOAT",7),
                    DataTypeEntity.of("DOUBLE",8),
                    DataTypeEntity.of("DOUBLE PRECISION",8),
                    DataTypeEntity.of("REAL",8),
                    DataTypeEntity.of("VARCHAR",12),
                    DataTypeEntity.of("TINYTEXT",12),
                    DataTypeEntity.of("DATE",91),
                    DataTypeEntity.of("YEAR",91),
                    DataTypeEntity.of("TIME",92),
                    DataTypeEntity.of("DATETIME",93),
                    DataTypeEntity.of("TIMESTAMP",93)));
        }};
    }


}


