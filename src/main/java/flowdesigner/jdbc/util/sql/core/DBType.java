package flowdesigner.jdbc.util.sql.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DBType {
    HIVE("Apache Hive","/images/db/hive.png", ""),
    MYSQL("MySQL","/images/db/mysql.png", ""),
    JTDS("odps", "/images/db/mysql.png", ""),
    HSQL("hsql", "/images/db/mysql.png", ""),
    DB2("db2", "/images/db/mysql.png", ""),
    POSTGRESQL("postgresql", "/images/db/mysql.png", ""),
    SYBASE("sybase", "/images/db/mysql.png", ""),
    SQL_SERVER("sqlserver", "/images/db/mysql.png", ""),
    ORACLE("oracle", "/images/db/mysql.png", ""),
    ALI_ORACLE("AliOracle", "/images/db/mysql.png", ""),
    MARIADB("mariadb", "/images/db/mysql.png", ""),
    DERBY("derby", "/images/db/mysql.png", ""),
    HBASE("hbase", "/images/db/mysql.png", ""),
    H2("h2", "/images/db/mysql.png", ""),
    DM("dm", "/images/db/mysql.png", "达梦数据库"),
    KINGBASE("kingbase", "/images/db/mysql.png",""),
    GBASE("gbase", "/images/db/mysql.png",""),
    XUGU("xugu", "/images/db/mysql.png",""),
    OCEANBASE("oceanbase", "/images/db/mysql.png",""),
    INFORMIX("informix", "/images/db/mysql.png",""),
    ODPS("odps", "/images/db/mysql.png","阿里云odps"),
    TERADATA("teradata", "/images/db/mysql.png",""),
    LOG4JDBC("log4jdbc", "/images/db/mysql.png","Log4JDBC"),
    PHOENIX("phoenix", "/images/db/mysql.png",""),
    ENTERPRISEDB("edb", "/images/db/mysql.png",""),
    KYLIN("kylin", "/images/db/mysql.png",""),
    SQLITE("sqlite", "/images/db/mysql.png",""),
    ALIYUN_ADS("aliyun_ads", "/images/db/mysql.png",""),
    ALIYUN_DRDS("aliyun_drds", "/images/db/mysql.png",""),
    PRESTO("presto", "/images/db/mysql.png",""),
    ELASTIC_SEARCH("elastic_search", "/images/db/mysql.png",""),
    CLICKHOUSE("clickhouse", "/images/db/mysql.png","");

    @Getter
    private String name;
    @Getter
    private String png;
    private String comment;

    public static DBType fromTypeName(String typeName) {
        for (DBType type : DBType.values()) {
            if (type.getName().equalsIgnoreCase(typeName)) {
                return type;
            }
        }
        throw new IllegalArgumentException(typeName + "for DBType.name not exists");
    }

    @Override
    public String toString() {
        return this.name;
    }
}
