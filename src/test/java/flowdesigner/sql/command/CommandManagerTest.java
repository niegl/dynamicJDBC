package flowdesigner.sql.command;

import com.alibaba.druid.DbType;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.fastjson2.JSON;
import flowdesigner.db.DbUtils;
import flowdesigner.jdbc.command.CommandKey;
import flowdesigner.jdbc.command.CommandManager;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.command.impl.DBExecuteImpl;
import flowdesigner.jdbc.command.impl.DBReverseGetFKReferenceImpl;
import flowdesigner.jdbc.command.impl.DBReverseGetFunctionsImpl;
import flowdesigner.jdbc.command.model.DataTypeEntity;
import flowdesigner.jdbc.command.model.TableEntity;
import flowdesigner.jdbc.driver.DynamicDriver;
import flowdesigner.jdbc.command.model.FKColumnField;
import flowdesigner.db.operators.SQLOperatorUtils;
import flowdesigner.util.raw.kit.StringKit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.sql.Types;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class CommandManagerTest {
    Connection connection = null;

    Connection getHive() {
        DynamicDriver dynamicDriver = new DynamicDriver("C:\\文档\\项目\\北京能耗\\能耗资料\\new\\new\\05.代码实现及单元测试\\lib");
//        DynamicDriver dynamicDriver = new DynamicDriver("C:\\Users\\nieguangling\\AppData\\Roaming\\DBeaverData\\drivers\\maven\\maven-central\\mysql");
        Properties properties = new Properties();
        properties.setProperty("driverClassName","org.apache.hive.jdbc.HiveDriver");
        properties.setProperty("url","jdbc:hive2://172.30.224.36:10000");
//        properties.setProperty("driverClassName","com.mysql.cj.jdbc.Driver");
//        properties.setProperty("url","jdbc:mysql://localhost:3306");
        properties.setProperty("username","admin");
        properties.setProperty("password","admin");
        properties.setProperty("druid.failFast","true");
//        properties.setProperty("connectTimeout","3000");
        dynamicDriver.set_propertyInfo(properties);

        try {
//            dynamicDriver.createDataSource();
            connection = dynamicDriver.getConnection();
        } catch (SQLException e) {
            System.out.println(dynamicDriver.get_errMessage());
            e.printStackTrace();
        }
        assertNotNull(connection);

        DataSource dataSource = dynamicDriver.getDataSource();
        if (dataSource instanceof DruidDataSource druidDataSource) {
            String properties1 = druidDataSource.getProperties();
            System.out.println(properties1);
        }

        return connection;
    }

    Connection getMySQL() {
//        DynamicDriver dynamicDriver = new DynamicDriver("C:\\文档\\项目\\北京能耗\\能耗资料\\new\\new\\05.代码实现及单元测试\\lib");
        DynamicDriver dynamicDriver = new DynamicDriver("C:\\Users\\nieguangling\\AppData\\Roaming\\DBeaverData\\drivers\\maven\\maven-central\\mysql");
        Properties properties = new Properties();
//        properties.setProperty("driverClassName","org.apache.hive.jdbc.HiveDriver");
//        properties.setProperty("url","jdbc:hive2://10.248.190.13:10000");
        properties.setProperty("driverClassName","com.mysql.cj.jdbc.Driver");
        properties.setProperty("url","jdbc:mysql://localhost:3306");
        properties.setProperty("username","root");
        properties.setProperty("password","123456");
        properties.setProperty("maxWait","3000");
        properties.setProperty("druid.failFast","true");
        dynamicDriver.set_propertyInfo(properties);

        try {
//            dynamicDriver.createDataSource();
            connection = dynamicDriver.getConnection();
        } catch (SQLException e) {
            System.out.println(dynamicDriver.get_errMessage());
            e.printStackTrace();
        }
        assertNotNull(connection);
        return connection;
    }

    Connection getImpala() {
//        DynamicDriver dynamicDriver = new DynamicDriver("C:\\文档\\项目\\北京能耗\\能耗资料\\new\\new\\05.代码实现及单元测试\\lib");
        DynamicDriver dynamicDriver = new DynamicDriver("C:\\setup\\Cloudera_ImpalaJDBC41_2.5.38\\Cloudera_ImpalaJDBC41_2.5.38");
        Properties properties = new Properties();
//        properties.setProperty("driverClassName","org.apache.hive.jdbc.HiveDriver");
//        properties.setProperty("url","jdbc:hive2://10.248.190.13:10000");
        properties.setProperty("driverClassName","com.cloudera.impala.jdbc41.Driver");
        properties.setProperty("url","jdbc:impala://10.247.53.17:21050");
        dynamicDriver.set_propertyInfo(properties);

        try {
//            dynamicDriver.createDataSource();
            connection = dynamicDriver.getConnection();
        } catch (SQLException e) {
            System.out.println(dynamicDriver.get_errMessage());
            e.printStackTrace();
        }
        assertNotNull(connection);
        return connection;
    }

    Connection getSQLServer() {
//        DynamicDriver dynamicDriver = new DynamicDriver("C:\\文档\\项目\\北京能耗\\能耗资料\\new\\new\\05.代码实现及单元测试\\lib");
//        DynamicDriver dynamicDriver = new DynamicDriver("C:\\Users\\nieguangling\\AppData\\Roaming\\DBeaverData\\drivers\\maven\\maven-central\\net.sourceforge.jtds");
        DynamicDriver dynamicDriver = new DynamicDriver("C:\\Users\\nieguangling\\Desktop\\1\\SQL Server (Old driver jTDS)\\1.3.1");

        Properties properties = new Properties();
//        properties.setProperty("driverClassName","org.apache.hive.jdbc.HiveDriver");
//        properties.setProperty("url","jdbc:hive2://10.248.190.13:10000");
        properties.setProperty("driverClassName","net.sourceforge.jtds.jdbc.Driver");
        properties.setProperty("url","jdbc:jtds:sqlserver://localhost:1433");
        properties.setProperty("maxWait","3000");
        properties.setProperty("druid.failFast","true");
        properties.setProperty("username","SA");
        properties.setProperty("password","P@ssw0rd01");
        dynamicDriver.set_propertyInfo(properties);

        try {
//            dynamicDriver.createDataSource();
            connection = dynamicDriver.getConnection();
        } catch (SQLException e) {

            e.printStackTrace();
        }
        System.out.println(dynamicDriver.get_errMessage());
        assertNotNull(connection);
        return connection;
    }

    @Test
    void testGetCatalog() throws SQLException {
        connection = getSQLServer();

        DatabaseMetaData meta = connection.getMetaData();
        ResultSet catalog = meta.getCatalogs();
        System.out.println("cat-----------------");
        System.out.println(meta.supportsCatalogsInTableDefinitions());
        while (catalog.next()) {
            System.out.println(catalog.getString("TABLE_CAT"));
        }
        System.out.println("schema-----------------");
        System.out.println(meta.supportsSchemasInTableDefinitions());
        ResultSet schemas = meta.getSchemas();
        while (schemas.next()) {
            System.out.println(schemas.getString("TABLE_CATALOG") + "," + schemas.getString("TABLE_SCHEM"));
        }
    }

    @Test
    void testGetAllTables() throws SQLException {
        connection = getSQLServer();
        DatabaseMetaData meta = connection.getMetaData();
        String catalog = connection.getCatalog();
//        String schema = connection.getSchema();

//        System.out.println(catalog + "," + schema);

        boolean supportsSchemasInTableDefinitions = connection.getMetaData().supportsSchemasInTableDefinitions();
        boolean supportsCatalogsInTableDefinitions = connection.getMetaData().supportsCatalogsInTableDefinitions();
        System.out.println(supportsCatalogsInTableDefinitions);
        System.out.println(supportsSchemasInTableDefinitions);
        ResultSet catalogs = meta.getCatalogs();
        while (catalogs.next()) {
            String TABLE_CAT = catalogs.getString("TABLE_CAT");
            System.out.println("," + TABLE_CAT + "," );
        }
        ResultSet schemas = meta.getSchemas();
        while (schemas.next()) {
            String TABLE_CATALOG = schemas.getString("TABLE_CATALOG");
            String TABLE_CAT = schemas.getString("TABLE_SCHEM");
            System.out.println(TABLE_CATALOG + "," + TABLE_CAT + "," );
        }
        ResultSet rs = meta.getTables(null, null, null, new String[]{"TABLE"});
        List<TableEntity> tableEntities = new ArrayList<TableEntity>();
        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME");
            String TABLE_CAT = rs.getString("TABLE_CAT");
            String TABLE_SCHEM = rs.getString("TABLE_SCHEM");

            System.out.println(tableName + "," + TABLE_CAT + "," + TABLE_SCHEM);

        }
    }

    @Test
    void testGetDDL() throws SQLException {
        connection = getHive();
        DatabaseMetaData meta = connection.getMetaData();
        String catalog = connection.getCatalog();
        ResultSet rsCols = meta.getColumns(null, "bmnc_pcode", null, "%");
        while(rsCols.next()) {
            String TABLE_CAT = rsCols.getString("TABLE_CAT");
            String TABLE_SCHEM = rsCols.getString("TABLE_SCHEM");
            String TABLE_NAME = rsCols.getString("TABLE_NAME");
            String colName = rsCols.getString("COLUMN_NAME");
            String remarks = StringKit.trim(rsCols.getString("REMARKS"));
            String typeName = rsCols.getString("TYPE_NAME");
            int dataType = rsCols.getInt("DATA_TYPE");
            int columnSize = rsCols.getInt("COLUMN_SIZE");
            int decimalDigits = rsCols.getInt("DECIMAL_DIGITS");
            String defaultValue = rsCols.getString("COLUMN_DEF");
            String isNullable = rsCols.getString("IS_NULLABLE");
            System.out.println(TABLE_CAT + "." + TABLE_SCHEM);
            System.out.println(TABLE_NAME + "." + colName + "," + remarks);

        }
    }

    @Test
    void testGetAllView() throws SQLException {
        connection = getSQLServer();
        DatabaseMetaData meta = connection.getMetaData();
        String catalog = connection.getCatalog();
//        String schema = connection.getSchema();

//        System.out.println(catalog + "," + schema);

        boolean supportsSchemasInTableDefinitions = meta.supportsSchemasInTableDefinitions();
        boolean supportsCatalogsInTableDefinitions = meta.supportsCatalogsInTableDefinitions();
        System.out.println(supportsCatalogsInTableDefinitions);
        System.out.println(supportsSchemasInTableDefinitions);

        System.out.println(meta.getDatabaseMajorVersion());
        System.out.println(meta.getDatabaseProductVersion());

        System.out.println(meta.getDatabaseMajorVersion());
        System.out.println(meta.getDatabaseMinorVersion());
        ResultSet rs = meta.getTables("master", null, null, new String[]{"TABLE","VIEW"});
        List<TableEntity> tableEntities = new ArrayList<TableEntity>();
        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME");
            String TABLE_CAT = rs.getString("TABLE_CAT");
            String TABLE_SCHEM = rs.getString("TABLE_SCHEM");
            String TABLE_TYPE = rs.getString("TABLE_TYPE");

            System.out.println(tableName + "," + TABLE_CAT + "," + TABLE_SCHEM+ "," + TABLE_TYPE);

        }
    }

    @AfterEach
    void tearDown() {
        JdbcUtils.close(connection);
    }

    @Test
    void testExecuteUpdate() throws SQLException {
        long start = Instant.now().toEpochMilli();
        DBExecuteImpl dbExecute = new DBExecuteImpl();
        var exec = dbExecute.exec(0,connection, "ALTER TABLE test.tb_emp6 DROP FOREIGN KEY fk_emp_dept1;",200);

        long end = Instant.now().toEpochMilli();
        String s = JSON.toJSONString(exec);
        System.out.println(s);
        System.out.println(end - start);
    }
    @Test
    void testExecuteSelect() throws SQLException, InterruptedException {
        connection = getHive();
        long start = Instant.now().toEpochMilli();
        DBExecuteImpl dbExecute = new DBExecuteImpl();
        var exec = dbExecute.exec(0,connection, "SELECT destination_participant_id FROM udms_sdata.cut_pi_exit_cms_filtered",200);

        String s = JSON.toJSONString(exec);
        System.out.println(s);

        var exec1 =dbExecute.queryNextStatus(200);
        String s1 = JSON.toJSONString(exec1);
        System.out.println(s1);

        dbExecute.release();
    }

    @Test
    void testJDBCProperties() throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet clientInfoProperties = metaData.getClientInfoProperties();
        while (clientInfoProperties.next()) {
            String NAME = clientInfoProperties.getString("NAME");
            String DEFAULT_VALUE = clientInfoProperties.getString("DEFAULT_VALUE");
            String DESCRIPTION = clientInfoProperties.getString("DESCRIPTION");

            System.out.println(NAME.concat(",") + DEFAULT_VALUE + "," + DESCRIPTION);
        }
