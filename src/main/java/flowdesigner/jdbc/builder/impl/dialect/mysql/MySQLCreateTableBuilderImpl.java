package flowdesigner.jdbc.builder.impl.dialect.mysql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLConstraint;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKeyImpl;
import com.alibaba.druid.sql.ast.statement.SQLTableConstraint;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.parser.Token;
import flowdesigner.jdbc.builder.impl.SQLCreateTableBuilderImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MySQLCreateTableBuilderImpl extends SQLCreateTableBuilderImpl {
    public MySQLCreateTableBuilderImpl(@NotNull DbType dbType) {
        super(dbType);
    }

    @Override
    protected @NotNull SQLConstraint createPrimaryKey(List<String> columnNames, boolean hasConstraint, SQLName name) {
        MySqlPrimaryKey pk = new MySqlPrimaryKey();
        buildIndex(pk.getIndexDefinition(), Token.PRIMARY.name,"", Token.KEY.name, columnNames);
        if (name != null) {
            pk.setName(name);
        }
        pk.setHasConstraint(hasConstraint);
        return pk;
    }
}
