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

import flowdesigner.jdbc.command.model.*;
import flowdesigner.util.raw.kit.JdbcKit;
import flowdesigner.util.raw.kit.StringKit;
import flowdesigner.util.sql.kit.ConnParseKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;

/**
 * @desc : 数据库方言抽象类
 */
@Slf4j
public class DBDialect {

    public String getCatalogPattern(Connection conn) throws SQLException{
        boolean supportsCatalogsInTableDefinitions = conn.getMetaData().supportsCatalogsInTableDefinitions();
        if (supportsCatalogsInTableDefinitions) {
            return getCatalogPattern(conn, conn.getCatalog());
        }
        return null;
    };

    public String getCatalogPattern(Connection conn, String catalogPattern) throws SQLException{
        boolean support = conn.getMetaData().supportsCatalogsInTableDefinitions();
        if (support) {
            if (StringKit.isBlank(catalogPattern)) {
                return null;
            }
            return catalogPattern;
        }
        return null;
    };

    /**
     * 获取数据库SchemaPattern
     * @param conn
     * @return
     * @throws SQLException
     */
    public String getSchemaPattern(Connection conn) throws SQLException{
        boolean supportsSchemasInTableDefinitions = conn.getMetaData().supportsSchemasInTableDefinitions();
        if (supportsSchemasInTableDefinitions) {
            return getSchemaPattern(conn, conn.getSchema());
        }
        return null;
    };

    public String getSchemaPattern(Connection conn, String schemaPattern) throws SQLException{
        boolean support = conn.getMetaData().supportsSchemasInTableDefinitions();
        if (support) {
            if (StringKit.isBlank(schemaPattern)) {
                return null;
            }
            return schemaPattern;
        }
        return null;
    }
    /**
     * 获取数据库TableNamePattern
     * @param conn
     * @return
     * @throws SQLException
     */
    public String getTableNamePattern(Connection conn) throws SQLException{
        return null;
    }


    /**
     * 将resultset,主键的resultset装到一个二元组中，并返回
     * @param conn
     * @param tableEntity
     * @return
     * @throws SQLException
     */
    public Pair<ResultSet,ResultSet> getColumnAndPrimaryKeyResultSetPair(Connection conn, TableEntity tableEntity) throws SQLException {
        DatabaseMetaData connMetaData = conn.getMetaData();
        String tableCat = tableEntity.getTABLE_CAT();
        String tableSchem = tableEntity.getTABLE_SCHEM();//getSchemaPattern(conn);
        String tableName = tableEntity.getTABLE_NAME();

        ResultSet rs = connMetaData.getColumns(tableCat, tableSchem, tableName, "%");
        ResultSet pkRs = connMetaData.getPrimaryKeys(tableCat, tableSchem, tableName);

        return Pair.of(rs,pkRs);
    }

    /**
     * 根据结果集，创建表实体对象，仅填充表名，中文名，注释信息
     * @param connection
     * @param rs
     * @return
     */
    public SchemaEntity createSchemaEntity(Connection connection, ResultSet rs) throws SQLException {
        SchemaEntity entity = new SchemaEntity();
        fillSchemaEntityNoTable(entity, connection,rs);
        if (StringKit.isNotBlank(entity.getDefKey())) {
            return entity;
        } else {
            return null;
        }
    }

