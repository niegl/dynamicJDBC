package flowdesigner.jdbc.builder;

public interface SQLAlterTableBuiler {
    SQLAlterTableBuiler setName(String tableName);

    SQLAlterTableBuiler renameTable(String toName);

    SQLAlterTableBuiler addColumn(String columnName, String columnType);

    SQLAlterTableBuiler dropColomn(String columnName, String columnType);

    SQLAlterTableBuiler alterColumn(String columnName, String toColumnName, String toColumnType, String toColumnComment,
                                    String after, boolean first);
}
