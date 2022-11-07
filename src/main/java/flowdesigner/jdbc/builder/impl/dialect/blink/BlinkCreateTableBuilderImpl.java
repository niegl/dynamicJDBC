package flowdesigner.jdbc.builder.impl.dialect.blink;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyImpl;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKeyImpl;
import com.alibaba.druid.sql.ast.statement.SQLUnique;
import com.alibaba.druid.sql.dialect.blink.ast.BlinkCreateTableStatement;
import flowdesigner.jdbc.builder.SQLCreateTableBuilder;
import flowdesigner.jdbc.builder.impl.SQLCreateTableBuilderImpl;
import org.jetbrains.annotations.NotNull;

public class BlinkCreateTableBuilderImpl extends SQLCreateTableBuilderImpl {

    public BlinkCreateTableBuilderImpl(@NotNull DbType dbType) {
        super(dbType);
    }

    /**
     * 没有 Constraint关键字
     * @param name
     * @return
     */
    @Override
    protected SQLPrimaryKeyImpl createPrimaryKey(StringBuffer name) {
        name.setLength(0);
        return super.createPrimaryKey(name);
    }

    /**
     * 不支持
     * @param name
     * @return
     */
    @Override
    protected SQLUnique createUnique(StringBuffer name) {
        return null;
    }
    /**
     * 不支持
     * @param name
     * @return
     */
    @Override
    protected SQLForeignKeyImpl createForeignKey(StringBuffer name) {
        return null;
    }
    /**
     * 不支持
     * @param type
     * @return
     */
    @Override
    public SQLCreateTableBuilder setTemporary(SQLCreateTableStatement.Type type) {
        return this;
    }

    @Override
    protected BlinkCreateTableStatement createSQLCreateTableStatement() {
        return new BlinkCreateTableStatement();
    }
}
