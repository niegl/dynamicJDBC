package flowdesigner.jdbc.command.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 表的主键. 一个表只能有一个主键
 */
@Data
public class TablePrimaryKey {

/**
 * Retrieves a description of the given table's primary key columns.  They
 * are ordered by COLUMN_NAME.
 *
 * <P>Each primary key column description has the following columns:
 *  <OL>
 *  <LI><B>TABLE_CAT</B> String {@code =>} table catalog (may be {@code null})
 *  <LI><B>TABLE_SCHEM</B> String {@code =>} table schema (may be {@code null})
 *  <LI><B>TABLE_NAME</B> String {@code =>} table name
 *  <LI><B>COLUMN_NAME</B> String {@code =>} column name
 *  <LI><B>KEY_SEQ</B> short {@code =>} sequence number within primary key( a value
 *  of 1 represents the first column of the primary key, a value of 2 would
 *  represent the second column within the primary key).
 *  <LI><B>PK_NAME</B> String {@code =>} primary key name (may be {@code null})
 *  </OL>
 * </P>
 */
    String pk_name; // 主键名称 (may be null)
    private List<TablePrimaryKeyColumnField> columns = new ArrayList<>(); //组成主键的字段列表，KEY_SEQ为字段在列表中的顺序

}
