/*
 * Copyright 2019-2029 FISOK(www.fisok.cn).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package flowdesigner.jdbc.util.raw.kit;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author : 杨松<yangsong158@qq.com>
 * @date : 2019/12/28 22:58
 * @desc :
 */
public abstract class JdbcKit {
    /** 使用java自带的log工具 */
    private static final Logger logger = Logger.getLogger("JdbcKit");

    public static void close(AutoCloseable closeable){
        try {
            if(closeable!=null){
                closeable.close();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE,"关闭出错",e);
        }
    }

//    public static Connection getConnection(String driverClassName, String url, String username, String password){
//        try {
//            Class.forName(driverClassName);
//        } catch (ClassNotFoundException e) {
//            logger.error("",e);
//            throw new RuntimeException("驱动加载失败，驱动类不存在(ClassNotFoundException)！出错消息："+e.getMessage(),e);
//        }
//
//        Connection conn = null;
//        Properties props = new Properties();
//        props.put("user", StringKit.nvl(username,""));
//        props.put("password", StringKit.nvl(password,""));
//
//        conn = getConnection(driverClassName,url,props);
//        return conn;
//    }

    public static Connection getConnection(String driverClassName,String url, Properties props){
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            logger.severe(e.getMessage());
            throw new RuntimeException("驱动加载失败，驱动类不存在(ClassNotFoundException)！出错消息："+e.getMessage(),e);
        }
        Connection conn = null;
        try {
            props.put("remarksReporting", "true");        //获取Oracle元数据 REMARKS信息
            props.put("useInformationSchema", "true");    //获取MySQL元数据 REMARKS信息
            conn = DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            throw new RuntimeException("连接失败!出错消息："+e.getMessage(),e);
        }
        return conn;
    }


    public static boolean isNumeric(int sqlType) {
        return Types.BIT == sqlType
                || Types.BIGINT == sqlType
                || Types.DECIMAL == sqlType
                || Types.DOUBLE == sqlType
                || Types.FLOAT == sqlType
                || Types.INTEGER == sqlType
                || Types.NUMERIC == sqlType
                || Types.REAL == sqlType
                || Types.SMALLINT == sqlType
                || Types.TINYINT == sqlType;
    }

    public static boolean isDate(int sqlType) {
        return Types.DATE == sqlType
                || Types.TIME == sqlType
                || Types.TIME_WITH_TIMEZONE == sqlType
                || Types.TIMESTAMP_WITH_TIMEZONE == sqlType
                || Types.TIMESTAMP == sqlType ;
    }
    public static boolean isShortString(int sqlType) {
        return Types.VARCHAR == sqlType
                || Types.CHAR == sqlType;
//                || Types.LONGNVARCHAR == sqlType
//                || Types.LONGVARCHAR == sqlType;
    }
}
