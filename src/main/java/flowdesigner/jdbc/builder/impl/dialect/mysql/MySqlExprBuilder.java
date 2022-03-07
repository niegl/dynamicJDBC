package flowdesigner.jdbc.builder.impl.dialect.mysql;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import flowdesigner.jdbc.builder.impl.SQLExprBuilder;

import java.util.Collection;

public class MySqlExprBuilder extends SQLExprBuilder {

    @Override
    public MysqlForeignKey builderForeignKey(String index_name, Collection<String> columns,
                                             String referenceTable, Collection<String> referenceColumn) {
        MysqlForeignKey fk = new MysqlForeignKey();
        if (index_name != null) {
            fk.setIndexName(new SQLIdentifierExpr(index_name));
        }

        this.names(fk.getReferencingColumns(), fk, columns);
        fk.setReferencedTableName(new SQLIdentifierExpr(referenceTable));
        this.names(fk.getReferencedColumns(), referenceColumn);

        return fk;
    }
}
