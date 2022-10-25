package flowdesigner.jdbc.builder;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.builder.SQLDeleteBuilder;
import com.alibaba.druid.sql.builder.SQLUpdateBuilder;
import com.alibaba.druid.sql.builder.impl.SQLDeleteBuilderImpl;
import com.alibaba.druid.sql.builder.impl.SQLUpdateBuilderImpl;
import flowdesigner.jdbc.builder.impl.*;
import flowdesigner.jdbc.builder.impl.dialect.db2.DB2CreateTableBuilderImpl;
import flowdesigner.jdbc.builder.impl.dialect.db2.DB2SelectBuilderImpl;
import flowdesigner.jdbc.builder.impl.dialect.hive.HiveAlterTableBuilderImpl;
import flowdesigner.jdbc.builder.impl.dialect.hive.HiveCreateTableBuilderImpl;
import flowdesigner.jdbc.builder.impl.dialect.hive.SQLHiveInsertBuilderImpl;
import flowdesigner.jdbc.builder.impl.dialect.mysql.MySQLAlterTableBuilderImpl;
import flowdesigner.jdbc.builder.impl.dialect.mysql.MySQLCreateTableBuilderImpl;
import flowdesigner.jdbc.builder.impl.dialect.mysql.MySQLSelectBuilderImpl;
import flowdesigner.jdbc.builder.impl.dialect.oracle.OracleAlterTableBuilderImpl;
import flowdesigner.jdbc.builder.impl.dialect.oracle.OracleCreateTableBuilderImpl;
import flowdesigner.jdbc.builder.impl.dialect.oracle.OracleSelectBuilderImpl;
import flowdesigner.jdbc.builder.impl.dialect.pg.PGSelectBuilderImpl;

public class SQLBuilderFactory {

    public static SQLSelectBuilder createSelectSQLBuilder(DbType dbType) {
        return createSelectSQLBuilder(null, dbType);
    }

    /**
     * 创建用于解析或构造SQL的builder。当SQL不为空时，当前功能为解析SQL；当SQL为空时，功能为创建。
     * @param sql 待解析的SQL
     * @param dbType 数据库类型
     * @return builder
     */
    public static SQLSelectBuilder createSelectSQLBuilder(String sql, DbType dbType) {
        SQLSelectStatement statement = new SQLSelectStatement(dbType);
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
    public static SQLAlterTableBuilder createAlterTableBuilder(String sql, DbType dbType) {
        return switch (dbType) {
            case mysql -> new MySQLAlterTableBuilderImpl(sql);
            case oracle -> new OracleAlterTableBuilderImpl(sql);
            case hive -> new HiveAlterTableBuilderImpl(sql);
            default -> new SQLAlterTableBuilderImpl(sql, dbType);
        };
    }

    public static SQLCreateDatabaseBuilder createCreateDatabaseBuilder(DbType dbType) {
        return new SQLCreateDatabaseBuilderImpl(dbType);
    }
    public static SQLCreateDatabaseBuilder createCreateDatabaseBuilder(String sql, DbType dbType) {
        return new SQLCreateDatabaseBuilderImpl(sql, dbType);
    }

    public static SQLCreateTableBuilder createCreateTableBuilder(DbType dbType) {
        return switch (dbType) {
            case hive -> new HiveCreateTableBuilderImpl(dbType);
            case mysql -> new MySQLCreateTableBuilderImpl(dbType);
            case oracle -> new OracleCreateTableBuilderImpl(dbType);
            case db2 -> new DB2CreateTableBuilderImpl(dbType);
            default -> new SQLCreateTableBuilderImpl(dbType);
        };
    }
    public static SQLCreateTableBuilder createCreateTableBuilder(String sql, DbType dbType) {
        return new SQLCreateTableBuilderImpl(sql, dbType);
    }

    public static SQLDropTableBuilder createDropTableBuilder(DbType dbType) {
        return new SQLDropTableBuilderImpl(dbType);
    }
    public static SQLDropTableBuilder createDropTableBuilder(String sql, DbType dbType) {
        return new SQLDropTableBuilderImpl(sql, dbType);
    }

    public static SQLInsertBuilder createInsertBuilder(DbType dbType) {
        return switch (dbType) {
            case hive -> new SQLHiveInsertBuilderImpl(dbType);
            default -> new SQLInsertBuilderImpl(dbType);
        };
    }
    public static SQLInsertBuilder createInsertBuilder(String sql, DbType dbType) {
        return switch (dbType) {
            case hive -> new SQLHiveInsertBuilderImpl(sql, dbType);
            default -> new SQLInsertBuilderImpl(sql, dbType);
        };
    }


}