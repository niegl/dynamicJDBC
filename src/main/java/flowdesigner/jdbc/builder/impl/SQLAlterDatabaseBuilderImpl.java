package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterCharacter;
import com.alibaba.druid.sql.ast.statement.SQLAlterDatabaseStatement;
import com.alibaba.druid.sql.parser.Token;
import flowdesigner.jdbc.builder.SQLAlterDatabaseBuilder;

import java.util.List;
import java.util.Map;

public class SQLAlterDatabaseBuilderImpl extends SQLBuilderImpl implements SQLAlterDatabaseBuilder {

    private SQLAlterDatabaseStatement stmt;
//    private DbType dbType;


    public SQLAlterDatabaseBuilderImpl(DbType dbType) {
        super(dbType);
//        this.dbType = dbType;
    }
    public SQLAlterDatabaseBuilderImpl(String sql, DbType dbType) {
        super(dbType);
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        if (stmtList.isEmpty()) {
            throw new IllegalArgumentException("not support empty-statement :" + sql);
        } else if (stmtList.size() > 1) {
            throw new IllegalArgumentException("not support multi-statement :" + sql);
        } else {
            SQLAlterDatabaseStatement stmt = (SQLAlterDatabaseStatement)stmtList.get(0);
            this.stmt = stmt;
//            this.dbType = dbType;
        }
    }
    public SQLAlterDatabaseBuilderImpl(SQLAlterDatabaseStatement stmt, DbType dbType){
        super(dbType);
        this.stmt = stmt;
//        this.dbType = dbType;
    }

//    @Override
//    public SQLAlterDatabaseBuilder setType(DbType dbType) {
//        this.dbType = dbType;
//        return this;
//    }

    @Override
    public SQLAlterDatabaseBuilder setName(String db_name) {
        SQLAlterDatabaseStatement statement = getSQLStatement();
        statement.setName(new SQLIdentifierExpr(db_name));
        return this;
    }

    /**
     * 更改数据库属性设置
     * @param databaseName 数据库名称
     * @param alterOption 数据库属性。支持：CHARACTER SET、CHARSET、COLLATE。注意保证CHARSET在COLLATE之前设置.
     */
    @Override
    public SQLAlterDatabaseBuilder alter(String databaseName, Map<String, String> alterOption) {
        setName(databaseName);

        SQLAlterDatabaseStatement statement = getSQLStatement();
        SQLAlterCharacter alterCharacter = statement.getCharacter();
        if (alterCharacter == null) {
            statement.setCharacter(new SQLAlterCharacter());
            alterCharacter = statement.getCharacter();
        }

        boolean bCharset = false;
        assert alterCharacter != null;
        for (Map.Entry<String, String> entry :
                alterOption.entrySet()) {
            String token = entry.getKey();
            String tokenValue = entry.getValue();

            if (token.equalsIgnoreCase("CHARACTER ".concat(Token.SET.name))) {
                alterCharacter.setCharacterSet(new SQLIdentifierExpr(tokenValue));
                bCharset = true;
            } else if (token.equalsIgnoreCase("CHARSET")) {
                alterCharacter.setCharacterSet(new SQLIdentifierExpr(tokenValue));
                bCharset = true;
            } else if (token.equalsIgnoreCase("COLLATE")) {
                SQLExpr characterSet = alterCharacter.getCharacterSet();
                SQLBinaryOpExpr binaryExpr = new SQLBinaryOpExpr(characterSet, SQLBinaryOperator.COLLATE,
                        new SQLIdentifierExpr(tokenValue), DbType.mysql);
                alterCharacter.setCharacterSet(binaryExpr);
            }
            //ads 特殊支持
//            else if (token.equalsIgnoreCase("SHARDS") || token.equalsIgnoreCase("SHARD_ID")
//                    || token.equalsIgnoreCase("REPLICATION") || token.equalsIgnoreCase("STORAGE_DEPENDENCY")
//                    || token.equalsIgnoreCase("REPLICA_TYPE") || token.equalsIgnoreCase("DATA_REPLICATION")) {
//                stmt.getOptions().put(token, new SQLIdentifierExpr(tokenValue));
//            } else {
//                break;
//            }
        }

        return this;
    }

    public SQLAlterDatabaseStatement getSQLStatement() {
        if (stmt == null) {
            stmt = new SQLAlterDatabaseStatement(dbType);
        }
        return stmt;
    }

    public String toString() {
        return SQLUtils.toSQLString(stmt, dbType);
    }

}
