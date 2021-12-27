package flowdesigner.jdbc.command.impl;

import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.dialect.DBDialect;
import flowdesigner.jdbc.dialect.DBDialectMatcher;
import flowdesigner.jdbc.model.TypeInfoEntity;
import flowdesigner.jdbc.util.sql.core.DBType;
import flowdesigner.jdbc.util.sql.kit.DBTypeKit;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class DBReverseGetTypeInfoImpl implements Command<ExecResult> {

    public ExecResult exec(Connection conn, Map<String, String> params) throws SQLException {
        ExecResult ret = new ExecResult();

        List<TypeInfoEntity> tableEntities = null;
        tableEntities = getTypeInfo(conn);
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
}


