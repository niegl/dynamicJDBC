package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyConstraint;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyImpl;
import com.alibaba.druid.sql.ast.statement.SQLLateralViewTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.druid.sql.parser.Token;
import lombok.Setter;

import java.util.Collection;

public class SQLExprBuilder {

    /**
     * 支持hive的 CONSTRAINT constraint_name FOREIGN KEY (col_name, ...) REFERENCES table_name(col_name, ...) DISABLE NOVALIDATE
     */
    @Setter
    protected boolean   DISABLE_NOVALIDATE = false;

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
