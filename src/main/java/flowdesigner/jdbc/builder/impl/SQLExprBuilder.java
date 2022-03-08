package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyConstraint;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyImpl;
import com.alibaba.druid.sql.ast.statement.SQLLateralViewTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.druid.sql.parser.Token;

import java.util.Collection;

public class SQLExprBuilder {

    public SQLForeignKeyConstraint builderForeignKey(String index_name, Collection<String> columns,
                                                     String referenceTable, Collection<String> referenceColumn) {
        SQLForeignKeyImpl fk = createForeignKey();

        this.names(fk.getReferencingColumns(), fk, columns);
        fk.setReferencedTableName(new SQLIdentifierExpr(referenceTable));
        this.names(fk.getReferencedColumns(), fk, referenceColumn);

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
