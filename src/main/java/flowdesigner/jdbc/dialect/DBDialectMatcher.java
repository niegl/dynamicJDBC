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
package flowdesigner.jdbc.dialect;

import com.alibaba.druid.DbType;
import flowdesigner.jdbc.dialect.impl.*;


import java.util.HashMap;
import java.util.Map;

/**
 * @desc :
 */
public class DBDialectMatcher {

    private static final Map<DbType,DBDialect> dialects = new HashMap<>() {{
        put(DbType.mysql, new DBDialectMySQL());
        put(DbType.oracle, new DBDialectOracle());
        put(DbType.postgresql, new DBDialectPostgreSQL());
        put(DbType.db2, new DBDialectDB2());
        put(DbType.dm, new DBDialectDM());
        put(DbType.jtds, new DBDialectSQLServer_jTDS());
        put(DbType.kingbase, new DBDialectKingbase());
        put(DbType.sqlite, new DBDialectSQLite());
    }};

    /**
     * 数据库方言解析类，默认的情况下，使用默认的
     * @param dbType
     * @return
     */
    public static DBDialect getDBDialect(DbType dbType){
        DBDialect dbDialect = dialects.get(dbType);
        if(dbDialect == null){
            dbDialect = new DBDialect();
        }
        return dbDialect;
    }
}