//        ResultSet attributes = metaData.getAttributes();
//        while (clientInfoProperties.next()) {
//            String NAME = clientInfoProperties.getString("NAME");
//            String DEFAULT_VALUE = clientInfoProperties.getString("DEFAULT_VALUE");
//            String DESCRIPTION = clientInfoProperties.getString("DESCRIPTION");
//
//            System.out.println(NAME.concat(",") + DEFAULT_VALUE + "," + DESCRIPTION);
//        }
    }

    @Test
    void getAttributes() throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet clientInfoProperties = metaData.getAttributes(null,null,null,null);
        while (clientInfoProperties.next()) {
            String NAME = clientInfoProperties.getString("ATTR_NAME");
            String DEFAULT_VALUE = clientInfoProperties.getString("TYPE_NAME");
            String DESCRIPTION = clientInfoProperties.getString("ATTR_TYPE_NAME");

            System.out.println(NAME.concat(",") + DEFAULT_VALUE + "," + DESCRIPTION);
        }

        Properties clientInfo = connection.getClientInfo();
        for (var info : clientInfo.entrySet()) {
            System.out.println(info.getKey());
            System.out.println(info.getValue());
        }
//        ResultSet attributes = metaData.getAttributes();
//        while (clientInfoProperties.next()) {
//            String NAME = clientInfoProperties.getString("NAME");
//            String DEFAULT_VALUE = clientInfoProperties.getString("DEFAULT_VALUE");
//            String DESCRIPTION = clientInfoProperties.getString("DESCRIPTION");
//
//            System.out.println(NAME.concat(",") + DEFAULT_VALUE + "," + DESCRIPTION);
//        }
    }

    @Test
    void exeCommand() {
        DynamicDriver dynamicDriver = new DynamicDriver("C:\\文档\\历史\\历史资料\\hive");

        Properties properties = new Properties();
        properties.setProperty("driverClassName","org.apache.hive.jdbc.HiveDriver");
        properties.setProperty("url","jdbc:hive2://10.248.190.13:10000");
        dynamicDriver.set_propertyInfo(properties);
        Connection connection = null;
        try {
//            dynamicDriver.createDataSource();
            connection = dynamicDriver.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assertNotNull(connection);

    }


    @Test
    void testExeCommandGetSchemas() throws SQLException {
        connection = getSQLServer();
        ExecResult cc = CommandManager.exeCommand(connection, CommandKey.CMD_DBReverseGetSchemas,new HashMap<String,String>());
        String s = JSON.toJSONString(cc);
        System.out.println(s);

    }

    @Test
    void testExeCommandGetView() throws SQLException {
        connection = getHive();
        long start = Instant.now().toEpochMilli();
        ExecResult cc = CommandManager.exeCommand(connection, CommandKey.CMD_DBReverseGetAllTablesList,new HashMap<String,String>(){{
            put("types","TABLE,VIEW");
            put("schemaPattern","bmnc_pcodevw");
        }});
        String s = JSON.toJSONString(cc);
        System.out.println(s);
        long end = Instant.now().toEpochMilli();

        log.info("start time" + (end - start));
    }

    @Test
    void testExeCommandGetDDL() {

        connection = getHive();

        long start = Instant.now().toEpochMilli();
        ExecResult cc = CommandManager.exeCommand(connection, CommandKey.CMD_DBReverseGetTableDDL,new HashMap<String,String>(){{
            put("schemaPattern","bmnc_pcode");
            put("types","TABLE");
//            put("schemaPattern","test");
            put("tables","calendar");
        }});
        long end = Instant.now().toEpochMilli();
        String s = JSON.toJSONString(cc);
        System.out.println(s);
        System.out.println(end - start);


//        ResultSet tables = connection.getMetaData().getTables(null, null, "NewTable", new String[]{"TABLE"});
//        while (tables.next()) {
//            System.out.println(tables.getString("TABLE_NAME"));
//        }
    }

    @Test
    void testExeCommandParseDDL() {

        long start = Instant.now().toEpochMilli();
        ExecResult cc = CommandManager.exeCommand(null, CommandKey.CMD_ParseDDLToTableImpl,new HashMap<String,String>(){{
            put("ddl","CREATE TABLE IF NOT EXISTS employee ( eid int, name String,salary String, destination String) COMMENT 'Employee details'");
            put("dbType","hive");
        }});
        long end = Instant.now().toEpochMilli();
        String s = JSON.toJSONString(cc);
        System.out.println(s);
        System.out.println(end - start);
    }

    @Test
    void testExeCommandParseDDL1() throws SQLException {

        long start = Instant.now().toEpochMilli();
        ExecResult cc = CommandManager.exeCommand(null, CommandKey.CMD_ParseDDLToTableImpl,new HashMap<String,String>(){{
            put("ddl","CREATE TABLE `pmart.t98_line_person_times_traction_engcspt_period_st`(\n" +
                    "  `househld_pty_id` varchar(60) COMMENT '当事人', \n" +
                    "  `househld_pty_nm` varchar(100) COMMENT '当事人名称', \n" +
                    "  `line_id` varchar(30) COMMENT '线路编号', \n" +
                    "  `stat_dt` string COMMENT '统计日期', \n" +
                    "  `stat_indx_cd` varchar(8) COMMENT '统计索引', \n" +
                    "  `measr_unit` varchar(60) COMMENT '单位', \n" +
                    "  `stat_val` decimal(18,2) COMMENT '统计值');");
            put("dbType","hive");
        }});
        long end = Instant.now().toEpochMilli();
        String s = JSON.toJSONString(cc);
        System.out.println(s);
        System.out.println(end - start);
    }

    @Test
    void testExeCommandParseSelect() throws SQLException {

        long start = Instant.now().toEpochMilli();
        ExecResult cc = CommandManager.exeCommand(null, CommandKey.CMD_ParseDDLToTableImpl,new HashMap<String,String>(){{
            put("ddl","SELECT l.stat_line_nme, sum(a.pasgr_quatity) AS sum_pasgr_quatity\n" +
                    "FROM bmnc_pmart.t98_pasgr_qtty_date_st a\n" +
                    "\tINNER JOIN bmnc_pcode.t99_stat_line_cd_his l ON a.line_id = l.stat_line_id\n" +
                    "WHERE a.stat_period_cd = '10'\n" +
                    "GROUP BY sum(a.pasgr_quatity), l.stat_line_nme");
            put("dbType","hive");
        }});
        long end = Instant.now().toEpochMilli();
        String s = JSON.toJSONString(cc);
        System.out.println(s);
        System.out.println(end - start);
    }

    @Test
    void testExeCommandGetImportedKeys() throws SQLException {

        connection = getMySQL();
        ExecResult cc = CommandManager.exeCommand(connection, CommandKey.CMD_DBReverseGetFKColumnFieldImpl,new HashMap<String,String>(){{
            put("Table","Orders");
            put("schemaPattern","test");
        }});
        String s = JSON.toJSONString(cc);
        System.out.println(s);
    }

    @Test
    void testExeCommandgetPrimaryKey() throws SQLException {

        connection = getMySQL();
        ExecResult cc = CommandManager.exeCommand(connection, CommandKey.CMD_DBReverseGetPrimaryKeys,new HashMap<String,String>(){{
            put("Table","Persons");
            put("schemaPattern","test");
        }});
        String s = JSON.toJSONString(cc);
        System.out.println(s);
    }

    @Test
    void testGetImportedKeys() throws SQLException {

        DBReverseGetFKReferenceImpl getFKReference = new DBReverseGetFKReferenceImpl();
        ExecResult<List<FKColumnField>> execResult = getFKReference.exec(connection, new HashMap<String, String>() {{
            put("Table", "tb_emp3");
        }});

        System.out.println(execResult.getBody());

    }

    @Test
    void testGetFunctions() throws SQLException {

        DBReverseGetFunctionsImpl getFKReference = new DBReverseGetFunctionsImpl();
        ExecResult<Set<String>> execResult = getFKReference.exec(connection, new HashMap<>());

        System.out.println(execResult.getBody());

    }

    @Test
    void testExeCommandCrossReference() throws SQLException {

//
//        ExecResult cc = CommandManager.exeCommand(connection, CommandKey.CMD_DBReverseGetFKColumnFieldImpl,new HashMap<String,String>(){{
////            put("schemaPattern","test");
//            String foreignCatalog = params.get("foreignCatalog").toUpperCase();
//            String foreignSchema = params.get("foreignSchema").toUpperCase();
//            String foreignTable = params.get("foreignTable").toUpperCase();
//            put("foreignCatalog","NewTable");
//            put("foreignSchema","NewTable");
//            put("foreignTable","NewTable");
//        }});
//        String s = JSON.toJSONString(cc);
        ResultSet rs = connection.getMetaData().getCrossReference(null, null, "tb_emp3",
                null,null,"NewTable");
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
            System.out.println(fkColumnField);
        }
    }

    @Test
    void getSupportFunctionType() throws SQLException {
        SQLOperatorUtils.getFunctionType(DbType.sqlserver,"^=");
    }

    @Test
    void getSupportFunctions2() throws SQLException {
        connection = getMySQL();
        DatabaseMetaData meta = connection.getMetaData();
         String functions1 = meta.getTimeDateFunctions();
        String functions2 = meta.getSystemFunctions();
        String functions3 = meta.getStringFunctions();
        String functions4 = meta.getNumericFunctions();
        String functions5 = meta.getSystemFunctions();


//        ResultSet functions5 = meta.getFunctions(null,null,"%");
//        while (functions5.next()) {
//            String FUNCTION_NAME = functions5.getString("FUNCTION_NAME");
//            System.out.println(FUNCTION_NAME);
//        }
        System.out.println(functions1);
        System.out.println(functions2);
        System.out.println(functions3);
        System.out.println(functions4);
        System.out.println(functions5);
    }

    @Test
    void testPrimaryKeys() throws SQLException {
        ExecResult cc = CommandManager.exeCommand(connection, CommandKey.CMD_DBReverseGetPrimaryKeys,new HashMap<String,String>(){{
            put("schemaPattern","bmnc_pcode");
            put("table","t99_stat_index_cd");
        }});
        String s = JSON.toJSONString(cc);
        System.out.println(s);

    }

    @Test
    void testTypeInfo() throws SQLException {
        connection = getSQLServer();
        ExecResult<List<DataTypeEntity>> cc = CommandManager.exeCommand(connection, CommandKey.CMD_DBReverseGetTypeInfo,new HashMap<String,String>(){{
            put("dbType","SQLServer");
        }});
//        String s = JSON.toJSONString(cc);
//        System.out.println(s);
        List<DataTypeEntity> body = cc.getBody();
        for (DataTypeEntity entity: body) {
            System.out.println("put(\"" + entity.getTYPE_NAME().toUpperCase() + "\"," + entity.getDATA_TYPE() + ");");
        }

    }

}