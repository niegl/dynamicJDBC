package flowdesigner.jdbc.command.impl;


import com.alibaba.druid.DbType;
import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.dialect.DBDialect;
import flowdesigner.jdbc.dialect.DBDialectMatcher;
import flowdesigner.jdbc.command.model.FKColumnField;
import flowdesigner.util.raw.kit.StringKit;
import flowdesigner.util.DbTypeKit;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 通过元数据接口getCrossReference 获取表外键
 */
@Slf4j
public class DBReverseGetFKInfoImpl implements Command<ExecResult<List<FKColumnField>>> {

    public ExecResult<List<FKColumnField>> exec(Connection conn, Map<String, String> params) throws SQLException {
        String schemaPattern = params.getOrDefault("schemaPattern",null);
        String table = params.getOrDefault("table", params.getOrDefault("Table", null));
        if (StringKit.isBlank(table)) {
            throw new IllegalArgumentException("parameter [table] not specified");
        }

        ExecResult<List<FKColumnField>> ret = new ExecResult<>();
        List<FKColumnField> tableEntities = getImportedKeys(conn, schemaPattern, table);
        ret.setStatus(ExecResult.SUCCESS);
        ret.setBody(tableEntities);

        return ret;
    }

    protected List<FKColumnField> getImportedKeys(Connection conn, String schemaPattern, String table) throws SQLException {
        List<FKColumnField> tableEntities;
        DbType dbType = DbTypeKit.getDbType(conn);
        DBDialect dbDialect = DBDialectMatcher.getDBDialect(dbType);
        String catalog = null;

        // 如果数据库支持catalog，那么实际传递过来的是catalog
        boolean supportsCatalogs = conn.getMetaData().supportsCatalogsInTableDefinitions();
        if (supportsCatalogs) {
            catalog = schemaPattern;
            schemaPattern = null;
        }

        tableEntities = dbDialect.getImportedKeys(conn, catalog, schemaPattern, table);

        return tableEntities;
    }

}
