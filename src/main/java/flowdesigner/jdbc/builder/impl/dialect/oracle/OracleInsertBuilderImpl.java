package flowdesigner.jdbc.builder.impl.dialect.oracle;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import flowdesigner.jdbc.builder.impl.SQLInsertBuilderImpl;
import org.jetbrains.annotations.NotNull;

public class OracleInsertBuilderImpl extends SQLInsertBuilderImpl {
    public OracleInsertBuilderImpl(@NotNull DbType dbType) {
        super(dbType);
    }

    @Override
    protected OracleInsertStatement createSQLInsertStatement() {
        return new OracleInsertStatement();
    }
}
