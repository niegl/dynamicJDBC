package flowdesigner.jdbc.builder.impl.dialect.oracle;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLConstraint;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OraclePrimaryKey;
import flowdesigner.jdbc.builder.impl.SQLCreateTableBuilderImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OracleCreateTableBuilderImpl extends SQLCreateTableBuilderImpl {

    public OracleCreateTableBuilderImpl(@NotNull DbType dbType) {
        super(dbType);
    }

    @Override
    protected @NotNull SQLConstraint createPrimaryKey(List<String> columnNames, boolean hasConstraint, SQLName name) {
        OraclePrimaryKey primaryKey = new OraclePrimaryKey();

        return super.createPrimaryKey(columnNames, hasConstraint, name);
    }
}
