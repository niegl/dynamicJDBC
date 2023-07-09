package flowdesigner.sql.builder;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.builder.SQLDeleteBuilder;
import com.alibaba.druid.sql.builder.SQLUpdateBuilder;
import com.alibaba.druid.sql.builder.impl.SQLDeleteBuilderImpl;
import com.alibaba.druid.sql.builder.impl.SQLUpdateBuilderImpl;
import flowdesigner.sql.builder.impl.*;
import flowdesigner.sql.dialect.antspark.AntSparkCreateTableBuilderImpl;
import flowdesigner.sql.dialect.blink.BlinkCreateTableBuilderImpl;
import flowdesigner.sql.dialect.clickhouse.ClickhouseCreateTableBuilderImpl;
import flowdesigner.sql.dialect.db2.DB2CreateTableBuilderImpl;
import flowdesigner.sql.dialect.db2.DB2SelectBuilderImpl;
import flowdesigner.sql.dialect.hive.HiveAlterTableBuilderImpl;
import flowdesigner.sql.dialect.hive.HiveCreateTableBuilderImpl;
import flowdesigner.sql.dialect.hive.HiveInsertBuilderImpl;
import flowdesigner.sql.dialect.mysql.builder.*;
import flowdesigner.sql.dialect.odps.OdpsCreateTableBuilderImpl;
import flowdesigner.sql.dialect.oracle.*;
import flowdesigner.sql.dialect.oscar.builder.OscarDropDatabaseBuilderImpl;
import flowdesigner.sql.dialect.pg.builder.PGDropDatabaseBuilderImpl;
import flowdesigner.sql.dialect.pg.builder.PGInsertBuilderImpl;
import flowdesigner.sql.dialect.pg.builder.PGSelectBuilderImpl;
import flowdesigner.sql.dialect.sqlserver.SQLServerInsertBuilderImpl;

public class SQLBuilderFactory {

    public static SQLSelectBuilder createSelectSQLBuilder(DbType dbType) {
        return switch (dbType) {
            case mysql -> new MySQLSelectBuilderImpl( dbType);
            case db2 -> new DB2SelectBuilderImpl( dbType);
            case oracle -> new OracleSelectBuilderImpl( dbType);
            case postgresql -> new PGSelectBuilderImpl( dbType);
            default -> new SQLSelectBuilderImpl( dbType);
        };
    }

    /**
     * 创建用于解析或构造SQL的builder。当SQL不为空时，当前功能为解析SQL；当SQL为空时，功能为创建。
     * @param sql 待解析的SQL
     * @param dbType 数据库类型
     * @return builder
     */
    public static SQLSelectBuilder createSelectSQLBuilder(String sql, DbType dbType) {
        SQLSelectStatement statement = null;
        if (sql != null && !sql.isEmpty()) {
            statement = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, dbType, null);
        }

        return switch (dbType) {
            case mysql -> new MySQLSelectBuilderImpl(statement, dbType);
            case db2 -> new DB2SelectBuilderImpl(statement, dbType);
            case oracle -> new OracleSelectBuilderImpl(statement, dbType);
            case postgresql -> new PGSelectBuilderImpl(statement, dbType);
            default -> new SQLSelectBuilderImpl(statement, dbType);
        };
    }

    public static SQLDeleteBuilder createDeleteBuilder(DbType dbType) {
        return new SQLDeleteBuilderImpl(dbType);
    }
    public static SQLDeleteBuilder createDeleteBuilder(String sql,DbType dbType) {
        return new SQLDeleteBuilderImpl(sql, dbType);
    }

    public static SQLUpdateBuilder createUpdateBuilder(DbType dbType) {
        return new SQLUpdateBuilderImpl(dbType);
    }
    public static SQLUpdateBuilder createUpdateBuilder(String sql, DbType dbType) {
        return new SQLUpdateBuilderImpl(sql, dbType);
    }

    public static SQLAlterDatabaseBuilder createAlterDatabaseBuilder(DbType dbType) {
        return new SQLAlterDatabaseBuilderImpl(dbType);
    }
    public static SQLAlterDatabaseBuilder createAlterDatabaseBuilder(String sql, DbType dbType) {
        return new SQLAlterDatabaseBuilderImpl(sql, dbType);
    }

    public static SQLAlterTableBuilder createAlterTableBuilder(DbType dbType) {
        return switch (dbType) {
            case mysql -> new MySQLAlterTableBuilderImpl();
            case oracle -> new OracleAlterTableBuilderImpl();
            case hive -> new HiveAlterTableBuilderImpl();
            default -> new SQLAlterTableBuilderImpl(dbType);
        };
    }

    public static SQLCreateDatabaseBuilder createCreateDatabaseBuilder(DbType dbType) {
        return switch (dbType) {

            case mysql,mariadb,h2, elastic_search, drds -> new MySQLCreateDatabaseBuilderImpl(dbType);
            default -> new SQLCreateDatabaseBuilderImpl(dbType);
        };
    }

    public static SQLCreateTableBuilder createCreateTableBuilder(DbType dbType) {
        return switch (dbType) {
            case hive -> new HiveCreateTableBuilderImpl(dbType);
            case clickhouse -> new ClickhouseCreateTableBuilderImpl(dbType);
            case mysql, mariadb, drds, elastic_search -> new MySQLCreateTableBuilderImpl(dbType);
            case oracle -> new OracleCreateTableBuilderImpl(dbType);
            case db2 -> new DB2CreateTableBuilderImpl(dbType);
            case odps -> new OdpsCreateTableBuilderImpl(dbType);
            case blink -> new BlinkCreateTableBuilderImpl(dbType);
            case antspark -> new AntSparkCreateTableBuilderImpl(dbType);
            default -> new SQLCreateTableBuilderImpl(dbType);
        };
    }

    public static SQLDropDatabaseBuilder createSQLDropDatabaseBuilder(DbType dbType) {
        return switch (dbType) {
            case postgresql -> new PGDropDatabaseBuilderImpl(dbType);
            case oscar -> new OscarDropDatabaseBuilderImpl(dbType);
            case oracle , oceanbase_oracle -> new SQLDropUserBuilderImpl(dbType);
            default -> new SQLDropDatabaseBuilderImpl(dbType);
        };
    }
    public static SQLDropTableBuilder createDropTableBuilder(DbType dbType) {
        return new SQLDropTableBuilderImpl(dbType);
    }

    public static SQLInsertBuilder createInsertBuilder(DbType dbType) {
        return switch (dbType) {
            case postgresql -> new PGInsertBuilderImpl(dbType);
            case sqlserver -> new SQLServerInsertBuilderImpl(dbType);
            case oracle -> new OracleInsertBuilderImpl(dbType);
            case mysql -> new MySQLInsertBuilderImpl(dbType);
            case hive -> new HiveInsertBuilderImpl(dbType);
            default -> new SQLInsertBuilderImpl(dbType);
        };
    }


}