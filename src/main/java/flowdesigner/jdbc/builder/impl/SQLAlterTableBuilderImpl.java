package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableChangeColumn;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.Token;
import flowdesigner.jdbc.builder.SQLAlterTableBuilder;

import java.util.List;

/**
 * 该类主要用于SQLAlterTable相关的操作。
 * 如Rename Table、Alter Column、Alter Table Comment、Delete/Replace Columns，etc
 */
public class SQLAlterTableBuilderImpl extends SQLBuilderImpl implements SQLAlterTableBuilder {

    protected SQLAlterTableStatement stmt;
//    protected DbType             dbType;
//    protected SQLExprBuilder     exprBuilder;

    public SQLAlterTableBuilderImpl(DbType dbType) {
        super(dbType);
        exprBuilder = new SQLExprBuilder();
    }
    public SQLAlterTableBuilderImpl(String sql, DbType dbType) {
        this(dbType);

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        if (stmtList.isEmpty()) {
            throw new IllegalArgumentException("not support empty-statement :" + sql);
        } else if (stmtList.size() > 1) {
            throw new IllegalArgumentException("not support multi-statement :" + sql);
        } else {
            SQLAlterTableStatement stmt = (SQLAlterTableStatement)stmtList.get(0);
            this.stmt = stmt;
        }
    }
    public SQLAlterTableBuilderImpl(SQLAlterTableStatement stmt, DbType dbType){
        this(dbType);
        this.stmt = stmt;
    }
    public SQLAlterTableBuilderImpl(DbType dbType, SQLExprBuilder exprBuilder) {
        this(dbType);
        this.exprBuilder = exprBuilder;
    }
    public SQLAlterTableBuilderImpl(String sql, DbType dbType, SQLExprBuilder exprBuilder) {
        this(sql, dbType);
        this.exprBuilder = exprBuilder;
    }

    protected SQLExprBuilder getExprBuilder() {
        return exprBuilder;
    }

//    @Override
//    public SQLAlterTableBuilder setType(DbType dbType) {
//        this.dbType = dbType;
//        return this;
//    }
    /**
     * 设置原表名
     * @param tableName 原表名
     * @return
     */
    @Override
    public SQLAlterTableBuilder setName(String tableName) {
        SQLAlterTableStatement statement = getSQLStatement();
        statement.setName(new SQLIdentifierExpr(tableName));
        return this;
    }

    /**
     * 设置数据库名
     */
    @Override
    public SQLAlterTableBuilder setSchema(String schemaName) {
        if (schemaName == null) {
            return null;
        }
        SQLAlterTableStatement statement = getSQLStatement();
        SQLName name = statement.getName();
        if (name == null) {
            return null;
        }
        if (name instanceof SQLIdentifierExpr) {
            SQLPropertyExpr propertyExpr = new SQLPropertyExpr();
            propertyExpr.setName(((SQLIdentifierExpr) name).getName());
            propertyExpr.setOwner(schemaName);
        } else if (name instanceof SQLPropertyExpr) {
            ((SQLPropertyExpr)name).setOwner(schemaName);
        }

        return this;
    }

    /**
     * Rename Table 操作
     * @param toName 变更后的表名
     * @return
     */
    @Override
    public SQLAlterTableBuilder renameTable(String toName) {
        SQLAlterTableStatement statement = getSQLStatement();
        SQLAlterTableRename alterTableRename = new SQLAlterTableRename(new SQLIdentifierExpr(toName));
        statement.addItem(alterTableRename);
        return this;
    }

    /**
     * Add Columns 操作
     * @param columnName 列名
     * @param columnType 列类型
     * @return
     */
    @Override
    public SQLAlterTableBuilder addColumn(String columnName, String columnType) {
        SQLAlterTableStatement statement = getSQLStatement();
        SQLAlterTableAddColumn alterTableAddColumn = new SQLAlterTableAddColumn();
        SQLColumnDefinition column = getColumn(columnName, columnType);
        alterTableAddColumn.addColumn(column);
        statement.addItem(alterTableAddColumn);
        return this;
    }

