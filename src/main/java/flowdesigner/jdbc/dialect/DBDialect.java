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
import flowdesigner.util.sql.ConnParseKit;
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
//        boolean supportsCatalogsInTableDefinitions = conn.getMetaData().supportsCatalogsInTableDefinitions();
//        if (supportsCatalogsInTableDefinitions) {
//            return getCatalogPattern(conn, conn.getCatalog());
//        }
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
    public String getTableNamePattern(Connection conn, String tableNamePattern) throws SQLException{
        if (StringKit.isBlank(tableNamePattern)) {
            return null;
        }
        return tableNamePattern;
    }

    public String getTableNamePattern(Connection conn) throws SQLException{
        return getTableNamePattern(conn,null);
    }

    /**
     * 获取指定表的主键
     * @param conn
     * @param schema
     * @param tableName
     * @return
     * @throws SQLException
     */
    public TablePrimaryKey getPrimaryKey(Connection conn, String catalog, String schema,String tableName) throws SQLException {
        TablePrimaryKey primaryKey = new TablePrimaryKey();

        catalog = getCatalogPattern(conn, catalog);
        schema = getSchemaPattern(conn, schema);

        DatabaseMetaData connMetaData = conn.getMetaData();
        ResultSet rs = connMetaData.getPrimaryKeys(catalog, schema, tableName);
        try {
            while (rs.next()) {
                String COLUMN_NAME = rs.getString("COLUMN_NAME");
                short KEY_SEQ = rs.getShort("KEY_SEQ");
                String PK_NAME = rs.getString("PK_NAME");

                if (PK_NAME != null) {
                    primaryKey.setPk_name(PK_NAME);
                }

                TablePrimaryKeyColumnField columnField = new TablePrimaryKeyColumnField();
                columnField.setCOLUMN_NAME(COLUMN_NAME);
                columnField.setKEY_SEQ(KEY_SEQ);
                primaryKey.getColumns().add(columnField);
            }
        } finally {
            JdbcKit.close(rs.getStatement());
            JdbcKit.close(rs);
        }

        return primaryKey;
    }

    /**
     * 根据结果集，创建表实体对象，仅填充表名，中文名，注释信息
     * @param connection
     * @param rs
     * @return
     */
    public SchemaEntity createSchemaEntity(Connection connection, ResultSet rs) throws SQLException {
        SchemaEntity entity = null;
        String tableCat = rs.getString("TABLE_CATALOG");
        String tableSchema = rs.getString("TABLE_SCHEM");

        if (StringKit.isNotBlank(tableSchema)) {
            entity = new SchemaEntity();
            entity.setTABLE_CAT(tableCat);
            entity.setTABLE_SCHEM(tableSchema);

            if (!StringKit.isBlank(tableCat)) {
                tableCat = tableCat + ".";
            }
            entity.setDefKey(tableCat + tableSchema);
        }

        return entity;
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
     * @param tableEntity
     * @param rs
     * @throws SQLException
     */
    protected void fillTableEntityNoColumn(TableEntity tableEntity, Connection connection, ResultSet rs) throws SQLException {
        String tableCat = rs.getString("TABLE_CAT");
        String tableSchem = rs.getString("TABLE_SCHEM");
        String tableName = rs.getString("TABLE_NAME");
        String TABLE_TYPE = rs.getString(4);
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

        // 兼容catalog-》schema两层架构，考虑界面的显示将schema放置到表名前
        DatabaseMetaData metaData = connection.getMetaData();
        boolean supportsCatalogs = metaData.supportsCatalogsInTableDefinitions();
        boolean supportsSchemas = metaData.supportsSchemasInTableDefinitions();
        if (supportsCatalogs && supportsSchemas) {
            tableName = tableSchem + "." + tableName;
        }

        tableEntity.setTABLE_CAT(tableCat);
        tableEntity.setTABLE_SCHEM(tableSchem);
        tableEntity.setTABLE_NAME(tableName);
        tableEntity.setTABLE_TYPE(TABLE_TYPE);
        tableEntity.setREMARKS(remarks);
    }

    /**
     * 传入表名，中文名，注释信息，获取字段明细，索引信息
     * @param tableEntity
     * @param conn
     */
    public void fillTableEntity(TableEntity tableEntity, Connection conn) throws SQLException {
//        String tableName = tableEntity.getTABLE_NAME();
//
//        ResultSet rs = null;
//        ResultSet pkRs = null;
//
//        try {
//            Pair<ResultSet,ResultSet> pair = getColumnAndPrimaryKeyResultSetPair(conn,tableEntity);
//            rs = pair.getLeft();
//            pkRs = pair.getRight();
//
//            Set<String> pkSet = new HashSet<String>();
//            while(pkRs.next()) {
//                String columnName = pkRs.getString("COLUMN_NAME");
//                pkSet.add(columnName);
//            }
//
//            while(rs.next()) {
//                ColumnField field = new ColumnField();
//                fillColumnField(field, conn, rs, pkSet);
//                tableEntity.getFields().add(field);
//            }
//            fillTableIndexes(tableEntity,conn);
//        } catch (SQLException e) {
//            log.error("读取数据表"+tableName+"的字段明细出错");
////            throw new RuntimeException("读取数据表"+tableName+"的字段明细出错",e);
//        } finally {
//            JdbcKit.close(rs);
//            JdbcKit.close(pkRs);
//        }
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

//        field.setPrimaryKey(pkSet.contains(colName));
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

        try {
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
        } finally {
            JdbcKit.close(rs.getStatement());
            JdbcKit.close(rs);
        }

    }

    protected List<SchemaEntity> do_getAllSchemas(Connection conn, String catalog, String schemaPattern) throws SQLException {
        List<SchemaEntity> schemaEntities = new ArrayList<>();
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet rs = null;

        try {
            rs = meta.getSchemas(catalog, schemaPattern);
        } catch (java.lang.AbstractMethodError abstractMethodError) {
            // 有些数据库，如SQLserver不支持getSchemas(catalog, schemaPattern)接口,尝试getSchemas() 接口
            rs = meta.getSchemas();
        }

        if (null == rs) {
            return schemaEntities;
        }

        try {
            while (rs.next()) {
                SchemaEntity entity = createSchemaEntity(conn, rs);
                if(entity != null){
                    schemaEntities.add(entity);
                }
            }
        } finally {
            JdbcKit.close(rs);
        }

        return schemaEntities;
    }

    /**
     * 获取所有的数据库,区分catalog和schema.v1.01版本实现
     * @param conn
     * @return 数据库列表
     */
    public List<SchemaEntity> getAllSchemasWithCatalog(Connection conn, List<SchemaEntity> catalogEntities, String schemaPattern) throws SQLException {
        List<SchemaEntity> schemaEntities = new ArrayList<>();
        String catalog = getCatalogPattern(conn);
        schemaPattern = getSchemaPattern(conn, schemaPattern);

        // 如果没有指定获取schema，那么获取全部的schema
        if (catalogEntities.isEmpty()) {
            List<SchemaEntity> entities = do_getAllSchemas(conn, catalog, schemaPattern);
            schemaEntities.addAll(entities);
        } else {
            for(SchemaEntity entity: catalogEntities) {
                List<SchemaEntity> entities = do_getAllSchemas(conn, entity.getTABLE_CAT(), schemaPattern);
                schemaEntities.addAll(entities);
            }
        }

        return schemaEntities;

    }

    /**
     * 获取所有的数据库,不区分catalog和schema. v1.0版本
     * @param conn
     * @return 数据库列表
     */
    public List<SchemaEntity> getAllSchemas(Connection conn, String schemaPattern) throws SQLException {
        List<SchemaEntity> schemaEntities;
        String catalog = getCatalogPattern(conn);
        schemaPattern = getSchemaPattern(conn, schemaPattern);

        schemaEntities = do_getAllSchemas(conn, catalog, schemaPattern);

        return schemaEntities;

    }

    protected void do_getAllCatalogs(Connection conn, List<SchemaEntity> schemaEntities) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet rs = null;

        try {
            rs = meta.getCatalogs();
            while (rs.next()) {
                String TABLE_CAT = rs.getString("TABLE_CAT");
                SchemaEntity entity = new SchemaEntity();
                entity.setDefKey(TABLE_CAT);
                entity.setTABLE_CAT(TABLE_CAT);
                schemaEntities.add(entity);
            }

        } finally {
            JdbcKit.close(rs);
        }
    }

    public List<SchemaEntity> getAllCatalogs(Connection conn) throws SQLException {

        List<SchemaEntity> schemaEntities = new ArrayList<>();
        do_getAllCatalogs(conn, schemaEntities);

        return schemaEntities;
    }

    /**
     * 取所有的数据表清单
     * @param conn
     * @return
     */
    public List<TableEntity> getAllTables(Connection conn, String catalog, String schemaPattern, String[] types) throws SQLException {
        return getAllTables(conn, catalog, schemaPattern, null, types);
    }

    protected List<TableEntity> getAllTables(Connection conn, String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        catalog = getCatalogPattern(conn, catalog);
        schemaPattern = getSchemaPattern(conn, schemaPattern);
        tableNamePattern = getTableNamePattern(conn, tableNamePattern);
        return do_getAllTables(conn, catalog, schemaPattern, tableNamePattern, types);
    }

    private List<TableEntity> do_getAllTables(Connection conn, String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();

        ResultSet rs = meta.getTables(catalog, schemaPattern, tableNamePattern, types);
        // 提高单次获取数据条数，减少请求次数、网络传输，提高效率
        rs.setFetchSize(200);

        List<TableEntity> tableEntities = new ArrayList<TableEntity>();
        try {
            while (rs.next()) {
                String tableName = rs.getString(3);

                if (isValidTable(tableName)) {
                    TableEntity entity = createTableEntity(conn, rs);
                    if (entity != null) {
                        tableEntities.add(entity);
                    }
                }
            }
        } finally {
            JdbcKit.close(rs.getStatement());
            JdbcKit.close(rs);
        }

        return tableEntities;
    }

    /**
     * 是否是有效表：在获取表及表结构的时候，除去系统表
     * @param tableName 用于测试的表名
     * @return
     *  false：系统表；true：非系统表
     */
    protected boolean isValidTable(String tableName) {
        return true;
    }

    /**
     * 根据表名，创建数据表实体的字段及索引
     * @param conn
     * @param tableNamePattern
     * @return
     * @throws SQLException
     */
    public List<TableEntity> getTableEntities(Connection conn, String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        List<TableEntity> tableEntities = new ArrayList<>();
        String catalogPattern1 = getCatalogPattern(conn, catalog);
        String schemaPattern1 = getSchemaPattern(conn, schemaPattern);
        String tableNamePattern1 = getTableNamePattern(conn, tableNamePattern);

        DatabaseMetaData meta = conn.getMetaData();
        boolean supportsCatalogs = meta.supportsCatalogsInTableDefinitions();
        boolean supportsSchemas = meta.supportsSchemasInTableDefinitions();
        ResultSet rsCols = null;

        try {
            tableEntities = getAllTables(conn, catalog, schemaPattern, tableNamePattern, types);
            if (tableEntities.isEmpty()) {
                return tableEntities;
            }
            // 针对获取到多个表的情况，需要优化为一次性获取所有表的字段、主键、索引。然后在赋值到对应的 TableEntity，
            // 针对获取到多个表的情况，需要优化为一次性获取所有表的字段、主键、索引。然后在赋值到对应的 TableEntity。避免多次调用造成性能瓶颈。
            String table_cat_temp = "";
            String table_schema_temp = "";
            String table_name_temp = "";
            TableEntity tableEntity = null;

            rsCols = meta.getColumns(catalogPattern1, schemaPattern1, tableNamePattern1, "%");
            while(rsCols.next()) {
                String TABLE_CAT = rsCols.getString("TABLE_CAT");
                String TABLE_SCHEM = rsCols.getString("TABLE_SCHEM");
                String TABLE_NAME = rsCols.getString("TABLE_NAME");
                // 兼容catalog-》schema两层架构，考虑界面的显示将schema放置到表名前
                if (supportsCatalogs && supportsSchemas) {
                    TABLE_NAME = TABLE_SCHEM + "." + TABLE_NAME;
                }

                // 由于getTables和getColumns返回的 TABLE_CAT/TABLE_SCHEM 存在值不同的情况（""和null），归一化
                if(TABLE_CAT == null) TABLE_CAT = "";
                if(TABLE_SCHEM == null) TABLE_SCHEM = "";

                // 查找当前表--以便字段填充
                if (!Objects.equals(TABLE_CAT, table_cat_temp) || !Objects.equals(TABLE_SCHEM,table_schema_temp) || !Objects.equals(TABLE_NAME,table_name_temp)) {
                    boolean find = false;
                    for (TableEntity entity : tableEntities) {
                        String entityTABLECat = entity.getTABLE_CAT();
                        String entityTABLESchem = entity.getTABLE_SCHEM();
                        String entityTABLEName = entity.getTABLE_NAME();

                        if(entityTABLECat == null) entityTABLECat = "";
                        if(entityTABLESchem == null) entityTABLESchem = "";

                        if (Objects.equals(TABLE_CAT, entityTABLECat) && Objects.equals(TABLE_SCHEM, entityTABLESchem) && Objects.equals(TABLE_NAME, entityTABLEName)) {
                            tableEntity = entity;
                            find = true;
                            break;
                        }
                    }
                    if (!find) {
                        continue;
                    }
                    // 兼容catalog-》schema两层架构，考虑界面的显示将schema放置到表名前
                    if (supportsCatalogs && supportsSchemas) {
                        String newTableName = TABLE_SCHEM + "." + TABLE_NAME;
                        tableEntity.setTABLE_NAME(newTableName);
                    }
                }

                // 字段填充
                if (tableEntity != null) {
                    ColumnField field = new ColumnField();
                    fillColumnField(field, null, rsCols, null);
                    tableEntity.getFields().add(field);
                }

                table_cat_temp = TABLE_CAT;
                table_schema_temp = TABLE_SCHEM;
                table_name_temp = TABLE_NAME;
            }

            // step2: 各数据库方言的适配处理、处理类型名
            for (TableEntity entity : tableEntities) {
                fillTableEntity(entity, conn);
                entity.fillFieldsCalcValue();
            }

            return tableEntities;
        } catch (SQLException exception) {
            throw new SQLException("获取表DDL异常，error:" + exception.getMessage());
        }
        finally {
            if (rsCols != null) JdbcKit.close(rsCols.getStatement());
            JdbcKit.close(rsCols);
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
        try {
            List<FKColumnField> fkColumnFields = getFkColumnFields(rs);
            return fkColumnFields;
        } finally {
            JdbcKit.close(rs.getStatement());
            JdbcKit.close(rs);
        }

    }

    public List<FKColumnField> getFKReference(Connection conn, String schemaPattern, String table) throws SQLException {

        String schema = getSchemaPattern(conn, schemaPattern);
        String catalog = getCatalogPattern(conn, schemaPattern);

        DatabaseMetaData meta = conn.getMetaData();
        ResultSet rs = meta.getExportedKeys(catalog, schema, table);
        try {
            return getFkColumnFields(rs);
        } finally {
            JdbcKit.close(rs.getStatement());
            JdbcKit.close(rs);
        }

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
        try {
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
        } finally {
            JdbcKit.close(rs.getStatement());
            JdbcKit.close(rs);
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
        ResultSet typeInfo = meta.getTypeInfo();

        List<DataTypeEntity> infoEntities = new ArrayList<>();
        try {
            while (typeInfo.next()) {
                String TYPE_NAME = typeInfo.getString("TYPE_NAME");
                int DATA_TYPE = typeInfo.getInt("DATA_TYPE");
                DataTypeEntity typeInfoEntity = new DataTypeEntity(TYPE_NAME, DATA_TYPE);
                infoEntities.add(typeInfoEntity);
            }
        } finally {
            JdbcKit.close(typeInfo.getStatement());
            JdbcKit.close(typeInfo);
        }

        return infoEntities;
    }

    public Set<String> getAllFunctions(Connection conn) throws SQLException {
        Set<String> functions = new HashSet<>();
        String schemaPattern = getSchemaPattern(conn, null);
        String catalog = getCatalogPattern(conn, null);

        DatabaseMetaData meta = conn.getMetaData();
        ResultSet rs = meta.getFunctions(catalog, schemaPattern, "%");
        try {
            while (rs.next()) {
                String FUNCTION_NAME = rs.getString("FUNCTION_NAME");
                functions.add(FUNCTION_NAME);
            }
            Collections.addAll(functions, meta.getTimeDateFunctions().split(","));
            Collections.addAll(functions, meta.getStringFunctions().split(","));
            Collections.addAll(functions, meta.getNumericFunctions().split(","));
            Collections.addAll(functions, meta.getSystemFunctions().split(","));

            functions.removeIf(String::isEmpty);
        } finally {
            JdbcKit.close(rs.getStatement());
            JdbcKit.close(rs);
        }

        return functions;
    }
}
