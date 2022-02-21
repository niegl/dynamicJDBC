package flowdesigner.jdbc.command;

import com.google.gson.Gson;
import flowdesigner.jdbc.driver.DynamicDriver;
import flowdesigner.jdbc.model.SchemaEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        properties.setProperty("url","jdbc:mysql://192.168.101.105:3306");
        properties.setProperty("username","root");
        properties.setProperty("password","123456");
        properties.setProperty("maxWait","3000");
        dynamicDriver.setM_propertyInfo(properties);

        try {
            dynamicDriver.createDataSource();
            connection = dynamicDriver.getConnection();
        } catch (SQLException e) {
            System.out.println(dynamicDriver.get_errMessage());
            e.printStackTrace();
        }
        assertNotNull(connection);
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
            dynamicDriver.createDataSource();
            connection = dynamicDriver.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assertNotNull(connection);

    }


    @Test
    void testExeCommand() throws SQLException {
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
}