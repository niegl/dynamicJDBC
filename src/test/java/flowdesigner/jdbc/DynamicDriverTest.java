package flowdesigner.jdbc;

import com.alibaba.druid.support.json.JSONUtils;
import com.google.gson.Gson;
import flowdesigner.jdbc.command.CommandKey;
import flowdesigner.jdbc.command.CommandManager;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.driver.DynamicDriver;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class DynamicDriverTest {

    @org.junit.jupiter.api.Test
    void getConnection() throws SQLException {
//        DynamicDriver dynamicDriver = new DynamicDriver("C:\\文档\\历史\\历史资料\\hive");
        DynamicDriver dynamicDriver = new DynamicDriver("C:\\Users\\nieguangling\\AppData\\Roaming\\DBeaverData\\drivers\\maven\\maven-central\\mysql");
        Properties properties = new Properties();
//        properties.setProperty("driverClassName","org.apache.hive.jdbc.HiveDriver");
//        properties.setProperty("url","jdbc:hive2://10.248.190.13:10000");
        properties.setProperty("driverClassName","com.mysql.cj.jdbc.Driver");
        properties.setProperty("url","jdbc:mysql://192.168.2.43:3306");
        properties.setProperty("username","root");
        properties.setProperty("password","123456");
        properties.setProperty("maxWait","3000");
        dynamicDriver.setM_propertyInfo(properties);
        Connection connection = null;
        try {
//            dynamicDriver.createDataSource();
            connection = dynamicDriver.getConnection();
        } catch (SQLException e) {
            System.out.println(dynamicDriver.get_errMessage());
            e.printStackTrace();
        }
        assertNotNull(connection);

        DatabaseMetaData metaData = connection.getMetaData();
        System.out.println("----------开始打印类型数据");
        ResultSet typeInfo = metaData.getTypeInfo();
        while (typeInfo.next()) {
            String type_name = typeInfo.getString("TYPE_NAME");
            String DATA_TYPE = typeInfo.getString("DATA_TYPE");
            System.out.println("TypeInfo.of(\"" + type_name + "\"," + DATA_TYPE + "),");
        }
        Gson gson = new Gson();
        ExecResult cc = CommandManager.exeCommand(null, CommandKey.CMD_DBReverseGetTypeInfo,new HashMap<String,String>(){{
            put("dbType","mysql");
        }});
        String s = gson.toJson(cc);
        System.out.println(s);
    }
}