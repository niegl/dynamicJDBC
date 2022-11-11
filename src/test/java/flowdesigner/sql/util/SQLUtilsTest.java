package flowdesigner.sql.util;

import flowdesigner.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

class SQLUtilsTest {

    @Test
    void searchDriverClassName() {
        ArrayList<String> strings = SQLUtils.searchDriverClassName(Collections.singletonList("C:\\文档\\项目\\北京能耗\\能耗资料\\new\\new\\05.代码实现及单元测试\\lib\\hive-jdbc-1.2.2-standalone.jar"));
        System.out.println(strings);
    }
}