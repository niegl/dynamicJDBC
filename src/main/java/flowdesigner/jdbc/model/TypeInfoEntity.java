package flowdesigner.jdbc.model;

import flowdesigner.jdbc.command.impl.DBReverseGetTypeInfoImpl;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 该类代表了数据库中可定义的数据类型。
 */
@Data
@AllArgsConstructor
public class TypeInfoEntity {
    private String TYPE_NAME;//  => Type name
    private int DATA_TYPE;//  => SQL data type from java.sql.Types

    public static TypeInfoEntity of(String TYPE_NAME, int DATA_TYPE) {
        return new TypeInfoEntity(TYPE_NAME, DATA_TYPE);
    }
}
