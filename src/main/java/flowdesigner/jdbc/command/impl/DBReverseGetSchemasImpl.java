package flowdesigner.jdbc.command.impl;

import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.dialect.DBDialect;
import flowdesigner.jdbc.dialect.DBDialectMatcher;
import flowdesigner.jdbc.model.SchemaEntity;
import flowdesigner.jdbc.util.sql.core.DBType;
import flowdesigner.jdbc.util.sql.kit.DBTypeKit;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DBReverseGetSchemasImpl implements Command<ExecResult> {
    public ExecResult exec(Connection conn, Map<String, String> params) {

        String schema = params.getOrDefault("schemaPattern",null);

        ExecResult ret = new ExecResult();

        List<SchemaEntity> schemaEntities = null;
        try {
            schemaEntities = fetchSchemaEntities(conn, schema);
            ret.setStatus(ExecResult.SUCCESS);
            ret.setBody(schemaEntities);
        } catch (Exception e) {
            ret.setStatus(ExecResult.FAILED);
            ret.setBody(e.getMessage());
            logger.severe( e.getMessage());
        }

        return ret;
    }

    /**
     * 获取所有数据表列表
     * @return
     */
    protected List<SchemaEntity> fetchSchemaEntities(Connection conn, String schemaPattern) throws SQLException {
        List<SchemaEntity> schemaEntities;
        try {
            DBType dbType = DBTypeKit.getDBType(conn);
            DBDialect dbDialect = DBDialectMatcher.getDBDialect(dbType);
            schemaEntities = dbDialect.getAllSchemas(conn, schemaPattern);
        } catch (SQLException e) {
            logger.severe("读取表清单出错"+ e.getMessage() );
            throw new RuntimeException(e);
        }

        return schemaEntities;
    }
}
