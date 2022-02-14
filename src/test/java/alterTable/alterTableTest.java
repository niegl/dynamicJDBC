package alterTable;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import flowdesigner.jdbc.util.sql.core.DBType;

public class alterTableTest {

    @org.junit.jupiter.api.Test
    void testAlter() {
        SQLAlterTableStatement alterTableStatement = new SQLAlterTableStatement(DbType.hive);
        alterTableStatement.setName(new SQLIdentifierExpr("std_line"));
        SQLAlterTableRename alterTableRename = new SQLAlterTableRename(new SQLIdentifierExpr("std_line20"));
        alterTableStatement.addItem(alterTableRename);

        System.out.println(alterTableStatement);

        SQLAlterTableStatement alterTableStatement2 = new SQLAlterTableStatement(DbType.hive);
        alterTableStatement2.setName(new SQLIdentifierExpr("std_line"));
        SQLAlterTableAddColumn alterTableAddColumn = new SQLAlterTableAddColumn();
        SQLColumnDefinition column = new SQLColumnDefinition();
        column.setName("columnName");
        column.setDataType(
                SQLParserUtils.createExprParser("Stirng", alterTableStatement.getDbType()).parseDataType()
        );
        alterTableAddColumn.addColumn(column);
        alterTableStatement2.addItem(alterTableAddColumn);
        System.out.println(alterTableStatement2);

        SQLAlterTableStatement alterTableStatement3 = new SQLAlterTableStatement(DbType.mysql);
        alterTableStatement3.setName(new SQLIdentifierExpr("std_line"));
        SQLAlterTableReplaceColumn alterTableDropColumnItem = new SQLAlterTableReplaceColumn();
        alterTableDropColumnItem.addColumn(column);
        alterTableStatement3.addItem(alterTableDropColumnItem);
        System.out.println(alterTableStatement3);
    }
}
