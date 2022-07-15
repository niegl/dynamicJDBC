package flowdesigner.jdbc.util;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class SQLUtils{

    public static Set<String> getKeywords(DbType dbType) {
        Lexer lexer = SQLParserUtils.createLexer("select *", dbType);
        return lexer.getKeywords().getKeywords().keySet();
    }

    /**
     * 返回数据库关键字
     * @param dbType 数据库类型
     * @return 以逗号分隔的关键字列表
     */
    public static String getKeywordsAsString(DbType dbType) {
        Set<String> keywords = getKeywords(dbType);
        return StringUtils.join(keywords,",");
    }

}
