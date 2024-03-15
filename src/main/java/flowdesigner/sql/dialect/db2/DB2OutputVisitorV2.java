package flowdesigner.sql.dialect.db2;

import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddColumn;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2OutputVisitor;

public class DB2OutputVisitorV2 extends DB2OutputVisitor {
    public DB2OutputVisitorV2(StringBuilder appender) {
        super(appender);
    }

    public DB2OutputVisitorV2(StringBuilder appender, boolean parameterized) {
        super(appender, parameterized);
    }

    @Override
    public boolean visit(SQLAlterTableAddColumn x) {
        this.print0(this.ucase ? "ADD COLUMN " : "add column ");
        this.printAndAccept(x.getColumns(), ", ");
        return false;
    }
}
