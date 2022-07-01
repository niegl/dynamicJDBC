package flowdesigner.jdbc.util;

import flowdesigner.jdbc.operators.SQLOperatorUtils;
import org.junit.jupiter.api.Test;

class FunctionUtilsTest {

    @Test
    void getSupportFunctions() {

    }

    @Test
    void getFunctionType() {
        System.out.println(SQLOperatorUtils.getFunctionType("sum"));
        System.out.println(SQLOperatorUtils.getFunctionType("acos"));
    }
}