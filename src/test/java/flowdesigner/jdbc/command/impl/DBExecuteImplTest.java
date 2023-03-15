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

    Connection getConnection(DbType dbType) {

        DynamicDriver dynamicDriver = null;

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
    void execSelect() {
        exec(DbType.hive,"SELECT direction_cd, direction_desc\n" +
                "FROM std_pcode.t99_direction_cd;");
    }

    @Test
    void execInsert() {
        exec(DbType.hive,"INSERT INTO test.table1\n" +
                "(dimension_id, dimension_category, dimension_name, dimension_des, table_id, field_id, field_name, dimension_value_name, dimension_value_no)\n" +
                "VALUES('1', '1', '1', '1', '1', '1', '1', '1', '1');");
    }

    @Test
    void execShowDatabases() {
        exec(DbType.hive,"show databases;");
    }

    void exec(DbType dbType,String scripts) {
        getConnection(dbType);
        DBExecuteImpl dbExecute = new DBExecuteImpl();
        DBExecuteImpl.RunningStatus<Object> exec = dbExecute.exec(connection, scripts);
        String status = exec.getStatus();
        if (status.equals(ExecResult.SUCCESS)) {
            String s = JSON.toJSONString(exec);
            System.out.println(s);
        }

        dbExecute.release();
    }

}