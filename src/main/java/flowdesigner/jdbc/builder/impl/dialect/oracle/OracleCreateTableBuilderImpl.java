package flowdesigner.jdbc.builder.impl.dialect.oracle;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLCheck;
import com.alibaba.druid.sql.ast.statement.SQLConstraint;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCheck;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OraclePrimaryKey;
import flowdesigner.jdbc.builder.impl.SQLCreateTableBuilderImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OracleCreateTableBuilderImpl extends SQLCreateTableBuilderImpl {

    public OracleCreateTableBuilderImpl(@NotNull DbType dbType) {
        super(dbType);
    }

    @Override
    protected @NotNull SQLConstraint buildPrimaryKey(List<String> columnNames, StringBuffer name) {
        OraclePrimaryKey primaryKey = new OraclePrimaryKey();
        if (name != null) {
            primaryKey.setName(String.valueOf(name));
        }
        super.orderBy(columnNames, primaryKey.getColumns(), primaryKey);

        return primaryKey;
    }

    @Override
    protected @NotNull SQLCheck createCheck(StringBuffer name) {
        return new OracleCheck();
    }

    @Override
    protected OracleCreateTableStatement createSQLCreateTableStatement() {
        return new OracleCreateTableStatement();
    }
}
