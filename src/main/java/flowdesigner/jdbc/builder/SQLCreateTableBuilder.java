package flowdesigner.jdbc.builder;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;

import java.util.List;

public interface SQLCreateTableBuilder extends SQLBuilder {

//    SQLCreateTableBuilder setType(DbType dbType);

    /**
     * 支持数据库：hive <p>
     * 语法：CREATE [TEMPORARY] [EXTERNAL] TABLE ...
     * @param external
     * @return
     */
    SQLCreateTableBuilder setExternal(boolean external);

    SQLCreateTableBuilder setTemporary(String temporaryType);

    SQLCreateTableBuilder setTemporary(SQLCreateTableStatement.Type type);

    SQLCreateTableBuilder setLike(String tableName);

    SQLCreateTableBuilder setSelect(String select);

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

    SQLCreateTableBuilder addColumn(String columnName, String dataType, String comment);

    SQLCreateTableBuilder addColumn(String columnName, String dataType, boolean primary, boolean unique, boolean notNull);

    SQLCreateTableBuilder addColumnAutoIncrement(String columnName, String dataType);

    SQLCreateTableBuilder addColumn(SQLColumnDefinition column);

    SQLCreateTableBuilder addPrimaryKey(String primaryKeyName, List<String> columnNames);

    SQLCreateTableBuilder addUniqueKey(String uniqueKeyName, List<String> columnNames);

    SQLCreateTableBuilder addForeignKey(String foreignKeyName, List<String> referencingColumns,
                                        String referencedTableName, List<String> referencedColumns);

    SQLCreateTableBuilder addCheckConstraint(String name, String checkExpr);

    SQLCreateTableBuilder addPartitionColumn(String columnName, String columnType);

    SQLCreateTableBuilder addPartitionColumn(String columnName, String columnType, String columnComment);

    SQLCreateTableBuilder addPartitionColumn(SQLColumnDefinition column);

    SQLCreateTableBuilder addOption(String name, String value);

    SQLCreateTableBuilder addOption(String name, SQLExpr value);

    SQLCreateTableStatement getSQLStatement();
}
