package flowdesigner.jdbc.builder;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.builder.SQLSelectBuilder;

public interface SQLSelectBuilderEx extends SQLSelectBuilder {
    SQLSelectBuilderEx setType(DbType dbType);

    public SQLSelectBuilderEx join(String joinType, String table, String alias,
                                   String exprLeft, String exprRight, String operator);
}
