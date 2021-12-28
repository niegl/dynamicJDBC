package flowdesigner.jdbc.command;

import flowdesigner.jdbc.driver.DynamicDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
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
        dynamicDriver.setM_url("jdbc:hive2://10.248.190.13:10000");
        Properties properties = new Properties();
        properties.setProperty("DriverClassName","org.apache.hive.jdbc.HiveDriver");
        dynamicDriver.setM_propertyInfo(properties);
        Connection connection = dynamicDriver.getConnection();
        assertNotNull(connection);

    }
}