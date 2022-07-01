package flowdesigner.jdbc.builder;

import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;

public interface SQLInsertBuilder {
    SQLInsertBuilder setTableSource(String tableName);

    SQLInsertBuilder setInsertColumn(String column);
    SQLInsertBuilder setInsertColumns(String... columns);

    SQLInsertBuilder setIfNotExists(boolean ifNotExists);

    SQLInsertBuilder addPartition(String target, String value);

    SQLInsertBuilder addPartition(SQLAssignItem partition);

    SQLInsertBuilder setQuery(String query);

    SQLInsertBuilder setQuery(SQLSelectQuery query);

    SQLInsertBuilder setQuery(SQLSelect query);

    SQLInsertStatement getSQLInsertStatement();
}
