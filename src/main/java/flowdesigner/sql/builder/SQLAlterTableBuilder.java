package flowdesigner.sql.builder;

import java.util.List;

public interface SQLAlterTableBuilder extends SQLBuilder {
//    SQLAlterTableBuilder setType(DbType dbType);

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

    SQLAlterTableBuilder addForeignKey(boolean hasConstraint, List<String> columnName,
                                       String referenceTable, List<String> referenceColumn);

    SQLAlterTableBuilder addForeignKey(boolean hasConstraint, String constraintSymbol, String index_name, List<String> columnName,
                                       String referenceTable, List<String> referenceColumn);
}
