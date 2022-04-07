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
import flowdesigner.jdbc.util.raw.kit.StringKit;
import flowdesigner.jdbc.util.sql.core.DBType;
import flowdesigner.jdbc.util.sql.kit.DBTypeKit;
import lombok.SneakyThrows;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @desc : 数据库逆向，解析表清单的字段以及索引
 */
public class DBReverseGetTableDDLImpl implements Command<ExecResult<List<TableEntity>>> {
    public ExecResult<List<TableEntity>> exec(Connection conn, Map<String, String> params) throws SQLException {

        String schema = params.getOrDefault("schemaPattern",null);
        String tables = params.getOrDefault("tables",null);
        if (StringKit.isBlank(schema)) {
            schema = null;
        }
        if (StringKit.isBlank(tables)) {
            tables = "%";
        }
        List<String> tableList = Arrays.stream(tables.split(","))
                .collect(Collectors.toList());

        ExecResult<List<TableEntity>> ret = new ExecResult<>();
        List<TableEntity> tableEntities = fillTableEntities(conn, schema, tableList);
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
    protected List<TableEntity> fillTableEntities(Connection conn, String schemaPattern, List<String> tableNameList) throws SQLException {
        List<TableEntity> tableEntities = new ArrayList<>();

        DatabaseMetaData meta = conn.getMetaData();
        DBType dbType = DBTypeKit.getDBType(meta);
        DBDialect dbDialect = DBDialectMatcher.getDBDialect(dbType);

        for (String tableName : tableNameList) {
            List<TableEntity> tableEntities1 = dbDialect.createTableEntity(conn, meta, schemaPattern, tableName);
            if (tableEntities1.isEmpty()) {
                continue;
            }
            tableEntities.addAll(tableEntities1);
        }

        return tableEntities;
    }
}