    /**
     * 删除列 操作
     * @param columnName 删除列名
     * @param columnType 删除列类型（hive库需要）
     * @return
     */
    @Override
    public SQLAlterTableBuilder dropColomn(String columnName, String columnType) {
        SQLAlterTableStatement statement = getSQLStatement();
        SQLColumnDefinition column = getColumn(columnName, columnType);
        switch (dbType) {
            case hive:
                SQLAlterTableReplaceColumn alterTableReplaceColumn = new SQLAlterTableReplaceColumn();
                alterTableReplaceColumn.addColumn(column);
                statement.addItem(alterTableReplaceColumn);
                break;
            default:
                SQLAlterTableDropColumnItem alterTableDropColumnItem = new SQLAlterTableDropColumnItem();
                alterTableDropColumnItem.addColumn(new SQLIdentifierExpr(columnName));
                statement.addItem(alterTableDropColumnItem);
                break;
        }

        return this;
    }

    /**
     * 修改字段 操作
     * 语法：Change Column Name/Type/Position/Comment
     * @return
     */
    @Override
    public SQLAlterTableBuilder alterColumn(String columnName, String toColumnName, String toColumnType, String toColumnComment,
                                           String after, boolean first) {
        SQLAlterTableStatement statement = getSQLStatement();
        SQLColumnDefinition column = getColumn(toColumnName, toColumnType);
        column.setComment(toColumnComment);
        switch (dbType) {
            case mysql:
                MySqlAlterTableChangeColumn item = new MySqlAlterTableChangeColumn();
                item.setColumnName(new SQLIdentifierExpr(columnName));
                item.setNewColumnDefinition(column);
                statement.addItem(item);
                break;
            default:
                SQLAlterTableAlterColumn alterTableAlterColumn = new SQLAlterTableAlterColumn();
                alterTableAlterColumn.setOriginColumn(new SQLIdentifierExpr(columnName));

                alterTableAlterColumn.setColumn(column);
                alterTableAlterColumn.setFirst(first);
                if (after != null && !after.isEmpty()) {
                    alterTableAlterColumn.setAfter(new SQLIdentifierExpr(after));
                }
                statement.addItem(alterTableAlterColumn);
        }

        return this;
    }

    private SQLColumnDefinition getColumn(String columnName, String columnType) {
        SQLColumnDefinition column = new SQLColumnDefinition();
        column.setDbType(dbType);
        column.setName(columnName);
        column.setDataType(
                SQLParserUtils.createExprParser(columnType, dbType).parseDataType()
        );
        return column;
    }

    /**
     *      * 在修改表时添加主键约束.
     *      * 1、mysql语法：ALTER TABLE <数据表名>
     *      *                         // ADD [CONSTRAINT [symbol]] PRIMARY KEY [index_type] (key_part,...) [index_option] ...
     *      *                         // ADD [CONSTRAINT [symbol]] UNIQUE [INDEX|KEY] [index_name] [index_type] (key_part,...) [index_option] ...
     *      *                         // ADD [CONSTRAINT [symbol]] FOREIGN KEY [index_name] (col_name,...) reference_definition
     * @param columnName col_name
     * @param hasConstraint 是否有CONSTRAINT关键字
     * @param constraintSymbol 是否有[symbol]
     * @return
     */
    @Override
    public SQLAlterTableBuilder addPrimaryKey(String columnName, boolean hasConstraint, String constraintSymbol) {

        return addConstraint(columnName, hasConstraint, constraintSymbol, Token.PRIMARY.name, true);
    }

    @Override
    public SQLAlterTableBuilder addUniqueKey(String columnName, boolean hasConstraint, String constraintSymbol) {

        return addConstraint(columnName, hasConstraint, constraintSymbol, Token.UNIQUE.name, true);
    }

    @Override
    public SQLAlterTableBuilder addUniqueIndex(String columnName, boolean hasConstraint, String constraintSymbol) {

        return addConstraint(columnName, hasConstraint, constraintSymbol, Token.UNIQUE.name, false);
    }

    /**
     * // ADD [CONSTRAINT [symbol]] PRIMARY KEY [index_type] (key_part,...) [index_option] ...
     * // ADD [CONSTRAINT [symbol]] UNIQUE [INDEX|KEY] [index_name] [index_type] (key_part,...) [index_option] ...
     * // ADD [CONSTRAINT [symbol]] FOREIGN KEY [index_name] (col_name,...) reference_definition
     * @param columnName
     * @param hasConstraint
     * @param constraintSymbol
     * @return
     */
    private SQLAlterTableBuilder addConstraint(String columnName, boolean hasConstraint, String constraintSymbol, String type, boolean INDEXKEY) {
        SQLAlterTableStatement statement = getSQLStatement();

        SQLUnique pk = getPrimaryKey();
        if (constraintSymbol != null) {
            pk.setName(constraintSymbol);
        }
        pk.getIndexDefinition().setHasConstraint(hasConstraint);
        if (constraintSymbol != null) {
            pk.getIndexDefinition().setSymbol(new SQLIdentifierExpr(constraintSymbol));
        }

        pk.getIndexDefinition().setType(type);
        if (INDEXKEY) {
            pk.getIndexDefinition().setKey(true);
        } else {
            pk.getIndexDefinition().setIndex(true);
        }

        if (columnName != null) {
            SQLSelectOrderByItem item = new SQLSelectOrderByItem();
            item.setExpr(new SQLIdentifierExpr(columnName));
            item.setParent(pk);
            pk.getIndexDefinition().getColumns().add(item);
        }

        SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(pk);
        statement.addItem(item);

        return this;
    }

