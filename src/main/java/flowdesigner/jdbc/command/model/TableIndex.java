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
import java.util.ArrayList;
import java.util.List;

@Data
public class TableIndex {
    private String id;
    private String defKey;          //索引代码
    private boolean unique = false; //索引是否唯一
    private String defName;         //索引名称
    private String comment = "";    //索引注释说明
    private String indexQualifier;  //INDEX_QUALIFIER String => index catalog (may be null);
    private List<TableIndexColumnField> fields = new ArrayList<>(); //索引下的字段明细
    /**
     * 查找索引
     * @param fieldDefKey
     * @return
     */
    public TableIndexColumnField lookupField(String fieldDefKey){
        List<TableIndexColumnField> fields = getFields();
        for(TableIndexColumnField field : fields){
            if(fieldDefKey.equalsIgnoreCase(field.getFieldDefKey())){
                return field;
            }
        }
        return null;
    }
}
