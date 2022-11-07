package flowdesigner.jdbc.builder.impl.dialect.odps;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyImpl;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKeyImpl;
import com.alibaba.druid.sql.ast.statement.SQLUnique;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsCreateTableStatement;
import flowdesigner.jdbc.builder.SQLCreateTableBuilder;
import flowdesigner.jdbc.builder.impl.SQLCreateTableBuilderImpl;
import org.jetbrains.annotations.NotNull;

public class OdpsCreateTableBuilderImpl extends SQLCreateTableBuilderImpl {


    public OdpsCreateTableBuilderImpl(@NotNull DbType dbType) {
        super(dbType);
    }

    /**
     * 不支持
     * @param name
     * @return
     */
    @Override
    protected SQLPrimaryKeyImpl createPrimaryKey(StringBuffer name) {
        return null;
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

    /**
     * 支持数据库：hive,db2 <p>
     * 语法：CREATE [TEMPORARY] [EXTERNAL] TABLE ...
     *
     * @param external
     * @return
     */
    @Override
    public SQLCreateTableBuilder setExternal(boolean external) {
        SQLCreateTableStatement statement = getSQLStatement();
        statement.setExternal(external);
        return this;
    }

    @Override
    protected OdpsCreateTableStatement createSQLCreateTableStatement() {
        return new OdpsCreateTableStatement();
    }
}
