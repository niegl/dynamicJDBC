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
import flowdesigner.jdbc.command.dialect.DBDialect;
import flowdesigner.jdbc.command.dialect.DBDialectMatcher;
import flowdesigner.jdbc.util.sql.DbTypeKit;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

/**
 * 数据库逆向，解析表清单功能
 */
@Slf4j
public class DBReverseGetFunctionsImpl implements Command<ExecResult<Set<String>>> {

    public ExecResult<Set<String>> exec(Connection conn, Map<String, String> params) throws SQLException {

        ExecResult<Set<String>> ret = new ExecResult<>();
        Set<String> functions = fetchFunctions(conn);
        ret.setStatus(ExecResult.SUCCESS);
        ret.setBody(functions);

        return ret;
    }

    /**
     * 获取所有函数
     * @return
     */
    protected Set<String> fetchFunctions(Connection conn) throws SQLException {
        Set<String> functions;
        try {
            DbType dbType = DbTypeKit.getDbType(conn);
            DBDialect dbDialect = DBDialectMatcher.getDBDialect(dbType);
            functions = dbDialect.getAllFunctions(conn);
        } catch (SQLException e) {
            log.error("读取函数清单出错", e);
            throw new RuntimeException(e);
        }

        return functions;
    }
}
