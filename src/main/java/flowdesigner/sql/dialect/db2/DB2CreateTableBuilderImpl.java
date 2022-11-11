package flowdesigner.sql.dialect.db2;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2CreateTableStatement;
import flowdesigner.sql.builder.SQLCreateTableBuilder;
import flowdesigner.sql.builder.impl.SQLCreateTableBuilderImpl;
import org.jetbrains.annotations.NotNull;

public class DB2CreateTableBuilderImpl extends SQLCreateTableBuilderImpl {
    public DB2CreateTableBuilderImpl(@NotNull DbType dbType) {
        super(dbType);
    }

    @Override
    public SQLCreateTableBuilder setTemporary(SQLCreateTableStatement.Type type) {
        return super.setTemporary(SQLCreateTableStatement.Type.GLOBAL_TEMPORARY);
    }

    @Override
    protected DB2CreateTableStatement createSQLCreateTableStatement() {
        return new DB2CreateTableStatement();
    }
}
