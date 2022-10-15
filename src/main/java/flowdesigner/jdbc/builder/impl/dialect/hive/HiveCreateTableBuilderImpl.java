package flowdesigner.jdbc.builder.impl.dialect.hive;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import flowdesigner.jdbc.builder.SQLCreateTableBuilder;
import flowdesigner.jdbc.builder.impl.SQLCreateTableBuilderImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HiveCreateTableBuilderImpl extends SQLCreateTableBuilderImpl {

    public HiveCreateTableBuilderImpl(@NotNull DbType dbType) {
        super(dbType);
    }

    public HiveCreateTableBuilderImpl(String sql, DbType dbType) {
        super(sql, dbType);
    }

    public HiveCreateTableBuilderImpl(@Nullable SQLCreateTableStatement stmt, @NotNull DbType dbType) {
        super(stmt, dbType);
    }

    /**
     * 创建临时表--hive只有一种类型[TEMPORARY]
     *
     * @param temporaryType TEMPORARY
     * @return
     */
    @Override
    public SQLCreateTableBuilder setTemporary(String temporaryType) {
        return super.setTemporary(SQLCreateTableStatement.Type.TEMPORARY);
    }

}
