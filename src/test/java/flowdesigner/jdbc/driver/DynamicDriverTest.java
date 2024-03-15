package flowdesigner.jdbc.driver;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class DynamicDriverTest {

    /**
     * wrong path
     */
    @Test
    void createDataSource() {
        DynamicDriver dynamicDriver = new DynamicDriver("C:\\Users\\nieguangling\\Desktop\\1/Apache hive/https://github.com/timveil/hive-jdbc-uber-jar/releases/download/v1.9-2.6.5/hive-jdbc-uber-2.6.5.0-292.jar/");
        Properties properties = new Properties();
        properties.setProperty("driverClassName","org.apache.hive.jdbc.HiveDriver");
        properties.setProperty("url","jdbc:hive2://10.248.190.13:10000");
        properties.setProperty("username","root");
        properties.setProperty("password","123456");
        properties.setProperty("maxWait","3000");
        dynamicDriver.set_propertyInfo(properties);
        Connection connection = null;
        try {
            connection = dynamicDriver.getConnection();
        } catch (SQLException e) {

            e.printStackTrace();
        }
        System.out.println(dynamicDriver.get_errMessage());
    }
}