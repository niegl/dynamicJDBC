package flowdesigner.jdbc.builder.impl.dialect.mysql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import flowdesigner.jdbc.builder.impl.SQLInsertBuilderImpl;
import org.jetbrains.annotations.NotNull;

public class MySQLInsertBuilderImpl extends SQLInsertBuilderImpl {
    public MySQLInsertBuilderImpl(@NotNull DbType dbType) {
        super(dbType);
    }

    @Override
    protected MySqlInsertStatement createSQLInsertStatement() {
        return new MySqlInsertStatement();
    }
}
