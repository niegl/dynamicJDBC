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
                properties.setProperty("url","jdbc:hive2://10.247.53.17:10000/default");
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
        exec(DbType.hive,"SELECT pkey, point_name, point_value, datetime, recovertime\n" +
                "\t, description, alarm_level, alarm_type, professional, alarm_status\n" +
                "\t, affirm_status, affirm_user, affirm_datetime, alarm_comment, rec_seq\n" +
                "\t, data_dt\n" +
                "FROM bmnc_sdata.ams_alarm");
    }

    @Test
    void execInsert() throws SQLException {
        exec(DbType.hive,"INSERT INTO test.table1\n" +
                "(dimension_id, dimension_category, dimension_name, dimension_des, table_id, field_id, field_name, dimension_value_name, dimension_value_no)\n" +
                "VALUES('1', '1', '1', '1', '1', '1', '1', '1', '1');");
    }

    @Test
    void execCreate() throws SQLException {
        exec(DbType.hive,"CREATE TABLE `std_pdata.t70_exit`(\n" +
                "  `od_id` string COMMENT 'OD编号', \n" +
                "  `exit_dt` string COMMENT '出站日期', \n" +
                "  `exit_tm` string COMMENT '出站时间', \n" +
                "  `prod_id` string COMMENT '产品编号', \n" +
                "  `gate_id` string COMMENT '闸机编号', \n" +
                "  `exit_station_id` string COMMENT '出站车站编号', \n" +
                "  `exit_line_id` string COMMENT '出站线路编号'\n" +
                "--  `cch_irgul_list` string COMMENT 'CCH异常列表' \n" +
                "--  `data_dttm` timestamp COMMENT '数据时间戳', \n" +
                "--  `data_file_seq` string COMMENT '数据文件序号'\n" +
                ")\n" +
                "PARTITIONED BY ( \n" +
                "  `data_dt` string)\n" +
                "ROW FORMAT SERDE \n" +
                "  'org.apache.hadoop.hive.ql.io.orc.OrcSerde' \n" +
                "STORED AS INPUTFORMAT \n" +
                "  'org.apache.hadoop.hive.ql.io.orc.OrcInputFormat' \n" +
                "OUTPUTFORMAT \n" +
                "  'org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat'");
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

        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        dbExecute.release();
        dynamicDriver.close(connection);
    }

}