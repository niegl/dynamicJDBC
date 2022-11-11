package flowdesigner.sql.dialect.mysql;

import com.alibaba.druid.DbType;
import flowdesigner.sql.builder.SQLCreateDatabaseBuilder;
import flowdesigner.sql.builder.impl.SQLCreateDatabaseBuilderImpl;
import org.jetbrains.annotations.NotNull;

/**
 * 语法：CREATE {DATABASE | SCHEMA} [IF NOT EXISTS] db_name
 *     [create_option] ...
 */
public class MySQLCreateDatabaseBuilderImpl extends SQLCreateDatabaseBuilderImpl {
    public MySQLCreateDatabaseBuilderImpl(@NotNull DbType dbType) {
        super(dbType);
    }

    @Override
    public SQLCreateDatabaseBuilder setComment(String db_comment) {
        return this;
    }

}
