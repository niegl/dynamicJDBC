/*
 * Copyright 2019-2029 FISOK(www.fisok.cn).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package flowdesigner.jdbc.command.model;


import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Data
public class TableEntity {
    private String id;
    private int rowNo;              //行号，从1开始
    private String TABLE_CAT;
    private String TABLE_SCHEM;
    private String defKey;          //表代码
    private String defName;         //表名称
    private String comment = "";    //表注释说明
    private Map<String,String> properties = new LinkedHashMap<String,String>();     //扩展属性
    private List<ColumnField> fields = new ArrayList<ColumnField>();                //字段列表
    private List<TableIndex> indexes = new ArrayList<TableIndex>();             //表索引

    /**
     * 查找字段
     * @param columnDefKey
     * @return
     */
    public ColumnField lookupField(String columnDefKey) {
        List<ColumnField> fieldList = getFields();
        for(ColumnField field : fieldList){
            if(columnDefKey.equalsIgnoreCase(field.getDefKey())){
                return field;
            }
        }
        return null;
    }

    /**
     * 查找索引
     * @param indexDefKey
     * @return
     */
    public TableIndex lookupIndex(String indexDefKey){
        List<TableIndex> indexes = getIndexes();
        for(TableIndex index : indexes){
            if(indexDefKey.equalsIgnoreCase(index.getDefKey())){
                return index;
            }
        }
        return null;
    }

    public void fillFieldsCalcValue(){
        for(int i=1;i <= fields.size();i++){
            ColumnField field = fields.get(i-1);
            field.setTableEntity(this);
            field.fillConvertNames();
            field.setRowNo(i);
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableEntity entity = (TableEntity) o;
        return Objects.equals(defKey, entity.defKey) &&
                Objects.equals(defName, entity.defName);
    }

    public int hashCode() {
        return Objects.hash(defKey, defName);
    }
}
