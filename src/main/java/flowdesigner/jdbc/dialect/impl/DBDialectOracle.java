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


import flowdesigner.jdbc.dialect.DBDialect;
import flowdesigner.jdbc.util.raw.kit.StringKit;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 * @author : 杨松<yangsong158@qq.com>
 * @date : 2021/6/14
 * @desc : ORACLE数据库方言
 */
public class DBDialectOracle extends DBDialect {

    public String getSchemaPattern(Connection conn) throws SQLException {
        String schemaPattern = conn.getMetaData().getUserName().toUpperCase();
        if (StringKit.isNotBlank(schemaPattern)) {
            schemaPattern = schemaPattern.toUpperCase();
        }
        return schemaPattern;
    }


}
