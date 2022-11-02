package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyConstraint;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyImpl;
import com.alibaba.druid.sql.ast.statement.SQLLateralViewTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.druid.sql.parser.Token;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * 用于通用的expr的生成，功能类似 SQLExprParser
 */
public class SQLExprBuilder {

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

}
