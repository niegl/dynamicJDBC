package flowdesigner.jdbc.model;

import lombok.Data;

@Data
public class FKColumnField {
    private String PKTABLE_CAT;// String => parent key table catalog (may be null)
    private String PKTABLE_SCHEM;// String => parent key table schema (may be null)
    private String PKTABLE_NAME;// String => parent key table name
    private String PKCOLUMN_NAME;// String => parent key column name
    private String FKTABLE_CAT;// String => foreign key table catalog (may be null) being exported (may be null)
    private String FKTABLE_SCHEM;// String => foreign key table schema (may be null) being exported (may be null)
    private String FKTABLE_NAME;// String => foreign key table name being exported
    private String FKCOLUMN_NAME;// String => foreign key column name being exported
    private short KEY_SEQ;// short => sequence number within foreign key( a value of 1 represents the first column of the foreign key, a value of 2 would represent the second column within the foreign key).
    private short UPDATE_RULE;// short => What happens to foreign key when parent key is updated:

    private String FK_NAME;// String => foreign key name (may be null)
    private String PK_NAME;// String => parent key name (may be null)
}
