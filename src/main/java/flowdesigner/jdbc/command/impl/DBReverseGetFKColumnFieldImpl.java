package flowdesigner.jdbc.command.impl;


import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.command.dialect.DBDialect;
import flowdesigner.jdbc.command.dialect.DBDialectMatcher;
import flowdesigner.jdbc.command.model.FKColumnField;
import flowdesigner.jdbc.util.raw.kit.StringKit;
import flowdesigner.jdbc.util.sql.core.DBType;
import flowdesigner.jdbc.util.sql.kit.DBTypeKit;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 通过元数据接口getCrossReference 获取表外键
 */
public class DBReverseGetFKColumnFieldImpl implements Command<ExecResult<List<FKColumnField>>> {

    public ExecResult<List<FKColumnField>> exec(Connection conn, Map<String, String> params) throws SQLException {

        String table = params.get("Table");
        if (StringKit.isBlank(table)) {
            throw new IllegalArgumentException("Table not specified");
        }

        ExecResult<List<FKColumnField>> ret = new ExecResult<>();
        List<FKColumnField> tableEntities = getFKReference(conn, table);
        ret.setStatus(ExecResult.SUCCESS);
        ret.setBody(tableEntities);

        return ret;
    }

    protected List<FKColumnField> getFKReference(Connection conn, String table) throws SQLException {
        List<FKColumnField> tableEntities;
        try {
            DBType dbType = DBTypeKit.getDBType(conn);
            DBDialect dbDialect = DBDialectMatcher.getDBDialect(dbType);
            tableEntities = dbDialect.getFKColumnField(conn,table);
        } catch (SQLException e) {
            logger.severe("读取表清单出错"+ e.getMessage());
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
    protected List<FKColumnField> getCrossReference(Connection conn,
                                                  String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
        List<FKColumnField> tableEntities;
        try {
            DBType dbType = DBTypeKit.getDBType(conn);
            DBDialect dbDialect = DBDialectMatcher.getDBDialect(dbType);
            tableEntities = dbDialect.getFKColumnField(conn,foreignCatalog,foreignSchema,foreignTable);
        } catch (SQLException e) {
            logger.severe("读取表清单出错"+ e.getMessage());
            throw new RuntimeException(e);
        }

        return tableEntities;
    }
}
