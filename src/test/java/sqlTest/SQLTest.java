package sqlTest;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.parser.SQLParserUtils;

import java.sql.SQLSyntaxErrorException;
import java.util.List;

public class SQLTest {

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

    @org.junit.jupiter.api.Test
    void testNotNull() throws SQLSyntaxErrorException {
        String dbType = "mysql";
        String sql ="CREATE TABLE Persons\n" +
                "(\n" +
                "Id_P int NOT NULL,\n" +
                "LastName varchar(255) NOT NULL,\n" +
                "FirstName varchar(255),\n" +
                "Address varchar(255),\n" +
                "City varchar(255)\n" +
                ")";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("解析后的SQL 为 : [" + statement.toString() +"]");
    }

    @org.junit.jupiter.api.Test
    void testPrimary0() throws SQLSyntaxErrorException {
        String dbType = "mysql";
        String sql ="CREATE TABLE Persons\n" +
                "(\n" +
                "Id_P int NOT NULL PRIMARY KEY,\n" +
                "LastName varchar(255) NOT NULL,\n" +
                "FirstName varchar(255),\n" +
                "Address varchar(255),\n" +
                "City varchar(255)\n" +
                ")";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("解析后的SQL 为 : [" + statement.toString() +"]");
    }
    @org.junit.jupiter.api.Test
    void testPrimary() throws SQLSyntaxErrorException {
        String dbType = "mysql";
        String sql ="CREATE TABLE Persons\n" +
                "(\n" +
                "Id_P int NOT NULL,\n" +
                "LastName varchar(255) NOT NULL,\n" +
                "FirstName varchar(255),\n" +
                "Address varchar(255),\n" +
                "City varchar(255),\n" +
                "CONSTRAINT pk_PersonID PRIMARY KEY (Id_P,LastName)\n" +
                ")";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("解析后的SQL 为 : [" + statement.toString() +"]");
    }

    @org.junit.jupiter.api.Test
    void testUnique() throws SQLSyntaxErrorException {
        String dbType = "mysql";
        String sql ="CREATE TABLE Persons\n" +
                "(\n" +
                "Id_P int NOT NULL UNIQUE\n" +
                ")";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("解析后的SQL 为 : [" + statement.toString() +"]");
    }

    @org.junit.jupiter.api.Test
    void testUnique0() throws SQLSyntaxErrorException {
        String dbType = "mysql";
        String sql ="CREATE TABLE Persons\n" +
                "(\n" +
                "CONSTRAINT uc_PersonID UNIQUE (Id_P,LastName)\n" +
                ")";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("解析后的SQL 为 : [" + statement.toString() +"]");
    }

    @org.junit.jupiter.api.Test
    void testFOREIGN() throws SQLSyntaxErrorException {
        String dbType = "mysql";
        String sql ="CREATE TABLE Persons\n" +
                "(\n" +
                "FOREIGN KEY (Id_P) REFERENCES Persons(Id_P)\n" +
                ")";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("解析后的SQL 为 : [" + statement.toString() +"]");
    }

    @org.junit.jupiter.api.Test
    void testDatabase() throws SQLSyntaxErrorException {
        String dbType = "hive";
        String sql ="CREATE DATABASE IF NOT EXISTS db_name COMMENT database_comment";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("解析后的SQL 为 : [" + statement.toString() +"]");
    }

    @org.junit.jupiter.api.Test
    void testSelectJoin() throws SQLSyntaxErrorException {
        String dbType = "mysql";
        String sql ="SELECT a.runoob_id, a.runoob_author, b.runoob_count " +
                "FROM runoob_tbl a " +
                "INNER JOIN tcount_tbl b ON a.runoob_author = b.runoob_author;";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("解析后的SQL 为 : [" + statement.toString() +"]");
    }

    public static SQLStatement parser(String sql, String dbType) throws SQLSyntaxErrorException {
        List<SQLStatement> list = SQLUtils.parseStatements(sql, dbType);
        if (list.size() > 1) {
            throw new SQLSyntaxErrorException("MultiQueries is not supported,use single query instead ");
        }
        return list.get(0);
    }
}
