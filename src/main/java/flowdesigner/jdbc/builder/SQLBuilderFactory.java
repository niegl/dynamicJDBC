package flowdesigner.jdbc.builder;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.builder.SQLDeleteBuilder;
import com.alibaba.druid.sql.builder.SQLUpdateBuilder;
import com.alibaba.druid.sql.builder.impl.SQLDeleteBuilderImpl;
import com.alibaba.druid.sql.builder.impl.SQLUpdateBuilderImpl;
import flowdesigner.jdbc.builder.impl.*;
import flowdesigner.jdbc.builder.impl.dialect.mysql.MySQLAlterTableBuilderImpl;
import flowdesigner.jdbc.builder.impl.dialect.oracle.OracleAlterTableBuilderImpl;

public class SQLBuilderFactory {

    public static SQLSelectBuilder createSelectSQLBuilder(DbType dbType) {
        return new SQLSelectBuilderImpl(dbType);
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

    public static SQLDropTableBuilder createDropTableBuilder(DbType dbType) {
        return new SQLDropTableBuilderImpl(dbType);
    }
}