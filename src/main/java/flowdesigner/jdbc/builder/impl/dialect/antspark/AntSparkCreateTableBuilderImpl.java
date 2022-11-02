package flowdesigner.jdbc.builder.impl.dialect.antspark;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import flowdesigner.jdbc.builder.impl.SQLCreateTableBuilderImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AntSparkCreateTableBuilderImpl extends SQLCreateTableBuilderImpl {
    public AntSparkCreateTableBuilderImpl(@NotNull DbType dbType) {
        super(dbType);
    }

    /**
     * 支持语法： [CLUSTERED BY (col_name, col_name, ...) [SORTED BY (col_name [ASC|DESC], ...)] INTO num_buckets BUCKETS]。<p>
     * 即CLUSTERED BY后面必有 INTO num_buckets BUCKETS, 这里自动默认添加后半部分。
     * @param items
     */
    @Override
    public void addClusteredByItem(List<String> items) {
        SQLCreateTableStatement stmt = getSQLStatement();

        for (String item :
                items) {
            SQLSelectOrderByItem selectOrderByItem = exprBuilder.buildSelectOrderByItem(item);
            selectOrderByItem.setParent(stmt);
            stmt.addClusteredByItem(selectOrderByItem);
        }

        // 补全必须的部分
        super.setBuckets(2);
    }
}
