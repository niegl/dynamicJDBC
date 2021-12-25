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
import flowdesigner.jdbc.model.TableEntity;
import flowdesigner.jdbc.util.raw.kit.FileKit;
import flowdesigner.jdbc.util.raw.kit.IOKit;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author : 杨松<yangsong158@qq.com>
 * @date : 2021/9/11
 * @desc : 将DDL语句解析为表结构
 */
public class ParseDDLToTableImpl implements Command<ExecResult> {
    /** 使用java自带的log工具 */
    private static final Logger logger = Logger.getLogger("ParseDDLToTableImpl");
    private static String DB_URL = "jdbc:h2:mem:MockChiner;DB_CLOSE_DELAY=-1";

    public ExecResult exec(Connection connection,Map<String, String> params) {
        String ddlFile = params.get("ddlFile");
        ExecResult ret = new ExecResult();
        Connection conn = null;
        try {
            String ddlContent = readDDLFile(ddlFile);

            conn = DriverManager.getConnection(DB_URL);
            createTable(conn,ddlContent);

            String tables = getALLTablesString(conn);
            IOKit.close(conn);//conn.getMetaData(); 只能使用一次,所以要重新取一次
            conn = DriverManager.getConnection(DB_URL);
            ret = parseTableDDL(conn,tables);
        } catch (IOException | SQLException e) {
            ret.setBody(e.getMessage());
            ret.setStatus(ExecResult.FAILED);
            logger.log(Level.SEVERE,e.getMessage(),e);
        } finally {
            IOKit.close(conn);
        }
        return ret;
    }

    private String readDDLFile(String ddlFile) throws IOException {
        File inFile = new File(ddlFile);
        InputStream inputStream = null;
        try {
            inputStream = FileKit.openInputStream(inFile);
            return IOKit.toString(inputStream,"UTF-8");
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE,"读取DDL文件出错:"+ddlFile, e);
            throw new RuntimeException(e);
        } finally {
            IOKit.close(inputStream);
        }
    }

    private void createTable(Connection connection,String ddlScript) throws SQLException, IOException {
        Statement stmt = connection.createStatement();
        stmt.execute(ddlScript);
    }

    private String getALLTablesString(Connection connection) throws SQLException {
        Map<String,String> params = new HashMap<>();
        DBReverseGetAllTablesListImpl cmd = new DBReverseGetAllTablesListImpl();
//        cmd.setDbConn(connection);
        ExecResult ret = cmd.exec(connection, new HashMap<>());
        if(ret.getStatus().equals(ExecResult.SUCCESS)){
            StringBuffer tables = new StringBuffer();
            List<TableEntity> dataList = (List<TableEntity>)ret.getBody();
            dataList.forEach(tableEntity->{
                tables.append(tableEntity.getDefKey()).append(",");
            });
            if(tables.length()>0){
                return tables.substring(0,tables.length()-1);
            }
        }
        return "";
    }

    private ExecResult parseTableDDL(Connection connection,String tables){
        Map<String,String> params = new HashMap<>();
        params.put("tables",tables);

        DBReverseGetTableDDLImpl cmd = new DBReverseGetTableDDLImpl();
//        cmd.setDbConn(connection);
        ExecResult ret = cmd.exec(connection, params);
        return ret;
    }
}
