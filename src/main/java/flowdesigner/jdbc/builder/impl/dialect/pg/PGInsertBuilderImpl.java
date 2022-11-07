package flowdesigner.jdbc.builder.impl.dialect.pg;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;
import flowdesigner.jdbc.builder.impl.SQLInsertBuilderImpl;
import org.jetbrains.annotations.NotNull;

public class PGInsertBuilderImpl extends SQLInsertBuilderImpl {
    public PGInsertBuilderImpl(@NotNull DbType dbType) {
        super(dbType);
    }

    @Override
    protected PGInsertStatement createSQLInsertStatement() {
        return new PGInsertStatement();
    }
}
