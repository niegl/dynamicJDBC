package flowdesigner.jdbc.command.impl;

import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.command.dialect.DBDialect;
import flowdesigner.jdbc.command.dialect.DBDialectMatcher;
import flowdesigner.jdbc.command.model.FKColumnField;
import flowdesigner.jdbc.util.raw.kit.StringKit;
import flowdesigner.jdbc.util.sql.core.DBType;
import flowdesigner.jdbc.util.sql.kit.DBTypeKit;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 获取引用关系。如 A、B为一对多关系，可以获取对A表主键引用的B表中的列。
 */
@Slf4j
public class DBReverseGetFKReferenceImpl implements Command<ExecResult<List<FKColumnField>>> {

    public ExecResult<List<FKColumnField>> exec(Connection conn, Map<String, String> params) throws SQLException {

        String schemaPattern = params.getOrDefault("schemaPattern",null);
        String table = params.getOrDefault("table", params.getOrDefault("Table", null));
        if (StringKit.isBlank(table)) {
            throw new IllegalArgumentException("parameter [table] not specified");
        }

        ExecResult<List<FKColumnField>> ret = new ExecResult<>();
        List<FKColumnField> tableEntities = getFKReference(conn, schemaPattern, table);
        ret.setStatus(ExecResult.SUCCESS);
        ret.setBody(tableEntities);

        return ret;
    }

    protected List<FKColumnField> getFKReference(Connection conn, String schemaPattern, String table) throws SQLException {
        List<FKColumnField> tableEntities;
        try {
            DBType dbType = DBTypeKit.getDBType(conn);
            DBDialect dbDialect = DBDialectMatcher.getDBDialect(dbType);
            tableEntities = dbDialect.getFKReference(conn, schemaPattern, table);
        } catch (SQLException e) {
            log.error("读取表清单出错", e);
            throw new RuntimeException(e);
        }

        return tableEntities;
    }
}
