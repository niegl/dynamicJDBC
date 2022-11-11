package flowdesigner.sql.dialect.mysql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLCheck;
import com.alibaba.druid.sql.ast.statement.SQLConstraint;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyImpl;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.parser.Token;
import flowdesigner.sql.builder.SQLCreateTableBuilder;
import flowdesigner.sql.builder.impl.SQLCreateTableBuilderImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MySQLCreateTableBuilderImpl extends SQLCreateTableBuilderImpl {
    public MySQLCreateTableBuilderImpl(@NotNull DbType dbType) {
        super(dbType);
    }

    @Override
    protected @NotNull SQLConstraint buildPrimaryKey(List<String> columnNames, StringBuffer name) {
        MySqlPrimaryKey pk = new MySqlPrimaryKey();
        buildIndex(pk.getIndexDefinition(), Token.PRIMARY.name,"", Token.KEY.name, columnNames);
        if (name != null) {
            pk.setName(String.valueOf(name));
        }
        pk.setHasConstraint(true);
        return pk;
    }

    @Override
    protected SQLConstraint buildUnique(List<String> columnNames, StringBuffer name) {
        MySqlUnique unique = new MySqlUnique();
        if (name != null) {
            unique.setName(String.valueOf(name));
            unique.setHasConstraint(true);
        }
        buildIndex(unique.getIndexDefinition(), Token.UNIQUE.name, "", "", columnNames);
        return unique;
    }

    @Override
    protected @NotNull SQLForeignKeyImpl createForeignKey(StringBuffer name) {
        MysqlForeignKey foreignKey = new MysqlForeignKey();
        if (name != null) {
            foreignKey.setName(String.valueOf(name));
            foreignKey.setHasConstraint(true);
        }

        foreignKey.setOnUpdate(SQLForeignKeyImpl.Option.CASCADE);
        foreignKey.setOnDelete(SQLForeignKeyImpl.Option.RESTRICT);

        return foreignKey;
    }

    /**
     * 支持MySQL 8.0.16之前的语法，去掉CONSTRAINT [symbol]
     * @param name
     * @return
     */
    @Override
    protected @NotNull SQLCheck createCheck(StringBuffer name) {
        name.setLength(0);
        return super.createCheck(name);
    }

    /**
     * 数据库不支持 <p>
     * 语法：CREATE [EXTERNAL] TABLE ...
     *
     * @param external
     * @return
     */
    @Override
    public SQLCreateTableBuilder setExternal(boolean external) {
        return this;
    }

    @Override
    protected MySqlCreateTableStatement createSQLCreateTableStatement() {
        return new MySqlCreateTableStatement();
    }
}
