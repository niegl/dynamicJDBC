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
import flowdesigner.jdbc.command.model.SchemaEntity;
import flowdesigner.jdbc.command.model.TableEntity;
import flowdesigner.jdbc.dialect.DBDialect;
import flowdesigner.util.sql.ConnParseKit;
import flowdesigner.util.raw.kit.JdbcKit;
import flowdesigner.util.raw.kit.StringKit;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @desc : SQLServer数据库方言
 */
public class DBDialectSQLServer_jTDS extends DBDialect {

    private String querySQL = "SELECT\n" +
            "\ttable_code =\n" +
            "CASE\n" +
            "\t\tWHEN a.colorder= 1 THEN\n" +
            "\t\td.name ELSE ''\n" +
            "\tEND,\n" +
            "\ttable_comment =\n" +
            "CASE\n" +
            "\tWHEN a.colorder= 1 THEN\n" +
//            "\tISNULL( f.value, '' ) ELSE ''\n" +
            "\tCONVERT(nvarchar(1000), f.value) ELSE ''\n" +
            "\tEND,\n" +
            "\tcolumn_index = a.colorder,\n" +
            "\tcolumn_name = a.name,\n" +
            "\tcolumn_is_identity =\n" +
            "CASE\n" +
            "\tWHEN COLUMNPROPERTY( a.id, a.name, 'IsIdentity' ) = 1 THEN\n" +
            "\t'Y' ELSE ''\n" +
            "\tEND,\n" +
            "\tcolumn_is_primary_key =\n" +
            "CASE\n" +
            "\tWHEN EXISTS (\n" +
            "\tSELECT\n" +
            "\t\t1\n" +
            "\tFROM\n" +
            "\t\tsysobjects\n" +
            "\tWHERE\n" +
            "\t\txtype = 'PK'\n" +
            "\t\tAND name IN ( SELECT name FROM sysindexes WHERE indid IN ( SELECT indid FROM sysindexkeys WHERE id = a.id AND colid = a.colid ) )\n" +
            "\t\t) THEN\n" +
            "\t\t'Y' ELSE ''\n" +
            "\tEND,\n" +
            "\tcolumn_data_type = b.name,\n" +
            "\tcolumn_data_bytes = a.length,\n" +
            "\tcolumn_data_length = COLUMNPROPERTY( a.id, a.name, 'PRECISION' ),\n" +
            "\tcolumn_data_scale = ISNULL( COLUMNPROPERTY( a.id, a.name, 'Scale' ), 0 ),\n" +
            "\tcolumn_data_is_required =\n" +
            "CASE\n" +
            "\t\tWHEN a.isnullable= 1 THEN\n" +
            "\t\t'Y' ELSE ''\n" +
            "\tEND,\n" +
            "\tcolumn_default_value = ISNULL( e.text, '' ),\n" +
//            "\tcolumn_comment = ISNULL( g.[value], '' )\n" +
            "\tcolumn_comment = CONVERT(nvarchar(1000), ISNULL( g.[value], '' ))\n" +
            "FROM\n" +
            "\tsyscolumns a\n" +
            "\tLEFT JOIN systypes b ON a.xusertype= b.xusertype\n" +
            "\tINNER JOIN sysobjects d ON a.id= d.id\n" +
            "\tAND d.xtype= 'U'\n" +
            "\tAND d.name<> 'dtproperties'\n" +
            "\tLEFT JOIN syscomments e ON a.cdefault= e.id\n" +
            "\tLEFT JOIN sys.extended_properties g ON a.id= g.major_id\n" +
            "\tAND a.colid= g.minor_id\n" +
            "\tLEFT JOIN sys.extended_properties f ON d.id= f.major_id\n" +
            "\tAND f.minor_id= 0\n" +
            "where   d.name=?\n" +
            "ORDER BY\n" +
            "\ta.id,\n" +
            "\ta.colorder;";

    @Override
    public String getTableNamePattern(Connection conn, String tableNamePattern) throws SQLException {
        if (StringKit.isBlank(tableNamePattern)) {
            return "%";
        }
        return tableNamePattern;
    }

