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
package flowdesigner.jdbc.util.sql;


import com.alibaba.druid.DbType;
import com.alibaba.druid.util.JdbcUtils;
import flowdesigner.jdbc.driver.DynamicDriver;

import java.sql.Connection;

import static com.alibaba.druid.util.JdbcConstants.*;

/**
 * 两种方式获取数据库类型：<p>
 * 1、使用阿里接口，通过URL获取；<p>
 * 2、当获取不到URL时，使用自定义函数通过getDriverClassName获取.
 */
public abstract class DbTypeKit {

    /**
     * 根据连接信息推断数据库类型(类型描述字串）
     * @return
     */
    public static DbType getDbType(Connection connection) {
        DbType dbType = null;

        if (null == connection ) {
            return null;
        }

        String url = DynamicDriver.getUrl(connection);
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

    public static DbType getDbTypeStr(String driverName){
        switch (driverName) {
            case DB2_DRIVER:
            case DB2_DRIVER2:
            case DB2_DRIVER3:
                return DB2;
            case POSTGRESQL_DRIVER:
                return POSTGRESQL;
            //  DbType SYBASE = DbType.sybase;
            case SQL_SERVER_DRIVER :
            case SQL_SERVER_DRIVER_SQLJDBC4 :
            case SQL_SERVER_DRIVER_JTDS :
                return SQL_SERVER;

            case ORACLE_DRIVER :
            case ORACLE_DRIVER2 :
                return ORACLE;
            case ALI_ORACLE_DRIVER :
                return ALI_ORACLE ;
            case MYSQL_DRIVER :
            case MYSQL_DRIVER_6 :
            case MYSQL_DRIVER_REPLICATE :
                return MYSQL ;
            case MARIADB :
            case MARIADB_DRIVER :
                return DbType.mariadb;
            case TIDB_DRIVER :
                return TIDB ;
            //return DERBY = return.derby;
            case HBASE :
                return DbType.hbase;
            case HIVE_DRIVER :
                return HIVE;
            case H2_DRIVER :
                return H2 ;
            case DM_DRIVER :
                return DM ;
            case KINGBASE_DRIVER :
            case KINGBASE8_DRIVER :
                return KINGBASE;
            case GBASE_DRIVER :
                return GBASE;
            case XUGU_DRIVER :
                return XUGU ;
            //return OCEANBASE_ORACLE = return.oceanbase_oracle;
            case OCEANBASE_DRIVER :
            case OCEANBASE_DRIVER2 :
                return OCEANBASE ;
            //return INFORMIX = return.informix;
            /**
             * 阿里云odps
             */
            case ODPS_DRIVER :
                return ODPS;
            case TERADATA :
            case TERADATA_DRIVER :
                return DbType.teradata;
            /**
             * Log4JDBC
             */
            case LOG4JDBC :
            case LOG4JDBC_DRIVER :
                return DbType.log4jdbc;
            case PHOENIX :
            case PHOENIX_DRIVER :
                return DbType.phoenix;
            case ENTERPRISEDB_DRIVER :
                return ENTERPRISEDB;
            case KYLIN :
            case KYLIN_DRIVER :
                return DbType.kylin;
            case SQLITE:
            case SQLITE_DRIVER :
                return DbType.sqlite;
            case ALIYUN_ADS :
                return ALIYUN_DRDS;
            case PRESTO :
            case PRESTO_DRIVER :
                return DbType.presto;
            case TRINO :
            case TRINO_DRIVER :
                return DbType.trino;
            case ELASTIC_SEARCH :
            case ELASTIC_SEARCH_DRIVER :
                return DbType.elastic_search;
            case CLICKHOUSE_DRIVER :
                return CLICKHOUSE ;
            case KDB:
            case KDB_DRIVER :
                return DbType.kdb;
            /**
             * Aliyun PolarDB
             */
            case POLARDB_DRIVER:
                return POLARDB ;
            /**
             * GreenPlum
             */
            case GREENPLUM_DRIVER:
                return GREENPLUM;
            /**
             * oscar
             */
            case OSCAR_DRIVER:
                return OSCAR ;
            case TYDB_DRIVER:
            return TYDB;
        }

        return null;
    }
}
