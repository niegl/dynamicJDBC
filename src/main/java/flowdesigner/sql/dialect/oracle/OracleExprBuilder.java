package flowdesigner.sql.dialect.oracle;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleForeignKey;
import flowdesigner.sql.builder.impl.SQLExprBuilder;

public class OracleExprBuilder extends SQLExprBuilder {

    public OracleExprBuilder(DbType dbType) {
        super(dbType);
    }

    @Override
    protected OracleForeignKey createForeignKey() {
        return new OracleForeignKey();
    }
}
