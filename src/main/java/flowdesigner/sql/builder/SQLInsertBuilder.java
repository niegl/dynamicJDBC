package flowdesigner.sql.builder;

import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;

import java.util.ArrayList;

public interface SQLInsertBuilder extends SQLBuilder {
    SQLInsertBuilder setTableSource(String tableName);

    SQLInsertBuilder setInsertColumn(String column);
    SQLInsertBuilder setInsertColumns(String... columns);

    SQLInsertBuilder setOverwrite(boolean overwrite);

    /**
     * 生成insert语句中的values部分，可多次调用，生成value_list[，value_list...]
     * @param columnValues value list值
     */
    void addValueClause(ArrayList<String> columnValues);

    SQLInsertBuilder setIfNotExists(boolean ifNotExists);

    SQLInsertBuilder addPartition(String target, String value);

    SQLInsertBuilder addPartition(SQLAssignItem partition);

    SQLInsertBuilder setQuery(String query);

    SQLInsertBuilder setQuery(SQLSelectQuery query);

    SQLInsertBuilder setQuery(SQLSelect query);

    SQLInsertStatement getSQLStatement();
}
