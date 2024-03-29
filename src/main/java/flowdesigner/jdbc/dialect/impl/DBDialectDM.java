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
import flowdesigner.util.raw.kit.JdbcKit;
import flowdesigner.util.raw.kit.StringKit;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @desc : 达梦数据库方言
 */
public class DBDialectDM extends DBDialect {
    public String getSchemaPattern(Connection conn) throws SQLException {
        String schemaPattern = conn.getMetaData().getUserName().toUpperCase();
        if (StringKit.isNotBlank(schemaPattern)) {
            schemaPattern = schemaPattern.toUpperCase();
        }
        return schemaPattern;
    }

    @Override
    protected boolean isValidTable(String tableName) {
        return !tableName.startsWith("#") &&
                !tableName.equalsIgnoreCase("PDMAN_DB_VERSION");
    }

}
