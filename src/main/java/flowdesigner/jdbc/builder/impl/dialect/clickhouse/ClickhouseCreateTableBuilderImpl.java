package flowdesigner.jdbc.builder.impl.dialect.clickhouse;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.clickhouse.ast.ClickhouseCreateTableStatement;
import flowdesigner.jdbc.builder.impl.SQLCreateTableBuilderImpl;
import org.jetbrains.annotations.NotNull;

public class ClickhouseCreateTableBuilderImpl extends SQLCreateTableBuilderImpl {
    public ClickhouseCreateTableBuilderImpl(@NotNull DbType dbType) {
        super(dbType);
    }

    @Override
    protected ClickhouseCreateTableStatement createSQLCreateTableStatement() {
        return new ClickhouseCreateTableStatement();
    }
}
