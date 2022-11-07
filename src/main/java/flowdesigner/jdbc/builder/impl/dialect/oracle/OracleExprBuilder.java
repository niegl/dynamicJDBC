package flowdesigner.jdbc.builder.impl.dialect.oracle;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleForeignKey;
import flowdesigner.jdbc.builder.impl.SQLAlterTableBuilderImpl;
import flowdesigner.jdbc.builder.impl.SQLExprBuilder;

public class OracleExprBuilder extends SQLExprBuilder {

    public OracleExprBuilder(DbType dbType) {
        super(dbType);
    }

    @Override
    protected OracleForeignKey createForeignKey() {
        return new OracleForeignKey();
    }
}
