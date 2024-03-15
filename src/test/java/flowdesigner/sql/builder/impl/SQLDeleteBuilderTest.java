package flowdesigner.sql.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.builder.SQLBuilderFactory;
import com.alibaba.druid.sql.builder.SQLDeleteBuilder;
import org.junit.jupiter.api.Test;

public class SQLDeleteBuilderTest {

    @Test
    void testWhere() {
        SQLDeleteBuilder builder = SQLBuilderFactory.createDeleteBuilder(DbType.mysql);
        builder.from( "b");
        builder.where("{AT}");
        System.out.println(builder);
    }
}
