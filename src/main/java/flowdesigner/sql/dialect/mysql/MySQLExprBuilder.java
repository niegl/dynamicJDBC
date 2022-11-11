package flowdesigner.sql.dialect.mysql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyImpl;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import flowdesigner.sql.builder.impl.SQLExprBuilder;

import java.util.Collection;

public class MySQLExprBuilder extends SQLExprBuilder {

    public MySQLExprBuilder(DbType dbType) {
        super(dbType);
    }

    @Override
    public MysqlForeignKey builderForeignKey(String index_name, Collection<String> columns,
                                             String referenceTable, Collection<String> referenceColumn) {
        MysqlForeignKey fk = (MysqlForeignKey) createForeignKey();
        if (index_name != null) {
            fk.setIndexName(new SQLIdentifierExpr(index_name));
        }

        this.names(fk.getReferencingColumns(), fk, columns);
        fk.setReferencedTableName(new SQLIdentifierExpr(referenceTable));
        this.names(fk.getReferencedColumns(), referenceColumn);

        return fk;
    }

    protected SQLForeignKeyImpl createForeignKey() {
        return new MysqlForeignKey();
    }
}
