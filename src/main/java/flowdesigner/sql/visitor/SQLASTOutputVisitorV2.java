package flowdesigner.sql.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddColumn;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class SQLASTOutputVisitorV2 extends SQLASTOutputVisitor {
    public SQLASTOutputVisitorV2(Appendable appender) {
        super(appender);
    }

    public SQLASTOutputVisitorV2(Appendable appender, DbType dbType) {
        super(appender, dbType);
    }

    public SQLASTOutputVisitorV2(Appendable appender, boolean parameterized) {
        super(appender, parameterized);
    }

    @Override
    public boolean visit(SQLAlterTableAddColumn x) {
        return super.visit(x);
    }
}
