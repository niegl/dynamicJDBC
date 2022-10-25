package flowdesigner.jdbc.builder.impl.dialect.mysql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLConstraint;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKeyImpl;
import com.alibaba.druid.sql.ast.statement.SQLTableConstraint;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.parser.Token;
import flowdesigner.jdbc.builder.SQLCreateTableBuilder;
import flowdesigner.jdbc.builder.impl.SQLCreateTableBuilderImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MySQLCreateTableBuilderImpl extends SQLCreateTableBuilderImpl {
    public MySQLCreateTableBuilderImpl(@NotNull DbType dbType) {
        super(dbType);
    }

    @Override
    protected @NotNull SQLConstraint createPrimaryKey(List<String> columnNames, SQLName name) {
        MySqlPrimaryKey pk = new MySqlPrimaryKey();
        buildIndex(pk.getIndexDefinition(), Token.PRIMARY.name,"", Token.KEY.name, columnNames);
        if (name != null) {
            pk.setName(name);
        }
        pk.setHasConstraint(true);
        return pk;
    }

    @Override
    protected SQLConstraint createUnique(List<String> columnNames, SQLName name) {
        MySqlUnique unique = new MySqlUnique();
        if (name != null) {
            unique.setName(name);
            unique.setHasConstraint(true);
        }
        buildIndex(unique.getIndexDefinition(), Token.UNIQUE.name, "", "", columnNames);
        return unique;
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

}
