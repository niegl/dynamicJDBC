package flowdesigner.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class DynamicDriverTest {

    @org.junit.jupiter.api.Test
    void getConnection() throws SQLException {
        DynamicDriver dynamicDriver = new DynamicDriver(List.of(new String[]{"C:\\文档\\历史\\历史资料\\hive"}));
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
    }
}