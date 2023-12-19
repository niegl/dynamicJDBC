package flowdesigner.jdbc.command.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import flowdesigner.jdbc.command.Command;
import flowdesigner.jdbc.command.model.ColumnField;
import flowdesigner.jdbc.command.model.TableEntity;
import flowdesigner.jdbc.command.ExecResult;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 利用druid解析DDL
 */
@Slf4j
public class DBParseDDLToTableImpl implements Command<ExecResult<List<TableEntity>>> {

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
            TableEntity tableEntity = new TableEntity();
            if ((statement instanceof SQLCreateTableStatement)) {
                SQLCreateTableStatement stmt = (SQLCreateTableStatement) statement;

                tableEntity.setTABLE_NAME(stmt.getTableName());
                tableEntity.setREMARKS(stmt.getComment()!= null? stmt.getComment().toString():"");
                tableEntity.setTABLE_CAT(stmt.getCatalog());
                tableEntity.setTABLE_SCHEM(stmt.getSchema());

                for (SQLColumnDefinition col: stmt.getColumnDefinitions()) {
                    ColumnField field = new ColumnField();
                    field.setDefKey(col.getColumnName());
                    field.setDefName(col.getColumnName());
                    field.setComment(col.getComment()!=null? col.getComment().toString():null);
                    field.setTypeName(col.getDataType().toString());
                    field.setDataType(col.getDataType().jdbcType());

                    field.setPrimaryKey(col.isPrimaryKey());
                    field.setNotNull(col.containsNotNullConstaint());
                    field.setAutoIncrement(col.isAutoIncrement());
                    field.setDefaultValue(col.getDefaultExpr()!=null? col.getDefaultExpr().toString():null);

                    tableEntity.getFields().add(field);
                }

            } else if (statement instanceof SQLSelectStatement) {
                SQLSelectStatement stmt = (SQLSelectStatement) statement;
                tableEntity.setTABLE_NAME("");
                tableEntity.setREMARKS("");
                tableEntity.setTABLE_CAT("");
                tableEntity.setTABLE_SCHEM("");

                SQLSelectQuery query = stmt.getSelect().getQuery();
                if ((query instanceof SQLSelectQueryBlock)) {
                    SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
                    List<SQLSelectItem> selectList = queryBlock.getSelectList();
                    for (SQLSelectItem selectItem: selectList) {
                        ColumnField field = new ColumnField();
                        // 有别名使用别名，没有别名使用字段名，如果是函数，那么使用函数作为字段名
                        String columnName = "";
                        if (selectItem.getAlias() != null && !selectItem.getAlias().isEmpty()) {
                            columnName = selectItem.getAlias();
                        } else {
                            SQLExpr expr = selectItem.getExpr();
                            if (expr instanceof SQLIdentifierExpr || expr instanceof SQLPropertyExpr) {
                                columnName = ((SQLName) expr).getSimpleName();
                            } else {
                                columnName = expr.toString();
                            }
                        }
                        field.setDefKey(columnName);
                        field.setDefName(columnName);
//                        field.setTypeName(selectItem.computeDataType().toString());
//                        field.setDataType(selectItem.computeDataType().jdbcType());

                        tableEntity.getFields().add(field);
                    }
                }

            } else {
                continue;
            }

            tableEntities.add(tableEntity);
        }

        return tableEntities;
    }
}
