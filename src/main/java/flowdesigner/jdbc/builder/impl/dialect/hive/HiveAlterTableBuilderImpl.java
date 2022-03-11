package flowdesigner.jdbc.builder.impl.dialect.hive;

import com.alibaba.druid.DbType;
import flowdesigner.jdbc.builder.impl.SQLAlterTableBuilderImpl;

public class HiveAlterTableBuilderImpl extends SQLAlterTableBuilderImpl {
    public HiveAlterTableBuilderImpl() {
        super(DbType.hive);
        exprBuilder.setDISABLE_NOVALIDATE(true);
    }
}
