package flowdesigner.jdbc.builder.impl.dialect.hive;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLConstraint;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKeyImpl;
import com.alibaba.druid.sql.ast.statement.SQLTableConstraint;
import com.alibaba.druid.sql.parser.Token;
import flowdesigner.jdbc.builder.SQLCreateTableBuilder;
import flowdesigner.jdbc.builder.impl.SQLCreateTableBuilderImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
     * 支持数据库：hive <p>
     * 语法：CREATE [TEMPORARY] [EXTERNAL] TABLE ...
     *
     * @param external
     * @return
     */
    @Override
    public SQLCreateTableBuilder setExternal(boolean external) {
        SQLCreateTableStatement statement = getSQLStatement();
        if (statement != null) {
            statement.setExternal(external);
        }
        return this;
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

    /**
     * Hive需要在PRIMARY KEY 后面增加 NOVALIDATE关键字（默认）.<p>
     * 语法：[, PRIMARY KEY (col_name, ...) DISABLE NOVALIDATE RELY/NORELY ]
     * @param columnNames
     * @param hasConstraint
     * @param name
     * @return
     */
    @Override
    protected @NotNull SQLConstraint createPrimaryKey(List<String> columnNames, boolean hasConstraint, SQLName name) {

        SQLConstraint primaryKey = super.createPrimaryKey( columnNames, hasConstraint, name);
        if (primaryKey instanceof SQLPrimaryKeyImpl) {
            primaryKey.setName(null);
            ((SQLPrimaryKeyImpl)primaryKey).setDisableNovalidate(true);
            // 以下代码目前不起作用
            ((SQLPrimaryKeyImpl)primaryKey).setRely(false);
        }

        return primaryKey;
    }

    /**
     * 添加主键列.<p>
     * HIVE 数据库对 column_constraint_specification 语法中 PRIMARY KEY 的支持存在问题（虽然官网说2.1.0以后支持，但执行报错），故调用该接口只添加字段。
     * @param columnName 列名
     * @param dataType   列类型
     * @param primary    是否主键列
     * @param unique
     * @param notNull    是否为空
     * @return
     */
    @Override
    public SQLCreateTableBuilder addColumn(String columnName, String dataType, boolean primary, boolean unique, boolean notNull) {
        return super.addColumn(columnName, dataType);
    }
}
