package flowdesigner.jdbc.builder.impl.dialect.hive;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.parser.Token;
import flowdesigner.jdbc.builder.SQLCreateTableBuilder;
import flowdesigner.jdbc.builder.impl.SQLCreateTableBuilderImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
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
    protected @NotNull SQLConstraint buildPrimaryKey(List<String> columnNames, StringBuffer name) {

        SQLConstraint constraint = super.buildPrimaryKey( columnNames, name);
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
    protected SQLConstraint buildUnique(List<String> columnNames, StringBuffer name) {
        return null;
    }

    @Override
    protected @NotNull SQLForeignKeyImpl createForeignKey(StringBuffer name) {
        SQLForeignKeyImpl foreignKey = super.createForeignKey(name);
        foreignKey.setDisableNovalidate(true);

        return foreignKey;
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

    /**
     * 支持语法： [CLUSTERED BY (col_name, col_name, ...) [SORTED BY (col_name [ASC|DESC], ...)] INTO num_buckets BUCKETS]。<p>
     * 即CLUSTERED BY后面必有 INTO num_buckets BUCKETS, 这里自动默认添加后半部分。
     * @param items
     */
    @Override
    public void addClusteredByItem(List<String> items) {
        SQLCreateTableStatement stmt = getSQLStatement();

        for (String item :
                items) {
            SQLSelectOrderByItem selectOrderByItem = exprBuilder.buildSelectOrderByItem(item);
            selectOrderByItem.setParent(stmt);
            stmt.addClusteredByItem(selectOrderByItem);
        }

        // 补全必须的部分
        super.setBuckets(2);
    }

    @Override
    protected HiveCreateTableStatement createSQLCreateTableStatement() {
        return new HiveCreateTableStatement();
    }
}
