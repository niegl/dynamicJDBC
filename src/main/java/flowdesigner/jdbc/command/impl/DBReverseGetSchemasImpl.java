package flowdesigner.jdbc.command.impl;

import com.alibaba.druid.DbType;
import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.dialect.DBDialect;
import flowdesigner.jdbc.dialect.DBDialectMatcher;
import flowdesigner.jdbc.command.model.SchemaEntity;
import flowdesigner.util.DbTypeKit;
import flowdesigner.util.raw.kit.StringKit;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class DBReverseGetSchemasImpl implements Command<ExecResult<List<SchemaEntity>>> {
    public ExecResult<List<SchemaEntity>> exec(Connection conn, Map<String, String> params) throws SQLException {

        String catalog = params.getOrDefault("catalog",null);
        String schemaPattern = params.getOrDefault("schemaPattern",null);

        ExecResult<List<SchemaEntity>> ret = new ExecResult<>();
        List<SchemaEntity> schemaEntities = fetchSchemaEntities(conn, catalog, schemaPattern);
        ret.setStatus(ExecResult.SUCCESS);
        ret.setBody(schemaEntities);

        return ret;
    }

    /**
     * 获取所有数据表列表.
     * 分为以下几种情况：
     * <li>有catalog，没有schema</li>
     * <li>没有catalog，有schema</li>
     * <li>有catalog，有特定catalog下的所有schema</li>
     * <li>两个都有</li>
     * @return List<SchemaEntity>
     */
    private List<SchemaEntity> fetchSchemaEntities(Connection conn, @Nullable String catalog, @Nullable String schemaPattern) throws SQLException {
        List<SchemaEntity> schemaEntities = new ArrayList<>();
        try {
            DbType dbType = DbTypeKit.getDbType(conn);
            DBDialect dbDialect = DBDialectMatcher.getDBDialect(dbType);

            /*
            * 有些传统关系型数据库可能不支持meta.getSchemas()接口，这时需要替换为getCatalogs接口。
            * 目前通过support函数来判定是否支持接口.
            */
            // 如果没有指定catalog，则获取所有的catalog（如果指定了获取catalog下的所有schema）
            boolean supportsCatalogs = conn.getMetaData().supportsCatalogsInTableDefinitions();
            boolean supportsSchemas = conn.getMetaData().supportsSchemasInTableDefinitions();

            if (supportsCatalogs) {
                schemaEntities = dbDialect.getAllCatalogs(conn);
                // 以下代码在v1.01使用
//                if (StringKit.isBlank(catalog)) {
//                    schemaEntities = dbDialect.getAllCatalogs(conn);
//                } else {
//                    SchemaEntity entity = new SchemaEntity();
//                    entity.setTABLE_CAT(catalog);
//                    schemaEntities.add(entity);
//                }
            }
            // 如果指定了catalog,获取指定catalog下的所有schema；或者如果没有指定catalog则获取所有的schema
            else if (supportsSchemas) {
                schemaEntities = dbDialect.getAllSchemas(conn, schemaPattern);
            }
        } catch (SQLException e) {
            log.error("读取数据库清单出错", e );
            throw new RuntimeException(e);
        }

        return schemaEntities;
    }
}
