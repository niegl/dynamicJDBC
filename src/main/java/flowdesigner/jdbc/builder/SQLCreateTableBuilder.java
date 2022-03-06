package flowdesigner.jdbc.builder;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;

import java.util.List;

public interface SQLCreateTableBuilder {

    SQLCreateTableBuilder setType(DbType dbType);

    SQLCreateTableBuilder setName(String name);

    SQLCreateTableBuilder setSchema(String name);

    SQLCreateTableBuilder setIfNotExiists(boolean ifNotExists);

    default SQLCreateTableBuilder setLike(SQLName like) {
        throw new UnsupportedOperationException();
    }

    SQLCreateTableBuilder setBuckets(int buckets);

    SQLCreateTableBuilder setShards(int shards);

    SQLCreateTableBuilder setComment(String comment);

    SQLCreateTableBuilder setComment(SQLExpr comment);

    SQLCreateTableBuilder addColumn(String columnName, String dataType);

    SQLCreateTableBuilder addColumn(String columnName, String dataType, boolean primary, boolean unique, boolean notNull);

    SQLCreateTableBuilder addColumnAutoIncrement(String columnName, String dataType);

    SQLCreateTableBuilder addColumn(SQLColumnDefinition column);

    SQLCreateTableBuilder addPrimaryKey(String primaryKeyName, List<String> columnNames);

    SQLCreateTableBuilder addUniqueKey(String uniqueKeyName, List<String> columnNames);

    SQLCreateTableBuilder addForeignKey(String foreignKeyName, List<String> referencingColumns,
                                        String referencedTableName, List<String> referencedColumns);

    SQLCreateTableBuilder addPartitionColumn(String columnName);

    SQLCreateTableBuilder addPartitionColumn(String columnName, String columnComment);

    SQLCreateTableBuilder addPartitionColumn(SQLColumnDefinition column);

    SQLCreateTableBuilder addOption(String name, String value);

    SQLCreateTableBuilder addOption(String name, SQLExpr value);
}
