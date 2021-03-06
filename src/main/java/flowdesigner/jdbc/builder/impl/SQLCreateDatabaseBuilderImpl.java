package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.parser.Token;
import flowdesigner.jdbc.builder.SQLCreateDatabaseBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SQLCreateDatabaseBuilderImpl implements SQLCreateDatabaseBuilder {
    private SQLCreateDatabaseStatement  stmt;
    private DbType dbType;

    public SQLCreateDatabaseBuilderImpl(@NotNull DbType dbType){
        this.dbType = dbType;
    }
    public SQLCreateDatabaseBuilderImpl(String sql, DbType dbType) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        if (stmtList.isEmpty()) {
            throw new IllegalArgumentException("not support empty-statement :" + sql);
        } else if (stmtList.size() > 1) {
            throw new IllegalArgumentException("not support multi-statement :" + sql);
        } else {
            SQLCreateDatabaseStatement stmt = (SQLCreateDatabaseStatement)stmtList.get(0);
            this.stmt = stmt;
            this.dbType = dbType;
        }
    }
    public SQLCreateDatabaseBuilderImpl(SQLCreateDatabaseStatement stmt, @NotNull DbType dbType){
        this.stmt = stmt;
        this.dbType = dbType;
    }

    @Override
    public SQLCreateDatabaseBuilder setType(DbType dbType) {
        this.dbType = dbType;
        return this;
    }

    @Override
    public SQLCreateDatabaseBuilder setName(String db_name) {
        SQLCreateDatabaseStatement create = getSQLCreateDatabaseStatement();
        create.setName(new SQLIdentifierExpr(db_name));
        return this;
    }

    @Override
    public SQLCreateDatabaseBuilder setComment(String db_comment) {
        // mysql????????????????????????
        if (dbType.equals(DbType.mysql)) {
            return this;
        }
        SQLCreateDatabaseStatement create = getSQLCreateDatabaseStatement();
        create.setComment(new SQLIdentifierExpr(db_comment));
        return this;
    }

    @Override
    public SQLCreateDatabaseBuilder setIfNotExists(boolean ifNotExists) {
        SQLCreateDatabaseStatement create = getSQLCreateDatabaseStatement();
        create.setIfNotExists(ifNotExists);
        return this;
    }

    /**
     * JNI??????map??????????????????key,value??????????????????
     * @param ifNotExists
     * @param create_option_key key??????
     * @param create_option_value ???key?????????value??????
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
            //ads ????????????
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

    public SQLCreateDatabaseStatement getSQLCreateDatabaseStatement() {
        if (stmt == null) {
            stmt = createSQLCreateDatabaseStatement();
        }
        return stmt;
    }

    public SQLCreateDatabaseStatement createSQLCreateDatabaseStatement() {
        return new SQLCreateDatabaseStatement(dbType);
    }

    public String toString() {
        return SQLUtils.toSQLString(stmt, dbType);
    }
}
