package flowdesigner.jdbc.command.impl;

import com.alibaba.druid.DbType;
import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.dialect.DBDialect;
import flowdesigner.jdbc.dialect.DBDialectMatcher;
import flowdesigner.jdbc.model.TypeInfoEntity;
import flowdesigner.jdbc.util.sql.core.DBType;
import flowdesigner.jdbc.util.sql.kit.DBTypeKit;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public class DBReverseGetTypeInfoImpl implements Command<ExecResult> {

    public ExecResult exec(Connection conn, Map<String, String> params) throws SQLException {
        ExecResult ret = new ExecResult();

        List<TypeInfoEntity> tableEntities = null;
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
    protected List<TypeInfoEntity> getTypeInfo(Connection conn) throws SQLException {
        List<TypeInfoEntity> infoEntities;
        try {
            DBType dbType = DBTypeKit.getDBType(conn);
            DBDialect dbDialect = DBDialectMatcher.getDBDialect(dbType);
            infoEntities = dbDialect.getTypeInfo(conn);
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"读取表清单出错", e);
            throw new RuntimeException(e);
        }

        return infoEntities;
    }

    private static class TypeInfoConstant {
        @Getter
        private static final Map<String, List<TypeInfoEntity>> _items = new HashMap<String, List<TypeInfoEntity>>(){{
            put("hive", Arrays.asList(
                    TypeInfoEntity.of("VOID",0),
                    TypeInfoEntity.of("BOOLEAN",16),
                    TypeInfoEntity.of("TINYINT",-6),
                    TypeInfoEntity.of("SMALLINT",5),
                    TypeInfoEntity.of("INT",4),
                    TypeInfoEntity.of("BIGINT",-5),
                    TypeInfoEntity.of("FLOAT",6),
                    TypeInfoEntity.of("DOUBLE",8),
                    TypeInfoEntity.of("STRING",12),
                    TypeInfoEntity.of("CHAR",1),
                    TypeInfoEntity.of("VARCHAR",12),
                    TypeInfoEntity.of("DATE",91),
                    TypeInfoEntity.of("TIMESTAMP",93),
                    TypeInfoEntity.of("INTERVAL_YEAR_MONTH",1111),
                    TypeInfoEntity.of("INTERVAL_DAY_TIME",1111),
                    TypeInfoEntity.of("BINARY",-2),
                    TypeInfoEntity.of("DECIMAL",3),
                    TypeInfoEntity.of("ARRAY",2003),
                    TypeInfoEntity.of("MAP",2000),
                    TypeInfoEntity.of("STRUCT",2002),
                    TypeInfoEntity.of("UNIONTYPE",1111),
                    TypeInfoEntity.of("USER_DEFINED",1111)));
            put("mysql", Arrays.asList(
                    TypeInfoEntity.of("BIT",-7),
                    TypeInfoEntity.of("BOOL",16),
                    TypeInfoEntity.of("TINYINT",-6),
                    TypeInfoEntity.of("TINYINT UNSIGNED",-6),
                    TypeInfoEntity.of("BIGINT",-5),
                    TypeInfoEntity.of("BIGINT UNSIGNED",-5),
                    TypeInfoEntity.of("LONG VARBINARY",-4),
                    TypeInfoEntity.of("MEDIUMBLOB",-4),
                    TypeInfoEntity.of("LONGBLOB",-4),
                    TypeInfoEntity.of("BLOB",-4),
                    TypeInfoEntity.of("VARBINARY",-3),
                    TypeInfoEntity.of("TINYBLOB",-3),
                    TypeInfoEntity.of("BINARY",-2),
                    TypeInfoEntity.of("LONG VARCHAR",-1),
                    TypeInfoEntity.of("MEDIUMTEXT",-1),
                    TypeInfoEntity.of("LONGTEXT",-1),
                    TypeInfoEntity.of("TEXT",-1),
                    TypeInfoEntity.of("CHAR",1),
                    TypeInfoEntity.of("ENUM",1),
                    TypeInfoEntity.of("SET",1),
                    TypeInfoEntity.of("DECIMAL",3),
                    TypeInfoEntity.of("NUMERIC",3),
                    TypeInfoEntity.of("INTEGER",4),
                    TypeInfoEntity.of("INTEGER UNSIGNED",4),
                    TypeInfoEntity.of("INT",4),
                    TypeInfoEntity.of("INT UNSIGNED",4),
                    TypeInfoEntity.of("MEDIUMINT",4),
                    TypeInfoEntity.of("MEDIUMINT UNSIGNED",4),
                    TypeInfoEntity.of("SMALLINT",5),
                    TypeInfoEntity.of("SMALLINT UNSIGNED",5),
                    TypeInfoEntity.of("FLOAT",7),
                    TypeInfoEntity.of("DOUBLE",8),
                    TypeInfoEntity.of("DOUBLE PRECISION",8),
                    TypeInfoEntity.of("REAL",8),
                    TypeInfoEntity.of("VARCHAR",12),
                    TypeInfoEntity.of("TINYTEXT",12),
                    TypeInfoEntity.of("DATE",91),
                    TypeInfoEntity.of("YEAR",91),
                    TypeInfoEntity.of("TIME",92),
                    TypeInfoEntity.of("DATETIME",93),
                    TypeInfoEntity.of("TIMESTAMP",93)));
        }};
    }


}


