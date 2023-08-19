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

import com.alibaba.druid.DbType;
import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.dialect.DBDialect;
import flowdesigner.jdbc.dialect.DBDialectMatcher;
import flowdesigner.jdbc.command.model.TableEntity;
import flowdesigner.util.DbTypeKit;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.*;


/**
 * @desc : 数据库逆向，解析表清单的字段以及索引
 */
@Slf4j
public class DBReverseGetTableDDLImpl implements Command<ExecResult<List<TableEntity>>> {
    public ExecResult<List<TableEntity>> exec(Connection conn, Map<String, String> params) throws SQLException {

        String schema = params.getOrDefault("schemaPattern",null);
        String tables = params.getOrDefault("tables","");
        String types = params.getOrDefault("types","TABLE");

        String[] tableList = tables.split(",");

        ExecResult<List<TableEntity>> ret = new ExecResult<>();
        List<TableEntity> tableEntities = fillTablesEntities(conn, schema, tableList, types.split(","));
        ret.setStatus(ExecResult.SUCCESS);
        ret.setBody(tableEntities);

        return ret;
    }


    /**
     * 获取所有数据表实体的字段及索引
     *
     * @param conn
     * @param tableNameList
     * @return
     */
    protected List<TableEntity> fillTablesEntities(Connection conn, String schemaPattern, String[] tableNameList, String[] types) throws SQLException {
        List<TableEntity> tableEntities = new ArrayList<>();

        DatabaseMetaData meta = conn.getMetaData();
        DbType dbType = DbTypeKit.getDbType(conn);
        DBDialect dbDialect = DBDialectMatcher.getDBDialect(dbType);

        if (1 == tableNameList.length && "".equals(tableNameList[0])) {
            String tableNamePattern = dbDialect.getTableNamePattern(conn);
            return dbDialect.createTableEntity(conn, meta, schemaPattern, tableNamePattern, types);
        }

        for (String tableName : tableNameList) {
            List<TableEntity> tableEntities1 = dbDialect.createTableEntity(conn, meta, schemaPattern, tableName, types);
            if (tableEntities1.isEmpty()) {
                continue;
            }
            tableEntities.addAll(tableEntities1);
        }

        return tableEntities;
    }
}
