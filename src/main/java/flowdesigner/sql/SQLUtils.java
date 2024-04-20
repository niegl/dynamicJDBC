package flowdesigner.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.ads.visitor.AdsOutputVisitor;
import com.alibaba.druid.sql.dialect.antspark.visitor.AntsparkOutputVisitor;
import com.alibaba.druid.sql.dialect.blink.vsitor.BlinkOutputVisitor;
import com.alibaba.druid.sql.dialect.clickhouse.visitor.ClickhouseOutputVisitor;
import com.alibaba.druid.sql.dialect.h2.visitor.H2OutputVisitor;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveOutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.dialect.oscar.visitor.OscarOutputVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerOutputVisitor;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import flowdesigner.sql.dialect.db2.DB2OutputVisitorV2;
import flowdesigner.sql.dialect.presto.visitor.PrestoOutputVisitorV2;
import flowdesigner.sql.visitor.SQLASTOutputVisitorV2;
import flowdesigner.util.Utils;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.alibaba.druid.sql.SQLUtils.parseStatements;

/**
 * SQL语句相关的对象方法。
 */
public class SQLUtils {

    private static final SQLParserFeature[] FORMAT_DEFAULT_FEATURES = {
            SQLParserFeature.KeepComments,
            SQLParserFeature.EnableSQLBinaryOpExprGroup
    };

    
    public static FormatOption DEFAULT_FORMAT_OPTION = new FormatOption(true, true);
    public static FormatOption DEFAULT_LCASE_FORMAT_OPTION
            = new FormatOption(false, true);
    
    /**
     * 获取用户定义变量
     * @param dbType 数据库类型
     * @param sql set SQL语句
     * @return 变量列表
     */
    public static List<String> parseContextDefinition(String dbType, String sql) {
        List<SQLStatement> list = new ArrayList<>();

        try {
            list = parseStatements(sql, dbType);
        } catch (ParserException ignored) {
        }

        return list.stream()
                .filter(s -> s instanceof SQLSetStatement)
                .flatMap(s -> {
                    Collection<SQLAssignItem> items = ((SQLSetStatement) s).getItems();
                    items.removeIf(i -> i.getTarget() instanceof SQLPropertyExpr);
                    return items.stream().map(i -> i.getTarget().toString().replaceAll("@","") + "=" + i.getValue());
                })
                .toList();
    }

    public static String parseContextDefinitionASString(String dbType, String sql) {
        Collection<String> list = parseContextDefinition(dbType, sql);
        return StringUtils.join(list,',');
    }

    public static String toSQLString(SQLObject sqlObject, DbType dbType) {
        return toSQLString(sqlObject, dbType, null, new VisitorFeature[0]);
    }

    public static String toSQLString(SQLObject sqlObject, DbType dbType, FormatOption option) {
        return toSQLString(sqlObject, dbType, option, new VisitorFeature[0]);
    }

    public static String toSQLString(SQLObject sqlObject,
                                     DbType dbType,
                                     FormatOption option,
                                     VisitorFeature... features) {
        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = createOutputVisitor(out, dbType);

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

    public static SQLASTOutputVisitor createOutputVisitor(StringBuilder out, DbType dbType) {
        return createFormatOutputVisitor(out, null, dbType);
    }

    public static SQLASTOutputVisitor createFormatOutputVisitor(StringBuilder out,
                                                                List<SQLStatement> statementList,
                                                                DbType dbType) {
        if (dbType == null) {
            dbType = DbType.other;
        }

        switch (dbType) {
            case oracle:
            case oceanbase_oracle:
                if (statementList != null && statementList.size() != 1) {
                    return new OracleOutputVisitor(out, true);
                }

                return new OracleOutputVisitor(out, false);
            case mysql:
            case mariadb:
            case tidb:
                return new MySqlOutputVisitor(out);
            case postgresql:
                return new PGOutputVisitor(out);
            case sqlserver:
            case jtds:
                return new SQLServerOutputVisitor(out);
            case db2:
                return new DB2OutputVisitorV2(out);
            case odps:
                return new OdpsOutputVisitor(out);
            case h2:
                return new H2OutputVisitor(out);
            case hive:
                return new HiveOutputVisitor(out);
            case ads:
                return new AdsOutputVisitor(out);
            case blink:
                return new BlinkOutputVisitor(out);
            case antspark:
                return new AntsparkOutputVisitor(out);
            case presto:
                return new PrestoOutputVisitorV2(out);
            case clickhouse:
                return new ClickhouseOutputVisitor(out);
            case oscar:
                return new OscarOutputVisitor(out);
            default:
                return new SQLASTOutputVisitorV2(out, dbType);
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

    public static String checkSyntax(String sql, DbType dbType) {
        String result = "SUCCESS";
        try {
            List<SQLStatement> list = parseStatements(sql, dbType);
        } catch (ParserException exception) {
            result = exception.getMessage();
        }

        return result;
    }

    public static String getFrom(String sql, String dbType) {
        String result = null;
        SQLTableSource tableSource = null;

        // 删除最后一个分号;
        sql = Utils.trimLastColon(sql);

        try {
            SQLStatement stmt = com.alibaba.druid.sql.SQLUtils.parseSingleStatement(sql, dbType, new SQLParserFeature[0]);
            if (stmt instanceof SQLSelectStatement) {
                SQLSelectStatement selectStatement = (SQLSelectStatement) stmt;
                SQLSelect select = selectStatement.getSelect();

                SQLSelectQuery query = select.getQuery();

                while (tableSource == null) {
                    // 考虑SelectQuery为 SQLSelectQueryBlock 和 SQLUnionQuery的情况
                    if (query instanceof SQLSelectQueryBlock) {
                        SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
                        tableSource = queryBlock.getFrom();
                    } else if (query instanceof SQLUnionQuery) {
                        SQLUnionQuery unionQuery = (SQLUnionQuery) query;
                        query = unionQuery.getLeft();
                    }
                }

                while (result == null) {
                    if (tableSource instanceof SQLExprTableSource
//                        || tableSource instanceof SQLJoinTableSource
//                        || tableSource instanceof SQLUnionQueryTableSource
//                        || tableSource instanceof SQLValuesTableSource
                    ) {
                        SQLExprTableSource exprTableSource = (SQLExprTableSource) tableSource;
                        result = exprTableSource.getTableName();
                    } else if (tableSource instanceof SQLSubqueryTableSource) {
                        SQLSubqueryTableSource sqlSubqueryTableSource = (SQLSubqueryTableSource) tableSource;
                        result = sqlSubqueryTableSource.getAlias();
                    } else if (tableSource instanceof SQLJoinTableSource) {
                        SQLJoinTableSource joinTableSource = (SQLJoinTableSource) tableSource;
                        tableSource = joinTableSource.getRight();
                    } else {
                        result = "";
                    }
                }
            }


        } catch (ParserException exception) {
            result = exception.getMessage();
        }

        return result;
    }

}
