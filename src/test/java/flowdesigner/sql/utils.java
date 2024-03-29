package flowdesigner.sql;

import com.alibaba.druid.sql.parser.SQLType;
import com.alibaba.druid.util.HexBin;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class utils {

    @Test
    public void testEncode() {
        System.out.println(HexBin.encode(new byte[]{'d','a'}));
        System.out.println(Arrays.toString(HexBin.decode("6461")));
    }

    @Test
    void testSQLType() {
        System.out.println(SQLType.ADD.toString());
    }
}