    /**
     * 根据结果集，创建表实体对象，仅填充表名，中文名，注释信息
     * @param connection
     * @param rs
     * @return
     */
    public TableEntity createTableEntity(Connection connection,ResultSet rs) {
        try {
            TableEntity entity = new TableEntity();
            fillTableEntityNoColumn(entity, connection, rs);
            if (StringKit.isNotBlank(entity.getTABLE_NAME())) {
                return entity;
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        return null;
    }

    /**
     * 传入一个空对象，填充表名，中文名，注释信息
     * @param schemaEntity
     * @param rs
     * @throws SQLException
     */
    public void fillSchemaEntityNoTable(SchemaEntity schemaEntity, Connection connection,ResultSet rs) throws SQLException {
        String tableCat = rs.getString("TABLE_CATALOG");
        String tableSchem = rs.getString("TABLE_SCHEM");
        String defKey = tableCat+"_" + tableSchem;

        schemaEntity.setDefKey(defKey);
        schemaEntity.setTABLE_CAT(tableCat);
        schemaEntity.setTABLE_SCHEM(tableSchem);

    }

    /**
     * 传入一个空对象，填充表名，中文名，注释信息
     * @param tableEntity
     * @param rs
     * @throws SQLException
     */
    protected void fillTableEntityNoColumn(TableEntity tableEntity, Connection connection, ResultSet rs) throws SQLException {
        String tableCat = rs.getString(1);
        String tableSchem = rs.getString(2);
        String tableName = rs.getString(3);
        String remarks = StringKit.trim(rs.getString(5));
//        String defName = remarks;
//        String comment = "";

        //如果remark中有分号等分割符，则默认之后的就是注释说明文字
//        if(StringKit.isNotBlank(remarks)){
//            Pair<String, String> pair = ConnParseKit.parseNameAndComment(remarks);
//            defName = pair.getLeft();
//            comment = pair.getRight();
//        }
//        tableEntity.setDefKey(defKey);
        tableEntity.setTABLE_NAME(tableName);
        tableEntity.setREMARKS(remarks);
        tableEntity.setTABLE_CAT(tableCat);
        tableEntity.setTABLE_SCHEM(tableSchem);

    }

    /**
     * 传入表名，中文名，注释信息，获取字段明细，索引信息
     * @param tableEntity
     * @param conn
     */
    public void fillTableEntity(TableEntity tableEntity, Connection conn) throws SQLException {
        String tableName = tableEntity.getTABLE_NAME();

        ResultSet rs = null;
        ResultSet pkRs = null;

        try {
            Pair<ResultSet,ResultSet> pair = getColumnAndPrimaryKeyResultSetPair(conn,tableEntity);
            rs = pair.getLeft();
            pkRs = pair.getRight();

            Set<String> pkSet = new HashSet<String>();
            while(pkRs.next()){
                String columnName = pkRs.getString("COLUMN_NAME");
                pkSet.add(columnName);
            }

            while(rs.next()) {
                ColumnField field = new ColumnField();
                fillColumnField(field, conn, rs, pkSet);
                tableEntity.getFields().add(field);
            }
            fillTableIndexes(tableEntity,conn);
        } catch (SQLException e) {
            log.error("读取数据表"+tableName+"的字段明细出错");
//            throw new RuntimeException("读取数据表"+tableName+"的字段明细出错",e);
        } finally {
            JdbcKit.close(rs);
            JdbcKit.close(pkRs);
        }
    }

    /**
     * 填充列数据
     * TABLE_CAT String => 表类别（可为 null）
     * TABLE_SCHEM String => 表模式（可为 null）
     * TABLE_NAME String => 表名称
     * COLUMN_NAME String => 列名称
     * DATA_TYPE int => 来自 java.sql.Types 的 SQL 类型
     * TYPE_NAME String => 数据源依赖的类型名称，对于 UDT，该类型名称是完全限定的
     * COLUMN_SIZE int => 列的大小。
     * BUFFER_LENGTH 未被使用。
     * DECIMAL_DIGITS int => 小数部分的位数。对于 DECIMAL_DIGITS 不适用的数据类型，则返回 Null。
     * NUM_PREC_RADIX int => 基数（通常为 10 或 2）
     * NULLABLE int => 是否允许使用 NULL。
     * columnNoNulls - 可能不允许使用 NULL 值
     * columnNullable - 明确允许使用 NULL 值
     * columnNullableUnknown - 不知道是否可使用 null
     * REMARKS String => 描述列的注释（可为 null）
     * COLUMN_DEF String => 该列的默认值，当值在单引号内时应被解释为一个字符串（可为 null）
     * SQL_DATA_TYPE int => 未使用
     * SQL_DATETIME_SUB int => 未使用
     * CHAR_OCTET_LENGTH int => 对于 char 类型，该长度是列中的最大字节数
     * ORDINAL_POSITION int => 表中的列的索引（从 1 开始）
     * IS_NULLABLE String => ISO 规则用于确定列是否包括 null。
     * YES --- 如果参数可以包括 NULL
     * NO --- 如果参数不可以包括 NULL
     * 空字符串 --- 如果不知道参数是否可以包括 null
     * SCOPE_CATLOG String => 表的类别，它是引用属性的作用域（如果 DATA_TYPE 不是 REF，则为 null）
     * SCOPE_SCHEMA String => 表的模式，它是引用属性的作用域（如果 DATA_TYPE 不是 REF，则为 null）
     * SCOPE_TABLE String => 表名称，它是引用属性的作用域（如果 DATA_TYPE 不是 REF，则为 null）
     * SOURCE_DATA_TYPE short => 不同类型或用户生成 Ref 类型、来自 java.sql.Types 的 SQL 类型的源类型（如果 DATA_TYPE 不是 DISTINCT 或用户生成的 REF，则为 null）
     * IS_AUTOINCREMENT String => 指示此列是否自动增加
     * YES --- 如果该列自动增加
     * NO --- 如果该列不自动增加
     * 空字符串 --- 如果不能确定该列是否是自动增加参数
     * COLUMN_SIZE 列表示给定列的指定列大小。对于数值数据，这是最大精度。对于字符数据，这是字符长度。对于日期时间数据类型，这是 String 表示形式的字符长度（假定允许的最大小数秒组件的精度）。对于二进制数据，这是字节长度。对于 ROWID 数据类型，这是字节长度。对于列大小不适用的数据类型，则返回 Null。
     *
     *
     * 参数：
     * catalog - 类别名称；它必须与存储在数据库中的类别名称匹配；该参数为 "" 表示获取没有类别的那些描述；为 null 则表示该类别名称不应该用于缩小搜索范围
     * schemaPattern - 模式名称的模式；它必须与存储在数据库中的模式名称匹配；该参数为 "" 表示获取没有模式的那些描述；为 null 则表示该模式名称不应该用于缩小搜索范围
     * tableNamePattern - 表名称模式；它必须与存储在数据库中的表名称匹配
     * columnNamePattern - 列名称模式；它必须与存储在数据库中的列名称匹配
     * @param field
     * @param rs
     * @throws SQLException
     */
    public void fillColumnField(ColumnField field,Connection conn, ResultSet rs, Set<String> pkSet) throws SQLException {

        String colName = rs.getString("COLUMN_NAME");
        String remarks = StringKit.trim(rs.getString("REMARKS"));
        String typeName = rs.getString("TYPE_NAME");
        int dataType = rs.getInt("DATA_TYPE");
        int columnSize = rs.getInt("COLUMN_SIZE");
        int decimalDigits = rs.getInt("DECIMAL_DIGITS");
        String defaultValue = rs.getString("COLUMN_DEF");
        String isNullable = rs.getString("IS_NULLABLE");
        String isAutoincrement = "NO";
        defaultValue = StringKit.nvl(parseDefaultValue(dataType,defaultValue),"");


        String label = remarks;
        String comment = null;
        if(StringKit.isNotBlank(remarks)){
            Pair<String,String> columnRemarks = ConnParseKit.parseNameAndComment(remarks);
            label = columnRemarks.getLeft();
            comment = columnRemarks.getRight();
        }

        field.setDefKey(colName);
        field.setDefName(label);
        field.setComment(comment);
        field.setTypeName(typeName);
        field.setDataType(dataType);

        if(columnSize > 0){
            field.setLen(columnSize);
        }
        if(decimalDigits<=0){
            field.setScale(null);
        }else{
            field.setScale(decimalDigits);
        }
        //不需要长度的数据类型，把数据类型清除掉，防止部分数据库解析出有长度的情况
        if(withoutLenDataType(dataType)){
            field.setLen(null);
            field.setScale(null);
        }

        field.setPrimaryKey(pkSet.contains(colName));
        field.setNotNull(!"YES".equalsIgnoreCase(isNullable));
        field.setAutoIncrement(!"NO".equalsIgnoreCase(isAutoincrement));
        field.setDefaultValue(defaultValue);
    }

    /**
     * 不含长度的数据类型，这种数据类型不需要设置长度以及小数位
     * @param dataType
     * @return
     */
    protected boolean withoutLenDataType(int dataType) {
        int[] array = {Types.DATE, Types.TIMESTAMP, Types.TIME,
                Types.TIME_WITH_TIMEZONE,Types.TIMESTAMP_WITH_TIMEZONE,
                Types.CLOB,Types.BLOB, Types.NCLOB,
                Types.LONGVARCHAR,Types.LONGVARBINARY
        };
        for(int item : array){
            if(item == dataType){
                return true;
            }
        }
        return false;
    }

    public String parseDefaultValue(Integer dataType,String defaultValue){
        if(StringKit.isNotBlank(defaultValue)){
            if(defaultValue.indexOf("'::") > 0){
                defaultValue = defaultValue.substring(0,defaultValue.indexOf("'::")+1);
            }
            //如果是被''圈住，说明是字串，不需要处理
            if(defaultValue.startsWith("'") && defaultValue.endsWith("'")){
            }else{
                //如果全是数字，并且不以0开始,那么就是数字，不需要加双单引号
//                if(defaultValue.matches("[0-9]+") && !defaultValue.startsWith("0")){
                if(JdbcKit.isNumeric(dataType)){
                }else{
                    defaultValue = "'"+defaultValue+"'";
                }
            }
        }
        return defaultValue;
    }

    /**
     * 填充数据表的索引
     * @param tableEntity
     * @param conn
     * @throws SQLException
     */
    public void fillTableIndexes(TableEntity tableEntity, Connection conn) throws SQLException {
        String cat = tableEntity.getTABLE_CAT();
        String schem = tableEntity.getTABLE_SCHEM();
        String table = tableEntity.getTABLE_NAME();
        DatabaseMetaData dbMeta = conn.getMetaData();
        ResultSet rs = dbMeta.getIndexInfo(cat,schem, table,false,false);

        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME");
            String indexName = rs.getString("INDEX_NAME");
            String indexQualifier = rs.getString("INDEX_QUALIFIER");
            String columnName = rs.getString("COLUMN_NAME");
            String nonUnique = rs.getString("NON_UNIQUE");
            String ascOrDesc = rs.getString("ASC_OR_DESC");

            if(!table.equalsIgnoreCase(tableName)) {
                continue;
            }
            if(StringKit.isBlank(indexName)) {
                continue;
            }
            if(StringKit.isBlank(columnName)) {
                continue;
            }

            TableIndex index = tableEntity.lookupIndex(indexName);
            if(index == null) {
                index = new TableIndex();
                index.setDefKey(indexName);
                index.setUnique(!"1".equalsIgnoreCase(nonUnique));
                index.setIndexQualifier(indexQualifier);
                tableEntity.getIndexes().add(index);
            }

            TableIndexColumnField ticf = index.lookupField(columnName);
            if(ticf != null){
                continue;
            }
            ticf = new TableIndexColumnField();
            ticf.setFieldDefKey(columnName);
            ticf.setAscOrDesc(ascOrDesc);
            index.getFields().add(ticf);

        }

        JdbcKit.close(rs.getStatement());
        JdbcKit.close(rs);
    }

    /**
     * 取所有的数据库表单
     * @param conn
     * @return
     */
    public List<SchemaEntity> getAllSchemas(Connection conn, String schemaPattern) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();

        if (StringKit.isBlank(schemaPattern)) {
            schemaPattern = getSchemaPattern(conn, schemaPattern);
        }

        String catalog = conn.getCatalog();

        ResultSet rs = meta.getSchemas(catalog, schemaPattern);
        List<SchemaEntity> schemaEntities = new ArrayList<>();
        while (rs.next()) {
            String tableSchem = rs.getString("TABLE_SCHEM");
            /**
             *  SQL Server系统保留表
             *  trace_xe_action_map,trace_xe_event_map
             */
            if (!tableSchem.equalsIgnoreCase("PDMAN_DB_VERSION")
                    && !tableSchem.equalsIgnoreCase("trace_xe_action_map")
                    && !tableSchem.equalsIgnoreCase("trace_xe_event_map")){
                SchemaEntity entity = createSchemaEntity(conn,rs);
                if(entity != null){
                    schemaEntities.add(entity);
                }
            }else{
                continue;
            }
        }
        return schemaEntities;
    }

