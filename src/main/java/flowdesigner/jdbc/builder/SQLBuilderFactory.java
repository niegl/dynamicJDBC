package flowdesigner.jdbc.builder;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.builder.SQLDeleteBuilder;
import com.alibaba.druid.sql.builder.SQLUpdateBuilder;
import com.alibaba.druid.sql.builder.impl.SQLDeleteBuilderImpl;
import com.alibaba.druid.sql.builder.impl.SQLUpdateBuilderImpl;
import flowdesigner.jdbc.builder.impl.*;
import flowdesigner.jdbc.builder.impl.dialect.db2.DB2SelectBuilderImpl;
import flowdesigner.jdbc.builder.impl.dialect.hive.HiveAlterTableBuilderImpl;
import flowdesigner.jdbc.builder.impl.dialect.hive.SQLHiveInsertBuilderImpl;
import flowdesigner.jdbc.builder.impl.dialect.mysql.MySQLAlterTableBuilderImpl;
import flowdesigner.jdbc.builder.impl.dialect.mysql.MySQLSelectBuilderImpl;
import flowdesigner.jdbc.builder.impl.dialect.oracle.OracleAlterTableBuilderImpl;
import flowdesigner.jdbc.builder.impl.dialect.oracle.OracleSelectBuilderImpl;
import flowdesigner.jdbc.builder.impl.dialect.pg.PGSelectBuilderImpl;

public class SQLBuilderFactory {

    public static SQLSelectBuilder createSelectSQLBuilder(DbType dbType) {
        return createSelectSQLBuilder(new SQLSelectStatement(dbType), dbType);
    }
    public static SQLSelectBuilder createSelectSQLBuilder(SQLSelectStatement stmt, DbType dbType) {
        switch (dbType) {

            case mysql:
                return new MySQLSelectBuilderImpl(stmt, dbType);
            case db2:
                return new DB2SelectBuilderImpl(stmt, dbType);
            case oracle:
                return new OracleSelectBuilderImpl(stmt, dbType);
            case postgresql:
                return new PGSelectBuilderImpl(stmt, dbType);
            case sqlserver:
            case hive:
            case odps:
            default:
                return new SQLSelectBuilderImpl(stmt, dbType);
        }
    }

    public static SQLDeleteBuilder createDeleteBuilder(DbType dbType) {
        return new SQLDeleteBuilderImpl(dbType);
    }

    public static SQLUpdateBuilder createUpdateBuilder(DbType dbType) {
        return new SQLUpdateBuilderImpl(dbType);
    }

    public static SQLAlterDatabaseBuilder createAlterDatabaseBuilder(DbType dbType) {
        return new SQLAlterDatabaseBuilderImpl(dbType);
    }

    public static SQLAlterTableBuilder createAlterTableBuilder(DbType dbType) {
        switch (dbType) {
            case mysql:
                return new MySQLAlterTableBuilderImpl();
            case oracle:
                return new OracleAlterTableBuilderImpl();
            case hive:
                return new HiveAlterTableBuilderImpl();
            default:
                return new SQLAlterTableBuilderImpl(dbType);
        }
    }

    public static SQLCreateDatabaseBuilder createCreateDatabaseBuilder(DbType dbType) {
        return new SQLCreateDatabaseBuilderImpl(dbType);
    }

    public static SQLCreateTableBuilder createCreateTableBuilder(DbType dbType) {
        return new SQLCreateTableBuilderImpl(dbType);
    }
    public static SQLCreateTableBuilder createCreateTableBuilder(SQLCreateTableStatement stmt, DbType dbType) {
        return new SQLCreateTableBuilderImpl(stmt,dbType);
    }
    public static SQLDropTableBuilder createDropTableBuilder(DbType dbType) {
        return new SQLDropTableBuilderImpl(dbType);
    }

    public static SQLInsertBuilder createInsertBuilder(DbType dbType) {
        return createInsertBuilder(null, dbType);
    }

    public static SQLInsertBuilder createInsertBuilder(SQLInsertStatement stmt, DbType dbType) {
        switch (dbType) {
            case hive:
                return new SQLHiveInsertBuilderImpl(stmt, dbType);
            default:
                return new SQLInsertBuilderImpl(stmt, dbType);
        }
    }


}