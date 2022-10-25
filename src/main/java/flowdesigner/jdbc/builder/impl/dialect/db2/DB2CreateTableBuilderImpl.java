package flowdesigner.jdbc.builder.impl.dialect.db2;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import flowdesigner.jdbc.builder.SQLCreateTableBuilder;
import flowdesigner.jdbc.builder.impl.SQLCreateTableBuilderImpl;
import org.jetbrains.annotations.NotNull;

public class DB2CreateTableBuilderImpl extends SQLCreateTableBuilderImpl {
    public DB2CreateTableBuilderImpl(@NotNull DbType dbType) {
        super(dbType);
    }

    @Override
    public SQLCreateTableBuilder setTemporary(SQLCreateTableStatement.Type type) {
        return super.setTemporary(SQLCreateTableStatement.Type.GLOBAL_TEMPORARY);
    }

}
