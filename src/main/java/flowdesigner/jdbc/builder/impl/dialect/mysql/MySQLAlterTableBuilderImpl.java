package flowdesigner.jdbc.builder.impl.dialect.mysql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import flowdesigner.jdbc.builder.SQLAlterTableBuilder;
import flowdesigner.jdbc.builder.impl.SQLAlterTableBuilderImpl;

import java.util.Collection;
import java.util.List;

public class MySQLAlterTableBuilderImpl extends SQLAlterTableBuilderImpl {
    public MySQLAlterTableBuilderImpl() {
        super(DbType.mysql, new MySqlExprBuilder());
    }

    @Override
    protected MySqlExprBuilder getExprBuilder() {
        return (MySqlExprBuilder)exprBuilder;
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
