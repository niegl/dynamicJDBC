package flowdesigner.jdbc.builder.impl.dialect.mysql;

import com.alibaba.druid.DbType;
import flowdesigner.jdbc.builder.SQLCreateDatabaseBuilder;
import flowdesigner.jdbc.builder.impl.SQLCreateDatabaseBuilderImpl;
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
