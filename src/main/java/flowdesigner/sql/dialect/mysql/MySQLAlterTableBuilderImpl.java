package flowdesigner.sql.dialect.mysql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import flowdesigner.sql.builder.SQLAlterTableBuilder;
import flowdesigner.sql.builder.impl.SQLAlterTableBuilderImpl;

import java.util.List;

public class MySQLAlterTableBuilderImpl extends SQLAlterTableBuilderImpl {
    public MySQLAlterTableBuilderImpl() {
        super(new MySQLExprBuilder(DbType.mysql), DbType.mysql);
    }

    public MySQLAlterTableBuilderImpl(String sql) {
        super(sql, DbType.mysql);
    }

    @Override
    protected MySQLExprBuilder getExprBuilder() {
        return (MySQLExprBuilder)exprBuilder;
    }

    @Override
    public SQLAlterTableBuilder addForeignKey(boolean hasConstraint, String constraintSymbol, String index_name, List<String> columnName,
                                              String referenceTable, List<String> referenceColumn) {
        MysqlForeignKey fk = this.getExprBuilder().builderForeignKey(index_name, columnName,referenceTable, referenceColumn);
        if (constraintSymbol != null) {
            fk.setName(constraintSymbol);
        }
        fk.setHasConstraint(hasConstraint);
        SQLAlterTableAddConstraint constraint = new SQLAlterTableAddConstraint(fk);
        stmt.addItem(constraint);

        return this;
    }
}
