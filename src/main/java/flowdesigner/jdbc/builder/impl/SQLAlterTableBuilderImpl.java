package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableChangeColumn;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.sun.org.omg.CORBA.AttrDescriptionSeqHelper;
import flowdesigner.jdbc.builder.SQLAlterTableBuiler;

/**
 * 该类主要用于SQLAlterTable相关的操作。
 * 如Rename Table、Alter Column、Alter Table Comment、Delete/Replace Columns，etc
 */
public class SQLAlterTableBuilderImpl implements SQLAlterTableBuiler {
    private SQLAlterTableStatement stmt;
    private DbType             dbType;

    public SQLAlterTableBuilderImpl(DbType dbType){
        this.stmt = new SQLAlterTableStatement(dbType);
        this.dbType = dbType;
    }

    /**
     * 设置原表名
     * @param tableName 原表名
     * @return
     */
    public SQLAlterTableBuiler setName(String tableName) {
        stmt.setName(new SQLIdentifierExpr(tableName));
        return this;
    }

    /**
     * Rename Table 操作
     * @param toName 变更后的表名
     * @return
     */
    public SQLAlterTableBuiler renameTable(String toName) {
        SQLAlterTableRename alterTableRename = new SQLAlterTableRename(new SQLIdentifierExpr(toName));
        stmt.addItem(alterTableRename);
        return this;
    }

    /**
     * Add Columns 操作
     * @param columnName 列名
     * @param columnType 列类型
     * @return
     */
    public SQLAlterTableBuiler addColumn(String columnName, String columnType) {
        SQLAlterTableAddColumn alterTableAddColumn = new SQLAlterTableAddColumn();
        SQLColumnDefinition column = getColumn(columnName, columnType);
        alterTableAddColumn.addColumn(column);
        stmt.addItem(alterTableAddColumn);
        return this;
    }

    /**
     * 删除列 操作
     * @param columnName 删除列名
     * @param columnType 删除列类型（hive库需要）
     * @return
     */
    public SQLAlterTableBuiler dropColomn(String columnName, String columnType) {
        SQLColumnDefinition column = getColumn(columnName, columnType);
        switch (dbType) {
            case hive:
                SQLAlterTableReplaceColumn alterTableReplaceColumn = new SQLAlterTableReplaceColumn();
                alterTableReplaceColumn.addColumn(column);
                stmt.addItem(alterTableReplaceColumn);
                break;
            default:
                SQLAlterTableDropColumnItem alterTableDropColumnItem = new SQLAlterTableDropColumnItem();
                alterTableDropColumnItem.addColumn(new SQLIdentifierExpr(columnName));
                stmt.addItem(alterTableDropColumnItem);
                break;
        }

        return this;
    }

    /**
     * 修改字段 操作
     * 语法：Change Column Name/Type/Position/Comment
     * @return
     */
    public SQLAlterTableBuiler alterColumn(String columnName,String toColumnName, String toColumnType, String toColumnComment,
                                           String after,boolean first) {
        SQLColumnDefinition column = getColumn(toColumnName, toColumnType);
        column.setComment(toColumnComment);
        switch (dbType) {
            case mysql:
                MySqlAlterTableChangeColumn item = new MySqlAlterTableChangeColumn();
                item.setColumnName(new SQLIdentifierExpr(columnName));
                item.setNewColumnDefinition(column);
                stmt.addItem(item);
                break;
            default:
                SQLAlterTableAlterColumn alterTableAlterColumn = new SQLAlterTableAlterColumn();
                alterTableAlterColumn.setOriginColumn(new SQLIdentifierExpr(columnName));

                alterTableAlterColumn.setColumn(column);
                alterTableAlterColumn.setFirst(first);
                if (after != null && !after.isEmpty()) {
                    alterTableAlterColumn.setAfter(new SQLIdentifierExpr(after));
                }
                stmt.addItem(alterTableAlterColumn);
        }

        return this;
    }

    private SQLColumnDefinition getColumn(String columnName, String columnType) {
        SQLColumnDefinition column = new SQLColumnDefinition();
        column.setName(columnName);
        column.setDataType(
                SQLParserUtils.createExprParser(columnType, dbType).parseDataType()
        );
        return column;
    }

    @Override
    public String toString() {
        return stmt.toString();
    }
}
