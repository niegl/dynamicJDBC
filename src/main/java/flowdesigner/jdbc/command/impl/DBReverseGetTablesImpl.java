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
import com.github.houbb.auto.log.annotation.AutoLog;
import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.dialect.DBDialect;
import flowdesigner.jdbc.dialect.DBDialectMatcher;
import flowdesigner.jdbc.command.model.TableEntity;
import flowdesigner.util.sql.DbTypeKit;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 数据库逆向，解析表清单功能. 支持表和视图
 */
@Slf4j
public class DBReverseGetTablesImpl implements Command<ExecResult<List<TableEntity>>> {

    public ExecResult<List<TableEntity>> exec(Connection conn,Map<String, String> params) throws SQLException {
        String schema = params.getOrDefault("schemaPattern",null);
        String types = params.getOrDefault("types","TABLE");

        ExecResult<List<TableEntity>> ret = new ExecResult<>();
        List<TableEntity> tableEntities = getTableEntities(conn, schema, types.split(","));
        ret.setStatus(ExecResult.SUCCESS);
        ret.setBody(tableEntities);

        return ret;
    }

    /**
     * 获取所有数据表列表
     * @return
     */
    @AutoLog
    protected List<TableEntity> getTableEntities(Connection conn, String schemaPattern, String[] types) throws SQLException {
        DbType dbType = DbTypeKit.getDbType(conn);
        DBDialect dbDialect = DBDialectMatcher.getDBDialect(dbType);

        return dbDialect.getAllTables(conn, schemaPattern, types);
    }
}
