package flowdesigner.jdbc.command.impl;

import com.alibaba.druid.DbType;
import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.command.model.TableEntity;
import flowdesigner.jdbc.command.model.TablePrimaryKey;
import flowdesigner.jdbc.dialect.DBDialect;
import flowdesigner.jdbc.dialect.DBDialectMatcher;
import flowdesigner.util.DbTypeKit;
import flowdesigner.util.raw.kit.StringKit;

import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DBReverseGetPrimaryKeyImpl implements Command<ExecResult<TablePrimaryKey>> {
    @Override
    public ExecResult<TablePrimaryKey> exec(Connection connection, Map<String, String> params) throws SQLException {
        String schema = params.getOrDefault("schemaPattern",null);
        String table = params.getOrDefault("table",params.getOrDefault("Table", null));

        if (StringKit.isBlank(table)) {
            throw new InvalidParameterException("parameter [table] can not be empty!");
        }

        ExecResult<TablePrimaryKey> ret = new ExecResult<>();
        TablePrimaryKey primaryKey = getPrimaryKey(connection, schema, table);
        ret.setStatus(ExecResult.SUCCESS);
        ret.setBody(primaryKey);

        return ret;
    }

    private TablePrimaryKey getPrimaryKey(Connection conn, String schema, String table) throws SQLException {
        DbType dbType = DbTypeKit.getDbType(conn);
        DBDialect dbDialect = DBDialectMatcher.getDBDialect(dbType);
        String catalog = null;

        boolean supportsCatalogs = conn.getMetaData().supportsCatalogsInTableDefinitions();
        boolean supportsSchemas = conn.getMetaData().supportsSchemasInTableDefinitions();
        if (supportsCatalogs) {
            catalog = schema;
            schema = null;
            if (null == catalog) {
                throw new InvalidParameterException("parameter [schema] can not be empty!");
            }
            if (supportsSchemas) {
                String[] strings = table.split("\\.");
                if (1 == strings.length) {
                    throw new InvalidParameterException("parameter [table] can be schema.tablename format!");
                }
                schema = strings[0];
            }
        }
        if (supportsSchemas) {
            if ( null == schema ) {
                throw new InvalidParameterException("parameter [schema] can not be empty!");
            }
        }

        return dbDialect.getPrimaryKey(conn, catalog, schema, table);
    }

}