    /**
     * 适配不同的数据库下的PRIMARY KEY
     * @return
     */
    private SQLUnique getPrimaryKey() {
        switch (dbType) {
            case mysql:
                return new MySqlPrimaryKey();
            default:
                return new SQLPrimaryKeyImpl();
        }
    }

    /**
     * 删除主键
     * @return
     */
    @Override
    public SQLAlterTableBuilder dropPrimaryKey() {

        SQLAlterTableStatement statement = getSQLStatement();

        SQLAlterTableDropPrimaryKey item = new SQLAlterTableDropPrimaryKey();
        statement.addItem(item);

        return this;
    }

    /**
     * ALTER TABLE <表名> DROP FOREIGN KEY <外键约束名>;
     * @param Name 外键约束名
     * @return
     */
    @Override
    public SQLAlterTableBuilder dropForeignKey(String Name) {

        SQLAlterTableStatement statement = getSQLStatement();

        SQLAlterTableDropForeignKey item = new SQLAlterTableDropForeignKey();
        item.setIndexName(new SQLIdentifierExpr(Name));
        statement.addItem(item);

        return this;
    }

    /**
     * 删除索引/[mysql] 删除唯一约束unique
     * @return
     */
    @Override
    public SQLAlterTableBuilder dropIndex(String indexName) {

        SQLAlterTableStatement statement = getSQLStatement();

        SQLAlterTableDropIndex item = new SQLAlterTableDropIndex();
        item.setIndexName(new SQLIdentifierExpr(indexName));
        statement.addItem(item);

        return this;
    }

    public SQLAlterTableStatement getSQLStatement() {
        if (stmt == null) {
            stmt = createSQLAlterTableStatement();
        }
        return stmt;
    }

    private SQLAlterTableStatement createSQLAlterTableStatement() {

        if (stmt == null) {
            stmt = new SQLAlterTableStatement(dbType);
        }
        return stmt;
    }

//    @Override
//    public String toString() {
//        return SQLUtils.toSQLString(stmt, dbType);
//    }

    /**
     * ALTER TABLE <数据表名> ADD CONSTRAINT <外键名> FOREIGN KEY(<列名>) REFERENCES <主表名> (<列名>);
     *  ADD [CONSTRAINT [symbol]] PRIMARY KEY [index_type] (key_part,...) [index_option] ...
     *  ADD [CONSTRAINT [symbol]] UNIQUE [INDEX|KEY] [index_name] [index_type] (key_part,...) [index_option] ...
     *  ADD [CONSTRAINT [symbol]] FOREIGN KEY [index_name] (col_name,...) reference_definition
     * @param hasConstraint 是否有CONSTRAINT关键字
     * @param constraintSymbol 外键名
     * @param index_name 索引名称
     * @param columnName 列名
     * @param referenceTable 主表名
     * @param referenceColumn 主表列名
     * @return
     */
    @Override
    public SQLAlterTableBuilder addForeignKey(boolean hasConstraint, String constraintSymbol, String index_name, List<String> columnName,
                                              String referenceTable, List<String> referenceColumn) {
        SQLForeignKeyConstraint fk = this.getExprBuilder().builderForeignKey(index_name, columnName,referenceTable, referenceColumn);
        if (constraintSymbol != null) {
            fk.setName(new SQLIdentifierExpr(constraintSymbol));
        }
//        fk.setHasConstraint(hasConstraint);
        SQLAlterTableAddConstraint constraint = new SQLAlterTableAddConstraint(fk);
        stmt.addItem(constraint);
        return  this;
    }

    @Override
    public SQLAlterTableBuilder addForeignKey(boolean hasConstraint, List<String> columnName, String referenceTable, List<String> referenceColumn) {

        return addForeignKey(hasConstraint, null, null, columnName, referenceTable, referenceColumn);
    }
}