    public List<SchemaEntity> getAllCatalogs(Connection conn) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();

        ResultSet rs = meta.getCatalogs();
        List<SchemaEntity> schemaEntities = new ArrayList<>();
        while (rs.next()) {
            String TABLE_CAT = rs.getString("TABLE_CAT");
            /**
             *  SQL Server系统保留表
             *  trace_xe_action_map,trace_xe_event_map
             */
            if (!TABLE_CAT.equalsIgnoreCase("information_schema")
                    && !TABLE_CAT.equalsIgnoreCase("performance_schema")
                    && !TABLE_CAT.equalsIgnoreCase("mysql")
                    && !TABLE_CAT.equalsIgnoreCase("sys")
            ){
                SchemaEntity entity = new SchemaEntity();
                entity.setTABLE_CAT(TABLE_CAT);
                // 对于mysql类数据库来说，CAT和SCHEMA相等---待测试其他库
                entity.setTABLE_SCHEM(TABLE_CAT);
                schemaEntities.add(entity);
            }else{
                continue;
            }
        }
        return schemaEntities;
    }

    /**
     * 取所有的数据表清单
     * @param conn
     * @return
     */
    public List<TableEntity> getAllTables(Connection conn, String schemaPattern, String[] types) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();

        String catalog = getCatalogPattern(conn, schemaPattern);
        String schema = getSchemaPattern(conn, schemaPattern);
        String tableNamePattern = getTableNamePattern(conn);

        ResultSet rs = meta.getTables(catalog, schema, tableNamePattern, types);
        // 提高单次获取数据条数，减少请求次数、网络传输，提高效率
        rs.setFetchSize(200);


        List<TableEntity> tableEntities = new ArrayList<TableEntity>();
        while (rs.next()) {
            String tableName = rs.getString(3);
            /**
             *  SQL Server系统保留表
             *  trace_xe_action_map,trace_xe_event_map
             */
            if (!tableName.equalsIgnoreCase("PDMAN_DB_VERSION")
                    && !tableName.equalsIgnoreCase("trace_xe_action_map")
                    && !tableName.equalsIgnoreCase("trace_xe_event_map")){
                TableEntity entity = createTableEntity(conn,rs);
                if(entity != null){
                    tableEntities.add(entity);
                }
            }
        }
        return tableEntities;
    }

    /**
     * 根据表名，创建数据表实体的字段及索引
     * @param conn
     * @param meta
     * @param tableName
     * @return
     * @throws SQLException
     */
    public List<TableEntity> createTableEntity(Connection conn, DatabaseMetaData meta, String schemaPattern, String tableName, String[] types) throws SQLException {
        List<TableEntity> tableEntities = new ArrayList<>();
        ResultSet rs = null;
        try {
            String schemaPattern1 = getSchemaPattern(conn, schemaPattern);
            String catalogPattern = getCatalogPattern(conn, schemaPattern);

            rs = meta.getTables(catalogPattern, schemaPattern1, tableName, types);
            rs.setFetchSize(200);

            while (rs.next()) {
                TableEntity tableEntity = createTableEntity(conn, rs);
                if (tableEntity != null) {
                    tableEntities.add(tableEntity);
                }
            }

            for (TableEntity entity : tableEntities) {
                fillTableEntity(entity, conn);
                entity.fillFieldsCalcValue();
            }

            return tableEntities;
        } finally {
            JdbcKit.close(rs);
        }
    }

    /**
     PKTABLE_CAT String => primary key table catalog being imported (may be null)
     PKTABLE_SCHEM String => primary key table schema being imported (may be null)
     PKTABLE_NAME String => primary key table name being imported
     PKCOLUMN_NAME String => primary key column name being imported
     FKTABLE_CAT String => foreign key table catalog (may be null)
     FKTABLE_SCHEM String => foreign key table schema (may be null)
     FKTABLE_NAME String => foreign key table name
     FKCOLUMN_NAME String => foreign key column name
     FK_NAME String => foreign key name (may be null)
     PK_NAME String => primary key name (may be null)
     * @param conn
     * @param table
     * @return
     * @throws SQLException
     */
    public List<FKColumnField> getFKColumnField(Connection conn, String schemaPattern, String table) throws SQLException {
        String schema = getSchemaPattern(conn, schemaPattern);
        String catalog = getCatalogPattern(conn, schemaPattern);

        DatabaseMetaData meta = conn.getMetaData();

        ResultSet rs = meta.getImportedKeys(catalog, schema, table);
        return getFkColumnFields(rs);
    }

    public List<FKColumnField> getFKReference(Connection conn, String schemaPattern, String table) throws SQLException {

        String schema = getSchemaPattern(conn, schemaPattern);
        String catalog = getCatalogPattern(conn, schemaPattern);

        DatabaseMetaData meta = conn.getMetaData();
        ResultSet rs = meta.getExportedKeys(catalog, schema, table);
        return getFkColumnFields(rs);
    }

    @NotNull
    private List<FKColumnField> getFkColumnFields(ResultSet rs) throws SQLException {
        List<FKColumnField> tableEntities = new ArrayList<>();
        while (rs.next()) {
            FKColumnField fkColumnField = new FKColumnField();

            String PKTABLE_CAT = rs.getString("PKTABLE_CAT");
            String PKTABLE_SCHEM = rs.getString("PKTABLE_SCHEM");
            String PKTABLE_NAME = rs.getString("PKTABLE_NAME");
            String PKCOLUMN_NAME = rs.getString("PKCOLUMN_NAME");
            String FKTABLE_CAT = rs.getString("FKTABLE_CAT");
            String FKTABLE_SCHEM = rs.getString("FKTABLE_SCHEM");
            String FKTABLE_NAME = rs.getString("FKTABLE_NAME");
            String FKCOLUMN_NAME = rs.getString("FKCOLUMN_NAME");
            String PK_NAME = rs.getString("PK_NAME");
            String FK_NAME = rs.getString("FK_NAME");

            fkColumnField.setPKTABLE_CAT(PKTABLE_CAT);
            fkColumnField.setPKTABLE_SCHEM(PKTABLE_SCHEM);
            fkColumnField.setPKTABLE_NAME(PKTABLE_NAME);
            fkColumnField.setPKCOLUMN_NAME(PKCOLUMN_NAME);

            fkColumnField.setFKTABLE_CAT(FKTABLE_CAT);
            fkColumnField.setFKTABLE_SCHEM(FKTABLE_SCHEM);
            fkColumnField.setFKTABLE_NAME(FKTABLE_NAME);
            fkColumnField.setFKCOLUMN_NAME(FKCOLUMN_NAME);

            fkColumnField.setPK_NAME(PK_NAME);
            fkColumnField.setFK_NAME(FK_NAME);

            tableEntities.add(fkColumnField);
        }
        return tableEntities;
    }

    /**
     * 获取数据表外键
     * @param conn
     * @param foreignCatalog
     * @param foreignSchema
     * @param foreignTable
     * @return
     */
    public List<FKColumnField> getFKColumnField(Connection conn, String foreignCatalog,String foreignSchema, String foreignTable) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();

