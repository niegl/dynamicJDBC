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
package flowdesigner.jdbc.util.sql.kit;


import com.alibaba.druid.DbType;
import com.alibaba.druid.util.JdbcUtils;
import flowdesigner.jdbc.util.raw.kit.JdbcKit;
import flowdesigner.jdbc.util.sql.core.DBType;
//import com.kingbase8.jdbc.KbDatabaseMetaData;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * @author : 杨松<yangsong158@qq.com>
 * @date : 2020/5/11
 * @desc :
 */
public abstract class DBTypeKit {

    /**
     * 根据连接信息推断数据库类型
     * @param connection
     * @return
     */
    public static DBType getDBType(Connection connection) throws SQLException {
        String dbType = getDBTypeStr(connection);
        if (dbType == null) {
            return null;
        }
        return DBType.fromTypeName(dbType.toUpperCase());
    }

    public static DBType getDBType(DatabaseMetaData metaData) throws SQLException {
        String dbType = getDBTypeStr(metaData);
        if (dbType == null) {
            return null;
        }
        return DBType.fromTypeName(dbType.toUpperCase());
    }

    /**
     * 根据数据源推断数据库类型(类型描述字串）
     * @param dataSource
     * @return
     */
    public static String getDBTypeStr(DataSource dataSource){
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            return getDBTypeStr(connection);
        } catch (SQLException e) {
            JdbcKit.close(connection);
            throw new RuntimeException("从datasource中获取数据库类型DBType出错",e);
        } finally {
            JdbcKit.close(connection);
        }
    }

    /**
     * 根据连接信息推断数据库类型(类型描述字串）
     * @param connection
     * @return
     */
    public static String getDBTypeStr(Connection connection) {
        DatabaseMetaData dbMeta = null;
        try {
            dbMeta = connection.getMetaData();
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
        return getDBTypeStr(dbMeta);
    }

    /**
     * 根据连接信息推断数据库类型(类型描述字串）
     * @param metaData
     * @return
     */
    public static String getDBTypeStr(DatabaseMetaData metaData) {
        String dbType = null;

        try {
            String url = metaData.getURL();
            dbType = JdbcUtils.getDbType(url.toLowerCase(), null);
        } catch (SQLException ignored) {
        }

        if (null == dbType) {
            try {
                String driver = metaData.getDriverName(); // Hive JDBC
                dbType = getDBTypeStr(driver);
            } catch (SQLException sqlException) {
                throw new RuntimeException("从getURL中获取数据库类型出错", sqlException);
            }
        }

//            //增加人大金仓支持
//            if(StringKit.isBlank(dbType) && metaData instanceof KbDatabaseMetaData){
//                dbType = "kingbase";
//            }else if(StringKit.isBlank(dbType) && metaData instanceof OdpsDatabaseMetaData){
//                dbType = "odps";
//            }
        return dbType;
    }

    public static String getDBTypeStr(String driverName){
        if (driverName.equals("Hive JDBC")) {
            return DbType.hive.name();//.getName();
        } else {
            return null;
        }
    }
}
