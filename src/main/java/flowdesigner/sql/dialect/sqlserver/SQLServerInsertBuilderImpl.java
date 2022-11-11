package flowdesigner.sql.dialect.sqlserver;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerInsertStatement;
import flowdesigner.sql.builder.impl.SQLInsertBuilderImpl;
import org.jetbrains.annotations.NotNull;

public class SQLServerInsertBuilderImpl extends SQLInsertBuilderImpl {
    public SQLServerInsertBuilderImpl(@NotNull DbType dbType) {
        super(dbType);
    }

    @Override
    protected SQLServerInsertStatement createSQLInsertStatement() {
        return new SQLServerInsertStatement();
    }
}
