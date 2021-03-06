package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2CreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsCreateTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.Token;
import flowdesigner.jdbc.builder.SQLCreateTableBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SQLCreateTableBuilderImpl implements SQLCreateTableBuilder {

    private @Nullable SQLCreateTableStatement  stmt;
    private DbType dbType;

    public SQLCreateTableBuilderImpl(){}
    public SQLCreateTableBuilderImpl(@NotNull DbType dbType){
        this.dbType = dbType;
    }

    public SQLCreateTableBuilderImpl(String sql, DbType dbType){
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        if (stmtList.size() == 0) {
            throw new IllegalArgumentException("not support empty-statement :" + sql);
        }

        if (stmtList.size() > 1) {
            throw new IllegalArgumentException("not support multi-statement :" + sql);
        }

        SQLCreateTableStatement stmt = (SQLCreateTableStatement) stmtList.get(0);
        this.stmt = stmt;
        this.dbType = dbType;
    }

    public SQLCreateTableBuilderImpl(@Nullable SQLCreateTableStatement stmt, @NotNull DbType dbType){
        this.stmt = stmt;
        this.dbType = dbType;
    }

    @Override
    public SQLCreateTableBuilder setType(DbType dbType) {
        this.dbType = dbType;
        return this;
    }

    /**
     * ???????????????
     * @param temporaryType
     *          GLOBAL_TEMPORARY,
     *         LOCAL_TEMPORARY,
     *         TEMPORARY,
     *         SHADOW;
     * @return
     */
    @Override
    public SQLCreateTableBuilder setTemporary(String temporaryType) {
        SQLCreateTableStatement create = getSQLCreateTableStatement();
        create.setType(SQLCreateTableStatement.Type.GLOBAL_TEMPORARY);
        return this;
    }

    /**
     * ??????like????????????CREATE [TEMPORARY] [EXTERNAL] TABLE [IF NOT EXISTS] [db_name.]table_name
     *   LIKE existing_table_or_view_name
     * @param tableName
     * @return
     */
    @Override
    public SQLCreateTableBuilder setLike(String tableName) {
        SQLCreateTableStatement create = getSQLCreateTableStatement();
        SQLExpr name = SQLUtils.toSQLExpr(tableName, this.dbType);
        if (name instanceof SQLName) {
            create.setLike((SQLName) name);
        } else if (name instanceof SQLExprTableSource) {
            create.setLike( (SQLExprTableSource) name);
        }
        return this;
    }

    /**
     * ?????? create ... as select ??????
     * @param select select a,b from t
     * @return
     */
    @Override
    public SQLCreateTableBuilder setSelect(String select) {
        SQLCreateTableStatement create = getSQLCreateTableStatement();
        SQLExpr query = SQLUtils.toSQLExpr(select, this.dbType);
        if (query instanceof SQLQueryExpr) {
            stmt.setSelect(((SQLQueryExpr) query).getSubQuery());
        }

        return this;
    }

    @Override
    public SQLCreateTableBuilder setName(String name) {
        SQLCreateTableStatement create = getSQLCreateTableStatement();
        create.setTableName(name);
        return this;
    }

    /**
     * ????????????????????????setName????????????????????????.
     * @param name
     * @return
     */
    @Override
    public SQLCreateTableBuilder setSchema(String name) {
        SQLCreateTableStatement create = getSQLCreateTableStatement();
        create.setSchema(name);
        return this;
    }

    @Override
    public SQLCreateTableBuilder setIfNotExiists(boolean ifNotExists) {
        SQLCreateTableStatement create = getSQLCreateTableStatement();
        create.setIfNotExiists(ifNotExists);

        return this;
    }


    @Override
    public SQLCreateTableBuilder setBuckets(int buckets) {
        SQLCreateTableStatement create = getSQLCreateTableStatement();
        create.setBuckets(buckets);

        return this;
    }

    @Override
    public SQLCreateTableBuilder setShards(int shards) {
        SQLCreateTableStatement create = getSQLCreateTableStatement();
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
        SQLCreateTableStatement create = getSQLCreateTableStatement();
        create.setComment(comment);

        return this;
    }

    /**
     * ???????????????
     * @param columnName ??????
     * @param dataType ?????????
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
     * ???????????????.??? Id_P int NOT NULL PRIMARY KEY,
     * @param columnName ??????
     * @param dataType ?????????
     * @param primary ???????????????
     * @param notNull ????????????
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
     *        ????????????????????? AUTO_INCREMENT ??????????????????????????????????????????????????????
     *          * ????????? ???????????? AUTO_INCREMENT
     *          *
     *          * ??????????????????AUTO_INCREMENT ??????????????? 1????????????????????????????????????????????? 1???
     *          * ??????????????????????????????????????? AUTO_INCREMENT ????????????????????????????????????????????????????????????????????????????????????????????????????????????
     *          * AUTO_INCREMENT ??????????????????????????? NOT NULL ?????????
     *          * AUTO_INCREMENT ???????????????????????????????????????TINYINT???SMALLINT???INT???BIGINT ?????????
     *          * AUTO_INCREMENT ?????????????????????????????????????????????????????????????????????????????????AUTO_INCREMENT ???????????????
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
        SQLCreateTableStatement create = getSQLCreateTableStatement();
        create.addColumn(column);

        return this;
    }

    /**
     * ??????????????????????????????????????????????????? CONSTRAINT pk_PersonID PRIMARY KEY (Id_P,LastName)
     * @param primaryKeyName ?????????
     * @param columnNames ?????????????????????????????????????????????
     * @return
     */
    @Override
    public SQLCreateTableBuilder addPrimaryKey(String primaryKeyName, List<String> columnNames) {
        SQLTableConstraint constraint = null;
        boolean hasConstaint = false;
        SQLName name = null;

        if (primaryKeyName != null) {
            name = new SQLIdentifierExpr(primaryKeyName);
        }

        switch (dbType) {
            case mysql:
                MySqlPrimaryKey pk = new MySqlPrimaryKey();
                buildIndex(pk.getIndexDefinition(),Token.PRIMARY.name,"", Token.KEY.name,columnNames);
                if (name != null) {
                    pk.setName(name);
                }
                pk.setHasConstraint(hasConstaint);
                constraint = pk;
            default:
                break;
        }

        SQLCreateTableStatement create = getSQLCreateTableStatement();
        constraint.setParent(create);

        create.getTableElementList().add(constraint);

        return this;
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

        SQLCreateTableStatement create = getSQLCreateTableStatement();
        constraint.setParent(create);

        create.getTableElementList().add(constraint);

        return this;
    }

    /**
     * ???????????????
     * ?????????????????????1???foreignKeyName=null??????????????????2???foreignKeyName????????????????????????????????????
     * @param foreignKeyName 1???foreignKeyName=null??????????????????2???foreignKeyName????????????????????????????????????
     * @param referencingColumns ????????????
     * @param referencedTableName ????????????
     * @param referencedColumns ???????????????????????????????????????
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

        SQLCreateTableStatement create = getSQLCreateTableStatement();
        assert constraint != null;
        constraint.setParent(create);

        create.getTableElementList().add(constraint);

        return this;
    }

    private void buildIndex(SQLIndexDefinition indexDefinition, String token, String globalOrLocal, String indexOrKey, List<String> columnNames) {
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

    public void buildIndexRest(SQLIndex idx, SQLObject parent, List<String> columnNames) {

        for (String columnName :
                columnNames) {
            SQLSelectOrderByItem selectOrderByItem = buildSelectOrderByItem(columnName);
            selectOrderByItem.setParent(parent);
            idx.getColumns().add(selectOrderByItem);
        }
    }

    public SQLSelectOrderByItem buildSelectOrderByItem(String columnName) {
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
        SQLCreateTableStatement create = getSQLCreateTableStatement();
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
        SQLCreateTableStatement create = getSQLCreateTableStatement();
        create.addOption(name,value);

        return this;
    }

    @Override
    public SQLCreateTableStatement getSQLCreateTableStatement() {
        if (stmt == null) {
            stmt = createSQLCreateTableStatement();
        }
        return stmt;
    }

    public SQLCreateTableStatement createSQLCreateTableStatement() {
        switch (dbType) {
            case oracle:
                return new OracleCreateTableStatement();
            case mysql:
                return new MySqlCreateTableStatement();
            case db2:
                return new DB2CreateTableStatement();
            case hive:
                return new HiveCreateTableStatement();
            case odps:
                return new OdpsCreateTableStatement();
            default:
                return new SQLCreateTableStatement();
        }
    }

    public String toString() {
        return SQLUtils.toSQLString(stmt, dbType);
    }
}
