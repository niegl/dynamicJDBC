package flowdesigner.sql.dialect.presto.visitor;

import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddColumn;
import com.alibaba.druid.sql.dialect.presto.visitor.PrestoOutputVisitor;

public class PrestoOutputVisitorV2 extends PrestoOutputVisitor {
    public PrestoOutputVisitorV2(StringBuilder appender) {
        super(appender);
    }

    public PrestoOutputVisitorV2(StringBuilder appender, boolean parameterized) {
        super(appender, parameterized);
    }

    @Override
    public boolean visit(SQLAlterTableAddColumn x) {
        this.print0(this.ucase ? "ADD COLUMN " : "add column ");
        this.printAndAccept(x.getColumns(), ", ");
        return false;
    }
}
