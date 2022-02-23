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
import flowdesigner.jdbc.dialect.DBDialect;
import flowdesigner.jdbc.dialect.DBDialectMatcher;
import flowdesigner.jdbc.model.TableEntity;
import flowdesigner.jdbc.util.raw.kit.JdbcKit;
import flowdesigner.jdbc.util.raw.kit.StringKit;
import flowdesigner.jdbc.util.sql.core.DBType;
import flowdesigner.jdbc.util.sql.kit.DBTypeKit;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @desc : 数据库逆向，解析表清单的字段以及索引
 */
public class DBReverseGetTableDDLImpl implements Command<ExecResult> {
    public ExecResult exec(Connection conn, Map<String, String> params) {
//        super.init(params);
        String schema = params.get("schemaPattern").toUpperCase();
        String tables = params.get("tables").toUpperCase();
        if (StringKit.isBlank(schema)) {
            schema = null;
        }
        if (StringKit.isBlank(tables)) {
            throw new IllegalArgumentException("parameter [tables] not exists");
        }
        List<String> tableList = Arrays.stream(tables.split(","))
                .collect(Collectors.toList());


        ExecResult ret = new ExecResult();

//        Connection conn = null;
        try {
//            conn = createConnect();
            List<TableEntity> tableEntities = fillTableEntities(conn, schema, tableList);
            ret.setStatus(ExecResult.SUCCESS);
            ret.setBody(tableEntities);
        } catch (Exception e) {
            ret.setStatus(ExecResult.FAILED);
            ret.setBody(e.getMessage());
            logger.severe( e.getMessage());
        }
//        finally {
//            JdbcKit.close(conn);
//        }
        return ret;
    }


    /**
     * 获取所有数据表实体的字段及索引
     *
     * @param conn
     * @param tableNameList
     * @return
     */
    protected List<TableEntity> fillTableEntities(Connection conn, String schemaPattern, List<String> tableNameList) {
        List<TableEntity> tableEntities = new ArrayList<TableEntity>();

        try {
            DatabaseMetaData meta = conn.getMetaData();
            DBType dbType = DBTypeKit.getDBType(meta);
            DBDialect dbDialect = DBDialectMatcher.getDBDialect(dbType);

            for (String tableName : tableNameList) {
                TableEntity tableEntity = dbDialect.createTableEntity(conn, meta, schemaPattern, tableName);
                if (tableEntity == null) {
                    continue;
                }
                tableEntity.fillFieldsCalcValue();
                tableEntities.add(tableEntity);
            }
        } catch (SQLException e) {
            logger.severe("读取表清单出错" + e.getMessage());
            throw new RuntimeException("读取表清单出错|" + e.getMessage(), e);
        }

        return tableEntities;
    }
}
