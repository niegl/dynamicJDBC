package flowdesigner.jdbc;

import com.alibaba.druid.support.json.JSONUtils;
import com.google.gson.Gson;
import flowdesigner.jdbc.command.CommandKey;
import flowdesigner.jdbc.command.CommandManager;
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
        DynamicDriver dynamicDriver = new DynamicDriver("C:\\文档\\历史\\历史资料\\hive");
        dynamicDriver.setM_url("jdbc:hive2://10.248.190.13:10000");
        Properties properties = new Properties();
        properties.setProperty("DriverClassName","org.apache.hive.jdbc.HiveDriver");
        dynamicDriver.setM_propertyInfo(properties);
        Connection connection = dynamicDriver.getConnection();
        assertNotNull(connection);

        DatabaseMetaData metaData = connection.getMetaData();
        System.out.println("----------开始打印类型数据");
        ResultSet typeInfo = metaData.getTypeInfo();
        while (typeInfo.next()) {
            String type_name = typeInfo.getString("TYPE_NAME");
            System.out.println(type_name);
        }
        Gson gson = new Gson();
        var cc = CommandManager.exeCommand(dynamicDriver.getConnection(), CommandKey.CMD_DBReverseGetTypeInfo,new HashMap<>());
        String s = gson.toJson(cc);
        System.out.println(s);
    }
}