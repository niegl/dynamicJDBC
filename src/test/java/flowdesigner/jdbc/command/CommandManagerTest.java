package flowdesigner.jdbc.command;

import flowdesigner.jdbc.driver.DynamicDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class CommandManagerTest {

    @BeforeEach
    void setUp() {
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
}