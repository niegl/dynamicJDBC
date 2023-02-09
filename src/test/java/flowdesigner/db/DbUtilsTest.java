package flowdesigner.db;

import com.alibaba.druid.DbType;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DbUtilsTest {

    @Test
    void getFunctions() {
        Set<DbUtils.FunctionInfo> supportFunctions2 = DbUtils.getFunctions(DbType.postgresql);
        System.out.println(supportFunctions2);
    }

    @Test
    void pgFunctionsPreProcessor() {
        String content = "value == value → boolean\n" +
                "Equality comparison (this, and the other comparison operators, work on all JSON scalar values)\n" +
                "jsonb_path_query_array('[1, \"a\", 1, 3]', '$[*] ? (@ == 1)') → [1, 1]\n" +
                "value != value → boolean\n" +
                "Non-equality comparison\n" +
                "jsonb_path_query_array('[1, 2, 1, 3]', '$[*] ? (@ != 1)') → [2, 3]\n" +
                "value <> value → boolean\n" +
                "Non-equality comparison\n" +
                "jsonb_path_query_array('[1, 2, 1, 3]', '$[*] ? (@ != 1)') → [2, 3]\n" +
                "value < value → boolean\n" +
                "Less-than comparison\n" +
                "jsonb_path_query_array('[1, 2, 3]', '$[*] ? (@ < 2)') → [1]\n" +
                "value <= value → boolean\n" +
                "Less-than-or-equal-to comparison\n" +
                "jsonb_path_query_array('[\"a\", \"b\", \"c\"]', '$[*] ? (@ <= \"b\")') → [\"a\", \"b\"]\n" +
                "value > value → boolean\n" +
                "Greater-than comparison\n" +
                "jsonb_path_query_array('[1, 2, 3]', '$[*] ? (@ > 2)') → [3]\n" +
                "value >= value → boolean\n" +
                "Greater-than-or-equal-to comparison\n" +
                "jsonb_path_query_array('[1, 2, 3]', '$[*] ? (@ >= 2)') → [2, 3]\n" +
                "true → boolean\n" +
                "JSON constant true\n" +
                "jsonb_path_query('[{\"name\": \"John\", \"parent\": false}, {\"name\": \"Chris\", \"parent\": true}]', '$[*] ? (@.parent == true)') → {\"name\": \"Chris\", \"parent\": true}\n" +
                "false → boolean\n" +
                "JSON constant false\n" +
                "jsonb_path_query('[{\"name\": \"John\", \"parent\": false}, {\"name\": \"Chris\", \"parent\": true}]', '$[*] ? (@.parent == false)') → {\"name\": \"John\", \"parent\": false}\n" +
                "null → value\n" +
                "JSON constant null (note that, unlike in SQL, comparison to null works normally)\n" +
                "jsonb_path_query('[{\"name\": \"Mary\", \"job\": null}, {\"name\": \"Michael\", \"job\": \"driver\"}]', '$[*] ? (@.job == null) .name') → \"Mary\"\n" +
                "boolean && boolean → boolean\n" +
                "Boolean AND\n" +
                "jsonb_path_query('[1, 3, 7]', '$[*] ? (@ > 1 && @ < 5)') → 3\n" +
                "boolean || boolean → boolean\n" +
                "Boolean OR\n" +
                "jsonb_path_query('[1, 3, 7]', '$[*] ? (@ < 1 || @ > 5)') → 7\n" +
                "! boolean → boolean\n" +
                "Boolean NOT\n" +
                "jsonb_path_query('[1, 3, 7]', '$[*] ? (!(@ < 5))') → 7\n" +
                "boolean is unknown → boolean\n" +
                "Tests whether a Boolean condition is unknown.\n" +
                "jsonb_path_query('[-1, 2, 7, \"foo\"]', '$[*] ? ((@ > 0) is unknown)') → \"foo\"\n" +
                "string like_regex string [ flag string ] → boolean\n" +
                "Tests whether the first operand matches the regular expression given by the second operand, optionally with modifications described by a string of flag characters (see Section 9.16.2.3).\n" +
                "jsonb_path_query_array('[\"abc\", \"abd\", \"aBdC\", \"abdacb\", \"babc\"]', '$[*] ? (@ like_regex \"^ab.*c\")') → [\"abc\", \"abdacb\"]\n" +
                "string starts with string → boolean\n" +
                "Tests whether the second operand is an initial substring of the first operand.\n" +
                "jsonb_path_query('[\"John Smith\", \"Mary Stone\", \"Bob Johnson\"]', '$[*] ? (@ starts with \"John\")') → \"John Smith\"\n" +
                "exists ( path_expression ) → boolean\n" +
                "Tests whether a path expression matches at least one SQL/JSON item. Returns unknown if the path expression would result in an error; the second example uses this to avoid a no-such-key error in strict mode.\n" +
                "jsonb_path_query('{\"x\": [1, 2], \"y\": [2, 4]}', 'strict $.* ? (exists (@ ? (@[*] > 2)))') → [2, 4]";
        StringBuilder stringBuffer = new StringBuilder();

        String[] lines = content.split("\n");
        for (int i = 0; i < lines.length; i+=3) {
            String line1 = lines[i];
            String line2 = lines[i+1];

            String signature = line1.substring(0, line1.indexOf("→"));
            //如果只有一个参数，那么将参数替换为?
            int leftBracket = signature.indexOf("(");
            int rightBracket = signature.indexOf(")");
            if (leftBracket != rightBracket) {
                String substring = signature.substring(leftBracket+1, rightBracket);
                if (!substring.contains(",")) {
                    //几种特例
                    if (substring.trim().equals("VARIADIC \"any\"")) {

                    } else if (substring.trim().isEmpty()){

                    }
                    else {
                        signature = signature.substring(0, leftBracket) + "( ? " + signature.substring(rightBracket);
                    }
                }
            }

            System.out.println(line1 + ":" + signature + ":" + line2);
        }
    }


}