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
public class DBReverseGetFKColumnFieldImpl implements Command<ExecResult<List<FKColumnField>>> {

    public ExecResult<List<FKColumnField>> exec(Connection conn, Map<String, String> params) throws SQLException {
        String schemaPattern = params.getOrDefault("schemaPattern",null);
        String table = params.getOrDefault("table", params.getOrDefault("Table", null));
        if (StringKit.isBlank(table)) {
            throw new IllegalArgumentException("table not specified");
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
            DbType dbType = DbTypeKit.getDbType(conn);
            DBDialect dbDialect = DBDialectMatcher.getDBDialect(dbType);
            tableEntities = dbDialect.getFKColumnField(conn, schemaPattern, table);
        } catch (SQLException e) {
            log.error("读取表清单出错", e);
            throw new RuntimeException(e);
        }

        return tableEntities;
    }

    /**
     * 过期：获取数据表的外键
     * parentCatalog – a catalog name; must match the catalog name as it is stored in the database; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
     * parentSchema – a schema name; must match the schema name as it is stored in the database; "" retrieves those without a schema; null means drop schema name from the selection criteria
     * parentTable – the name of the table that exports the key; must match the table name as it is stored in the database
     * foreignCatalog – a catalog name; must match the catalog name as it is stored in the database; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
     * foreignSchema – a schema name; must match the schema name as it is stored in the database; "" retrieves those without a schema; null means drop schema name from the selection criteria
     * foreignTable – the name of the table that imports the key; must match the table name as it is stored in the database
     * @return ResultSet - each row is a foreign key column description
     */
    private List<FKColumnField> getCrossReference(Connection conn,
                                                  String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
        List<FKColumnField> tableEntities;
        try {
            DbType dbType = DbTypeKit.getDbType(conn);
            DBDialect dbDialect = DBDialectMatcher.getDBDialect(dbType);
            tableEntities = dbDialect.getFKColumnField(conn,foreignCatalog,foreignSchema,foreignTable);
        } catch (SQLException e) {
            log.error("读取表清单出错", e);
            throw new RuntimeException(e);
        }

        return tableEntities;
    }
}
