package flowdesigner.jdbc.command.impl;

import com.alibaba.fastjson2.JSON;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.driver.DynamicDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class DBExecuteImplTest {
    Connection connection;

    @BeforeEach
    void setUp() {
        DynamicDriver dynamicDriver = new DynamicDriver("C:\\文档\\项目\\北京能耗\\能耗资料\\new\\new\\05.代码实现及单元测试\\lib");
        Properties properties = new Properties();
        properties.setProperty("driverClassName","org.apache.hive.jdbc.HiveDriver");
        properties.setProperty("url","jdbc:hive2://10.248.190.13:10000/default");
//        properties.setProperty("driverClassName","com.mysql.cj.jdbc.Driver");
//        properties.setProperty("url","jdbc:mysql://localhost:3306");
        properties.setProperty("username","root");
        properties.setProperty("password","123456");
        properties.setProperty("maxWait","3000");
        properties.setProperty("connectTimeout","3000");
        dynamicDriver.set_propertyInfo(properties);

        try {
//            dynamicDriver.createDataSource();
            connection = dynamicDriver.getConnection();
        } catch (SQLException e) {
            System.out.println(dynamicDriver.get_errMessage());
            e.printStackTrace();
        }
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
        DBExecuteImpl dbExecute = new DBExecuteImpl();
        DBExecuteImpl.RunningStatus<Object> exec = dbExecute.exec(connection, "INSERT INTO test.field_info1\n" +
                "(field_on, field_name, table_on, field_desc, field_type, edition_no)\n" +
                "VALUES('dwh_pmart-t98_pasgr_qtty_query_st-pasgr_quatity', 'pasgr_quatity', 'dwh_pmart-t98_pasgr_qtty_query_st', 'dwh_pmart-t98_pasgr_qtty_query_st', '客运量', 'varchar');");
        String status = exec.getStatus();
        if (status.equals(ExecResult.SUCCESS)) {
            String s = JSON.toJSONString(exec);
            System.out.println(s);
        }

        dbExecute.release();
    }

}