    protected boolean isValidTable(String tableName) {
        return !tableName.equalsIgnoreCase("PDMAN_DB_VERSION")
                && !tableName.equalsIgnoreCase("trace_xe_action_map")
                && !tableName.equalsIgnoreCase("trace_xe_event_map")
                && !tableName.equalsIgnoreCase("spt_monitor")
                && !tableName.equalsIgnoreCase("spt_fallback_usg")
                && !tableName.equalsIgnoreCase("spt_fallback_dev")
                && !tableName.equalsIgnoreCase("spt_fallback_db")
                && !tableName.equalsIgnoreCase("MSreplication_options")
                ;
    }

    @Override
    public List<TableEntity> getAllTables(Connection conn, String catalog, String schemaPattern, String[] types) throws SQLException {
        // SQLserver数据库默认只获取当前catalog下的表。要想获取全部表，需要制定catalog获取
        ArrayList<TableEntity> tableEntities = new ArrayList<>();
        List<SchemaEntity> allCatalogs = getAllCatalogs(conn);
        for (SchemaEntity aCatalog: allCatalogs) {
            String tableCat = aCatalog.getTABLE_CAT();
            List<TableEntity> allTables = getAllTables(conn, tableCat, null, null, types);
            tableEntities.addAll(allTables);
        }
        return tableEntities;
    }

    @Override
    public void fillTableEntityNoColumn(TableEntity tableEntity, Connection conn, ResultSet rs) throws SQLException {
        super.fillTableEntityNoColumn(tableEntity,conn,rs);
        ResultSet nrs = getResultSet(conn,tableEntity.getTABLE_NAME());
        try {
            if (nrs.next()) {
                String remarks = nrs.getString("table_comment");
//            String defName = remarks;
//            String comment = "";

                //如果remark中有分号等分割符，则默认之后的就是注释说明文字
//            if(StringKit.isNotBlank(remarks)){
//                Pair<String, String> pair = ConnParseKit.parseNameAndComment(remarks);
//                defName = pair.getLeft();
//                comment = pair.getRight();
//            }
//            tableEntity.setTABLE_NAME(defName);
                tableEntity.setREMARKS(remarks);
            }
        } finally {
            JdbcKit.close(nrs.getStatement());
            JdbcKit.close(nrs);
        }

    }

    @Override
    public void fillTableEntity(TableEntity tableEntity, Connection conn) throws SQLException {
        super.fillTableEntity(tableEntity, conn);

        ResultSet nrs = getResultSet(conn,tableEntity.getTABLE_NAME());
        try {
            while (nrs.next()) {
                String columnName = nrs.getString("column_name");
                String columnIsPrimaryKey = nrs.getString("column_is_primary_key");
                String columnDataType = nrs.getString("column_data_type");
                String columnDataLength = nrs.getString("column_data_length");
                String columnDataScale = nrs.getString("column_data_scale");
                String columnDataIsRequired = nrs.getString("column_data_is_required");
                String columnDefaultValue = nrs.getString("column_default_value");
                String columnComment = nrs.getString("column_comment");
                ColumnField field = tableEntity.lookupField(columnName);
                if (field != null) {
                    String defName = columnComment;
                    String comment = "";

                    //如果remark中有分号等分割符，则默认之后的就是注释说明文字
                    if (StringKit.isNotBlank(columnComment)) {
                        Pair<String, String> pair = ConnParseKit.parseNameAndComment(columnComment);
                        defName = pair.getLeft();
                        comment = pair.getRight();
                    }
                    field.setDefName(defName);
                    field.setComment(comment);
                    field.setDefaultValue(columnDefaultValue);
                }

            }
        } finally {
            JdbcKit.close(nrs);
        }

    }





    private ResultSet getResultSet(Connection conn,String tableName) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        pstmt = conn.prepareStatement(querySQL);
        pstmt.setString(1,tableName);
        rs = pstmt.executeQuery();
        return rs;
    }
}
