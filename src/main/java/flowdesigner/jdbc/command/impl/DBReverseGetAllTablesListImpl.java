/*
 * Copyright 2019-2029 FISOK(www.fisok.cn).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package flowdesigner.jdbc.command.impl;


import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.command.dialect.DBDialect;
import flowdesigner.jdbc.command.dialect.DBDialectMatcher;
import flowdesigner.jdbc.command.model.TableEntity;
import flowdesigner.jdbc.util.sql.core.DBType;
import flowdesigner.jdbc.util.sql.kit.DBTypeKit;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * 数据库逆向，解析表清单功能
 */
public class DBReverseGetAllTablesListImpl implements Command<ExecResult<List<TableEntity>>> {

    public ExecResult<List<TableEntity>> exec(Connection conn,Map<String, String> params) throws SQLException {
        String schema = params.getOrDefault("schemaPattern",null);

        ExecResult<List<TableEntity>> ret = new ExecResult<>();
        List<TableEntity> tableEntities = fetchTableEntities(conn, schema);
        ret.setStatus(ExecResult.SUCCESS);
        ret.setBody(tableEntities);

        return ret;
    }

    /**
     * 获取所有数据表列表
     * @return
     */
    protected List<TableEntity> fetchTableEntities(Connection conn, String schemaPattern) throws SQLException {
        List<TableEntity> tableEntities;
        try {
            DBType dbType = DBTypeKit.getDBType(conn);
            DBDialect dbDialect = DBDialectMatcher.getDBDialect(dbType);
            tableEntities = dbDialect.getAllTables(conn, schemaPattern);
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"读取表清单出错", e);
            throw new RuntimeException(e);
        }

        return tableEntities;
    }
}