//        String schemaPattern = null;
        String schemaPattern = getSchemaPattern(conn);
        String tableNamePattern = getTableNamePattern(conn);
        String catalog = conn.getCatalog();

        ResultSet rs = meta.getCrossReference(catalog, schemaPattern, tableNamePattern
                , foreignCatalog,foreignSchema,foreignTable);
        List<FKColumnField> tableEntities = new ArrayList<>();
        while (rs.next()) {
            FKColumnField fkColumnField = new FKColumnField();
            String PKTABLE_CAT = rs.getString("PKTABLE_CAT");
            String PKTABLE_SCHEM = rs.getString("PKTABLE_SCHEM");
            String PKTABLE_NAME = rs.getString("PKTABLE_NAME");
            String PKCOLUMN_NAME = rs.getString("PKCOLUMN_NAME");
            String FKCOLUMN_NAME = rs.getString("FKCOLUMN_NAME");
            String FK_NAME = rs.getString("FK_NAME");
            fkColumnField.setPKTABLE_CAT(PKTABLE_CAT);
            fkColumnField.setPKTABLE_SCHEM(PKTABLE_SCHEM);
            fkColumnField.setPKTABLE_NAME(PKTABLE_NAME);
            fkColumnField.setPKCOLUMN_NAME(PKCOLUMN_NAME);
            fkColumnField.setFKCOLUMN_NAME(FKCOLUMN_NAME);
            fkColumnField.setFK_NAME(FK_NAME);

            tableEntities.add(fkColumnField);
        }
        return tableEntities;
    }

    /**
     * 获取数据库中的数据类型.
     * @param conn
     * @return
     * @throws SQLException
     */
    public List<DataTypeEntity> getDataType(Connection conn) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        List<DataTypeEntity> infoEntities = new ArrayList<>();
        ResultSet typeInfo = meta.getTypeInfo();
        while (typeInfo.next()) {
            String TYPE_NAME = typeInfo.getString("TYPE_NAME");
            int DATA_TYPE = typeInfo.getInt("DATA_TYPE");
            DataTypeEntity typeInfoEntity = new DataTypeEntity(TYPE_NAME,DATA_TYPE);
            infoEntities.add(typeInfoEntity);
        }
        return infoEntities;
    }

    public Set<String> getAllFunctions(Connection conn) throws SQLException {
        Set<String> functions = new HashSet<>();
        String schemaPattern = getSchemaPattern(conn, null);
        String catalog = getCatalogPattern(conn, null);

        DatabaseMetaData meta = conn.getMetaData();
        ResultSet functions1 = meta.getFunctions(catalog, schemaPattern, "%");
        while (functions1.next()) {
            String FUNCTION_NAME = functions1.getString("FUNCTION_NAME");
            functions.add(FUNCTION_NAME);
        }
        Collections.addAll(functions,meta.getTimeDateFunctions().split(","));
        Collections.addAll(functions,meta.getStringFunctions().split(","));
        Collections.addAll(functions,meta.getNumericFunctions().split(","));
        Collections.addAll(functions,meta.getSystemFunctions().split(","));

        functions.removeIf(String::isEmpty);

        return functions;
    }
}
