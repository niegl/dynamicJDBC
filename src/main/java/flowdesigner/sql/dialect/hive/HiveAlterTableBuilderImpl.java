package flowdesigner.sql.dialect.hive;

import com.alibaba.druid.DbType;
import flowdesigner.sql.builder.impl.SQLAlterTableBuilderImpl;

public class HiveAlterTableBuilderImpl extends SQLAlterTableBuilderImpl {
    public HiveAlterTableBuilderImpl() {
        super(DbType.hive);
        exprBuilder.setDISABLE_NOVALIDATE(true);
    }
    public HiveAlterTableBuilderImpl(String sql) {
        super(sql, DbType.hive);
        exprBuilder.setDISABLE_NOVALIDATE(true);
    }
}
