package flowdesigner.jdbc.Keywords;

import com.alibaba.druid.DbType;
import flowdesigner.jdbc.util.SQLUtils;
import org.junit.jupiter.api.Test;

class SQLKeywordsImplTest {

    @Test
    void getKeywords() {
        SQLUtils sqlKeywords = new SQLUtils();
        System.out.println(sqlKeywords.getKeywordsAsString(DbType.hive));
        System.out.println(sqlKeywords.getKeywordsAsString(DbType.mysql));
    }
}