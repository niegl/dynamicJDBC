package flowdesigner.sql.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyConstraint;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyImpl;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * 此类对应解析过程中的 SQLExprParser。<p>
 * SQLExprBuilder用来生成每个数据库特有的exper.<p>
 * 当不同的createBuilder，如SQLCreateTableBuilder、SQLCreateDatabaseBuilder...使用SQLExpr时就不用多次生成。
 */
public class SQLExprBuilder {

    public SQLExprBuilder(DbType dbType) {
        this.dbType = dbType;
    }

    /**
     * 不同的数据库可能会使用同一个ExprBuilder，通过dbType可以区分，进一步适配
     */
    private DbType dbType;

    /**
     * 支持hive的 CONSTRAINT constraint_name FOREIGN KEY (col_name, ...) REFERENCES table_name(col_name, ...) DISABLE NOVALIDATE
     */
    @Setter
    protected boolean   DISABLE_NOVALIDATE = false;

    /**
     * 用于字段列表中字段的生成，包括但不限于select字段列表，如:
     *     <li>
     *         CLUSTERED BY (col_name) 或 SORTED BY (col_name ASC) 中的 col_name 字段。
     *     </li>
     *     <li>
     *         SORTED BY (col_name ASC) 中的 col_name 字段。
     *     </li>
     *     <li>
     *         select col_name中的 col_name 字段。
     *     </li>
     * @param columnName 字段
     * @return
     */
    public SQLSelectOrderByItem buildSelectOrderByItem(String columnName) {
        return buildSelectOrderByItem(columnName, null);
    }
    public SQLSelectOrderByItem buildSelectOrderByItem(String columnName, @Nullable SQLOrderingSpecification orderingSpecification) {
        SQLSelectOrderByItem item = new SQLSelectOrderByItem();
        if (orderingSpecification != null) {
            item.setType(SQLOrderingSpecification.ASC);
        }
        item.setExpr(new SQLIdentifierExpr(columnName));

        return item;
    }

    public SQLForeignKeyConstraint builderForeignKey(String index_name, Collection<String> columns,
                                                     String referenceTable, Collection<String> referenceColumn) {
        SQLForeignKeyImpl fk = createForeignKey();

        this.names(fk.getReferencingColumns(), fk, columns);
        fk.setReferencedTableName(new SQLIdentifierExpr(referenceTable));
        this.names(fk.getReferencedColumns(), fk, referenceColumn);

        if (DISABLE_NOVALIDATE) {
            fk.setDisableNovalidate(true);
        }

        return fk;
    }

    protected SQLForeignKeyImpl createForeignKey() {
        return new SQLForeignKeyImpl();
    }

    public final void names(Collection<SQLName> exprCol, Collection<String> stringCol) {
        names(exprCol, null, stringCol);
    }

    public final void names(Collection<SQLName> exprCol, SQLObject parent, Collection<String> stringCol) {
        for (String colName :
                stringCol) {
            SQLName name = new SQLIdentifierExpr(colName);
            name.setParent(parent);
            exprCol.add(name);
        }
    }

    /**
     * 生成 target=value 形式的 SQLAssignItem，包括但不限于 分区赋值、表属性赋值（未实现）等。
     * @param variant
     * @param parent
     * @param target
     * @param value
     * @return
     */
    public SQLAssignItem buildAssignItem(boolean variant, SQLObject parent,
                                         SQLExpr target,SQLExpr value) {
        SQLAssignItem item = new SQLAssignItem();
        item.setTarget(target);
        item.setValue(value);

        return item;
    }

}
