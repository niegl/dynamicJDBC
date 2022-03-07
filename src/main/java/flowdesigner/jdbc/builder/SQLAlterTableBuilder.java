package flowdesigner.jdbc.builder;

import com.alibaba.druid.DbType;

import java.util.Collection;

public interface SQLAlterTableBuilder {
    SQLAlterTableBuilder setType(DbType dbType);

    SQLAlterTableBuilder setName(String tableName);

    SQLAlterTableBuilder setSchema(String schemaName);

    SQLAlterTableBuilder renameTable(String toName);

    SQLAlterTableBuilder addColumn(String columnName, String columnType);

    SQLAlterTableBuilder dropColomn(String columnName, String columnType);

    SQLAlterTableBuilder alterColumn(String columnName, String toColumnName, String toColumnType, String toColumnComment,
                                    String after, boolean first);

    SQLAlterTableBuilder addPrimaryKey(String columnName, boolean hasConstraint, String constraintSymbol);

    SQLAlterTableBuilder addUniqueKey(String columnName, boolean hasConstraint, String constraintSymbol);

    SQLAlterTableBuilder addUniqueIndex(String columnName, boolean hasConstraint, String constraintSymbol);

    SQLAlterTableBuilder dropPrimaryKey();

    SQLAlterTableBuilder dropForeignKey(String Name);

    SQLAlterTableBuilder dropIndex(String indexName);

    SQLAlterTableBuilder addForeignKey(boolean hasConstraint, Collection<String> columnName,
                                       String referenceTable, Collection<String> referenceColumn);

    SQLAlterTableBuilder addForeignKey(boolean hasConstraint, String constraintSymbol, String index_name, Collection<String> columnName,
                                       String referenceTable, Collection<String> referenceColumn);
}
