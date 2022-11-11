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

@Data
public class ColumnField {
    private int rowNo;              //行号，从1开始
    private String defKey;          //字段代码
    private String defName;         //字段名称
    private int dataType;           // SQL type from java.sql.Types
    private String typeName = "";       // TYPE_NAME String => Data source dependent type name, for a UDT the type name is fully qualified，如varchar

    private String comment = "";    //字段注释说明
    private String domain = "";                     //数据域（暂时留空，前端自行匹配）
    private Integer len = null;     //字段长度，如32
    private Integer scale = null;   //字段小数位数据
    private String typeFullName = "";//类型+长度+小数位数
    private Boolean primaryKey = Boolean.FALSE;     //是否主键
    private String primaryKeyName = "";
    private Boolean notNull = Boolean.FALSE;        //是否允许为空
    private String notNullName = "";
    private Boolean autoIncrement = Boolean.FALSE;  //是否自增
    private String autoIncrementName = "";
    private String refDict = "";               //引用数据字典
    private String defaultValue = "";               //默认值
    private Boolean hideInGraph = Boolean.FALSE;    //关系图是否隐藏（第15个之前，默认为true)


    public void fillConvertNames() {
        //处理类型名
        StringBuffer buffer = new StringBuffer(typeName);
        if(len != null && len > 0){
            buffer.append("(").append(len);
            if(scale != null && scale > 0){
                buffer.append(",").append(scale);
            }
            buffer.append(")");
        }
        typeFullName = buffer.toString();

        if(primaryKey == Boolean.TRUE) {
            primaryKeyName = "√";
        }
        if(notNull == Boolean.TRUE){
            notNullName = "√";
        }
        if(autoIncrement == Boolean.TRUE){
            autoIncrementName = "√";
        }
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return "ColumnField{" +
                "  rowNo=" + rowNo +
                ", defKey='" + defKey + '\'' +
                ", defName='" + defName + '\'' +
                ", dataType=" + dataType +
                ", typeName='" + typeName + '\'' +
                ", comment='" + comment + '\'' +
                ", domain='" + domain + '\'' +
                ", len=" + len +
                ", scale=" + scale +
                ", typeFullName='" + typeFullName + '\'' +
                ", primaryKey=" + primaryKey +
                ", primaryKeyName='" + primaryKeyName + '\'' +
                ", notNull=" + notNull +
                ", notNullName='" + notNullName + '\'' +
                ", autoIncrement=" + autoIncrement +
                ", autoIncrementName='" + autoIncrementName + '\'' +
                ", refDict='" + refDict + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", hideInGraph=" + hideInGraph +
                '}';
    }
}
