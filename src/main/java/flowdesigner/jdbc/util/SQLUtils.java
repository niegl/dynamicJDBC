package flowdesigner.jdbc.util;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import commonUtility.file.FileKit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.EmptyFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class SQLUtils{
    public SQLUtils() {
    }

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

    public static ArrayList<String> searchDriverClassName(List<String> files) {
        ArrayList<String> strings = new ArrayList<>();
        if (files == null) {
            return strings;
        }

        for (String file : files) {
            Collection<File> jarFiles = FileUtils.listFiles(new File(file),
                    FileFilterUtils.and(new SuffixFileFilter("jar"), EmptyFileFilter.NOT_EMPTY),
                    DirectoryFileFilter.INSTANCE);
            jarFiles.forEach(jarFile -> {
                String className = FileKit.readFileFromJar( jarFile,"java.sql.Driver");
                if (!className.isEmpty()) {
                    strings.add(className);
                }
            });
        }

        return strings;
    }

}
