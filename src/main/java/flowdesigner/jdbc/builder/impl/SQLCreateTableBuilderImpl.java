package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.antspark.ast.AntsparkCreateTableStatement;
import com.alibaba.druid.sql.dialect.blink.ast.BlinkCreateTableStatement;
import com.alibaba.druid.sql.dialect.clickhouse.ast.ClickhouseCreateTableStatement;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2CreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsCreateTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.Token;
import flowdesigner.jdbc.builder.SQLCreateTableBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SQLCreateTableBuilderImpl extends SQLBuilderImpl implements SQLCreateTableBuilder {

    private SQLCreateTableStatement  stmt;
//    private DbType dbType;

    public SQLCreateTableBuilderImpl(@NotNull DbType dbType) {
        super(dbType);
//        this.dbType = dbType;
    }

    public SQLCreateTableBuilderImpl(String sql, DbType dbType) {
        super(dbType);
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        if (stmtList.size() == 0) {
            throw new IllegalArgumentException("not support empty-statement :" + sql);
        }

        if (stmtList.size() > 1) {
            throw new IllegalArgumentException("not support multi-statement :" + sql);
        }

        if (stmtList.get(0) instanceof SQLCreateTableStatement statement) {
            this.stmt = statement;
        }
//        this.dbType = dbType;
    }

    public SQLCreateTableBuilderImpl(@Nullable SQLCreateTableStatement stmt, @NotNull DbType dbType) {
        super(dbType);
        this.stmt = stmt;
//        this.dbType = dbType;
    }

//    @Override
//    public SQLCreateTableBuilder setType(DbType dbType) {
//        this.dbType = dbType;
//        return this;
//    }

    /**
     * 支持数据库：hive <p>
     * 语法：CREATE [TEMPORARY] [EXTERNAL] TABLE ...
     *
     * @param external
     * @return
     */
    @Override
    public SQLCreateTableBuilder setExternal(boolean external) {
        throw new UnsupportedOperationException();
    }

    /**
     * 创建临时表
     * @param temporaryType
     *          GLOBAL_TEMPORARY,
     *         LOCAL_TEMPORARY,
     *         TEMPORARY,
     *         SHADOW;
     * @return
     */
    @Override
    public SQLCreateTableBuilder setTemporary(String temporaryType) throws IllegalArgumentException {
        SQLCreateTableStatement.Type type;
        type = SQLCreateTableStatement.Type.valueOf(temporaryType);
        return setTemporary(type);
    }

    @Override
    public SQLCreateTableBuilder setTemporary(SQLCreateTableStatement.Type type) {
        SQLCreateTableStatement create = getSQLStatement();
        create.setType(type);

        return this;
    }

    /**
     * 支持like。语法：CREATE [TEMPORARY] [EXTERNAL] TABLE [IF NOT EXISTS] [db_name.]table_name
     *   LIKE existing_table_or_view_name
     * @param tableName
     * @return
     */
    @Override
    public SQLCreateTableBuilder setLike(String tableName) {
        SQLCreateTableStatement create = getSQLStatement();
        SQLExpr name = SQLUtils.toSQLExpr(tableName, this.dbType);
        if (name instanceof SQLName) {
            create.setLike((SQLName) name);
        } else if (name instanceof SQLExprTableSource) {
            create.setLike( (SQLExprTableSource) name);
        }
        return this;
    }

    /**
     * 支持 create ... as select 语法
     * @param select select a,b from t
     * @return
     */
    @Override
    public SQLCreateTableBuilder setSelect(String select) {
        SQLCreateTableStatement create = getSQLStatement();
        SQLExpr query = SQLUtils.toSQLExpr(select, this.dbType);
        if (query instanceof SQLQueryExpr) {
            stmt.setSelect(((SQLQueryExpr) query).getSubQuery());
        }

        return this;
    }

    @Override
    public SQLCreateTableBuilder setName(String name) {
        SQLCreateTableStatement create = getSQLStatement();
        create.setTableName(name);
        return this;
    }

    /**
     * 注意：需要先调用setName以后在调用该方法.
     * @param name
     * @return
     */
    @Override
    public SQLCreateTableBuilder setSchema(String name) {
        SQLCreateTableStatement create = getSQLStatement();
        create.setSchema(name);
        return this;
    }

    @Override
    public SQLCreateTableBuilder setIfNotExiists(boolean ifNotExists) {
        SQLCreateTableStatement create = getSQLStatement();
        create.setIfNotExiists(ifNotExists);

        return this;
    }


    @Override
    public SQLCreateTableBuilder setBuckets(int buckets) {
        SQLCreateTableStatement create = getSQLStatement();
        create.setBuckets(buckets);

        return this;
    }

    @Override
    public SQLCreateTableBuilder setShards(int shards) {
        SQLCreateTableStatement create = getSQLStatement();
        create.setShards(shards);

        return this;
    }

    @Override
    public SQLCreateTableBuilder setComment(String comment) {
        SQLCharExpr expr;
        if (comment == null) {
            expr = null;
        } else {
            expr = new SQLCharExpr(comment);
        }
        setComment(expr);

        return this;
    }

    @Override
    public SQLCreateTableBuilder setComment(SQLExpr comment) {
        if (comment == null) {
            throw new IllegalArgumentException();
        }
        SQLCreateTableStatement create = getSQLStatement();
        create.setComment(comment);

        return this;
    }

    /**
     * 添加通用列
     * @param columnName 列名
     * @param dataType 列类型
     * @return
     */
    @Override
    public SQLCreateTableBuilder addColumn(String columnName, String dataType) {

        addColumn(columnName, dataType, false, false, false);
        return this;
    }

    @Override
    public SQLCreateTableBuilder addColumn(String columnName, String dataType, String comment) {

        SQLColumnDefinition column = getColumn(columnName, dataType, false, false, false, false);
        column.setComment(comment);
        addColumn(column);
        return this;
    }

    /**
     * 添加主键列.如 Id_P int NOT NULL PRIMARY KEY,
     * @param columnName 列名
     * @param dataType 列类型
     * @param primary 是否主键列
     * @param notNull 是否为空
     * @return
     */
    @Override
    public SQLCreateTableBuilder addColumn(String columnName, String dataType, boolean primary, boolean unique, boolean notNull) {

        SQLColumnDefinition column = getColumn(columnName, dataType, primary, unique, notNull, false);
        addColumn(column);
        return this;
    }

    private SQLColumnDefinition getColumn(String columnName, String dataType, boolean primary, boolean unique, boolean notNull, boolean autoIncrement) {
        SQLColumnDefinition column = new SQLColumnDefinition();
        column.setDbType(dbType);
        column.setName(columnName);
        column.setDataType(
                SQLParserUtils.createExprParser(dataType, dbType).parseDataType()
        );

        if (notNull) {
            SQLNotNullConstraint nullConstraint = new SQLNotNullConstraint();
            column.addConstraint(nullConstraint);
        }

        if (primary) {
            column.addConstraint(new SQLColumnPrimaryKey());
            if (autoIncrement) {
                column.setAutoIncrement(true);
            }
        } else if (unique) {
            column.addConstraint(new SQLColumnUniqueKey());
        }

        return column;
    }

    /**
     *
     * @param columnName
     * @param dataType
     *        通过给字段添加 AUTO_INCREMENT 属性来实现主键自增长。语法格式如下：
     *          * 字段名 数据类型 AUTO_INCREMENT
     *          *
     *          * 默认情况下，AUTO_INCREMENT 的初始值是 1，每新增一条记录，字段值自动加 1。
     *          * 一个表中只能有一个字段使用 AUTO_INCREMENT 约束，且该字段必须有唯一索引，以避免序号重复（即为主键或主键的一部分）。
     *          * AUTO_INCREMENT 约束的字段必须具备 NOT NULL 属性。
     *          * AUTO_INCREMENT 约束的字段只能是整数类型（TINYINT、SMALLINT、INT、BIGINT 等）。
     *          * AUTO_INCREMENT 约束字段的最大值受该字段的数据类型约束，如果达到上限，AUTO_INCREMENT 就会失效。
     * @return
     */
    @Override
    public SQLCreateTableBuilder addColumnAutoIncrement(String columnName, String dataType) {
        SQLColumnDefinition column = getColumn(columnName, dataType, true, false, false, true);
        addColumn(column);
        return this;
    }

    @Override
    public SQLCreateTableBuilder addColumn(SQLColumnDefinition column) {
        if (column == null) {
            throw new IllegalArgumentException();
        }
        SQLCreateTableStatement create = getSQLStatement();
        create.addColumn(column);

        return this;
    }

    /**
     * 添加主键约束。适用于多个主键列，如 CONSTRAINT pk_PersonID PRIMARY KEY (Id_P,LastName)
     * @param primaryKeyName 主键名,可以为空。部分数据库如HIVE不指定主键约束名称
     * @param columnNames 用于生成主键的列（一般为多个）
     * @return
     */
    @Override
    public SQLCreateTableBuilder addPrimaryKey(@Nullable String primaryKeyName, List<String> columnNames) {
        SQLConstraint constraint = null;
        boolean hasConstaint = false;
        SQLName name = null;

        if (primaryKeyName != null) {
            name = new SQLIdentifierExpr(primaryKeyName);
        }

        constraint = createConstraint(Token.PRIMARY, columnNames, hasConstaint, name);

        SQLCreateTableStatement create = getSQLStatement();
        constraint.setParent(create);
        create.getTableElementList().add((SQLTableElement) constraint);

        return this;
    }

    private SQLConstraint createConstraint(Token token, List<String> columnNames, boolean hasConstraint, SQLName name) {

        SQLConstraint constraint = switch (token) {
            case CHECK -> this.createCheck(columnNames);
            case PRIMARY -> this.createPrimaryKey(columnNames, hasConstraint, name);
            case KEY, UNIQUE -> this.createUnique(columnNames);
            case DEFAULT -> this.createDefault(columnNames);
            case FOREIGN -> this.createForeignKey(columnNames);
            default -> throw new ParserException("TODO : " + token);
        };

        return (SQLConstraint) constraint;
    }

    private SQLConstraint createForeignKey(List<String> columnNames) {
        return null;
    }

    private SQLConstraint createDefault(List<String> columnNames) {
        return null;
    }

    private SQLConstraint createUnique(List<String> columnNames) {
        return null;
    }

    private SQLConstraint createCheck(List<String> columnNames) {
        return null;
    }

    @NotNull
    protected SQLConstraint createPrimaryKey(List<String> columnNames, boolean hasConstraint, SQLName name) {
        SQLPrimaryKeyImpl pk = new SQLPrimaryKeyImpl();

        for (String columnName : columnNames) {
            SQLSelectOrderByItem item = new SQLSelectOrderByItem();
            item.setExpr((SQLExpr)new SQLIdentifierExpr(columnName));

            item.setParent(pk);
            pk.getColumns().add(item);
        }
        pk.setName(name);

        return pk;
    }

    @Override
    public SQLCreateTableBuilder addUniqueKey(String uniqueKeyName, List<String> columnNames) {
        SQLTableConstraint constraint = null;
        boolean hasConstaint = true;
        SQLName name = null;

        if (uniqueKeyName != null) {
            name = new SQLIdentifierExpr(uniqueKeyName);
        }

        switch (dbType) {
            case mysql:
                MySqlUnique unique = new MySqlUnique();
                if (name != null) {
                    unique.setName(name);
                }
                buildIndex(unique.getIndexDefinition(),Token.UNIQUE.name,"", Token.KEY.name,columnNames);
                unique.setHasConstraint(hasConstaint);
                constraint = unique;
            default:
                break;
        }

        SQLCreateTableStatement create = getSQLStatement();
        constraint.setParent(create);

        create.getTableElementList().add(constraint);

        return this;
    }

    /**
     * 添加外键。
     * 两种类型外键：1、foreignKeyName=null为单纯外键；2、foreignKeyName为外键键值时为外键约束。
     * @param foreignKeyName 1、foreignKeyName=null为单纯外键；2、foreignKeyName为外键键值时为外键约束。
     * @param referencingColumns 源表外键
     * @param referencedTableName 目标表名
     * @param referencedColumns 目标表参考键（一般为主键）
     * @return
     */
    @Override
    public SQLCreateTableBuilder addForeignKey(String foreignKeyName, List<String> referencingColumns,
                                               String referencedTableName, List<String> referencedColumns) {
        SQLTableConstraint constraint = null;
        boolean hasConstaint = false;
        SQLName name = null;

        if (foreignKeyName != null) {
            name = new SQLIdentifierExpr(foreignKeyName);
            hasConstaint = true;
        }

        switch (dbType) {
            case mysql:
                MysqlForeignKey pk = new MysqlForeignKey();
                buildForeignKey(pk,referencingColumns,referencedTableName,referencedColumns);
                if (name != null) {
                    pk.setName(name);
                }
                pk.setHasConstraint(hasConstaint);
                constraint = pk;
            default:
                break;
        }

        SQLCreateTableStatement create = getSQLStatement();
        assert constraint != null;
        constraint.setParent(create);

        create.getTableElementList().add(constraint);

        return this;
    }

    protected void buildIndex(SQLIndexDefinition indexDefinition, String token, String globalOrLocal, String indexOrKey, List<String> columnNames) {
        if (token.equalsIgnoreCase("FULLTEXT")
                || token.equalsIgnoreCase("UNIQUE")
                || token.equalsIgnoreCase("PRIMARY")
                || token.equalsIgnoreCase("SPATIAL")
                || token.equalsIgnoreCase("CLUSTERED")
                || token.equalsIgnoreCase("CLUSTERING")
                || token.equalsIgnoreCase("ANN")) {
            indexDefinition.setType(token);
        }

        if (globalOrLocal.equalsIgnoreCase("GLOBAL")) {
            indexDefinition.setGlobal(true);
        } else if (globalOrLocal.equalsIgnoreCase("LOCAL")) {
            indexDefinition.setLocal(true);
        }

        if (indexOrKey.equalsIgnoreCase("INDEX")) {
            indexDefinition.setIndex(true);

        } else if (indexOrKey.equalsIgnoreCase("KEY")) {
            indexDefinition.setKey(true);
        }

        buildIndexRest(indexDefinition,indexDefinition.getParent(),columnNames);

    }

    private void buildIndexRest(SQLIndex idx, SQLObject parent, List<String> columnNames) {

        for (String columnName :
                columnNames) {
            SQLSelectOrderByItem selectOrderByItem = buildSelectOrderByItem(columnName);
            selectOrderByItem.setParent(parent);
            idx.getColumns().add(selectOrderByItem);
        }
    }

    private SQLSelectOrderByItem buildSelectOrderByItem(String columnName) {
        SQLSelectOrderByItem item = new SQLSelectOrderByItem();
        item.setExpr(new SQLIdentifierExpr(columnName));

        return item;
    }

    public SQLForeignKeyImpl buildForeignKey(SQLForeignKeyImpl fk, List<String> referencingColumns,
                                             String referencedTableName,List<String> referencedColumns) {
//        MysqlForeignKey fk = new MysqlForeignKey();

//        if (lexer.token() != Token.LPAREN) {
//            SQLName indexName = name();
//            fk.setIndexName(indexName);
//        }
        for (String col :
                referencingColumns) {
            SQLName name = new SQLIdentifierExpr(col);
            name.setParent(fk);
            fk.getReferencingColumns().add(new SQLIdentifierExpr(col));
        }

        fk.setReferencedTableName(new SQLIdentifierExpr(referencedTableName));

        for (String col :
                referencedColumns) {
            SQLName name = new SQLIdentifierExpr(col);
            name.setParent(fk);
            fk.getReferencedColumns().add(new SQLIdentifierExpr(col));
        }

        return fk;
    }

    @Override
    public SQLCreateTableBuilder addPartitionColumn(String columnName) {
        SQLColumnDefinition column = new SQLColumnDefinition();
        column.setDbType(dbType);
        column.setName(columnName);
        addPartitionColumn(column);

        return this;
    }

    @Override
    public SQLCreateTableBuilder addPartitionColumn(String columnName, String columnComment) {
        SQLColumnDefinition column = new SQLColumnDefinition();
        column.setDbType(dbType);
        column.setName(columnName);
        column.setComment(columnComment);
        addPartitionColumn(column);

        return this;
    }

    @Override
    public SQLCreateTableBuilder addPartitionColumn(SQLColumnDefinition column) {
        if (column == null) {
            throw new IllegalArgumentException();
        }
        SQLCreateTableStatement create = getSQLStatement();
        create.addPartitionColumn(column);

        return this;
    }

    @Override
    public SQLCreateTableBuilder addOption(String name, String value) {
        SQLCharExpr expr = new SQLCharExpr(value);
        addOption(name,expr);

        return this;
    }

    @Override
    public SQLCreateTableBuilder addOption(String name, SQLExpr value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        SQLCreateTableStatement create = getSQLStatement();
        create.addOption(name,value);

        return this;
    }

    @Override
    public SQLCreateTableStatement getSQLStatement() {
        if (stmt == null) {
            stmt = createSQLCreateTableStatement();
        }
        return stmt;
    }

    private SQLCreateTableStatement createSQLCreateTableStatement() {
        return switch (dbType) {
            case antspark -> new AntsparkCreateTableStatement();
            case blink -> new BlinkCreateTableStatement();
            case clickhouse -> new ClickhouseCreateTableStatement();
            case oracle -> new OracleCreateTableStatement();
            case mysql -> new MySqlCreateTableStatement();
            case db2 -> new DB2CreateTableStatement();
            case hive -> new HiveCreateTableStatement();
            case odps -> new OdpsCreateTableStatement();
            default -> new SQLCreateTableStatement();
        };
    }

    public String toString() {
        return SQLUtils.toSQLString(stmt, dbType);
    }
}
