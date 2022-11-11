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


import flowdesigner.jdbc.command.model.ColumnField;
import flowdesigner.jdbc.dialect.DBDialect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @desc : MySQL数据库方言
 */
public class DBDialectSQLite extends DBDialect {
    private static Pattern NUMBER_PATTERN = Pattern.compile("(\\d+)");

    public String getSchemaPattern(Connection conn) throws SQLException {
        //原本应该取dbName,但是目前没有找到好的办法从URL中解析出来
        //schemaPattern = dbname;
        return null;
    }

    public void fillColumnField(ColumnField field, Connection conn, ResultSet rs, Set<String> pkSet) throws SQLException {
        super.fillColumnField(field, conn, rs, pkSet);
        int dataType = rs.getInt("DATA_TYPE");
        String dataTypeName = rs.getString("TYPE_NAME");
        String isAutoincrement = rs.getString("IS_AUTOINCREMENT");
        int columnSize = rs.getInt("COLUMN_SIZE");
        field.setAutoIncrement(!"NO".equalsIgnoreCase(isAutoincrement));

        if(NUMBER_PATTERN.matcher(dataTypeName).find()){
            DataType dataTypeX = parseDataType(dataTypeName);
            field.setTypeName(dataTypeX.type);
            field.setLen(dataTypeX.length);
        }
        //20000的长度，是被多余解析出来的，不使用
        if(columnSize >= 2000000000){
            field.setLen(null);
            field.setScale(null);
        }
        //非数字，则不需要scale
        if(!isNumber(dataType)){
           field.setScale(null);
        }

    }

    private DataType parseDataType(String dataTypeName){
        DataType dataType = new DataType();

        Matcher matcher = NUMBER_PATTERN.matcher(dataTypeName);
        if(matcher.find()){
            int idx = dataTypeName.indexOf("(");
            dataType.type = dataTypeName.substring(0,idx);
            dataType.length = Integer.parseInt(matcher.group(0));
        }
        return dataType;
    }

    /**
     * MySQL的整数长度比实际要少一位
     * @param dataType
     * @return
     */
    private static boolean isNumber(int dataType) {
        int[] array = {Types.NUMERIC, Types.DECIMAL, Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT, Types.REAL, Types.FLOAT, Types.DOUBLE};
        for(int item : array){
            if(item == dataType){
                return true;
            }
        }
        return false;
    }

    public static class DataType{
        private String type;
        private Integer length;
    }


}
