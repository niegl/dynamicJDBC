package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import com.alibaba.druid.sql.parser.Token;
import flowdesigner.jdbc.builder.SQLCreateDatabaseBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLCreateDatabaseBuilderImpl extends SQLBuilderImpl implements SQLCreateDatabaseBuilder {
    private SQLCreateDatabaseStatement  stmt;

    public SQLCreateDatabaseBuilderImpl(@NotNull DbType dbType) {
        this(new SQLExprBuilder(dbType), dbType);
    }
    public SQLCreateDatabaseBuilderImpl(SQLExprBuilder exprBuilder, DbType dbType ) {
        super( exprBuilder, dbType);
    }

    public SQLCreateDatabaseBuilderImpl(String sql, DbType dbType) {
        super(dbType);
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        if (stmtList.isEmpty()) {
            throw new IllegalArgumentException("not support empty-statement :" + sql);
        } else if (stmtList.size() > 1) {
            throw new IllegalArgumentException("not support multi-statement :" + sql);
        } else {
            SQLCreateDatabaseStatement stmt = (SQLCreateDatabaseStatement)stmtList.get(0);
            this.stmt = stmt;
        }
    }
    public SQLCreateDatabaseBuilderImpl(SQLCreateDatabaseStatement stmt, @NotNull DbType dbType){
        super(dbType);
        this.stmt = stmt;
    }

    @Override
    public SQLCreateDatabaseBuilder setName(String db_name) {
        SQLCreateDatabaseStatement create = getSQLStatement();
        create.setName(new SQLIdentifierExpr(db_name));
        return this;
    }

    @Override
    public SQLCreateDatabaseBuilder setComment(String db_comment) {
        // mysql数据库不支持注释
        if (dbType.equals(DbType.mysql)) {
            return this;
        }
        SQLCreateDatabaseStatement create = getSQLStatement();
        create.setComment(new SQLIdentifierExpr(db_comment));
        return this;
    }

    @Override
    public SQLCreateDatabaseBuilder setIfNotExists(boolean ifNotExists) {
        SQLCreateDatabaseStatement create = getSQLStatement();
        create.setIfNotExists(ifNotExists);
        return this;
    }

    /**
     * JNI传递map类型结果分为key,value两部分传递。
     * @param ifNotExists
     * @param create_option_key key列表
     * @param create_option_value 与key对应的value列表
     * @return
     */
    @Override
    public SQLCreateDatabaseBuilder createDatabase(boolean ifNotExists, String db_name, List<String> create_option_key, List<String> create_option_value) {
        HashMap<String, String> hashMap = new HashMap<>();
        for (int i = 0; i < create_option_key.size(); i++) {
            hashMap.put(create_option_key.get(i),create_option_value.get(i));
        }
        return createDatabase(ifNotExists,db_name,hashMap);
    }
    /**
     * CREATE {DATABASE | SCHEMA} [IF NOT EXISTS] db_name
     *     [create_option] ...
     *
     * create_option: [DEFAULT] {
     *     CHARACTER SET [=] charset_name
     *   | COLLATE [=] collation_name
     *   | ENCRYPTION [=] {'Y' | 'N'}
     * }
     * @param ifNotExists
     * @param db_name
     * @param create_option
     * @return
     */
    @Override
    public SQLCreateDatabaseBuilder createDatabase(boolean ifNotExists, String db_name, Map<String, String> create_option) {

        setIfNotExists(ifNotExists);
        setName(db_name);

        for (Map.Entry<String, String> entry :
                create_option.entrySet()) {
            String token = entry.getKey();
            String tokenValue = entry.getValue();

            if (token.equalsIgnoreCase("CHARACTER ".concat(Token.SET.name))) {
                stmt.setCharacterSet(tokenValue);
            } else if (token.equalsIgnoreCase("CHARSET")) {
                stmt.setCharacterSet(tokenValue);
            } else if (token.equalsIgnoreCase("COLLATE")) {
                stmt.setCollate(tokenValue);
            } else if (token.equalsIgnoreCase("PASSWORD")) {
                stmt.setPassword(new SQLIdentifierExpr(tokenValue));
            }
            //ads 特殊支持
            else if (token.equalsIgnoreCase("SHARDS") || token.equalsIgnoreCase("SHARD_ID")
                    || token.equalsIgnoreCase("REPLICATION") || token.equalsIgnoreCase("STORAGE_DEPENDENCY")
                    || token.equalsIgnoreCase("REPLICA_TYPE") || token.equalsIgnoreCase("DATA_REPLICATION")) {
                stmt.getOptions().put(token, new SQLIdentifierExpr(tokenValue));
            } else {
                break;
            }
        }

        return this;
    }

    public SQLCreateDatabaseStatement getSQLStatement() {
        if (stmt == null) {
            stmt = createSQLCreateDatabaseStatement();
        }
        return stmt;
    }

    protected SQLCreateDatabaseStatement createSQLCreateDatabaseStatement() {
        return new SQLCreateDatabaseStatement(dbType);
    }

//    public String toString() {
//        return SQLUtils.toSQLString(stmt, dbType);
//    }
}
