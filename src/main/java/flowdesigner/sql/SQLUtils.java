package flowdesigner.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2OutputVisitor;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import commonUtility.file.FileKit;
import flowdesigner.sql.dialect.db2.DB2OutputVisitorV2;
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

public class SQLUtils {

    private static final SQLParserFeature[] FORMAT_DEFAULT_FEATURES = {
            SQLParserFeature.KeepComments,
            SQLParserFeature.EnableSQLBinaryOpExprGroup
    };

    public static FormatOption DEFAULT_FORMAT_OPTION = new FormatOption(true, true);
    public static FormatOption DEFAULT_LCASE_FORMAT_OPTION
            = new FormatOption(false, true);

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

    public static String toSQLString(SQLObject sqlObject, DbType dbType) {
        return toSQLString(sqlObject, dbType, null, null);
    }

    public static String toSQLString(SQLObject sqlObject, DbType dbType, FormatOption option) {
        return toSQLString(sqlObject, dbType, option, null);
    }

    public static String toSQLString(SQLObject sqlObject,
                                     DbType dbType,
                                     FormatOption option,
                                     VisitorFeature... features) {
        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = createOutputVisitor(out, dbType);
System.out.println(visitor.getClass());
        if (option == null) {
            option = DEFAULT_FORMAT_OPTION;
        }

        visitor.setUppCase(option.isUppCase());
        visitor.setPrettyFormat(option.isPrettyFormat());
        visitor.setParameterized(option.isParameterized());

        int featuresValue = option.features;
        if (features != null) {
            for (VisitorFeature feature : features) {
                visitor.config(feature, true);
                featuresValue |= feature.mask;
            }
        }

        visitor.setFeatures(featuresValue);

        sqlObject.accept(visitor);

        String sql = out.toString();
        return sql;
    }

    public static SQLASTOutputVisitor createOutputVisitor(Appendable out, DbType dbType) {
        return createFormatOutputVisitor(out, null, dbType);
    }

    public static SQLASTOutputVisitor createFormatOutputVisitor(Appendable out,
                                                                List<SQLStatement> statementList,
                                                                DbType dbType) {
        if (dbType == null) {
            dbType = DbType.other;
        }

        switch (dbType) {
            case db2:
                return new DB2OutputVisitorV2(out);
            default:
                return com.alibaba.druid.sql.SQLUtils.createOutputVisitor(out, dbType);
        }

    }

    public static class FormatOption {
        private int features = VisitorFeature.of(
                VisitorFeature.OutputUCase,
                VisitorFeature.OutputPrettyFormat
        );

        public FormatOption() {
        }

        public FormatOption(VisitorFeature... features) {
            this.features = VisitorFeature.of(features);
        }

        public FormatOption(boolean ucase) {
            this(ucase, true);
        }

        public FormatOption(boolean ucase, boolean prettyFormat) {
            this(ucase, prettyFormat, false);
        }

        public FormatOption(boolean ucase, boolean prettyFormat, boolean parameterized) {
            this.features = VisitorFeature.config(this.features, VisitorFeature.OutputUCase, ucase);
            this.features = VisitorFeature.config(this.features, VisitorFeature.OutputPrettyFormat, prettyFormat);
            this.features = VisitorFeature.config(this.features, VisitorFeature.OutputParameterized, parameterized);
        }

        public boolean isDesensitize() {
            return isEnabled(VisitorFeature.OutputDesensitize);
        }

        public void setDesensitize(boolean val) {
            config(VisitorFeature.OutputDesensitize, val);
        }

        public boolean isUppCase() {
            return isEnabled(VisitorFeature.OutputUCase);
        }

        public void setUppCase(boolean val) {
            config(VisitorFeature.OutputUCase, val);
        }

        public boolean isPrettyFormat() {
            return isEnabled(VisitorFeature.OutputPrettyFormat);
        }

        public void setPrettyFormat(boolean prettyFormat) {
            config(VisitorFeature.OutputPrettyFormat, prettyFormat);
        }

        public boolean isParameterized() {
            return isEnabled(VisitorFeature.OutputParameterized);
        }

        public void setParameterized(boolean parameterized) {
            config(VisitorFeature.OutputParameterized, parameterized);
        }

        public void config(VisitorFeature feature, boolean state) {
            features = VisitorFeature.config(features, feature, state);
        }

        public final boolean isEnabled(VisitorFeature feature) {
            return VisitorFeature.isEnabled(this.features, feature);
        }
    }
}
