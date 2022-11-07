package flowdesigner.jdbc.builder.impl.dialect.oracle;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddConstraint;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleConstraint;
import flowdesigner.jdbc.builder.SQLAlterTableBuilder;
import flowdesigner.jdbc.builder.impl.SQLAlterTableBuilderImpl;

import java.util.List;

public class OracleAlterTableBuilderImpl extends SQLAlterTableBuilderImpl {
    public OracleAlterTableBuilderImpl() {
        super(new OracleExprBuilder(DbType.oracle), DbType.oracle);
    }
    public OracleAlterTableBuilderImpl(String sql) {
        super(sql, DbType.oracle);
    }

    @Override
    public SQLAlterTableBuilder addForeignKey(boolean hasConstraint, String constraintSymbol, String index_name, List<String> columnName,
                                              String referenceTable, List<String> referenceColumn) {
        OracleConstraint constraint = (OracleConstraint)this.getExprBuilder().builderForeignKey(index_name, columnName,referenceTable, referenceColumn);
        if (constraint != null) {
            constraint.setName(new SQLIdentifierExpr(constraintSymbol));
        }
        SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint();
        constraint.setParent(item);
        item.setParent(stmt);
        item.setConstraint(constraint);
        stmt.addItem(item);

        return this;
    }
}
