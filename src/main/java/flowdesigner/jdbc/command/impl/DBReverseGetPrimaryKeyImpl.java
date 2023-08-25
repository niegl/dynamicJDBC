package flowdesigner.jdbc.command.impl;

import com.alibaba.druid.DbType;
import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.command.model.TableEntity;
import flowdesigner.jdbc.command.model.TablePrimaryKey;
import flowdesigner.jdbc.dialect.DBDialect;
import flowdesigner.jdbc.dialect.DBDialectMatcher;
import flowdesigner.util.DbTypeKit;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DBReverseGetPrimaryKeyImpl implements Command<ExecResult<TablePrimaryKey>> {
    @Override
    public ExecResult<TablePrimaryKey> exec(Connection connection, Map<String, String> params) throws SQLException {
        String schema = params.getOrDefault("schemaPattern",null);
        String table = params.getOrDefault("table",null);

        DbType dbType = DbTypeKit.getDbType(connection);
        DBDialect dbDialect = DBDialectMatcher.getDBDialect(dbType);

        ExecResult<TablePrimaryKey> ret = new ExecResult<>();
        TablePrimaryKey primaryKey = dbDialect.getPrimaryKey(connection, schema, table);
        ret.setStatus(ExecResult.SUCCESS);
        ret.setBody(primaryKey);

        return ret;
    }
}
