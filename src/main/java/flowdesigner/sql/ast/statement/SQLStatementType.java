package flowdesigner.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCaseStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlSelectIntoStatement;
import com.alibaba.fastjson2.annotation.JSONField;

public enum SQLStatementType {
    //select statement
    SELECT(SQLSelectStatement.class),
    //update statement
    UPDATE(SQLUpdateStatement.class),
    //insert statement
    INSERT(SQLInsertStatement.class),
    //delete statement
    DELETE(SQLDeleteStatement.class),
    //while statement
    WHILE(SQLWhileStatement.class),
    //begin-end
    IF(SQLIfStatement.class),
    //begin-end
    LOOP(SQLLoopStatement.class),
    //begin-end
    BLOCK(SQLBlockStatement.class),
    //declare statement
    DECLARE(SQLDeclareStatement.class),
    //select into
    SELECTINTO(MySqlSelectIntoStatement.class),
    //case
    CASE(SQLCaseStatement.class),
    //set
    SET(SQLSetStatement.class),

    UNDEFINED(SQLStatement.class);

    public final Class<? extends SQLStatement> aClass;

    SQLStatementType() {
        this(null);
    }

    SQLStatementType(Class<? extends SQLStatement> aClass) {
        this.aClass = aClass;
    }

    public static SQLStatementType getType(SQLStatement stmt) {
        for (SQLStatementType type : SQLStatementType.values()) {
            if ( type.aClass.isInstance(stmt)) {
                return type;
            }
        }
        return UNDEFINED;
    }

    /**
     * 指定输出JSON字段，否则输出为ordinary
     * @return
     */
    @JSONField
    public String getName() {
        return this.name();
    }

}
