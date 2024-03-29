package flowdesigner.sql.dialect.pg.builder;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;
import flowdesigner.sql.builder.impl.SQLInsertBuilderImpl;
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
