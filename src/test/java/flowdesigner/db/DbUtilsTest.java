package flowdesigner.db;

import com.alibaba.druid.DbType;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NavigableMap;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class DbUtilsTest {

    @Test
    void getFunctions() {
        Set<DbUtils.FunctionInfo> supportFunctions2 = DbUtils.getFunctions(DbType.postgresql);
        System.out.println(supportFunctions2);
    }

    @Test
    void pgFunctionsPreProcessor() {
        String content = "pg_control_checkpoint () → record\n" +
                "Returns information about current checkpoint state, as shown in Table 9.83.\n" +
                "pg_control_system () → record\n" +
                "Returns information about current control file state, as shown in Table 9.84.\n" +
                "pg_control_init () → record\n" +
                "Returns information about cluster initialization state, as shown in Table 9.85.\n" +
                "pg_control_recovery () → record\n" +
                "Returns information about recovery state, as shown in Table 9.86.";
        int groupCount = 2;
        StringBuilder stringBuffer = new StringBuilder();

        String[] lines = content.split("\n");
        for (int i = 0; i < lines.length; i+=groupCount) {
            String line1 = lines[i];
            String line2 = lines[i+1];

            int i1 = line1.indexOf("→");
            String signature = line1;
            if (i1!=-1) {
                signature = line1.substring(0, i1);
            }
            //如果只有一个参数，那么将参数替换为?
            int leftBracket = signature.indexOf("(");
            int rightBracket = signature.indexOf(")");
            if (leftBracket != rightBracket) {
                String substring = signature.substring(leftBracket+1, rightBracket);
                if (!substring.contains(",")) {
                    //几种特例
                    if (substring.trim().equals("VARIADIC \"any\"")) {

                    } else if (substring.trim().isEmpty()){

                    } else if (substring.contains("[]")){

                    }
                    else {
                        signature = signature.substring(0, leftBracket) + "( ? " + signature.substring(rightBracket);
                    }
                }
            }

            System.out.println(line1 + ":" + signature + ":" + line2);
        }
    }

    @Test
    void marianDBFunctionsPreProcessor() {
        String content =
                "AVG\n" +
                "Returns the average value.\n" +
                "BIT_AND\n" +
                "Bitwise AND.\n" +
                "BIT_OR\n" +
                "Bitwise OR.\n" +
                "BIT_XOR\n" +
                "Bitwise XOR.\n" +
                "COUNT\n" +
                "Returns count of non-null values.\n" +
                "CUME_DIST\n" +
                "Window function that returns the cumulative distribution of a given row.\n" +
                "DENSE_RANK\n" +
                "Rank of a given row with identical values receiving the same result, no skipping.\n" +

                "FIRST_VALUE\n" +
                "Returns the first result from an ordered set.\n" +

                "JSON_ARRAYAGG\n" +
                "Returns a JSON array containing an element for each value in a given set of JSON or SQL values.\n" +
                "JSON_OBJECTAGG\n" +
                "Returns a JSON object containing key-value pairs.\n" +

                "LAG\n" +
                "Accesses data from a previous row in the same result set without the need for a self-join.\n" +
                "LAST_VALUE\n" +
                "Returns the last value in a list or set of values.\n" +

                "LEAD\n" +
                "Accesses data from a following row in the same result set without the need for a self-join.\n" +
                "MAX\n" +
                "Returns the maximum value.\n" +
                "MEDIAN\n" +
                "Window function that returns the median value of a range of values.\n" +
                "MIN\n" +
                "Returns the minimum value.\n" +
                "NTH_VALUE\n" +
                "Returns the value evaluated at the specified row number of the window frame.\n" +
                "NTILE\n" +
                "Returns an integer indicating which group a given row falls into.\n" +
                "PERCENT_RANK\n" +
                "Window function that returns the relative percent rank of a given row.\n" +
                "PERCENTILE_CONT\n" +
                "Continuous percentile.\n" +
                "PERCENTILE_DISC\n" +
                "Discrete percentile.\n" +

                "RANK\n" +
                "Rank of a given row with identical values receiving the same result.\n" +
                "ROW_NUMBER\n" +
                "Row number of a given row with identical values receiving a different result.\n" +
                "STD\n" +
                "Population standard deviation.\n" +
                "STDDEV\n" +
                "Population standard deviation.\n" +
                "STDDEV_POP\n" +
                "Returns the population standard deviation.\n" +

                "STDDEV_SAMP\n" +
                "Standard deviation.\n" +
                "SUM\n" +
                "Sum total.\n" +
                "VAR_POP\n" +
                "Population standard variance.\n" +
                "VAR_SAMP\n" +
                "Returns the sample variance.\n" +
                "VARIANCE\n" +
                "under: » MariaDB Server Documentation » Using MariaDB Server » SQL Statements & Structure » SQL Statements » Built-in Functions » Aggregate FunctionsPopulation standard variance.\n" +

                "Aggregate Functions as Window Functions\n" +
                "It is possible to use aggregate functions as window functions.\n" +
                "ColumnStore Window Functions\n" +
                "Summary of window function use with the ColumnStore engine\n" +

                "Window Frames\n" +
                "Some window functions operate on window frames.";
        int GROUP_COUNT = 2;
        StringBuilder stringBuffer = new StringBuilder();

        ArrayList<String> newLines = new ArrayList<>();
        String[] lines = content.split("\n");

        // 计算对其函数名需要宽度
        int NameLength = 0;
        for (int i = 0; i < lines.length; i+=GROUP_COUNT) {
            String functionName = lines[i];
            newLines.add(functionName);
            newLines.add(lines[i+1]);
            int length = functionName.length();
            if (length > NameLength) {
                NameLength = length;
            }
        }
        NameLength += 8;    //2个TAB长度

        for (int i = 0; i < newLines.size(); i+=GROUP_COUNT) {
            String line1 = newLines.get(i);
            String line2 = newLines.get(i+1);

            int spaceNeeded = NameLength - line1.length() ;
            String signature = line1 + " ".repeat(spaceNeeded) + ": :";

            String finalLine = signature + line2;

            System.out.println(finalLine);
        }
    }

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

}