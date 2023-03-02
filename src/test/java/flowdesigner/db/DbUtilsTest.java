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
        String content ="SYSCS_DIAG.CONTAINED_ROLES diagnostic table function\n" +
                "The SYSCS_DIAG.CONTAINED_ROLES diagnostic table function returns all the roles contained within the specified role.\n" +
                "SYSCS_DIAG.ERROR_LOG_READER diagnostic table function\n" +
                "The SYSCS_DIAG.ERROR_LOG_READER diagnostic table function contains all the useful SQL statements that are in the derby.log file or a log file that you specify.\n" +
                "SYSCS_DIAG.ERROR_MESSAGES diagnostic table\n" +
                "The SYSCS_DIAG.ERROR_MESSAGES diagnostic table shows all of the SQLStates, locale-sensitive error messages, and exception severities for a Derby database.\n" +
                "SYSCS_DIAG.LOCK_TABLE diagnostic table\n" +
                "The SYSCS_DIAG.LOCK_TABLE diagnostic table shows all of the locks that are currently held in the Derby database.\n" +
                "SYSCS_DIAG.SPACE_TABLE diagnostic table function\n" +
                "The SYSCS_DIAG.SPACE_TABLE diagnostic table function shows the space usage of a particular table and its indexes.\n" +
                "SYSCS_DIAG.STATEMENT_CACHE diagnostic table\n" +
                "The SYSCS_DIAG.STATEMENT_CACHE diagnostic table shows the contents of the SQL statement cache.\n" +
                "SYSCS_DIAG.STATEMENT_DURATION diagnostic table function\n" +
                "You can use the SYSCS_DIAG.STATEMENT_DURATION diagnostic table function to analyze the execution duration of the useful SQL statements in the derby.log file or a log file that you specify.\n" +
                "SYSCS_DIAG.TRANSACTION_TABLE diagnostic table\n" +
                "The SYSCS_DIAG.TRANSACTION_TABLE diagnostic table shows all of the transactions that are currently in the database.";
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