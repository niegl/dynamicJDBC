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
import flowdesigner.util.raw.kit.StringKit;
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
        List<TableEntity> tableEntities = getTableDDL(conn, schema, tableList, types.split(","));
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
    private List<TableEntity> getTableDDL(Connection conn, String schemaPattern, String[] tableNameList, String[] types) throws SQLException {
        List<TableEntity> tableEntities = new ArrayList<>();
        String catalog = null;
        DbType dbType = DbTypeKit.getDbType(conn);
        DBDialect dbDialect = DBDialectMatcher.getDBDialect(dbType);
        DatabaseMetaData meta = conn.getMetaData();

        // 如果数据库支持catalog，那么实际传递过来的是catalog
        boolean supportsCatalogs = meta.supportsCatalogsInTableDefinitions();
        if (supportsCatalogs) {
            catalog = schemaPattern;
            schemaPattern = null;
        }

        // getColumn()接口中，hive数据库必须指定具体的schema，否则会抛异常
        if (dbType == DbType.hive) {
            if (StringKit.isBlank(schemaPattern)) {
                throw new RuntimeException("parameter [schemaPattern] can not be empty");
            }
        }

        if (1 == tableNameList.length && "".equals(tableNameList[0])) {
            return dbDialect.getTableEntities(conn, catalog, schemaPattern, null, types);
        }

        for (String tableName : tableNameList) {
            List<TableEntity> tableEntities1 = dbDialect.getTableEntities(conn, catalog, schemaPattern, tableName, types);
            if (tableEntities1.isEmpty()) {
                continue;
            }
            tableEntities.addAll(tableEntities1);
        }

        return tableEntities;
    }
}
