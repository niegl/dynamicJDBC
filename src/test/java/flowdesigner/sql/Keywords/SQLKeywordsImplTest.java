package flowdesigner.sql.Keywords;

import com.alibaba.druid.DbType;
import flowdesigner.db.DbUtils;
import org.junit.jupiter.api.Test;

class SQLKeywordsImplTest {

    @Test
    void getKeywords() {
        System.out.println(DbUtils.getDbKeywordsAsString(DbType.hive));
        System.out.println(DbUtils.getDbKeywordsAsString(DbType.mysql));
    }
}