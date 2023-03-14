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
    void exec() {
        DBExecuteImpl dbExecute = new DBExecuteImpl();
        DBExecuteImpl.RunningStatus<Object> exec = dbExecute.exec(connection, "SELECT direction_cd, direction_desc\n" +
                "FROM std_pcode.t99_direction_cd;");
        String status = exec.getStatus();
        if (status.equals(ExecResult.SUCCESS)) {
            List<Map<String, Object>> result = (List<Map<String, Object>>) exec.getResult();
            System.out.println(result);
        }

        dbExecute.release();
    }

    @Test
    void execInsert() {
        getConnection(DbType.mysql);

        DBExecuteImpl dbExecute = new DBExecuteImpl();
        DBExecuteImpl.RunningStatus<Object> exec = dbExecute.exec(connection, "insert into building_t(address, campus_id, department_id, floors, idbuilding_t, name, test1 ) values('???', 1, 1, 1, 1, '??иж??1', ");
        String status = exec.getStatus();
        if (status.equals(ExecResult.SUCCESS)) {
            String s = JSON.toJSONString(exec);
            System.out.println(s);
        }

        dbExecute.release();
    }

}