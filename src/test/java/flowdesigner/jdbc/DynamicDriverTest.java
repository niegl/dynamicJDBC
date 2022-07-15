package flowdesigner.jdbc;

import com.alibaba.druid.util.DruidDataSourceUtils;
import com.alibaba.druid.util.JdbcUtils;
import com.google.gson.Gson;
import flowdesigner.jdbc.command.CommandKey;
import flowdesigner.jdbc.command.CommandManager;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.driver.DynamicDriver;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DynamicDriverTest {

    @Test
    Connection getConnection() throws SQLException {
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
        Connection connection = null;
        try {
//            dynamicDriver.createDataSource();
            connection = dynamicDriver.getConnection();
        } catch (SQLException e) {
            System.out.println(dynamicDriver.get_errMessage());
            e.printStackTrace();
        }
        assertNotNull(connection);

        JdbcUtils.executeQuery(connection,"select 'x'", new ArrayList<>());
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

        return connection;
    }

    @Test
    void testDriverManager() throws SQLException {
        Connection dynamicDriver = getConnection();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            boolean b = driver.acceptsURL("jdbc:mysql://192.168.2.43:3306");
            System.out.println(b + ","+driver.getMajorVersion() + "," + driver.getMinorVersion());
        }

        System.out.println(dynamicDriver);
    }

    DynamicDriver getDriver() {
        DynamicDriver dynamicDriver = new DynamicDriver("C:\\文档\\项目\\北京能耗\\能耗资料\\new\\new\\05.代码实现及单元测试\\lib");
//        DynamicDriver dynamicDriver = new DynamicDriver("C:\\Users\\nieguangling\\AppData\\Roaming\\DBeaverData\\drivers\\maven\\maven-central\\mysql");
        Properties properties = new Properties();
        properties.setProperty("driverClassName","org.apache.hive.jdbc.HiveDriver");
        properties.setProperty("url","jdbc:hive2://10.248.190.13:10000/default");
//        properties.setProperty("driverClassName","com.mysql.cj.jdbc.Driver");
//        properties.setProperty("url","jdbc:mysql://localhost:3306");
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

        assert connection != null;
        return dynamicDriver;
    }

    DynamicDriver getMariaDBDriver() {
        DynamicDriver dynamicDriver = new DynamicDriver("C:\\Users\\nieguangling\\AppData\\Roaming\\DBeaverData\\drivers\\maven\\maven-central\\org.mariadb.jdbc");
        Properties properties = new Properties();
        properties.setProperty("driverClassName","org.mariadb.jdbc.Driver");
        properties.setProperty("url","jdbc:mariadb://192.168.2.43:3306");
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

        assert connection != null;
        return dynamicDriver;
    }

    @Test
    void testDruidDataSource() {
        DynamicDriver driver = getDriver();
        String url = DruidDataSourceUtils.getUrl(driver.getDataSource());
        System.out.println(url);
        long id = DruidDataSourceUtils.getID(driver.getDataSource());
        System.out.println(id);
        String name = DruidDataSourceUtils.getName(driver.getDataSource());
        System.out.println(name);

    }

    @Test
    void printMariaDBFunctions() throws SQLException {
        DynamicDriver mariaDBDriver = getMariaDBDriver();
        DatabaseMetaData meta = mariaDBDriver.getConnection().getMetaData();
        ResultSet functions = meta.getFunctions(null, null, null);
                while (functions.next()) {
            String FUNCTION_NAME = functions.getString("FUNCTION_NAME");
            System.out.println(FUNCTION_NAME);
        }
    }

}