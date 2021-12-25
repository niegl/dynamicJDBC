package flowdesigner.jdbc.command.impl;


import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.dialect.DBDialect;
import flowdesigner.jdbc.dialect.DBDialectMatcher;
import flowdesigner.jdbc.model.FKColumnField;
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
public class DBReverseGetFKColumnFieldImpl implements Command<ExecResult> {

    public ExecResult exec(Connection conn, Map<String, String> params) {

        String foreignCatalog = params.get("foreignCatalog").toUpperCase();
        String foreignSchema = params.get("foreignSchema").toUpperCase();
        String foreignTable = params.get("foreignTable").toUpperCase();
        if (StringKit.isBlank(foreignCatalog)
                ||StringKit.isBlank(foreignSchema)
                ||StringKit.isBlank(foreignTable)) {
            throw new IllegalArgumentException("parameter [foreign*] not exists");
        }

        ExecResult ret = new ExecResult();

        //获取连接正常的情况下，进入下一步
        List<FKColumnField> tableEntities = null;
        try {
            tableEntities = getCrossReference(conn
                    ,foreignCatalog,foreignSchema,foreignTable);
            ret.setStatus(ExecResult.SUCCESS);
            ret.setBody(tableEntities);
        } catch (Exception e) {
            ret.setStatus(ExecResult.FAILED);
            ret.setBody(e.getMessage());
            logger.severe( e.getMessage());
        } finally {
//            JdbcKit.close(conn);
        }

        return ret;
    }

    /**
     * 获取数据表的外键
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
