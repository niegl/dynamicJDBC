package flowdesigner.jdbc.command;

import com.alibaba.druid.util.JdbcUtils;
import com.google.gson.Gson;
import flowdesigner.jdbc.command.model.TableEntity;
import flowdesigner.jdbc.driver.DynamicDriver;
import flowdesigner.jdbc.command.model.FKColumnField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class CommandManagerTest {
    Connection connection = null;
    @BeforeEach
    void setUp() {
//        DynamicDriver dynamicDriver = new DynamicDriver("C:\\文档\\历史\\历史资料\\hive");
        DynamicDriver dynamicDriver = new DynamicDriver("C:\\Users\\nieguangling\\AppData\\Roaming\\DBeaverData\\drivers\\maven\\maven-central\\mysql");
        Properties properties = new Properties();
//        properties.setProperty("driverClassName","org.apache.hive.jdbc.HiveDriver");
//        properties.setProperty("url","jdbc:hive2://10.248.190.13:10000");
        properties.setProperty("driverClassName","com.mysql.cj.jdbc.Driver");
        properties.setProperty("url","jdbc:mysql://localhost:3306");
        properties.setProperty("username","root");
        properties.setProperty("password","123456");
        properties.setProperty("maxWait","3000");
        dynamicDriver.setM_propertyInfo(properties);

        try {
//            dynamicDriver.createDataSource();
            connection = dynamicDriver.getConnection();
        } catch (SQLException e) {
            System.out.println(dynamicDriver.get_errMessage());
            e.printStackTrace();
        }
        assertNotNull(connection);
    }

    @Test
    void testGetAllTables() throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        String catalog = connection.getCatalog();
        String schema = connection.getSchema();

        System.out.println(catalog + "," + schema);

        boolean supportsSchemasInTableDefinitions = connection.getMetaData().supportsSchemasInTableDefinitions();
        boolean supportsCatalogsInTableDefinitions = connection.getMetaData().supportsCatalogsInTableDefinitions();
        System.out.println(supportsCatalogsInTableDefinitions);
        System.out.println(supportsSchemasInTableDefinitions);
        ResultSet rs = meta.getTables(null, null, null, new String[]{"TABLE"});
        List<TableEntity> tableEntities = new ArrayList<TableEntity>();
        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME");
            String TABLE_CAT = rs.getString("TABLE_CAT");
            String TABLE_SCHEM = rs.getString("TABLE_SCHEM");

            System.out.println(tableName + "," + TABLE_CAT + "," + TABLE_SCHEM);

        }
    }

    @AfterEach
    void tearDown() {
        JdbcUtils.close(connection);
    }

    @Test
    void testExecuteUpdate() throws SQLException {
        Gson gson = new Gson();
        ExecResult cc = CommandManager.exeCommand(connection, CommandKey.CMD_DBExecuteUpdateCommandImpl,new HashMap<String,String>(){{
//            put("schemaPattern","test");
            put("SQL","ALTER TABLE test_db11.test_db111 ADD PRIMARY KEY(id);");
        }});
        String s = gson.toJson(cc);
        System.out.println(s);
    }

    @Test
    void testJDBCProperties() throws SQLException {
        Gson gson = new Gson();
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
    void exeCommand() {
        DynamicDriver dynamicDriver = new DynamicDriver("C:\\文档\\历史\\历史资料\\hive");

        Properties properties = new Properties();
        properties.setProperty("driverClassName","org.apache.hive.jdbc.HiveDriver");
        properties.setProperty("url","jdbc:hive2://10.248.190.13:10000");
        dynamicDriver.setM_propertyInfo(properties);
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
        Gson gson = new Gson();
        ExecResult cc = CommandManager.exeCommand(connection, CommandKey.CMD_DBReverseGetSchemas,new HashMap<String,String>());
        String s = gson.toJson(cc);
        System.out.println(s);

        DatabaseMetaData meta = connection.getMetaData();
        ResultSet catalog = meta.getCatalogs();
        while (catalog.next()) {
            System.out.println(catalog.getString("TABLE_CAT"));
        }

        System.out.println(meta.supportsSchemasInTableDefinitions());//-------
        System.out.println(meta.supportsSchemasInIndexDefinitions());
        System.out.println(meta.supportsSchemasInDataManipulation());
        System.out.println(meta.supportsSchemasInPrivilegeDefinitions());
        System.out.println(meta.supportsSchemasInProcedureCalls());

        ResultSet schemas = meta.getSchemas(null,null);
        while (schemas.next()) {
            String tableSchem = schemas.getString("TABLE_SCHEM");
            /**
             *  SQL Server系统保留表
             *  trace_xe_action_map,trace_xe_event_map
             */
            if (!tableSchem.equalsIgnoreCase("PDMAN_DB_VERSION")
                    && !tableSchem.equalsIgnoreCase("trace_xe_action_map")
                    && !tableSchem.equalsIgnoreCase("trace_xe_event_map")){
                System.out.println(tableSchem);
            }else{
                continue;
            }
        }

    }

    @Test
    void testExeCommandGetDDL() throws SQLException {
        Gson gson = new Gson();
        ExecResult cc = CommandManager.exeCommand(connection, CommandKey.CMD_DBReverseGetTableDDL,new HashMap<String,String>(){{
//            put("schemaPattern","test");
            put("tables","NewTable");
        }});
        String s = gson.toJson(cc);
        System.out.println(s);

        ResultSet tables = connection.getMetaData().getTables(null, null, "NewTable", new String[]{"TABLE"});
        while (tables.next()) {
            System.out.println(tables.getString("TABLE_NAME"));
        }
    }

    @Test
    void testExeCommandGetImportedKeys() throws SQLException {

        Gson gson = new Gson();
        ExecResult cc = CommandManager.exeCommand(connection, CommandKey.CMD_DBReverseGetFKColumnFieldImpl,new HashMap<String,String>(){{
            put("Table","NewTable");
        }});
        String s = gson.toJson(cc);
        System.out.println(s);
    }

    @Test
    void testExeCommandCrossReference() throws SQLException {

//        Gson gson = new Gson();
//        ExecResult cc = CommandManager.exeCommand(connection, CommandKey.CMD_DBReverseGetFKColumnFieldImpl,new HashMap<String,String>(){{
////            put("schemaPattern","test");
//            String foreignCatalog = params.get("foreignCatalog").toUpperCase();
//            String foreignSchema = params.get("foreignSchema").toUpperCase();
//            String foreignTable = params.get("foreignTable").toUpperCase();
//            put("foreignCatalog","NewTable");
//            put("foreignSchema","NewTable");
//            put("foreignTable","NewTable");
//        }});
//        String s = gson.toJson(cc);
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
}