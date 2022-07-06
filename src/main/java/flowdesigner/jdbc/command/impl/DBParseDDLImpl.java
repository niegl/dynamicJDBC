package flowdesigner.jdbc.command.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.command.model.ColumnField;
import flowdesigner.jdbc.command.model.TableEntity;
import flowdesigner.jdbc.util.raw.kit.IOKit;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 利用druid解析DDL
 */
public class DBParseDDLImpl implements Command<ExecResult<List<TableEntity>>> {
    /** 使用java自带的log工具 */
    private static final Logger logger = Logger.getLogger("DBParseDDLImpl");

    public ExecResult<List<TableEntity>> exec(Connection connection, Map<String, String> params) throws SQLException {

        String ddlContent = params.get("ddl");
        String dbType = params.get("dbType");
        if (ddlContent == null || dbType == null) {
            throw new SQLException("参数【ddl】或【dbType】为 null");
        }

        ExecResult<List<TableEntity>> ret = new ExecResult<>();
        List<TableEntity> tableEntities = parseTableDDL(ddlContent, dbType);
        ret.setStatus(ExecResult.SUCCESS);
        ret.setBody(tableEntities);

        return ret;
    }

    private List<TableEntity> parseTableDDL(String ddl, String dbType) {
        List<TableEntity> tableEntities = new ArrayList<>();

        List<SQLStatement> list = SQLUtils.parseStatements(ddl, dbType);
        for (SQLStatement statement : list) {
            if (!(statement instanceof SQLCreateTableStatement)) {
                continue;
            }

            TableEntity tableEntity = new TableEntity();
            SQLCreateTableStatement stmt = (SQLCreateTableStatement) statement;

            tableEntity.setDefKey(stmt.getTableName());
            tableEntity.setDefName(stmt.getTableName());
            tableEntity.setComment(stmt.getComment()!= null? stmt.getComment().toString():"");
            tableEntity.setTABLE_CAT(stmt.getCatalog());
            tableEntity.setTABLE_SCHEM(stmt.getSchema());

            for (SQLColumnDefinition col: stmt.getColumnDefinitions()) {
                ColumnField field = new ColumnField();
                field.setDefKey(col.getColumnName());
                field.setDefName(col.getColumnName());
                field.setComment(col.getComment()!=null? col.getComment().toString():null);
                field.setTypeName(col.getDataType().getName());
                field.setDataType(col.getDataType().jdbcType());

                field.setPrimaryKey(col.isPrimaryKey());
                field.setNotNull(col.containsNotNullConstaint());
                field.setAutoIncrement(col.isAutoIncrement());
                field.setDefaultValue(col.getDefaultExpr()!=null? col.getDefaultExpr().toString():null);

                tableEntity.getFields().add(field);
            }

            tableEntities.add(tableEntity);
        }

        return tableEntities;
    }
}
