package flowdesigner.jdbc.command.impl;

import com.alibaba.druid.DbType;
import com.alibaba.fastjson2.JSON;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.driver.DynamicDriver;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class DBExecuteImplTest {
    Connection connection;
    DynamicDriver dynamicDriver = null;

    Connection getConnection(DbType dbType) {



        switch (dbType) {
            case hive -> {
                dynamicDriver = new DynamicDriver("C:\\文档\\项目\\北京能耗\\能耗资料\\new\\new\\05.代码实现及单元测试\\lib");
                Properties properties = new Properties();
                properties.setProperty("driverClassName","org.apache.hive.jdbc.HiveDriver");
                properties.setProperty("url","jdbc:hive2://10.248.190.13:10000/default");
                dynamicDriver.set_propertyInfo(properties);
            }
            case mysql -> {
                dynamicDriver = new DynamicDriver("C:\\Users\\nieguangling\\AppData\\Roaming\\DBeaverData\\drivers\\maven\\maven-central\\mysql");
                Properties properties = new Properties();
                properties.setProperty("driverClassName","com.mysql.cj.jdbc.Driver");
                properties.setProperty("url","jdbc:mysql://localhost:3306");
                properties.setProperty("username","root");
                properties.setProperty("password","123456");
                dynamicDriver.set_propertyInfo(properties);
            }
        }

        try {
            connection = dynamicDriver.getConnection();
        } catch (SQLException e) {
            System.out.println(dynamicDriver.get_errMessage());
            e.printStackTrace();
        }
        assertNotNull(connection);

        return connection;
    }

    @Test
    void execSelect() throws SQLException {
        exec(DbType.hive,"SELECT dimension_id, dimension_category, dimension_name, dimension_des, table_id, field_id, field_name, dimension_value_name, dimension_value_no\n" +
                "FROM test.table1;");
    }

    @Test
    void execInsert() throws SQLException {
        exec(DbType.hive,"INSERT INTO test.table1\n" +
                "(dimension_id, dimension_category, dimension_name, dimension_des, table_id, field_id, field_name, dimension_value_name, dimension_value_no)\n" +
                "VALUES('1', '1', '1', '1', '1', '1', '1', '1', '1');");
    }

    @Test
    void execShowDatabases() throws SQLException {
        exec(DbType.hive,"show databases;");
    }

    void exec(DbType dbType,String scripts) throws SQLException {
        getConnection(dbType);
        DBExecuteImpl dbExecute = new DBExecuteImpl();
        DBExecuteImpl.RunningStatus<Object> exec = dbExecute.exec(0,connection, scripts);
        String status = exec.getStatus();
        if (status.equals(ExecResult.SUCCESS)) {
            String s = JSON.toJSONString(exec);
            System.out.println(s);
        }

        dbExecute.release();
        dynamicDriver.close(connection);
    }

}