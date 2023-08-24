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
package flowdesigner.jdbc.dialect.impl;

import flowdesigner.jdbc.command.model.TableEntity;
import flowdesigner.jdbc.dialect.DBDialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @desc : 人大金仓Kinbbase方言
 */
public class DBDialectKingbase extends DBDialect {
    public List<TableEntity> getAllTables(Connection conn) throws SQLException {
        List<TableEntity> tableEntities = new ArrayList<TableEntity>();
        DatabaseMetaData meta = conn.getMetaData();

        String schemaPattern = getSchemaPattern(conn);
        String tableNamePattern = getTableNamePattern(conn);
        String catalog = conn.getCatalog();

        ResultSet rs = meta.getTables(catalog, schemaPattern, tableNamePattern, new String[]{"TABLE"});
        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME");
            /**
             *  SQL Server系统保留表
             *  trace_xe_action_map,trace_xe_event_map
             */
            if (!tableName.equalsIgnoreCase("PDMAN_DB_VERSION")
                    && !tableName.equalsIgnoreCase("sysmac_compartment")
                    && !tableName.equalsIgnoreCase("sysmac_label")
                    && !tableName.equalsIgnoreCase("sysmac_level")
                    && !tableName.equalsIgnoreCase("sysmac_obj")
                    && !tableName.equalsIgnoreCase("sysmac_policy")
                    && !tableName.equalsIgnoreCase("sysmac_policy_enforcement")
                    && !tableName.equalsIgnoreCase("sysmac_user")
                    && !tableName.equalsIgnoreCase("dual")){
                TableEntity entity = createTableEntity(conn,rs);
                if(entity != null){
                    tableEntities.add(entity);
                }
            }else{
                continue;
            }
        }
        return tableEntities;
    }
}
