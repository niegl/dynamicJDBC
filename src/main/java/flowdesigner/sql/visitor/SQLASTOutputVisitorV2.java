package flowdesigner.sql.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddColumn;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class SQLASTOutputVisitorV2 extends SQLASTOutputVisitor {
    public SQLASTOutputVisitorV2(StringBuilder appender) {
        super(appender);
    }

    public SQLASTOutputVisitorV2(StringBuilder appender, DbType dbType) {
        super(appender, dbType);
    }

    public SQLASTOutputVisitorV2(StringBuilder appender, boolean parameterized) {
        super(appender, parameterized);
    }

    @Override
    public boolean visit(SQLAlterTableAddColumn x) {

        if (DbType.odps != this.dbType && DbType.hive != this.dbType) {
            this.print0(this.ucase ? "ADD " : "add ");
        } else {
            this.print0(this.ucase ? "ADD COLUMNS (" : "add columns (");
        }

        this.printAndAccept(x.getColumns(), ", ");
        if (DbType.odps == this.dbType || DbType.hive == this.dbType) {
            this.print(')');
        }

        Boolean restrict = x.getRestrict();
        if (restrict != null && restrict) {
            this.print0(this.ucase ? " RESTRICT" : " restrict");
        }

        if (x.isCascade()) {
            this.print0(this.ucase ? " CASCADE" : " cascade");
        }

        return false;
    }
}
