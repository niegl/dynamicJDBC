package sqlTest;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleExportParameterVisitor;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import org.junit.jupiter.api.Test;

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
        System.out.println("????????????SQL ??? : [" + statement.toString() +"]");
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
        System.out.println("????????????SQL ??? : [" + statement.toString() +"]");
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
        System.out.println("????????????SQL ??? : [" + statement.toString() +"]");
    }

    @org.junit.jupiter.api.Test
    void testUnique() throws SQLSyntaxErrorException {
        String dbType = "mysql";
        String sql ="CREATE TABLE Persons\n" +
                "(\n" +
                "Id_P int NOT NULL UNIQUE\n" +
                ")";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("????????????SQL ??? : [" + statement.toString() +"]");
    }

    @org.junit.jupiter.api.Test
    void testUnique0() throws SQLSyntaxErrorException {
        String dbType = "mysql";
        String sql ="CREATE TABLE Persons\n" +
                "(\n" +
                "CONSTRAINT uc_PersonID UNIQUE (Id_P,LastName)\n" +
                ")";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("????????????SQL ??? : [" + statement.toString() +"]");
    }

    @org.junit.jupiter.api.Test
    void testFOREIGN() throws SQLSyntaxErrorException {
        String dbType = "mysql";
        String sql ="CREATE TABLE Persons\n" +
                "(\n" +
                "FOREIGN KEY (Id_P) REFERENCES Persons(Id_P)\n" +
                ")";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("????????????SQL ??? : [" + statement.toString() +"]");
    }

    @org.junit.jupiter.api.Test
    void testDatabase() throws SQLSyntaxErrorException {
        String dbType = "hive";
        String sql ="CREATE DATABASE IF NOT EXISTS db_name COMMENT database_comment";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("????????????SQL ??? : [" + statement.toString() +"]");
    }

    @org.junit.jupiter.api.Test
    void testSelectJoin() throws SQLSyntaxErrorException {
        String dbType = "mysql";
        String sql ="SELECT a.runoob_id, a.runoob_author, b.runoob_count " +
                "FROM runoob_tbl a " +
                "INNER JOIN tcount_tbl b ON a.runoob_author = b.runoob_author;";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("????????????SQL ??? : [" + statement.toString() +"]");
    }

    /**
     * UNION [ALL | DISTINCT]
     * @throws SQLSyntaxErrorException
     */
    @org.junit.jupiter.api.Test
    void testUnion() throws SQLSyntaxErrorException {
        String dbType = "mysql";
        String sql ="select a.alarm_level_desc from std_pcode.t99_alarm_level_cd a union (select b.type_id  from std_pcode.dev_alarm_type_info b)";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("????????????SQL ??? : [" + statement.toString() +"]");
        sql ="SELECT a, b\n" +
                "FROM (SELECT building_t.idbuilding_t, building_t.name, building_t.floors, campus_t.idcampus_t FROM test.building_t INNER JOIN test.campus_t  ON building_t.campus_id = campus_t.idcampus_t) vv\n" +
                "UNION ALL\n" +
                "SELECT c, d\n" +
                "FROM test.building_t tt";
        statement = parser(sql, dbType);
        System.out.println("????????????SQL ??? : [" + statement.toString() +"]");
        sql ="SELECT a, b\n" +
                "FROM (SELECT building_t.idbuilding_t, building_t.name, building_t.floors, campus_t.idcampus_t FROM test.building_t INNER JOIN test.campus_t  ON building_t.campus_id = campus_t.idcampus_t) vv\n" +
                "UNION ALL\n" +
                "SELECT c, d\n" +
                "FROM test.building_t tt\n" +
                "UNION DISTINCT\n" +
                "SELECT e, f\n" +
                "FROM test.building_t2 tt";
        statement = parser(sql, dbType);
        System.out.println("????????????SQL ??? : [" + statement.toString() +"]");
    }

    @org.junit.jupiter.api.Test
    void testSelectBlock() throws SQLSyntaxErrorException {
        String dbType = "mysql";
        String sql ="select a.alarm_level_desc from std_pcode.t99_alarm_level_cd a";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("????????????SQL ??? : [" + statement.toString() +"]");
    }

    @org.junit.jupiter.api.Test
    void testTempTable0() throws SQLSyntaxErrorException {
        String dbType = "mysql";
        String sql ="CREATE TEMPORARY TABLE list_bucket_multiple (col1 STRING, col2 int, col3 STRING);";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("????????????SQL ??? : [" + statement.toString() +"]");
    }

    @org.junit.jupiter.api.Test
    void testTempTable1() throws SQLSyntaxErrorException {
        String dbType = "mysql";
        String sql ="CREATE TEMPORARY TABLE total_amount_by_discount like abc;";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("????????????SQL ??? : [" + statement.toString() +"]");
    }

    @org.junit.jupiter.api.Test
    void testTempTable2() throws SQLSyntaxErrorException {
        String dbType = "mysql";
        String sql ="CREATE TEMPORARY TABLE total_amount_by_discount as SELECT discount, total_amount FROM abc;";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("????????????SQL ??? : [" + statement.toString() +"]");
    }

    @org.junit.jupiter.api.Test
    void testTempTable3() throws SQLSyntaxErrorException {
        String dbType = "hive";
        String sql ="\n" +
                "\n" +
                "\n" +
                "--B0000001 ?????????\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "create temporary TABLE t1001\n" +
                "(\n" +
                "start_tm  timestamp,\n" +
                "end_tm timestamp,\n" +
                "stn_id varchar(50),\n" +
                "line_id varchar(50),\n" +
                "entry_qty int,\n" +
                "exit_qty int);\n" +
                "\n" +
                "insert into t1001\n" +
                "select\n" +
                "cast (concat(substr(start_tm,1,13),':00:00')  as timestamp ) as start_tm,\n" +
                "from_unixtime(unix_timestamp(cast (concat(substr(start_tm,1,13),':00:00')  as timestamp ) )+3600,'yyyy-MM-dd HH:mm:ss') as end_tm,\n" +
                "a.stn_id,\n" +
                "a.line_id,\n" +
                "sum(entry_qty),\n" +
                "sum(exit_qty)\n" +
                "from pdata.T05_Stn_PQ_Period_Stat a where a.start_tm>=to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2)))\n" +
                "group by\n" +
                "substr(start_tm,1,13),\n" +
                "a.stn_id,\n" +
                "a.line_id\n" +
                ";\n" +
                "\n" +
                "\n" +
                "--hour ???\n" +
                "insert OVERWRITE TABLE  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "A.start_tm,\n" +
                "A.end_tm,\n" +
                "A.Entry_Qty,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(A.start_tm,1,4),substr(A.start_tm,6,2),substr(A.start_tm,9,2)),\n" +
                "'B0000001',\n" +
                "'Hour'\n" +
                "FROM t1001 A\n" +
                " left join  PDATA.T02_Fac_Point_Pty_Rel_H B ON A.Stn_ID=B.Fac_Point_ID AND B.Fac_Point_Pty_Rel_Ctgy_CD='05'\n" +
                "where B.Pty_ID is not null and A.start_tm>=to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2)))\n" +
                ";\n" +
                "\n" +
                "--hour ??????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "A.start_tm,\n" +
                "A.end_tm,\n" +
                "A.Entry_Qty,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(A.start_tm,1,4),substr(A.start_tm,6,2),substr(A.start_tm,9,2)),\n" +
                "'B0000001',\n" +
                "'Hour'\n" +
                "FROM ( select line_id,start_tm,end_tm,sum(Entry_Qty) Entry_Qty from t1001 group by line_id,start_tm,end_tm ) A\n" +
                " left join pdata.t02_line_pty_rel_h B ON A.line_id=B.line_id AND B.line_pty_rel_ctgy_cd='20'\n" +
                "where B.Pty_ID is not null and A.start_tm>=to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2)))\n" +
                ";\n" +
                "\n" +
                "--hour ????????????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "'21021',\n" +
                "start_tm,\n" +
                "end_tm,\n" +
                "sum(Entry_Qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(start_tm,1,4),substr(start_tm,6,2),substr(start_tm,9,2)),\n" +
                "'B0000001',\n" +
                "'Hour'\n" +
                "FROM t1001 \n" +
                "where start_tm>=to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2)))\n" +
                "group by start_tm,end_tm \n" +
                ";\n" +
                "\n" +
                "--Day ???\n" +
                "insert OVERWRITE TABLE  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT \n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "cast(Stat_Dt as timestamp),\n" +
                "cast(date_add(Stat_Dt,1) as timestamp),\n" +
                "Entry_Qty,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(stat_dt,1,4),substr(stat_dt,6,2),substr(stat_dt,9,2)),\n" +
                "'B0000001',\n" +
                "'Day'\n" +
                "FROM PDATA.T05_Stn_PQ_Dt_Stat A \n" +
                " left join  PDATA.T02_Fac_Point_Pty_Rel_H B ON A.Stn_ID=B.Fac_Point_ID AND B.Fac_Point_Pty_Rel_Ctgy_CD='05'\n" +
                "where B.Pty_ID is not null\n" +
                "and stat_dt>=to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2)))\n" +
                ";\n" +
                "\n" +
                "--Day ??????\n" +
                "insert INTO  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT \n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "cast(Stat_Dt as timestamp),\n" +
                "cast(date_add(Stat_Dt,1) as timestamp),\n" +
                "Entry_Qty,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(stat_dt,1,4),substr(stat_dt,6,2),substr(stat_dt,9,2)),\n" +
                "'B0000001',\n" +
                "'Day'\n" +
                "FROM ( SELECT Stat_Dt,line_id,SUM(Entry_Qty) Entry_Qty FROM PDATA.T05_Stn_PQ_Dt_Stat \n" +
                "        where stat_dt>=to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2)))\n" +
                "        GROUP BY Stat_Dt,line_id\n" +
                ") A \n" +
                " left join  pdata.t02_line_pty_rel_h B ON A.line_id=B.line_id AND B.line_pty_rel_ctgy_cd='20'\n" +
                "where B.Pty_ID is not null\n" +
                ";\n" +
                "\n" +
                "--Day ????????????\n" +
                "insert INTO  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT \n" +
                "1,\n" +
                "'jz',\n" +
                "'21021',\n" +
                "cast(Stat_Dt as timestamp),\n" +
                "cast(date_add(Stat_Dt,1) as timestamp),\n" +
                "sum(Entry_Qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(stat_dt,1,4),substr(stat_dt,6,2),substr(stat_dt,9,2)),\n" +
                "'B0000001',\n" +
                "'Day'\n" +
                "FROM PDATA.T05_Stn_PQ_Dt_Stat \n" +
                "where stat_dt>=to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2)))\n" +
                "GROUP BY Stat_Dt\n" +
                ";\n" +
                "\n" +
                "\n" +
                "--Month ???\n" +
                "\n" +
                "insert OVERWRITE TABLE  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "  SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(Stat_Dt),1) as timestamp),\n" +
                "sum(Entry_Qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2)),\n" +
                "'B0000001',\n" +
                "'Month'\n" +
                "FROM PDATA.T05_Stn_PQ_Dt_Stat A\n" +
                " left join  PDATA.T02_Fac_Point_Pty_Rel_H B ON A.Stn_ID=B.Fac_Point_ID AND B.Fac_Point_Pty_Rel_Ctgy_CD='05'\n" +
                "where B.Pty_ID is not null\n" +
                "and A.stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'MM')\n" +
                "group by B.Pty_ID ,cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(Stat_Dt),1) as timestamp),\n" +
                "concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2))\n" +
                ";\n" +
                "\n" +
                "--Month ??????\n" +
                "insert into ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "  SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "f_starttime,\n" +
                "f_stoptime,\n" +
                "Entry_Qty,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(SUBSTR(f_starttime,1,4),SUBSTR(f_starttime,6,2)),\n" +
                "'B0000001',\n" +
                "'Month'\n" +
                "FROM(select cast(trunc(Stat_Dt,'MM') as timestamp) f_starttime,\n" +
                "        cast(date_add(last_day(Stat_Dt),1) as timestamp) f_stoptime,sum(Entry_Qty) Entry_Qty,line_id\n" +
                "        from  PDATA.T05_Stn_PQ_Dt_Stat \n" +
                "        where stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'MM')\n" +
                "        group by cast(trunc(Stat_Dt,'MM') as timestamp),cast(date_add(last_day(Stat_Dt),1) as timestamp),line_id\n" +
                ") A\n" +
                " left join  pdata.t02_line_pty_rel_h B ON A.line_id=B.line_id AND B.line_pty_rel_ctgy_cd='20'\n" +
                "where B.Pty_ID is not null\n" +
                ";\n" +
                "\n" +
                "--Month ????????????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "'21021',\n" +
                "cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(Stat_Dt),1) as timestamp),\n" +
                "sum(Entry_Qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2)),\n" +
                "'B0000001',\n" +
                "'Month'\n" +
                "FROM PDATA.T05_Stn_PQ_Dt_Stat \n" +
                "where stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'MM')\n" +
                "group by cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(Stat_Dt),1) as timestamp),\n" +
                "concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2))\n" +
                ";\n" +
                "\n" +
                "--Year ??????\n" +
                "insert OVERWRITE TABLE  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "  SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "cast(trunc(Stat_Dt,'YY') as timestamp),\n" +
                "cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp),\n" +
                "sum(Entry_Qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "SUBSTR(Stat_Dt,1,4),\n" +
                "'B0000001',\n" +
                "'Year'\n" +
                "FROM PDATA.T05_Stn_PQ_Dt_Stat A\n" +
                " left join  PDATA.T02_Fac_Point_Pty_Rel_H B ON A.Stn_ID=B.Fac_Point_ID AND B.Fac_Point_Pty_Rel_Ctgy_CD='05'\n" +
                "where B.Pty_ID is not null \n" +
                "and  A.stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'YY')\n" +
                "group by B.Pty_ID ,cast(trunc(Stat_Dt,'YY') as timestamp),\n" +
                "cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp),\n" +
                "SUBSTR(Stat_Dt,1,4)\n" +
                ";\n" +
                "\n" +
                "--Year ??????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "  SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "f_starttime,\n" +
                "f_stoptime,\n" +
                "Entry_Qty,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "SUBSTR(f_starttime,1,4),\n" +
                "'B0000001',\n" +
                "'Year'\n" +
                "FROM ( select cast(trunc(Stat_Dt,'YY') as timestamp) f_starttime,\n" +
                "        cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp) f_stoptime,sum(Entry_Qty) Entry_Qty,line_id\n" +
                "        from PDATA.T05_Stn_PQ_Dt_Stat \n" +
                "        where stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'YY')\n" +
                "        group by cast(trunc(Stat_Dt,'YY') as timestamp) ,\n" +
                "        cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp) ,line_id\n" +
                ")A\n" +
                " left join  pdata.t02_line_pty_rel_h B ON A.line_id=B.line_id AND B.line_pty_rel_ctgy_cd='20'\n" +
                "where B.Pty_ID is not null\n" +
                ";\n" +
                "\n" +
                "--Year ????????????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "  SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "'21021',\n" +
                "cast(trunc(Stat_Dt,'YY') as timestamp),\n" +
                "cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp),\n" +
                "sum(Entry_Qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "SUBSTR(Stat_Dt,1,4),\n" +
                "'B0000001',\n" +
                "'Year'\n" +
                "from PDATA.T05_Stn_PQ_Dt_Stat \n" +
                "where stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'YY')\n" +
                "group by cast(trunc(Stat_Dt,'YY') as timestamp) ,cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp)\n" +
                ",SUBSTR(Stat_Dt,1,4)\n" +
                ";\n" +
                "\n" +
                "\n" +
                "\n" +
                "--B0000002 ?????????\n" +
                "\n" +
                "\n" +
                "-- Hour ??????\n" +
                "insert OVERWRITE TABLE  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "A.start_tm,\n" +
                "A.end_tm,\n" +
                "A.Exit_Qty,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(A.start_tm,1,4),substr(A.start_tm,6,2),substr(A.start_tm,9,2)),\n" +
                "'B0000002',\n" +
                "'Hour'\n" +
                "FROM t1001 A\n" +
                " left join  PDATA.T02_Fac_Point_Pty_Rel_H B ON A.Stn_ID=B.Fac_Point_ID AND B.Fac_Point_Pty_Rel_Ctgy_CD='05'\n" +
                "where B.Pty_ID is not null and A.start_tm>=to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2)))\n" +
                ";\n" +
                "\n" +
                "--hour ??????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "A.start_tm,\n" +
                "A.end_tm,\n" +
                "A.Exit_Qty,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(A.start_tm,1,4),substr(A.start_tm,6,2),substr(A.start_tm,9,2)),\n" +
                "'B0000002',\n" +
                "'Hour'\n" +
                "FROM ( select line_id,start_tm,end_tm,sum(Exit_Qty) Exit_Qty from t1001 group by line_id,start_tm,end_tm ) A\n" +
                " left join pdata.t02_line_pty_rel_h B ON A.line_id=B.line_id AND B.line_pty_rel_ctgy_cd='20'\n" +
                "where B.Pty_ID is not null and A.start_tm>=to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2)))\n" +
                ";\n" +
                "\n" +
                "--hour ????????????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "'21021',\n" +
                "start_tm,\n" +
                "end_tm,\n" +
                "sum(Exit_Qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(start_tm,1,4),substr(start_tm,6,2),substr(start_tm,9,2)),\n" +
                "'B0000002',\n" +
                "'Hour'\n" +
                "FROM t1001 \n" +
                "where start_tm>=to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2)))\n" +
                "group by start_tm,end_tm\n" +
                ";\n" +
                "\n" +
                "\n" +
                "-- Day ??????\n" +
                "\n" +
                "insert OVERWRITE TABLE  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT \n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "cast(Stat_Dt as timestamp),\n" +
                "cast(date_add(Stat_Dt,1) as timestamp),\n" +
                "Exit_Qty,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(stat_dt,1,4),substr(stat_dt,6,2),substr(stat_dt,9,2)),\n" +
                "'B0000002',\n" +
                "'Day'\n" +
                "FROM PDATA.T05_Stn_PQ_Dt_Stat A \n" +
                " left join  PDATA.T02_Fac_Point_Pty_Rel_H B ON A.Stn_ID=B.Fac_Point_ID AND B.Fac_Point_Pty_Rel_Ctgy_CD='05'\n" +
                "where B.Pty_ID is not null\n" +
                "and A.stat_dt>=to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2)))\n" +
                ";\n" +
                "\n" +
                "--Day ??????\n" +
                "insert INTO  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT \n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "cast(Stat_Dt as timestamp),\n" +
                "cast(date_add(Stat_Dt,1) as timestamp),\n" +
                "Exit_Qty,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(stat_dt,1,4),substr(stat_dt,6,2),substr(stat_dt,9,2)),\n" +
                "'B0000002',\n" +
                "'Day'\n" +
                "FROM ( SELECT Stat_Dt,line_id,SUM(Exit_Qty) Exit_Qty FROM PDATA.T05_Stn_PQ_Dt_Stat \n" +
                "        where stat_dt>=to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2)))\n" +
                "        GROUP BY Stat_Dt,line_id\n" +
                ") A \n" +
                " left join  pdata.t02_line_pty_rel_h B ON A.line_id=B.line_id AND B.line_pty_rel_ctgy_cd='20'\n" +
                "where B.Pty_ID is not null\n" +
                ";\n" +
                "\n" +
                "--Day ????????????\n" +
                "insert INTO  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT \n" +
                "1,\n" +
                "'jz',\n" +
                "'21021',\n" +
                "cast(Stat_Dt as timestamp),\n" +
                "cast(date_add(Stat_Dt,1) as timestamp),\n" +
                "sum(Exit_Qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(stat_dt,1,4),substr(stat_dt,6,2),substr(stat_dt,9,2)),\n" +
                "'B0000002',\n" +
                "'Day'\n" +
                "FROM PDATA.T05_Stn_PQ_Dt_Stat \n" +
                "where stat_dt>=to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2)))\n" +
                "GROUP BY Stat_Dt\n" +
                ";\n" +
                "\n" +
                "\n" +
                "-- Month ??????\n" +
                "insert OVERWRITE TABLE  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "  SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(Stat_Dt),1) as timestamp),\n" +
                "sum(Exit_Qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2)),\n" +
                "'B0000002',\n" +
                "'Month'\n" +
                "FROM PDATA.T05_Stn_PQ_Dt_Stat A\n" +
                " left join  PDATA.T02_Fac_Point_Pty_Rel_H B ON A.Stn_ID=B.Fac_Point_ID AND B.Fac_Point_Pty_Rel_Ctgy_CD='05'\n" +
                "where B.Pty_ID is not null \n" +
                "and A.stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'MM')\n" +
                "group by B.Pty_ID ,cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(Stat_Dt),1) as timestamp),\n" +
                "concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2))\n" +
                ";\n" +
                "\n" +
                "\n" +
                "--Month ??????\n" +
                "insert into ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "  SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "f_starttime,\n" +
                "f_stoptime,\n" +
                "Exit_Qty,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(SUBSTR(f_starttime,1,4),SUBSTR(f_starttime,6,2)),\n" +
                "'B0000002',\n" +
                "'Month'\n" +
                "FROM(select cast(trunc(Stat_Dt,'MM') as timestamp) f_starttime,\n" +
                "        cast(date_add(last_day(Stat_Dt),1) as timestamp) f_stoptime,sum(Exit_Qty) Exit_Qty,line_id\n" +
                "        from  PDATA.T05_Stn_PQ_Dt_Stat \n" +
                "        where stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'MM')\n" +
                "        group by cast(trunc(Stat_Dt,'MM') as timestamp),cast(date_add(last_day(Stat_Dt),1) as timestamp),line_id\n" +
                ") A\n" +
                " left join  pdata.t02_line_pty_rel_h B ON A.line_id=B.line_id AND B.line_pty_rel_ctgy_cd='20'\n" +
                "where B.Pty_ID is not null\n" +
                ";\n" +
                "\n" +
                "--Month ????????????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "'21021',\n" +
                "cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(Stat_Dt),1) as timestamp),\n" +
                "sum(Exit_Qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2)),\n" +
                "'B0000002',\n" +
                "'Month'\n" +
                "FROM PDATA.T05_Stn_PQ_Dt_Stat \n" +
                "where stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'MM')\n" +
                "group by cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(Stat_Dt),1) as timestamp),\n" +
                "concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2))\n" +
                ";\n" +
                "\n" +
                "\n" +
                "-- Year ??????\n" +
                "insert OVERWRITE TABLE  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)  \n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "cast(trunc(Stat_Dt,'YY') as timestamp),\n" +
                "cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp),\n" +
                "sum(Exit_Qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "SUBSTR(stat_dt,1,4),\n" +
                "'B0000002',\n" +
                "'Year'\n" +
                "FROM PDATA.T05_Stn_PQ_Dt_Stat A\n" +
                " left join  PDATA.T02_Fac_Point_Pty_Rel_H B ON A.Stn_ID=B.Fac_Point_ID AND B.Fac_Point_Pty_Rel_Ctgy_CD='05'\n" +
                "where B.Pty_ID is not null \n" +
                "and A.stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'YY')\n" +
                "group by B.Pty_ID ,cast(trunc(Stat_Dt,'YY') as timestamp),\n" +
                "cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp),SUBSTR(stat_dt,1,4)\n" +
                ";\n" +
                "\n" +
                "--Year ??????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "  SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "f_starttime,\n" +
                "f_stoptime,\n" +
                "Exit_Qty,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "SUBSTR(f_starttime,1,4),\n" +
                "'B0000002',\n" +
                "'Year'\n" +
                "FROM ( select cast(trunc(Stat_Dt,'YY') as timestamp) f_starttime,\n" +
                "        cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp) f_stoptime,sum(Exit_Qty) Exit_Qty,line_id\n" +
                "        from PDATA.T05_Stn_PQ_Dt_Stat \n" +
                "        where stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'YY')\n" +
                "        group by cast(trunc(Stat_Dt,'YY') as timestamp) ,\n" +
                "        cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp) ,line_id\n" +
                ")A\n" +
                " left join  pdata.t02_line_pty_rel_h B ON A.line_id=B.line_id AND B.line_pty_rel_ctgy_cd='20'\n" +
                "where B.Pty_ID is not null\n" +
                ";\n" +
                "\n" +
                "--Year ????????????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "  SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "'21021',\n" +
                "cast(trunc(Stat_Dt,'YY') as timestamp),\n" +
                "cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp),\n" +
                "sum(Exit_Qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "SUBSTR(stat_dt,1,4),\n" +
                "'B0000002',\n" +
                "'Year'\n" +
                "from PDATA.T05_Stn_PQ_Dt_Stat \n" +
                "where stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'YY')\n" +
                "group by cast(trunc(Stat_Dt,'YY') as timestamp) ,cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp),SUBSTR(stat_dt,1,4)\n" +
                ";\n" +
                "\n" +
                "\n" +
                "--B0000004       ?????????\n" +
                "\n" +
                "-- Day ??????\n" +
                "insert OVERWRITE TABLE  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "cast(Stat_Dt as timestamp),\n" +
                "cast(date_add(Stat_Dt,1) as timestamp),\n" +
                "pasgr_qty,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(stat_dt,1,4),substr(stat_dt,6,2),substr(stat_dt,9,2)),\n" +
                "'B0000004',\n" +
                "'Day'\n" +
                "FROM PDATA.T05_Pasgr_Qty_Dt_Stat A\n" +
                " left join  pdata.T02_Line_Pty_Rel_H  B ON A.line_id=B.line_id  AND B.line_pty_rel_ctgy_cd='20'\n" +
                "where B.Pty_ID is not null\n" +
                "and A.stat_dt>=to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2)))\n" +
                ";\n" +
                "\n" +
                "-- Day ????????????\n" +
                "insert into ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "'21021',\n" +
                "cast(Stat_Dt as timestamp),\n" +
                "cast(date_add(Stat_Dt,1) as timestamp),\n" +
                "sum(pasgr_qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(stat_dt,1,4),substr(stat_dt,6,2),substr(stat_dt,9,2)),\n" +
                "'B0000004',\n" +
                "'Day'\n" +
                "FROM PDATA.T05_Pasgr_Qty_Dt_Stat \n" +
                "where stat_dt>=to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2)))\n" +
                "group by Stat_Dt\n" +
                ";\n" +
                "\n" +
                "-- Month ??????\n" +
                "insert OVERWRITE TABLE  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "  SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(Stat_Dt),1) as timestamp),\n" +
                "sum(pasgr_qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2)),\n" +
                "'B0000004',\n" +
                "'Month'\n" +
                "FROM PDATA.T05_Pasgr_Qty_Dt_Stat A\n" +
                " left join  pdata.T02_Line_Pty_Rel_H  B ON A.line_id=B.line_id  AND B.line_pty_rel_ctgy_cd='20'\n" +
                "where B.Pty_ID is not null\n" +
                "and A.stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'MM')\n" +
                "group by B.Pty_ID ,cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(Stat_Dt),1) as timestamp),\n" +
                "concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2))\n" +
                ";\n" +
                "\n" +
                "-- Month ????????????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "  SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "'21021',\n" +
                "cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(Stat_Dt),1) as timestamp),\n" +
                "sum(pasgr_qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2)),\n" +
                "'B0000004',\n" +
                "'Month'\n" +
                "FROM PDATA.T05_Pasgr_Qty_Dt_Stat \n" +
                "where stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'MM')\n" +
                "group by cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(Stat_Dt),1) as timestamp),\n" +
                "concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2))\n" +
                ";\n" +
                "\n" +
                "-- Year ??????\n" +
                "insert OVERWRITE TABLE  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "cast(trunc(Stat_Dt,'YY') as timestamp),\n" +
                "cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp),\n" +
                "sum(pasgr_qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "SUBSTR(A.Stat_Dt,1,4),\n" +
                "'B0000004',\n" +
                "'Year'\n" +
                "FROM  PDATA.T05_Pasgr_Qty_Dt_Stat  A\n" +
                " left join  pdata.T02_Line_Pty_Rel_H  B ON A.line_id=B.line_id  AND B.line_pty_rel_ctgy_cd='20'\n" +
                "where B.Pty_ID is not null\n" +
                "and A.stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'YY')\n" +
                "group by B.Pty_ID ,cast(trunc(Stat_Dt,'YY') as timestamp),\n" +
                "cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp),SUBSTR(A.Stat_Dt,1,4)\n" +
                ";\n" +
                "\n" +
                "-- Year ????????????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "'21021',\n" +
                "cast(trunc(Stat_Dt,'YY') as timestamp),\n" +
                "cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp),\n" +
                "sum(pasgr_qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "SUBSTR(Stat_Dt,1,4),\n" +
                "'B0000004',\n" +
                "'Year'\n" +
                "FROM  PDATA.T05_Pasgr_Qty_Dt_Stat \n" +
                "where stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'YY')\n" +
                "group by cast(trunc(Stat_Dt,'YY') as timestamp),\n" +
                "cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp),\n" +
                "SUBSTR(Stat_Dt,1,4)\n" +
                ";\n" +
                "\n" +
                "\n" +
                "\n" +
                "--B0000005\n" +
                "\n" +
                "\n" +
                "--Day ??????\n" +
                "\n" +
                "insert OVERWRITE TABLE  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "cast(Stat_Dt as timestamp),\n" +
                "cast(date_add(Stat_Dt,1) as timestamp),\n" +
                "pasgr_turnover,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(stat_dt,1,4),substr(stat_dt,6,2),substr(stat_dt,9,2)),\n" +
                "'B0000005',\n" +
                "'Day'\n" +
                "FROM PDATA.T05_Pasgr_Turnover_Dt_Stat A\n" +
                " left join  pdata.T02_Line_Pty_Rel_H  B ON A.line_id=B.line_id  AND B.line_pty_rel_ctgy_cd='20'\n" +
                "where B.Pty_ID is not null\n" +
                "and A.stat_dt>=to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2)))\n" +
                ";\n" +
                "\n" +
                "--Day ????????????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "'21021',\n" +
                "cast(Stat_Dt as timestamp),\n" +
                "cast(date_add(Stat_Dt,1) as timestamp),\n" +
                "sum(pasgr_turnover),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(stat_dt,1,4),substr(stat_dt,6,2),substr(stat_dt,9,2)),\n" +
                "'B0000005',\n" +
                "'Day'\n" +
                "FROM PDATA.T05_Pasgr_Turnover_Dt_Stat \n" +
                "where stat_dt>=to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2)))\n" +
                "group by Stat_Dt\n" +
                ";\n" +
                "\n" +
                "\n" +
                "\n" +
                "--Month ??????\n" +
                "insert OVERWRITE TABLE  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "  SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(Stat_Dt),1) as timestamp),\n" +
                "sum(pasgr_turnover),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(SUBSTR(A.Stat_Dt,1,4),SUBSTR(A.Stat_Dt,6,2)),\n" +
                "'B0000005',\n" +
                "'Month'\n" +
                "FROM PDATA.T05_Pasgr_Turnover_Dt_Stat A\n" +
                " left join  pdata.T02_Line_Pty_Rel_H  B ON A.line_id=B.line_id  AND B.line_pty_rel_ctgy_cd='20'\n" +
                "where B.Pty_ID is not null\n" +
                "and A.stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'MM')\n" +
                "group by B.Pty_ID ,cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(Stat_Dt),1) as timestamp),\n" +
                "concat(SUBSTR(A.Stat_Dt,1,4),SUBSTR(A.Stat_Dt,6,2))\n" +
                ";\n" +
                "\n" +
                "--Month ????????????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "'21021',\n" +
                "cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(Stat_Dt),1) as timestamp),\n" +
                "sum(pasgr_turnover),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2)),\n" +
                "'B0000005',\n" +
                "'Month'\n" +
                "FROM PDATA.T05_Pasgr_Turnover_Dt_Stat \n" +
                "where stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'MM')\n" +
                "group by cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(Stat_Dt),1) as timestamp),\n" +
                "concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2))\n" +
                ";\n" +
                "\n" +
                "\n" +
                "--Year ??????\n" +
                "insert OVERWRITE TABLE  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "cast(trunc(Stat_Dt,'YY') as timestamp),\n" +
                "cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp),\n" +
                "sum(pasgr_turnover),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "SUBSTR(stat_dt,1,4),\n" +
                "'B0000005',\n" +
                "'Year'\n" +
                "FROM  PDATA.T05_Pasgr_Turnover_Dt_Stat  A\n" +
                " left join  pdata.T02_Line_Pty_Rel_H  B ON A.line_id=B.line_id  AND B.line_pty_rel_ctgy_cd='20'\n" +
                "where B.Pty_ID is not null\n" +
                "and A.stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'YY')\n" +
                "group by B.Pty_ID ,cast(trunc(Stat_Dt,'YY') as timestamp),\n" +
                "cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp),SUBSTR(stat_dt,1,4)\n" +
                ";\n" +
                "\n" +
                "--Year ???????????? \n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "'21021',\n" +
                "cast(trunc(Stat_Dt,'YY') as timestamp),\n" +
                "cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp),\n" +
                "sum(pasgr_turnover),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "SUBSTR(stat_dt,1,4),\n" +
                "'B0000005',\n" +
                "'Year'\n" +
                "FROM  PDATA.T05_Pasgr_Turnover_Dt_Stat  \n" +
                "where stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'YY')\n" +
                "group by cast(trunc(Stat_Dt,'YY') as timestamp),\n" +
                "cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp),SUBSTR(stat_dt,1,4)\n" +
                ";\n" +
                "\n" +
                "\n" +
                "--B0000007\n" +
                "\n" +
                "--Day ??????\n" +
                "--?????????20180105???????????????????????????????????????7???\n" +
                "insert OVERWRITE TABLE  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "cast(Stat_Dt as timestamp),\n" +
                "cast(date_add(Stat_Dt,1) as timestamp),\n" +
                "trac_km/100,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(A.stat_dt,1,4),substr(A.stat_dt,6,2),substr(A.stat_dt,9,2)),\n" +
                "'B0000007',\n" +
                "'Day'\n" +
                "FROM PDATA.T05_Train_Mov_Stat A\n" +
                " left join  pdata.T02_Line_Pty_Rel_H  B ON A.line_id=B.line_id  AND B.line_pty_rel_ctgy_cd='20'\n" +
                "where B.Pty_ID is not null\n" +
                "and A.stat_dt>=date_add(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))),-7)\n" +
                ";\n" +
                "\n" +
                "--Day ????????????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "'21021',\n" +
                "cast(Stat_Dt as timestamp),\n" +
                "cast(date_add(Stat_Dt,1) as timestamp),\n" +
                "sum(trac_km)/100,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(stat_dt,1,4),substr(stat_dt,6,2),substr(stat_dt,9,2)),\n" +
                "'B0000007',\n" +
                "'Day'\n" +
                "FROM PDATA.T05_Train_Mov_Stat \n" +
                "where stat_dt>=date_add(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))),-7)\n" +
                "group by Stat_Dt\n" +
                ";\n" +
                "\n" +
                "--Month ??????\n" +
                "insert OVERWRITE TABLE  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(Stat_Dt),1) as timestamp),\n" +
                "sum(trac_km)/100,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(SUBSTR(A.Stat_Dt,1,4),SUBSTR(A.Stat_Dt,6,2)),\n" +
                "'B0000007',\n" +
                "'Month'\n" +
                "FROM PDATA.T05_Train_Mov_Stat A\n" +
                " left join  pdata.T02_Line_Pty_Rel_H  B ON A.line_id=B.line_id  AND B.line_pty_rel_ctgy_cd='20'\n" +
                "where B.Pty_ID is not null\n" +
                "and A.stat_dt>=trunc(date_add(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,-7),'MM')\n" +
                "group by B.Pty_ID ,cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(Stat_Dt),1) as timestamp),\n" +
                "concat(SUBSTR(A.Stat_Dt,1,4),SUBSTR(A.Stat_Dt,6,2))\n" +
                ";\n" +
                "\n" +
                "--Month ????????????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "'21021',\n" +
                "cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(Stat_Dt),1) as timestamp),\n" +
                "sum(trac_km)/100,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2)),\n" +
                "'B0000007',\n" +
                "'Month'\n" +
                "FROM PDATA.T05_Train_Mov_Stat \n" +
                "where stat_dt>=trunc(date_add(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,-7),'MM')\n" +
                "group by cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(Stat_Dt),1) as timestamp),\n" +
                "concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2))\n" +
                ";\n" +
                "\n" +
                "--Year ??????\n" +
                "insert OVERWRITE TABLE  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "cast(trunc(Stat_Dt,'YY') as timestamp),\n" +
                "cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp),\n" +
                "sum(trac_km)/100,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "SUBSTR(A.Stat_Dt,1,4),\n" +
                "'B0000007',\n" +
                "'Year'\n" +
                "FROM  PDATA.T05_Train_Mov_Stat  A\n" +
                " left join  pdata.T02_Line_Pty_Rel_H  B ON A.line_id=B.line_id  AND B.line_pty_rel_ctgy_cd='20'\n" +
                "where B.Pty_ID is not null\n" +
                "and A.stat_dt>=trunc(date_add(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,-7),'YY')\n" +
                "group by B.Pty_ID ,cast(trunc(Stat_Dt,'YY') as timestamp),\n" +
                "cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp),\n" +
                "SUBSTR(A.Stat_Dt,1,4)\n" +
                ";\n" +
                "\n" +
                "--Year ????????????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "'21021',\n" +
                "cast(trunc(Stat_Dt,'YY') as timestamp),\n" +
                "cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp),\n" +
                "sum(trac_km)/100,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "SUBSTR(Stat_Dt,1,4),\n" +
                "'B0000007',\n" +
                "'Year'\n" +
                "FROM  PDATA.T05_Train_Mov_Stat \n" +
                "where stat_dt>=trunc(date_add(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,-7),'YY')\n" +
                "group by cast(trunc(Stat_Dt,'YY') as timestamp),\n" +
                "cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp),SUBSTR(Stat_Dt,1,4)\n" +
                ";\n" +
                "\n" +
                "\n" +
                "\n" +
                "--'B0000003' ?????????\n" +
                "create temporary TABLE t1002\n" +
                "(\n" +
                "start_tm  timestamp,\n" +
                "end_tm timestamp,\n" +
                "traf_stn_id varchar(50),\n" +
                "xfer_out_line_id varchar(50),\n" +
                "xfer_in_line_id varchar(50),\n" +
                "traf_qty int);\n" +
                "\n" +
                "insert into t1002\n" +
                "select\n" +
                "cast (concat(substr(start_tm,1,13),':00:00')  as timestamp ) as start_tm,\n" +
                "from_unixtime(unix_timestamp(cast (concat(substr(start_tm,1,13),':00:00')  as timestamp ) )+3600,'yyyy-MM-dd HH:mm:ss') as end_tm,\n" +
                "traf_stn_id,\n" +
                "xfer_out_line_id,\n" +
                "xfer_in_line_id,\n" +
                "sum(traf_qty)\n" +
                "from pdata.T05_TRAF_QTY_PERIOD_STAT where stat_dt>=to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2)))\n" +
                "group by\n" +
                "substr(start_tm,1,13),\n" +
                "traf_stn_id,\n" +
                "xfer_out_line_id,\n" +
                "xfer_in_line_id\n" +
                ";\n" +
                "\n" +
                "--hour ??????\n" +
                "insert OVERWRITE TABLE  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "C.Pty_ID,\n" +
                "A.start_tm,\n" +
                "A.end_tm,\n" +
                "A.traf_qty,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(A.start_tm,1,4),substr(A.start_tm,6,2),substr(A.start_tm,9,2)),\n" +
                "'B0000003',\n" +
                "'Hour'\n" +
                "FROM t1002 A\n" +
                "inner join pdata.T02_Stn_Traf_Stn_Rel_H B  on A.Traf_Stn_ID=B.Traf_Stn_ID and A.xfer_in_line_id=substr(B.stn_id,1,2)\n" +
                "inner join pdata.T02_Fac_Point_Pty_Rel_H C on  B.stn_id=C.Fac_Point_ID and C.Fac_Point_Pty_Rel_Ctgy_CD='05'\n" +
                ";\n" +
                "\n" +
                "--hour ??????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "A.start_tm,\n" +
                "A.end_tm,\n" +
                "A.traf_qty,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(A.start_tm,1,4),substr(A.start_tm,6,2),substr(A.start_tm,9,2)),\n" +
                "'B0000003',\n" +
                "'Hour'\n" +
                " from (\n" +
                "  select xfer_in_line_id,start_tm,end_tm,sum(traf_qty) traf_qty from t1002 group by xfer_in_line_id,start_tm,end_tm\n" +
                " ) A\n" +
                " left join  pdata.t02_line_pty_rel_h B ON A.xfer_in_line_id=B.line_id AND B.line_pty_rel_ctgy_cd='20'\n" +
                "where B.Pty_ID is not null\n" +
                ";\n" +
                "\n" +
                "--hour ????????????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "select\n" +
                "1,\n" +
                "'jz',\n" +
                "'21021',\n" +
                "start_tm,\n" +
                "end_tm,\n" +
                "sum(traf_qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(start_tm,1,4),substr(start_tm,6,2),substr(start_tm,9,2)),\n" +
                "'B0000003',\n" +
                "'Hour'\n" +
                " from  t1002\n" +
                " group by start_tm,end_tm \n" +
                ";\n" +
                "\n" +
                "--Day ??????\n" +
                "insert OVERWRITE TABLE  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "select\n" +
                "1,\n" +
                "'jz',\n" +
                "C.Pty_ID,\n" +
                "cast(A.Stat_Dt as timestamp),\n" +
                "cast(date_add(A.Stat_Dt,1) as timestamp),\n" +
                "traf_qty,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(stat_dt,1,4),substr(stat_dt,6,2),substr(stat_dt,9,2)),\n" +
                "'B0000003',\n" +
                "'Day'\n" +
                " from\n" +
                "pdata.T05_Traf_Qty_Dt_Stat A\n" +
                "inner join pdata.T02_Stn_Traf_Stn_Rel_H B  on A.Traf_Stn_ID=B.Traf_Stn_ID and A.xfer_in_line_id=substr(B.stn_id,1,2)\n" +
                "inner join pdata.T02_Fac_Point_Pty_Rel_H C on  B.stn_id=C.Fac_Point_ID and C.Fac_Point_Pty_Rel_Ctgy_CD='05'\n" +
                "where c.Pty_ID is not null\n" +
                "AND A.stat_dt>=to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2)))\n" +
                ";\n" +
                "\n" +
                "--Day ??????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "select\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "cast(A.Stat_Dt as timestamp),\n" +
                "cast(date_add(A.Stat_Dt,1) as timestamp),\n" +
                "traf_qty,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(stat_dt,1,4),substr(stat_dt,6,2),substr(stat_dt,9,2)),\n" +
                "'B0000003',\n" +
                "'Day'\n" +
                " from ( select xfer_in_line_id,Stat_Dt,sum(traf_qty) traf_qty from pdata.T05_Traf_Qty_Dt_Stat\n" +
                "        where stat_dt>=to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2)))\n" +
                "        group by xfer_in_line_id,Stat_Dt \n" +
                " ) A\n" +
                " left join  pdata.t02_line_pty_rel_h B ON A.xfer_in_line_id=B.line_id AND B.line_pty_rel_ctgy_cd='20'\n" +
                "where B.Pty_ID is not null\n" +
                ";\n" +
                "\n" +
                "--Day ????????????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "select\n" +
                "1,\n" +
                "'jz',\n" +
                "'21021',\n" +
                "cast(Stat_Dt as timestamp),\n" +
                "cast(date_add(Stat_Dt,1) as timestamp),\n" +
                "sum(traf_qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(substr(stat_dt,1,4),substr(stat_dt,6,2),substr(stat_dt,9,2)),\n" +
                "'B0000003',\n" +
                "'Day'\n" +
                " from  pdata.T05_Traf_Qty_Dt_Stat\n" +
                " where stat_dt>=to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2)))\n" +
                " group by Stat_Dt \n" +
                ";\n" +
                "\n" +
                "--Month ??????\n" +
                "insert OVERWRITE TABLE  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "select\n" +
                "1,\n" +
                "'jz',\n" +
                "C.Pty_ID,\n" +
                "cast(trunc(A.Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(A.Stat_Dt),1) as timestamp),\n" +
                "sum(traf_qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2)),\n" +
                "'B0000003',\n" +
                "'Month'\n" +
                " from\n" +
                "pdata.T05_Traf_Qty_Dt_Stat A\n" +
                "inner join pdata.T02_Stn_Traf_Stn_Rel_H B  on A.Traf_Stn_ID=B.Traf_Stn_ID and A.xfer_in_line_id=substr(B.stn_id,1,2)\n" +
                "inner join pdata.T02_Fac_Point_Pty_Rel_H C on  B.stn_id=C.Fac_Point_ID and C.Fac_Point_Pty_Rel_Ctgy_CD='05'\n" +
                "where c.Pty_ID is not null \n" +
                "and A.stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'MM')\n" +
                "group by C.Pty_ID ,cast(trunc(A.Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(A.Stat_Dt),1) as timestamp),concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2))\n" +
                ";\n" +
                "\n" +
                "--Month ??????\n" +
                "insert INTO  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "select\n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "f_starttime,\n" +
                "f_stoptime,\n" +
                "traf_qty,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "A.stat_dt,\n" +
                "'B0000003',\n" +
                "'Month'\n" +
                " from\n" +
                "( select cast(trunc(Stat_Dt,'MM') as timestamp) f_starttime,\n" +
                "        cast(date_add(last_day(Stat_Dt),1) as timestamp) f_stoptime,\n" +
                "        concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2)) stat_dt,\n" +
                "        xfer_in_line_id,sum(traf_qty) traf_qty from pdata.T05_Traf_Qty_Dt_Stat\n" +
                "        where stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'MM')\n" +
                "        group by xfer_in_line_id ,cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "        cast(date_add(last_day(Stat_Dt),1) as timestamp),concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2))\n" +
                ") A\n" +
                " left join  pdata.T02_Line_Pty_Rel_H  B ON A.xfer_in_line_id=B.line_id  AND B.line_pty_rel_ctgy_cd='20'\n" +
                "where B.Pty_ID is not null\n" +
                ";\n" +
                "\n" +
                "--Month ????????????\n" +
                "insert INTO  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "select\n" +
                "1,\n" +
                "'jz',\n" +
                "'21021',\n" +
                "cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                "cast(date_add(last_day(Stat_Dt),1) as timestamp),\n" +
                "sum(traf_qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2)),\n" +
                "'B0000003',\n" +
                "'Month'\n" +
                " from pdata.T05_Traf_Qty_Dt_Stat\n" +
                "where stat_dt>=trunc(to_date(CONCAT(SUBSTR(${TXDATE},1,4),'-',SUBSTR(${TXDATE},5,2),'-',SUBSTR(${TXDATE},7,2))) ,'MM')\n" +
                " group by cast(trunc(Stat_Dt,'MM') as timestamp),\n" +
                " cast(date_add(last_day(Stat_Dt),1) as timestamp),concat(SUBSTR(Stat_Dt,1,4),SUBSTR(Stat_Dt,6,2))\n" +
                ";\n" +
                "\n" +
                "--Year ??????\n" +
                "insert OVERWRITE TABLE  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "select \n" +
                "1,\n" +
                "'jz',\n" +
                "C.Pty_ID,\n" +
                "cast(trunc(A.Stat_Dt,'YY') as timestamp),\n" +
                "cast(date_add(to_date(concat(year(A.Stat_Dt),'-12-31')),1) as timestamp),\n" +
                "sum(traf_qty),\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "SUBSTR(A.Stat_Dt,1,4),\n" +
                "'B0000003',\n" +
                "'Year'\n" +
                " from \n" +
                "pdata.T05_Traf_Qty_Dt_Stat A \n" +
                "inner join pdata.T02_Stn_Traf_Stn_Rel_H B  on A.Traf_Stn_ID=B.Traf_Stn_ID and A.xfer_in_line_id=substr(B.stn_id,1,2)\n" +
                "inner join pdata.T02_Fac_Point_Pty_Rel_H C on  B.stn_id=C.Fac_Point_ID and C.Fac_Point_Pty_Rel_Ctgy_CD='05'\n" +
                "where c.Pty_ID is not null \n" +
                "group by C.Pty_ID ,cast(trunc(A.Stat_Dt,'YY') as timestamp),\n" +
                "cast(date_add(to_date(concat(year(A.Stat_Dt),'-12-31')),1) as timestamp),SUBSTR(A.Stat_Dt,1,4)\n" +
                ";\n" +
                "\n" +
                "\n" +
                "--Year ??????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "select \n" +
                "1,\n" +
                "'jz',\n" +
                "B.Pty_ID,\n" +
                "f_starttime,\n" +
                "f_stoptime,\n" +
                "traf_qty,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "SUBSTR(f_starttime,1,4),\n" +
                "'B0000003',\n" +
                "'Year'\n" +
                " from \n" +
                "( select cast(trunc(Stat_Dt,'YY') as timestamp) f_starttime,\n" +
                "        cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp) f_stoptime,\n" +
                "        sum(traf_qty) traf_qty,xfer_in_line_id\n" +
                "  from pdata.T05_Traf_Qty_Dt_Stat \n" +
                "        group by cast(trunc(Stat_Dt,'YY') as timestamp),cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp),xfer_in_line_id\n" +
                ") A \n" +
                " left join  pdata.T02_Line_Pty_Rel_H  B ON A.xfer_in_line_id=B.line_id  AND B.line_pty_rel_ctgy_cd='20'\n" +
                "where B.Pty_ID is not null\n" +
                ";\n" +
                "\n" +
                "--Year ????????????\n" +
                "insert into  ENERGY_CONFIG.T_EQ_EnergyQuota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "select \n" +
                "1,\n" +
                "'jz',\n" +
                "'21021',\n" +
                "cast(trunc(Stat_Dt,'YY') as timestamp) f_starttime,\n" +
                "cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp) f_stoptime,\n" +
                "sum(traf_qty) traf_qty,\n" +
                "1,\n" +
                "null,\n" +
                "current_timestamp,\n" +
                "SUBSTR(Stat_Dt,1,4),\n" +
                "'B0000003',\n" +
                "'Year'\n" +
                " from pdata.T05_Traf_Qty_Dt_Stat \n" +
                "group by cast(trunc(Stat_Dt,'YY') as timestamp),cast(date_add(to_date(concat(year(Stat_Dt),'-12-31')),1) as timestamp),SUBSTR(Stat_Dt,1,4)\n" +
                ";\n" +
                "\n" +
                "--???????????????  20180531\n" +
                "insert into energy_config.t_eq_energyquota PARTITION(stat_dt,f_quotacode,f_recordtype)\n" +
                "SELECT '1' f_accountsetid,'yf' f_energyobjtype,\n" +
                "case when substr(f_energyobjid,1,7)='2102101' then '2101101'\n" +
                " when substr(f_energyobjid,1,7)='2102102' then '2101102'\n" +
                " when substr(f_energyobjid,1,7)='2102103' then '2101103'\n" +
                " when substr(f_energyobjid,1,7)='2102104' then '2101104'\n" +
                " end f_energyobjid, \n" +
                "f_starttime, f_stoptime, sum(f_quotaval) f_quotaval, '1' f_state, null, current_timestamp, stat_dt, f_quotacode, f_recordtype \n" +
                "FROM energy_config.t_eq_energyquota\n" +
                "where opt_tm>=current_date and length(f_energyobjid)=9\n" +
                "group by substr(f_energyobjid,1,7), f_starttime, f_stoptime, stat_dt, f_quotacode, f_recordtype;\n" +
                "\n" +
                "\n";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("????????????SQL ??? : [" + statement.toString() +"]");
    }

    @org.junit.jupiter.api.Test
    void testInsert() throws SQLSyntaxErrorException {
        String dbType = "hive";
        String sql ="INSERT INTO first_table_name PARTITION(data_dt='${erg_date}') (column1, column2,  columnN) \n" +
                "SELECT column1, column2, columnN \n" +
                "FROM second_table_name\n" +
                "WHERE column1=2;";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("????????????SQL ??? : [" + statement.toString() +"]");
    }

    @org.junit.jupiter.api.Test
    void testOr() throws SQLSyntaxErrorException {
        String dbType = "hive";
        String sql ="SELECT a, b FROM tablea WHERE data_dt = '2022-10' AND a = 2 OR (b = 4 AND aa = 2)";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("????????????SQL ??? : [" + statement.toString() +"]");
    }

    @org.junit.jupiter.api.Test
    void testJoin() throws SQLSyntaxErrorException {
        String dbType = "hive";
        String sql ="SELECT a, b FROM A join B on A.a=B.a and A.b = B.b;";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("????????????SQL ??? : [" + statement.toString() +"]");
    }

    @org.junit.jupiter.api.Test
    void testSet() throws SQLSyntaxErrorException {
        String dbType = "hive";
        String sql ="--Modify history list:Created by ?????????\n" +
                "\n" +
                "set ngmr.furion.pool=DEFAULT;";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("????????????SQL ??? : [" + statement.toString() +"]");
    }

    @org.junit.jupiter.api.Test
    void testInsertIntoColumns() throws SQLSyntaxErrorException {
        String dbType = "hive";
        String sql ="INSERT INTO Persons (LastName, Address) VALUES (('Wilson', 'Champs-Elysees'),('Wilson2', 'Champs-Elysees2'))";
        SQLStatement statement = parser(sql, dbType);
        System.out.println("????????????SQL ??? : [" + statement.toString() +"]");
    }

    @Test
    void testExportParamized() {
        String sql = "select name,age from test_tab1 where name='name' and age = 11 and id in  ('A','B')";
        final StringBuilder out = new StringBuilder();
        final ExportParameterVisitor visitor = new OracleExportParameterVisitor(out);
        // visitor.setParameterizedMergeInList(true);
        SQLStatementParser parser = new OracleStatementParser(sql);
        final SQLStatement parseStatement = parser.parseStatement();
        parseStatement.accept(visitor);
        final List<Object> plist = visitor.getParameters();
        sql = out.toString();
        System.out.println("src:"+sql);
        System.out.println("sql:"+sql+" params:"+plist);
    }

    @Test
    void testGroup() throws SQLSyntaxErrorException {
        String sql = "select name,age from test_tab1 group by name";
        parser(sql, "hive");
    }

    @Test
    void testDescribeFunction() throws SQLSyntaxErrorException {
        String sql = "describe function acos";
        parser(sql, "hive");
    }
    @Test
    void testParseCreate() throws SQLSyntaxErrorException {
        String sql = "CREATE TABLE IF NOT EXISTS `runoob_tbl`(\n" +
                "   `runoob_id` INT UNSIGNED AUTO_INCREMENT,\n" +
                "   `runoob_title` VARCHAR(100) NOT NULL,\n" +
                "   `runoob_author` VARCHAR(40) NOT NULL,\n" +
                "   `submission_date` DATE,\n" +
                "   PRIMARY KEY ( `runoob_id` )\n" +
                ")ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        parser(sql, "mysql");
    }

    @Test
    void testVariant() throws SQLSyntaxErrorException {
        String sql = "INSERT  OVERWRITE  TABLE ftpchk.partition_table\n" +
                " PARTITION(dt='20220608',seqdt='20220608170320_00')\n" +
                "(name,\n" +
                "cardnum)\n" +
                " SELECT  name,cardnum  from ftpchk.temp_11002120220608110021_02_00_00_0_20220608170320_00;";
        parser(sql, "hive");
    }



    public static SQLStatement parser(String sql, String dbType) throws SQLSyntaxErrorException {
        List<SQLStatement> list = SQLUtils.parseStatements(sql, dbType);
        list.forEach(statement -> {
            final StringBuilder out = new StringBuilder();
            SQLASTOutputVisitor visitor = new SQLASTOutputVisitor(out);
//            visitor.setDesensitize(true);
            visitor.setParameterizedQuesUnMergeInList(true);
            statement.accept(visitor);
            System.out.println("????????????SQL ??? : [" + out.toString() +"]");
        });
        if (list.size() > 1) {
            throw new SQLSyntaxErrorException("MultiQueries is not supported,use single query instead");
        }
        return list.get(0);
    }
}
