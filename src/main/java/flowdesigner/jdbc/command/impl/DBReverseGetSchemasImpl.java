package flowdesigner.jdbc.command.impl;

import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.command.dialect.DBDialect;
import flowdesigner.jdbc.command.dialect.DBDialectMatcher;
import flowdesigner.jdbc.command.model.SchemaEntity;
import flowdesigner.jdbc.util.sql.core.DBType;
import flowdesigner.jdbc.util.sql.kit.DBTypeKit;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DBReverseGetSchemasImpl implements Command<ExecResult<List<SchemaEntity>>> {
    public ExecResult<List<SchemaEntity>> exec(Connection conn, Map<String, String> params) throws SQLException {

        String schema = params.getOrDefault("schemaPattern",null);

        ExecResult<List<SchemaEntity>> ret = new ExecResult<>();
        List<SchemaEntity> schemaEntities = fetchSchemaEntities(conn, schema);
        ret.setStatus(ExecResult.SUCCESS);
        ret.setBody(schemaEntities);

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

            /*
            * 有些传统关系型数据库可能不支持meta.getSchemas()接口，这时需要替换为getCatalogs接口。
            * 目前通过support函数来判定是否支持接口.
            */
            boolean supportsSchemas = conn.getMetaData().supportsSchemasInTableDefinitions();
            if (supportsSchemas) {
                schemaEntities = dbDialect.getAllSchemas(conn, schemaPattern);
            } else {
                schemaEntities = dbDialect.getAllCatalogs(conn);
            }
        } catch (SQLException e) {
            logger.severe("读取表清单出错"+ e.getMessage() );
            throw new RuntimeException(e);
        }

        return schemaEntities;
    }
}
