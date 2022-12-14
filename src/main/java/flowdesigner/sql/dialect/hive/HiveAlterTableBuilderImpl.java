package flowdesigner.sql.dialect.hive;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableReplaceColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.parser.ParserException;
import flowdesigner.sql.builder.SQLAlterTableBuilder;
import flowdesigner.sql.builder.impl.SQLAlterTableBuilderImpl;

import java.util.Map;

public class HiveAlterTableBuilderImpl extends SQLAlterTableBuilderImpl {
    public HiveAlterTableBuilderImpl() {
        super(DbType.hive);
        exprBuilder.setDISABLE_NOVALIDATE(true);
    }
    public HiveAlterTableBuilderImpl(String sql) {
        super(sql, DbType.hive);
        exprBuilder.setDISABLE_NOVALIDATE(true);
    }

    /**
     * 适配hive语法：ALTER TABLE db_name REPLACE COLUMNS (line_id int, b int)
     *
     * @param columns
     * @return
     */
    @Override
    public SQLAlterTableBuilder replaceColumn(Map<String, String> columns) {
        SQLAlterTableStatement statement = getSQLStatement();

        if (columns == null) {
            return this;
        }

        SQLAlterTableReplaceColumn alterTableReplaceColumn = new SQLAlterTableReplaceColumn();
        for ( Map.Entry<String,String> entry: columns.entrySet()) {
            SQLColumnDefinition column = getColumn(entry.getKey(), entry.getValue());
            alterTableReplaceColumn.addColumn(column);
        }
        statement.addItem(alterTableReplaceColumn);

        return this;
    }

    /**
     * ALTER TABLE table_name DROP CONSTRAINT constraint_name;
     *
     * @param name 外键约束名
     * @return
     */
    @Override
    public SQLAlterTableBuilder dropForeignKey(String name) {
        return dropConstraint(name);
    }

    /**
     * 删除主键
     *
     * @return
     */
    @Override
    public SQLAlterTableBuilder dropPrimaryKey(String name) {
        return dropConstraint(name);
    }

}
