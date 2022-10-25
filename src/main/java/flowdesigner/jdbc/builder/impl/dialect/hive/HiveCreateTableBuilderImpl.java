package flowdesigner.jdbc.builder.impl.dialect.hive;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.*;
import flowdesigner.jdbc.builder.SQLCreateTableBuilder;
import flowdesigner.jdbc.builder.impl.SQLCreateTableBuilderImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HiveCreateTableBuilderImpl extends SQLCreateTableBuilderImpl {

    public HiveCreateTableBuilderImpl(@NotNull DbType dbType) {
        super(dbType);
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
     * @param name
     * @return
     */
    @Override
    protected @NotNull SQLConstraint createPrimaryKey(List<String> columnNames, SQLName name) {

        SQLConstraint constraint = super.createPrimaryKey( columnNames, name);
        if (constraint instanceof SQLPrimaryKeyImpl primaryKey) {
            constraint.setName(null);
            primaryKey.setDisableNovalidate(true);
            // 以下代码目前不起作用
            primaryKey.setRely(Boolean.TRUE);
        }

        return constraint;
    }

    /**
     * hive目前不支持，执行报错
     * @param columnNames
     * @param name
     * @return
     */
    @Override
    protected SQLConstraint createUnique(List<String> columnNames, SQLName name) {
        return null;
    }

    /**
     * 添加主键列.<p>
     * 注意：HIVE 数据库对 column_constraint_specification 语法中 PRIMARY KEY 的支持存在问题（虽然官网说2.1.0以后支持，但执行报错）。
     * @param columnName 列名
     * @param dataType   列类型
     * @param primary    是否主键列
     * @param unique
     * @param notNull    是否为空
     * @return
     */
    @Override
    public SQLCreateTableBuilder addColumn(String columnName, String dataType, boolean primary, boolean unique, boolean notNull) {
        if (primary) {
            throw new IllegalArgumentException("PRIMARY KEY not supported in current Hive version");
        }

        return super.addColumn(columnName, dataType, false, unique, notNull);
    }
}
