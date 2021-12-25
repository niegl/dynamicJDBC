package flowdesigner.jdbc.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SchemaEntity {
    private String defKey;          //代码
    @EqualsAndHashCode.Include
    private String TABLE_CAT;
    @EqualsAndHashCode.Include
    private String TABLE_SCHEM;
    private List<TableEntity> tables = new ArrayList<>();

    /**
     * 查找字段
     * @param defKey
     * @return
     */
    public TableEntity lookupTable(String defKey){
        List<TableEntity> tableList = getTables();
        for(TableEntity table : tableList){
            if(defKey.equalsIgnoreCase(table.getDefKey())){
                return table;
            }
        }
        return null;
    }
}
