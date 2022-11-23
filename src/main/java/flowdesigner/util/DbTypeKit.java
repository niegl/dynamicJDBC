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
package flowdesigner.util;


import com.alibaba.druid.DbType;
import com.alibaba.druid.util.JdbcUtils;
import flowdesigner.jdbc.driver.DynamicDriver;
import flowdesigner.util.raw.kit.StringKit;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static com.alibaba.druid.util.JdbcConstants.*;

/**
 * 两种方式获取数据库类型：<p>
 * 1、使用阿里接口，通过URL获取；<p>
 * 2、当获取不到URL时，使用自定义函数通过getDriverClassName获取.
 */
@Slf4j
public abstract class DbTypeKit {

    /**
     * 根据连接信息推断数据库类型(类型描述字串）
     * @return
     */
    public static DbType getDbType(Connection connection) {
        DbType dbType = null;
        String url = null;

        if (null == connection ) {
            return null;
        }

        try {
            DatabaseMetaData metaData = connection.getMetaData();
            url = metaData.getURL();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        if (url == null) {
            url = DynamicDriver.getUrl(connection);
        }

        if (url != null) {
            dbType = JdbcUtils.getDbTypeRaw(url.toLowerCase(), null);
        }

//            //增加人大金仓支持
//            if(StringKit.isBlank(dbType) && metaData instanceof KbDatabaseMetaData){
//                dbType = "kingbase";
//            }else if(StringKit.isBlank(dbType) && metaData instanceof OdpsDatabaseMetaData){
//                dbType = "odps";
//            }
        return dbType;
    }

    /**
     * 通过driverClassName获取数据库类型
     * @param driverName 驱动类
     * @return
     */
    public static DbType getDbTypeStr(String driverName){
        return switch (driverName) {
            case DB2_DRIVER, DB2_DRIVER2, DB2_DRIVER3 -> DB2;
            case POSTGRESQL_DRIVER -> POSTGRESQL;
            //  DbType SYBASE = DbType.sybase;
            case SQL_SERVER_DRIVER, SQL_SERVER_DRIVER_SQLJDBC4, SQL_SERVER_DRIVER_JTDS -> SQL_SERVER;
            case ORACLE_DRIVER, ORACLE_DRIVER2 -> ORACLE;
            case ALI_ORACLE_DRIVER -> ALI_ORACLE;
            case MYSQL_DRIVER, MYSQL_DRIVER_6, MYSQL_DRIVER_REPLICATE -> MYSQL;
            case MARIADB, MARIADB_DRIVER -> DbType.mariadb;
            case TIDB_DRIVER -> TIDB;
            //return DERBY = return.derby;
            case HBASE -> DbType.hbase;
            case HIVE_DRIVER -> HIVE;
            case H2_DRIVER -> H2;
            case DM_DRIVER -> DM;
            case KINGBASE_DRIVER, KINGBASE8_DRIVER -> KINGBASE;
            case GBASE_DRIVER -> GBASE;
            case XUGU_DRIVER -> XUGU;
            //return OCEANBASE_ORACLE = return.oceanbase_oracle;
            case OCEANBASE_DRIVER, OCEANBASE_DRIVER2 -> OCEANBASE;
            //return INFORMIX = return.informix;
            /**
             * 阿里云odps
             */
            case ODPS_DRIVER -> ODPS;
            case TERADATA, TERADATA_DRIVER -> DbType.teradata;
            /**
             * Log4JDBC
             */
            case LOG4JDBC, LOG4JDBC_DRIVER -> DbType.log4jdbc;
            case PHOENIX, PHOENIX_DRIVER -> DbType.phoenix;
            case ENTERPRISEDB_DRIVER -> ENTERPRISEDB;
            case KYLIN, KYLIN_DRIVER -> DbType.kylin;
            case SQLITE, SQLITE_DRIVER -> DbType.sqlite;
            case ALIYUN_ADS -> ALIYUN_DRDS;
            case PRESTO, PRESTO_DRIVER -> DbType.presto;
            case TRINO, TRINO_DRIVER -> DbType.trino;
            case ELASTIC_SEARCH, ELASTIC_SEARCH_DRIVER -> DbType.elastic_search;
            case CLICKHOUSE_DRIVER -> CLICKHOUSE;
            case KDB, KDB_DRIVER -> DbType.kdb;
            /**
             * Aliyun PolarDB
             */
            case POLARDB_DRIVER -> POLARDB;
            /**
             * GreenPlum
             */
            case GREENPLUM_DRIVER -> GREENPLUM;
            /**
             * oscar
             */
            case OSCAR_DRIVER -> OSCAR;
            case TYDB_DRIVER -> TYDB;
            default -> null;
        };

    }
}
