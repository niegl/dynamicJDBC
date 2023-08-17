package flowdesigner.db;

import com.alibaba.druid.DbType;
import flowdesigner.jdbc.command.impl.DBExecuteImpl;
import flowdesigner.util.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class DbUtilsTest {

    @Test
    void getFunctions() {
        Set<DbUtils.FunctionInfo> supportFunctions2 = DbUtils.getFunctions(DbType.gbase);
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
                "";
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

    @Test
    void H2DBFunctionsPreProcessor() {
        String content ="ABS\n" +
                "ABS( { numeric | interval } )\n" +
                "Returns the absolute value of a specified value. The returned value is of the same data type as the parameter.\n" +
                "\n" +
                "Note that TINYINT, SMALLINT, INT, and BIGINT data types cannot represent absolute values of their minimum negative values, because they have more negative values than positive. For example, for INT data type allowed values are from -2147483648 to 2147483647. ABS(-2147483648) should be 2147483648, but this value is not allowed for this data type. It leads to an exception. To avoid it cast argument of this function to a higher data type.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "ABS(I)\n" +
                "ABS(CAST(I AS BIGINT))\n" +
                "\n" +
                "ACOS\n" +
                "ACOS(numeric)\n" +
                "Calculate the arc cosine. See also Java Math.acos. This method returns a double.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "ACOS(D)\n" +
                "\n" +
                "ASIN\n" +
                "ASIN(numeric)\n" +
                "Calculate the arc sine. See also Java Math.asin. This method returns a double.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "ASIN(D)\n" +
                "\n" +
                "ATAN\n" +
                "ATAN(numeric)\n" +
                "Calculate the arc tangent. See also Java Math.atan. This method returns a double.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "ATAN(D)\n" +
                "\n" +
                "COS\n" +
                "COS(numeric)\n" +
                "Calculate the trigonometric cosine. See also Java Math.cos. This method returns a double.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "COS(ANGLE)\n" +
                "\n" +
                "COSH\n" +
                "COSH(numeric)\n" +
                "Calculate the hyperbolic cosine. See also Java Math.cosh. This method returns a double.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "COSH(X)\n" +
                "\n" +
                "COT\n" +
                "COT(numeric)\n" +
                "Calculate the trigonometric cotangent (1/TAN(ANGLE)). See also Java Math.* functions. This method returns a double.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "COT(ANGLE)\n" +
                "\n" +
                "SIN\n" +
                "SIN(numeric)\n" +
                "Calculate the trigonometric sine. See also Java Math.sin. This method returns a double.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "SIN(ANGLE)\n" +
                "\n" +
                "SINH\n" +
                "SINH(numeric)\n" +
                "Calculate the hyperbolic sine. See also Java Math.sinh. This method returns a double.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "SINH(ANGLE)\n" +
                "\n" +
                "TAN\n" +
                "TAN(numeric)\n" +
                "Calculate the trigonometric tangent. See also Java Math.tan. This method returns a double.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "TAN(ANGLE)\n" +
                "\n" +
                "TANH\n" +
                "TANH(numeric)\n" +
                "Calculate the hyperbolic tangent. See also Java Math.tanh. This method returns a double.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "TANH(X)\n" +
                "\n" +
                "ATAN2\n" +
                "ATAN2(numeric, numeric)\n" +
                "Calculate the angle when converting the rectangular coordinates to polar coordinates. See also Java Math.atan2. This method returns a double.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "ATAN2(X, Y)\n" +
                "\n" +
                "BITAND\n" +
                "BITAND(expression, expression)\n" +
                "The bitwise AND operation. Arguments should have TINYINT, SMALLINT, INTEGER, BIGINT, BINARY, or BINARY VARYING data type. This function returns result of the same data type.\n" +
                "\n" +
                "For aggregate function see BIT_AND_AGG.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "BITAND(A, B)\n" +
                "\n" +
                "BITOR\n" +
                "BITOR(expression, expression)\n" +
                "The bitwise OR operation. Arguments should have TINYINT, SMALLINT, INTEGER, BIGINT, BINARY, or BINARY VARYING data type. This function returns result of the same data type.\n" +
                "\n" +
                "For aggregate function see BIT_OR_AGG.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "BITOR(A, B)\n" +
                "\n" +
                "BITXOR\n" +
                "BITXOR(expression, expression)\n" +
                "Arguments should have TINYINT, SMALLINT, INTEGER, BIGINT, BINARY, or BINARY VARYING data type. This function returns result of the same data type.\n" +
                "\n" +
                "For aggregate function see BIT_XOR_AGG.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "The bitwise XOR operation.\n" +
                "\n" +
                "BITNOT\n" +
                "BITNOT(expression)\n" +
                "The bitwise NOT operation. Argument should have TINYINT, SMALLINT, INTEGER, BIGINT, BINARY, or BINARY VARYING data type. This function returns result of the same data type.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "BITNOT(A)\n" +
                "\n" +
                "BITNAND\n" +
                "BITNAND(expression, expression)\n" +
                "The bitwise NAND operation equivalent to BITNOT(BITAND(expression, expression)). Arguments should have TINYINT, SMALLINT, INTEGER, BIGINT, BINARY, or BINARY VARYING data type. This function returns result of the same data type.\n" +
                "\n" +
                "For aggregate function see BIT_NAND_AGG.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "BITNAND(A, B)\n" +
                "\n" +
                "BITNOR\n" +
                "BITNOR(expression, expression)\n" +
                "The bitwise NOR operation equivalent to BITNOT(BITOR(expression, expression)). Arguments should have TINYINT, SMALLINT, INTEGER, BIGINT, BINARY, or BINARY VARYING data type. This function returns result of the same data type.\n" +
                "\n" +
                "For aggregate function see BIT_NOR_AGG.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "BITNOR(A, B)\n" +
                "\n" +
                "BITXNOR\n" +
                "BITXNOR(expression, expression)\n" +
                "The bitwise XNOR operation equivalent to BITNOT(BITXOR(expression, expression)). Arguments should have TINYINT, SMALLINT, INTEGER, BIGINT, BINARY, or BINARY VARYING data type. This function returns result of the same data type.\n" +
                "\n" +
                "For aggregate function see BIT_XNOR_AGG.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "BITXNOR(A, B)\n" +
                "\n" +
                "BITGET\n" +
                "BITGET(expression, long)\n" +
                "Returns true if and only if the first argument has a bit set in the position specified by the second parameter. The first argument should have TINYINT, SMALLINT, INTEGER, BIGINT, BINARY, or BINARY VARYING data type. This method returns a boolean. The second argument is zero-indexed; the least significant bit has position 0.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "BITGET(A, 1)\n" +
                "\n" +
                "BITCOUNT\n" +
                "BITCOUNT(expression)\n" +
                "Returns count of set bits in the specified value. Value should have TINYINT, SMALLINT, INTEGER, BIGINT, BINARY, or BINARY VARYING data type. This method returns a long.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "BITCOUNT(A)\n" +
                "\n" +
                "LSHIFT\n" +
                "LSHIFT(expression, long)\n" +
                "The bitwise signed left shift operation. Shifts the first argument by the number of bits given by the second argument. Argument should have TINYINT, SMALLINT, INTEGER, BIGINT, BINARY, or BINARY VARYING data type. This function returns result of the same data type.\n" +
                "\n" +
                "If number of bits is negative, a signed right shift is performed instead. For numeric values a sign bit is used for left-padding (with negative offset). If number of bits is equal to or larger than number of bits in value all bits are pushed out from the value. For binary string arguments signed and unsigned shifts return the same results.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "LSHIFT(A, B)\n" +
                "\n" +
                "RSHIFT\n" +
                "RSHIFT(expression, long)\n" +
                "The bitwise signed right shift operation. Shifts the first argument by the number of bits given by the second argument. Argument should have TINYINT, SMALLINT, INTEGER, BIGINT, BINARY, or BINARY VARYING data type. This function returns result of the same data type.\n" +
                "\n" +
                "If number of bits is negative, a signed left shift is performed instead. For numeric values a sign bit is used for left-padding (with positive offset). If number of bits is equal to or larger than number of bits in value all bits are pushed out from the value. For binary string arguments signed and unsigned shifts return the same results.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "RSHIFT(A, B)\n" +
                "\n" +
                "ULSHIFT\n" +
                "ULSHIFT(expression, long)\n" +
                "The bitwise unsigned left shift operation. Shifts the first argument by the number of bits given by the second argument. Argument should have TINYINT, SMALLINT, INTEGER, BIGINT, BINARY, or BINARY VARYING data type. This function returns result of the same data type.\n" +
                "\n" +
                "If number of bits is negative, an unsigned right shift is performed instead. If number of bits is equal to or larger than number of bits in value all bits are pushed out from the value. For binary string arguments signed and unsigned shifts return the same results.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "ULSHIFT(A, B)\n" +
                "\n" +
                "URSHIFT\n" +
                "URSHIFT(expression, long)\n" +
                "The bitwise unsigned right shift operation. Shifts the first argument by the number of bits given by the second argument. Argument should have TINYINT, SMALLINT, INTEGER, BIGINT, BINARY, or BINARY VARYING data type. This function returns result of the same data type.\n" +
                "\n" +
                "If number of bits is negative, an unsigned left shift is performed instead. If number of bits is equal to or larger than number of bits in value all bits are pushed out from the value. For binary string arguments signed and unsigned shifts return the same results.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "URSHIFT(A, B)\n" +
                "\n" +
                "ROTATELEFT\n" +
                "ROTATELEFT(expression, long)\n" +
                "The bitwise left rotation operation. Rotates the first argument by the number of bits given by the second argument. Argument should have TINYINT, SMALLINT, INTEGER, BIGINT, BINARY, or BINARY VARYING data type. This function returns result of the same data type.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "ROTATELEFT(A, B)\n" +
                "\n" +
                "ROTATERIGHT\n" +
                "ROTATERIGHT(expression, long)\n" +
                "The bitwise right rotation operation. Rotates the first argument by the number of bits given by the second argument. Argument should have TINYINT, SMALLINT, INTEGER, BIGINT, BINARY, or BINARY VARYING data type. This function returns result of the same data type.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "ROTATERIGHT(A, B)\n" +
                "\n" +
                "MOD\n" +
                "MOD(dividendNumeric, divisorNumeric)\n" +
                "The modulus expression.\n" +
                "\n" +
                "Result has the same type as divisor. Result is NULL if either of arguments is NULL. If divisor is 0, an exception is raised. Result has the same sign as dividend or is equal to 0.\n" +
                "\n" +
                "Usually arguments should have scale 0, but it isn't required by H2.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "MOD(A, B)\n" +
                "\n" +
                "CEIL\n" +
                "{ CEIL | CEILING } (numeric)\n" +
                "Returns the smallest integer value that is greater than or equal to the argument. This method returns value of the same type as argument, but with scale set to 0 and adjusted precision, if applicable.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CEIL(A)\n" +
                "\n" +
                "DEGREES\n" +
                "DEGREES(numeric)\n" +
                "See also Java Math.toDegrees. This method returns a double.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "DEGREES(A)\n" +
                "\n" +
                "EXP\n" +
                "EXP(numeric)\n" +
                "See also Java Math.exp. This method returns a double.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "EXP(A)\n" +
                "\n" +
                "FLOOR\n" +
                "FLOOR(numeric)\n" +
                "Returns the largest integer value that is less than or equal to the argument. This method returns value of the same type as argument, but with scale set to 0 and adjusted precision, if applicable.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "FLOOR(A)\n" +
                "\n" +
                "LN\n" +
                "LN(numeric)\n" +
                "Calculates the natural (base e) logarithm as a double value. Argument must be a positive numeric value.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "LN(A)\n" +
                "\n" +
                "LOG\n" +
                "LOG({baseNumeric, numeric | {numeric}})\n" +
                "Calculates the logarithm with specified base as a double value. Argument and base must be positive numeric values. Base cannot be equal to 1.\n" +
                "\n" +
                "The default base is e (natural logarithm), in the PostgreSQL mode the default base is base 10. In MSSQLServer mode the optional base is specified after the argument.\n" +
                "\n" +
                "Single-argument variant of LOG function is deprecated, use LN or LOG10 instead.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "LOG(2, A)\n" +
                "\n" +
                "LOG10\n" +
                "LOG10(numeric)\n" +
                "Calculates the base 10 logarithm as a double value. Argument must be a positive numeric value.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "LOG10(A)\n" +
                "\n" +
                "ORA_HASH\n" +
                "ORA_HASH(expression [, bucketLong [, seedLong]])\n" +
                "Computes a hash value. Optional bucket argument determines the maximum returned value. This argument should be between 0 and 4294967295, default is 4294967295. Optional seed argument is combined with the given expression to return the different values for the same expression. This argument should be between 0 and 4294967295, default is 0. This method returns a long value between 0 and the specified or default bucket value inclusive.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "ORA_HASH(A)\n" +
                "\n" +
                "RADIANS\n" +
                "RADIANS(numeric)\n" +
                "See also Java Math.toRadians. This method returns a double.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "RADIANS(A)\n" +
                "\n" +
                "SQRT\n" +
                "SQRT(numeric)\n" +
                "See also Java Math.sqrt. This method returns a double.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "SQRT(A)\n" +
                "\n" +
                "PI\n" +
                "PI()\n" +
                "See also Java Math.PI. This method returns a double.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "PI()\n" +
                "\n" +
                "POWER\n" +
                "POWER(numeric, numeric)\n" +
                "See also Java Math.pow. This method returns a double.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "POWER(A, B)\n" +
                "\n" +
                "RAND\n" +
                "{ RAND | RANDOM } ( [ int ] )\n" +
                "Calling the function without parameter returns the next a pseudo random number. Calling it with an parameter seeds the session's random number generator. This method returns a double between 0 (including) and 1 (excluding).\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "RAND()\n" +
                "\n" +
                "RANDOM_UUID\n" +
                "{ RANDOM_UUID | UUID } ()\n" +
                "Returns a new UUID with 122 pseudo random bits.\n" +
                "\n" +
                "Please note that using an index on randomly generated data will result on poor performance once there are millions of rows in a table. The reason is that the cache behavior is very bad with randomly distributed data. This is a problem for any database system.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "RANDOM_UUID()\n" +
                "\n" +
                "ROUND\n" +
                "ROUND(numeric [, digitsInt])\n" +
                "Rounds to a number of fractional digits. This method returns value of the same type as argument, but with adjusted precision and scale, if applicable.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "ROUND(N, 2)\n" +
                "\n" +
                "SECURE_RAND\n" +
                "SECURE_RAND(int)\n" +
                "Generates a number of cryptographically secure random numbers. This method returns bytes.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL SECURE_RAND(16)\n" +
                "\n" +
                "SIGN\n" +
                "SIGN( { numeric | interval } )\n" +
                "Returns -1 if the value is smaller than 0, 0 if zero or NaN, and otherwise 1.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "SIGN(N)\n" +
                "\n" +
                "ENCRYPT\n" +
                "ENCRYPT(algorithmString, keyBytes, dataBytes)\n" +
                "Encrypts data using a key. The supported algorithm is AES. The block size is 16 bytes. This method returns bytes.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL ENCRYPT('AES', '00', STRINGTOUTF8('Test'))\n" +
                "\n" +
                "DECRYPT\n" +
                "DECRYPT(algorithmString, keyBytes, dataBytes)\n" +
                "Decrypts data using a key. The supported algorithm is AES. The block size is 16 bytes. This method returns bytes.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL TRIM(CHAR(0) FROM UTF8TOSTRING(\n" +
                "    DECRYPT('AES', '00', '3fabb4de8f1ee2e97d7793bab2db1116')))\n" +
                "\n" +
                "HASH\n" +
                "HASH(algorithmString, expression [, iterationInt])\n" +
                "Calculate the hash value using an algorithm, and repeat this process for a number of iterations.\n" +
                "\n" +
                "This function supports MD5, SHA-1, SHA-224, SHA-256, SHA-384, SHA-512, SHA3-224, SHA3-256, SHA3-384, and SHA3-512 algorithms. SHA-224, SHA-384, and SHA-512 may be unavailable in some JREs.\n" +
                "\n" +
                "MD5 and SHA-1 algorithms should not be considered as secure.\n" +
                "\n" +
                "If this function is used to encrypt a password, a random salt should be concatenated with a password and this salt and result of the function should be stored to prevent a rainbow table attack and number of iterations should be large enough to slow down a dictionary or a brute force attack.\n" +
                "\n" +
                "This method returns bytes.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL HASH('SHA-256', 'Text', 1000)\n" +
                "CALL HASH('SHA3-256', X'0102')\n" +
                "\n" +
                "TRUNC\n" +
                "{ TRUNC | TRUNCATE } ( { {numeric [, digitsInt] }\n" +
                "| { timestamp | timestampWithTimeZone | date | timestampString } } )\n" +
                "When a numeric argument is specified, truncates it to a number of digits (to the next value closer to 0) and returns value of the same type as argument, but with adjusted precision and scale, if applicable.\n" +
                "\n" +
                "This function with datetime or string argument is deprecated, use DATE_TRUNC instead. When used with a timestamp, truncates the timestamp to a date (day) value and returns a timestamp with or without time zone depending on type of the argument. When used with a date, returns a timestamp at start of this date. When used with a timestamp as string, truncates the timestamp to a date (day) value and returns a timestamp without time zone.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "TRUNCATE(N, 2)\n" +
                "\n" +
                "COMPRESS\n" +
                "COMPRESS(dataBytes [, algorithmString])\n" +
                "Compresses the data using the specified compression algorithm. Supported algorithms are: LZF (faster but lower compression; default), and DEFLATE (higher compression). Compression does not always reduce size. Very small objects and objects with little redundancy may get larger. This method returns bytes.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "COMPRESS(STRINGTOUTF8('Test'))\n" +
                "\n" +
                "EXPAND\n" +
                "EXPAND(bytes)\n" +
                "Expands data that was compressed using the COMPRESS function. This method returns bytes.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "UTF8TOSTRING(EXPAND(COMPRESS(STRINGTOUTF8('Test'))))\n" +
                "\n" +
                "ZERO\n" +
                "ZERO()\n" +
                "Returns the value 0. This function can be used even if numeric literals are disabled.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "ZERO()\n" +
                "\n" +
                "String Functions\n" +
                "ASCII\n" +
                "ASCII(string)\n" +
                "Returns the ASCII value of the first character in the string. This method returns an int.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "ASCII('Hi')\n" +
                "\n" +
                "BIT_LENGTH\n" +
                "BIT_LENGTH(bytes)\n" +
                "Returns the number of bits in a binary string. This method returns a long.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "BIT_LENGTH(NAME)\n" +
                "\n" +
                "CHAR_LENGTH\n" +
                "{ CHAR_LENGTH | CHARACTER_LENGTH | { LENGTH } } ( string )\n" +
                "Returns the number of characters in a character string. This method returns a long.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CHAR_LENGTH(NAME)\n" +
                "\n" +
                "OCTET_LENGTH\n" +
                "OCTET_LENGTH(bytes)\n" +
                "Returns the number of bytes in a binary string. This method returns a long.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "OCTET_LENGTH(NAME)\n" +
                "\n" +
                "CHAR\n" +
                "{ CHAR | CHR } ( int )\n" +
                "Returns the character that represents the ASCII value. This method returns a string.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CHAR(65)\n" +
                "\n" +
                "CONCAT\n" +
                "CONCAT(string, string [,...])\n" +
                "Combines strings. Unlike with the operator ||, NULL parameters are ignored, and do not cause the result to become NULL. If all parameters are NULL the result is an empty string. This method returns a string.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CONCAT(NAME, '!')\n" +
                "\n" +
                "CONCAT_WS\n" +
                "CONCAT_WS(separatorString, string, string [,...])\n" +
                "Combines strings with separator. If separator is NULL it is treated like an empty string. Other NULL parameters are ignored. Remaining non-NULL parameters, if any, are concatenated with the specified separator. If there are no remaining parameters the result is an empty string. This method returns a string.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CONCAT_WS(',', NAME, '!')\n" +
                "\n" +
                "DIFFERENCE\n" +
                "DIFFERENCE(string, string)\n" +
                "Returns the difference between the sounds of two strings. The difference is calculated as a number of matched characters in the same positions in SOUNDEX representations of arguments. This method returns an int between 0 and 4 inclusive, or null if any of its parameters is null. Note that value of 0 means that strings are not similar to each other. Value of 4 means that strings are fully similar to each other (have the same SOUNDEX representation).\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "DIFFERENCE(T1.NAME, T2.NAME)\n" +
                "\n" +
                "HEXTORAW\n" +
                "HEXTORAW(string)\n" +
                "Converts a hex representation of a string to a string. 4 hex characters per string character are used.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "HEXTORAW(DATA)\n" +
                "\n" +
                "RAWTOHEX\n" +
                "RAWTOHEX({string|bytes})\n" +
                "Converts a string or bytes to the hex representation. 4 hex characters per string character are used. This method returns a string.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "RAWTOHEX(DATA)\n" +
                "\n" +
                "INSERT Function\n" +
                "INSERT(originalString, startInt, lengthInt, addString)\n" +
                "Inserts a additional string into the original string at a specified start position. The length specifies the number of characters that are removed at the start position in the original string. This method returns a string.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "INSERT(NAME, 1, 1, ' ')\n" +
                "\n" +
                "LOWER\n" +
                "{ LOWER | { LCASE } } ( string )\n" +
                "Converts a string to lowercase.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "LOWER(NAME)\n" +
                "\n" +
                "UPPER\n" +
                "{ UPPER | { UCASE } } ( string )\n" +
                "Converts a string to uppercase.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "UPPER(NAME)\n" +
                "\n" +
                "LEFT\n" +
                "LEFT(string, int)\n" +
                "Returns the leftmost number of characters.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "LEFT(NAME, 3)\n" +
                "\n" +
                "RIGHT\n" +
                "RIGHT(string, int)\n" +
                "Returns the rightmost number of characters.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "RIGHT(NAME, 3)\n" +
                "\n" +
                "LOCATE\n" +
                "{ LOCATE(searchString, string [, startInt]) }\n" +
                "| { INSTR(string, searchString, [, startInt]) }\n" +
                "| { POSITION(searchString, string) }\n" +
                "Returns the location of a search string in a string. If a start position is used, the characters before it are ignored. If position is negative, the rightmost location is returned. 0 is returned if the search string is not found. Please note this function is case sensitive, even if the parameters are not.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "LOCATE('.', NAME)\n" +
                "\n" +
                "LPAD\n" +
                "LPAD(string, int[, paddingString])\n" +
                "Left pad the string to the specified length. If the length is shorter than the string, it will be truncated at the end. If the padding string is not set, spaces will be used.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "LPAD(AMOUNT, 10, '*')\n" +
                "\n" +
                "RPAD\n" +
                "RPAD(string, int[, paddingString])\n" +
                "Right pad the string to the specified length. If the length is shorter than the string, it will be truncated. If the padding string is not set, spaces will be used.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "RPAD(TEXT, 10, '-')\n" +
                "\n" +
                "LTRIM\n" +
                "LTRIM(string [, characterToTrimString])\n" +
                "Removes all leading spaces or other specified characters from a string.\n" +
                "\n" +
                "This function is deprecated, use TRIM instead of it.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "LTRIM(NAME)\n" +
                "\n" +
                "RTRIM\n" +
                "RTRIM(string [, characterToTrimString])\n" +
                "Removes all trailing spaces or other specified characters from a string.\n" +
                "\n" +
                "This function is deprecated, use TRIM instead of it.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "RTRIM(NAME)\n" +
                "\n" +
                "TRIM\n" +
                "TRIM ( [ [ LEADING | TRAILING | BOTH ] [ string ] FROM ] string )\n" +
                "Removes all leading spaces, trailing spaces, or spaces at both ends, from a string. Other characters can be removed as well.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "TRIM(BOTH '_' FROM NAME)\n" +
                "\n" +
                "REGEXP_REPLACE\n" +
                "REGEXP_REPLACE(inputString, regexString, replacementString [, flagsString])\n" +
                "Replaces each substring that matches a regular expression. For details, see the Java String.replaceAll() method. If any parameter is null (except optional flagsString parameter), the result is null.\n" +
                "\n" +
                "Flags values are limited to 'i', 'c', 'n', 'm'. Other symbols cause exception. Multiple symbols could be used in one flagsString parameter (like 'im'). Later flags override first ones, for example 'ic' is equivalent to case sensitive matching 'c'.\n" +
                "\n" +
                "'i' enables case insensitive matching (Pattern.CASE_INSENSITIVE)\n" +
                "\n" +
                "'c' disables case insensitive matching (Pattern.CASE_INSENSITIVE)\n" +
                "\n" +
                "'n' allows the period to match the newline character (Pattern.DOTALL)\n" +
                "\n" +
                "'m' enables multiline mode (Pattern.MULTILINE)\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "REGEXP_REPLACE('Hello    World', ' +', ' ')\n" +
                "REGEXP_REPLACE('Hello WWWWorld', 'w+', 'W', 'i')\n" +
                "\n" +
                "REGEXP_LIKE\n" +
                "REGEXP_LIKE(inputString, regexString [, flagsString])\n" +
                "Matches string to a regular expression. For details, see the Java Matcher.find() method. If any parameter is null (except optional flagsString parameter), the result is null.\n" +
                "\n" +
                "Flags values are limited to 'i', 'c', 'n', 'm'. Other symbols cause exception. Multiple symbols could be used in one flagsString parameter (like 'im'). Later flags override first ones, for example 'ic' is equivalent to case sensitive matching 'c'.\n" +
                "\n" +
                "'i' enables case insensitive matching (Pattern.CASE_INSENSITIVE)\n" +
                "\n" +
                "'c' disables case insensitive matching (Pattern.CASE_INSENSITIVE)\n" +
                "\n" +
                "'n' allows the period to match the newline character (Pattern.DOTALL)\n" +
                "\n" +
                "'m' enables multiline mode (Pattern.MULTILINE)\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "REGEXP_LIKE('Hello    World', '[A-Z ]*', 'i')\n" +
                "\n" +
                "REGEXP_SUBSTR\n" +
                "REGEXP_SUBSTR(inputString, regexString [, positionInt, occurrenceInt, flagsString, groupInt])\n" +
                "Matches string to a regular expression and returns the matched substring. For details, see the java.util.regex.Pattern and related functionality.\n" +
                "\n" +
                "The parameter position specifies where in inputString the match should start. Occurrence indicates which occurrence of pattern in inputString to search for.\n" +
                "\n" +
                "Flags values are limited to 'i', 'c', 'n', 'm'. Other symbols cause exception. Multiple symbols could be used in one flagsString parameter (like 'im'). Later flags override first ones, for example 'ic' is equivalent to case sensitive matching 'c'.\n" +
                "\n" +
                "'i' enables case insensitive matching (Pattern.CASE_INSENSITIVE)\n" +
                "\n" +
                "'c' disables case insensitive matching (Pattern.CASE_INSENSITIVE)\n" +
                "\n" +
                "'n' allows the period to match the newline character (Pattern.DOTALL)\n" +
                "\n" +
                "'m' enables multiline mode (Pattern.MULTILINE)\n" +
                "\n" +
                "If the pattern has groups, the group parameter can be used to specify which group to return.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "REGEXP_SUBSTR('2020-10-01', '\\d{4}')\n" +
                "REGEXP_SUBSTR('2020-10-01', '(\\d{4})-(\\d{2})-(\\d{2})', 1, 1, NULL, 2)\n" +
                "\n" +
                "REPEAT\n" +
                "REPEAT(string, int)\n" +
                "Returns a string repeated some number of times.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "REPEAT(NAME || ' ', 10)\n" +
                "\n" +
                "REPLACE\n" +
                "REPLACE(string, searchString [, replacementString])\n" +
                "Replaces all occurrences of a search string in a text with another string. If no replacement is specified, the search string is removed from the original string. If any parameter is null, the result is null.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "REPLACE(NAME, ' ')\n" +
                "\n" +
                "SOUNDEX\n" +
                "SOUNDEX(string)\n" +
                "Returns a four character code representing the sound of a string. This method returns a string, or null if parameter is null. See https://en.wikipedia.org/wiki/Soundex for more information.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "SOUNDEX(NAME)\n" +
                "\n" +
                "SPACE\n" +
                "SPACE(int)\n" +
                "Returns a string consisting of a number of spaces.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "SPACE(80)\n" +
                "\n" +
                "STRINGDECODE\n" +
                "STRINGDECODE(string)\n" +
                "Converts a encoded string using the Java string literal encoding format. Special characters are \\b, \\t, \\n, \\f, \\r, \\\", \\\\, \\<octal>, \\u<unicode>. This method returns a string.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL STRINGENCODE(STRINGDECODE('Lines 1\\nLine 2'))\n" +
                "\n" +
                "STRINGENCODE\n" +
                "STRINGENCODE(string)\n" +
                "Encodes special characters in a string using the Java string literal encoding format. Special characters are \\b, \\t, \\n, \\f, \\r, \\\", \\\\, \\<octal>, \\u<unicode>. This method returns a string.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL STRINGENCODE(STRINGDECODE('Lines 1\\nLine 2'))\n" +
                "\n" +
                "STRINGTOUTF8\n" +
                "STRINGTOUTF8(string)\n" +
                "Encodes a string to a byte array using the UTF8 encoding format. This method returns bytes.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL UTF8TOSTRING(STRINGTOUTF8('This is a test'))\n" +
                "\n" +
                "SUBSTRING\n" +
                "SUBSTRING ( {string|bytes} FROM startInt [ FOR lengthInt ] )\n" +
                "| { { SUBSTRING | SUBSTR } ( {string|bytes}, startInt [, lengthInt ] ) }\n" +
                "Returns a substring of a string starting at a position. If the start index is negative, then the start index is relative to the end of the string. The length is optional.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL SUBSTRING('[Hello]' FROM 2 FOR 5);\n" +
                "CALL SUBSTRING('hour' FROM 2);\n" +
                "\n" +
                "UTF8TOSTRING\n" +
                "UTF8TOSTRING(bytes)\n" +
                "Decodes a byte array in the UTF8 format to a string.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL UTF8TOSTRING(STRINGTOUTF8('This is a test'))\n" +
                "\n" +
                "QUOTE_IDENT\n" +
                "QUOTE_IDENT(string)\n" +
                "Quotes the specified identifier. Identifier is surrounded by double quotes. If identifier contains double quotes they are repeated twice.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "QUOTE_IDENT('Column 1')\n" +
                "\n" +
                "XMLATTR\n" +
                "XMLATTR(nameString, valueString)\n" +
                "Creates an XML attribute element of the form name=value. The value is encoded as XML text. This method returns a string.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL XMLNODE('a', XMLATTR('href', 'https://h2database.com'))\n" +
                "\n" +
                "XMLNODE\n" +
                "XMLNODE(elementString [, attributesString [, contentString [, indentBoolean]]])\n" +
                "Create an XML node element. An empty or null attribute string means no attributes are set. An empty or null content string means the node is empty. The content is indented by default if it contains a newline. This method returns a string.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL XMLNODE('a', XMLATTR('href', 'https://h2database.com'), 'H2')\n" +
                "\n" +
                "XMLCOMMENT\n" +
                "XMLCOMMENT(commentString)\n" +
                "Creates an XML comment. Two dashes (--) are converted to - -. This method returns a string.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL XMLCOMMENT('Test')\n" +
                "\n" +
                "XMLCDATA\n" +
                "XMLCDATA(valueString)\n" +
                "Creates an XML CDATA element. If the value contains ]]>, an XML text element is created instead. This method returns a string.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL XMLCDATA('data')\n" +
                "\n" +
                "XMLSTARTDOC\n" +
                "XMLSTARTDOC()\n" +
                "Returns the XML declaration. The result is always <?xml version=1.0?>.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL XMLSTARTDOC()\n" +
                "\n" +
                "XMLTEXT\n" +
                "XMLTEXT(valueString [, escapeNewlineBoolean])\n" +
                "Creates an XML text element. If enabled, newline and linefeed is converted to an XML entity (&#). This method returns a string.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL XMLTEXT('test')\n" +
                "\n" +
                "TO_CHAR\n" +
                "TO_CHAR(value [, formatString[, nlsParamString]])\n" +
                "Oracle-compatible TO_CHAR function that can format a timestamp, a number, or text.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL TO_CHAR(TIMESTAMP '2010-01-01 00:00:00', 'DD MON, YYYY')\n" +
                "\n" +
                "TRANSLATE\n" +
                "TRANSLATE(value, searchString, replacementString)\n" +
                "Oracle-compatible TRANSLATE function that replaces a sequence of characters in a string with another set of characters.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL TRANSLATE('Hello world', 'eo', 'EO')\n" +
                "\n" +
                "Time and Date Functions\n" +
                "CURRENT_DATE\n" +
                "CURRENT_DATE\n" +
                "Returns the current date.\n" +
                "\n" +
                "These functions return the same value within a transaction (default) or within a command depending on database mode.\n" +
                "\n" +
                "SET TIME ZONE command reevaluates the value for these functions using the same original UTC timestamp of transaction.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CURRENT_DATE\n" +
                "\n" +
                "CURRENT_TIME\n" +
                "CURRENT_TIME [ (int) ]\n" +
                "Returns the current time with time zone. If fractional seconds precision is specified it should be from 0 to 9, 0 is default. The specified value can be used only to limit precision of a result. The actual maximum available precision depends on operating system and JVM and can be 3 (milliseconds) or higher. Higher precision is not available before Java 9.\n" +
                "\n" +
                "This function returns the same value within a transaction (default) or within a command depending on database mode.\n" +
                "\n" +
                "SET TIME ZONE command reevaluates the value for this function using the same original UTC timestamp of transaction.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CURRENT_TIME\n" +
                "CURRENT_TIME(9)\n" +
                "\n" +
                "CURRENT_TIMESTAMP\n" +
                "CURRENT_TIMESTAMP [ (int) ]\n" +
                "Returns the current timestamp with time zone. Time zone offset is set to a current time zone offset. If fractional seconds precision is specified it should be from 0 to 9, 6 is default. The specified value can be used only to limit precision of a result. The actual maximum available precision depends on operating system and JVM and can be 3 (milliseconds) or higher. Higher precision is not available before Java 9.\n" +
                "\n" +
                "This function returns the same value within a transaction (default) or within a command depending on database mode.\n" +
                "\n" +
                "SET TIME ZONE command reevaluates the value for this function using the same original UTC timestamp of transaction.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CURRENT_TIMESTAMP\n" +
                "CURRENT_TIMESTAMP(9)\n" +
                "\n" +
                "LOCALTIME\n" +
                "LOCALTIME [ (int) ]\n" +
                "Returns the current time without time zone. If fractional seconds precision is specified it should be from 0 to 9, 0 is default. The specified value can be used only to limit precision of a result. The actual maximum available precision depends on operating system and JVM and can be 3 (milliseconds) or higher. Higher precision is not available before Java 9.\n" +
                "\n" +
                "These functions return the same value within a transaction (default) or within a command depending on database mode.\n" +
                "\n" +
                "SET TIME ZONE command reevaluates the value for these functions using the same original UTC timestamp of transaction.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "LOCALTIME\n" +
                "LOCALTIME(9)\n" +
                "\n" +
                "LOCALTIMESTAMP\n" +
                "LOCALTIMESTAMP [ (int) ]\n" +
                "Returns the current timestamp without time zone. If fractional seconds precision is specified it should be from 0 to 9, 6 is default. The specified value can be used only to limit precision of a result. The actual maximum available precision depends on operating system and JVM and can be 3 (milliseconds) or higher. Higher precision is not available before Java 9.\n" +
                "\n" +
                "The returned value has date and time without time zone information. If time zone has DST transitions the returned values are ambiguous during transition from DST to normal time. For absolute timestamps use the CURRENT_TIMESTAMP function and TIMESTAMP WITH TIME ZONE data type.\n" +
                "\n" +
                "These functions return the same value within a transaction (default) or within a command depending on database mode.\n" +
                "\n" +
                "SET TIME ZONE reevaluates the value for these functions using the same original UTC timestamp of transaction.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "LOCALTIMESTAMP\n" +
                "LOCALTIMESTAMP(9)\n" +
                "\n" +
                "DATEADD\n" +
                "{ DATEADD| TIMESTAMPADD } (datetimeField, addIntLong, dateAndTime)\n" +
                "Adds units to a date-time value. The datetimeField indicates the unit. Use negative values to subtract units. addIntLong may be a long value when manipulating milliseconds, microseconds, or nanoseconds otherwise its range is restricted to int. This method returns a value with the same type as specified value if unit is compatible with this value. If specified field is a HOUR, MINUTE, SECOND, MILLISECOND, etc and value is a DATE value DATEADD returns combined TIMESTAMP. Fields DAY, MONTH, YEAR, WEEK, etc are not allowed for TIME values. Fields TIMEZONE_HOUR, TIMEZONE_MINUTE, and TIMEZONE_SECOND are only allowed for TIMESTAMP WITH TIME ZONE values.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "DATEADD(MONTH, 1, DATE '2001-01-31')\n" +
                "\n" +
                "DATEDIFF\n" +
                "{ DATEDIFF | TIMESTAMPDIFF } (datetimeField, aDateAndTime, bDateAndTime)\n" +
                "Returns the number of crossed unit boundaries between two date/time values. This method returns a long. The datetimeField indicates the unit. Only TIMEZONE_HOUR, TIMEZONE_MINUTE, and TIMEZONE_SECOND fields use the time zone offset component. With all other fields if date/time values have time zone offset component it is ignored.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "DATEDIFF(YEAR, T1.CREATED, T2.CREATED)\n" +
                "\n" +
                "DATE_TRUNC\n" +
                "DATE_TRUNC (datetimeField, dateAndTime)\n" +
                "Truncates the specified date-time value to the specified field.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "DATE_TRUNC(DAY, TIMESTAMP '2010-01-03 10:40:00');\n" +
                "\n" +
                "DAYNAME\n" +
                "DAYNAME(dateAndTime)\n" +
                "Returns the name of the day (in English).\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "DAYNAME(CREATED)\n" +
                "\n" +
                "DAY_OF_MONTH\n" +
                "DAY_OF_MONTH({dateAndTime|interval})\n" +
                "Returns the day of the month (1-31).\n" +
                "\n" +
                "This function is deprecated, use EXTRACT instead of it.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "DAY_OF_MONTH(CREATED)\n" +
                "\n" +
                "DAY_OF_WEEK\n" +
                "DAY_OF_WEEK(dateAndTime)\n" +
                "Returns the day of the week (1-7), locale-specific.\n" +
                "\n" +
                "This function is deprecated, use EXTRACT instead of it.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "DAY_OF_WEEK(CREATED)\n" +
                "\n" +
                "ISO_DAY_OF_WEEK\n" +
                "ISO_DAY_OF_WEEK(dateAndTime)\n" +
                "Returns the ISO day of the week (1 means Monday).\n" +
                "\n" +
                "This function is deprecated, use EXTRACT instead of it.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "ISO_DAY_OF_WEEK(CREATED)\n" +
                "\n" +
                "DAY_OF_YEAR\n" +
                "DAY_OF_YEAR({dateAndTime|interval})\n" +
                "Returns the day of the year (1-366).\n" +
                "\n" +
                "This function is deprecated, use EXTRACT instead of it.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "DAY_OF_YEAR(CREATED)\n" +
                "\n" +
                "EXTRACT\n" +
                "EXTRACT ( datetimeField FROM { dateAndTime | interval })\n" +
                "Returns a value of the specific time unit from a date/time value. This method returns a numeric value with EPOCH field and an int for all other fields.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "EXTRACT(SECOND FROM CURRENT_TIMESTAMP)\n" +
                "\n" +
                "FORMATDATETIME\n" +
                "FORMATDATETIME ( dateAndTime, formatString\n" +
                "[ , localeString [ , timeZoneString ] ] )\n" +
                "Formats a date, time or timestamp as a string. The most important format characters are: y year, M month, d day, H hour, m minute, s second. For details of the format, see java.time.format.DateTimeFormatter.\n" +
                "\n" +
                "If timeZoneString is specified, it is used in formatted string if formatString has time zone. If TIMESTAMP WITH TIME ZONE is passed and timeZoneString is specified, the timestamp is converted to the specified time zone and its UTC value is preserved.\n" +
                "\n" +
                "This method returns a string.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL FORMATDATETIME(TIMESTAMP '2001-02-03 04:05:06',\n" +
                "    'EEE, d MMM yyyy HH:mm:ss z', 'en', 'GMT')\n" +
                "\n" +
                "HOUR\n" +
                "HOUR({dateAndTime|interval})\n" +
                "Returns the hour (0-23) from a date/time value.\n" +
                "\n" +
                "This function is deprecated, use EXTRACT instead of it.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "HOUR(CREATED)\n" +
                "\n" +
                "MINUTE\n" +
                "MINUTE({dateAndTime|interval})\n" +
                "Returns the minute (0-59) from a date/time value.\n" +
                "\n" +
                "This function is deprecated, use EXTRACT instead of it.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "MINUTE(CREATED)\n" +
                "\n" +
                "MONTH\n" +
                "MONTH({dateAndTime|interval})\n" +
                "Returns the month (1-12) from a date/time value.\n" +
                "\n" +
                "This function is deprecated, use EXTRACT instead of it.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "MONTH(CREATED)\n" +
                "\n" +
                "MONTHNAME\n" +
                "MONTHNAME(dateAndTime)\n" +
                "Returns the name of the month (in English).\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "MONTHNAME(CREATED)\n" +
                "\n" +
                "PARSEDATETIME\n" +
                "PARSEDATETIME(string, formatString\n" +
                "[, localeString [, timeZoneString]])\n" +
                "Parses a string and returns a TIMESTAMP WITH TIME ZONE value. The most important format characters are: y year, M month, d day, H hour, m minute, s second. For details of the format, see java.time.format.DateTimeFormatter.\n" +
                "\n" +
                "If timeZoneString is specified, it is used as default.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL PARSEDATETIME('Sat, 3 Feb 2001 03:05:06 GMT',\n" +
                "    'EEE, d MMM yyyy HH:mm:ss z', 'en', 'GMT')\n" +
                "\n" +
                "QUARTER\n" +
                "QUARTER(dateAndTime)\n" +
                "Returns the quarter (1-4) from a date/time value.\n" +
                "\n" +
                "This function is deprecated, use EXTRACT instead of it.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "QUARTER(CREATED)\n" +
                "\n" +
                "SECOND\n" +
                "SECOND(dateAndTime)\n" +
                "Returns the second (0-59) from a date/time value.\n" +
                "\n" +
                "This function is deprecated, use EXTRACT instead of it.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "SECOND(CREATED|interval)\n" +
                "\n" +
                "WEEK\n" +
                "WEEK(dateAndTime)\n" +
                "Returns the week (1-53) from a date/time value.\n" +
                "\n" +
                "This function is deprecated, use EXTRACT instead of it.\n" +
                "\n" +
                "This function uses the current system locale.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "WEEK(CREATED)\n" +
                "\n" +
                "ISO_WEEK\n" +
                "ISO_WEEK(dateAndTime)\n" +
                "Returns the ISO week (1-53) from a date/time value.\n" +
                "\n" +
                "This function is deprecated, use EXTRACT instead of it.\n" +
                "\n" +
                "This function uses the ISO definition when first week of year should have at least four days and week is started with Monday.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "ISO_WEEK(CREATED)\n" +
                "\n" +
                "YEAR\n" +
                "YEAR({dateAndTime|interval})\n" +
                "Returns the year from a date/time value.\n" +
                "\n" +
                "This function is deprecated, use EXTRACT instead of it.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "YEAR(CREATED)\n" +
                "\n" +
                "ISO_YEAR\n" +
                "ISO_YEAR(dateAndTime)\n" +
                "Returns the ISO week year from a date/time value.\n" +
                "\n" +
                "This function is deprecated, use EXTRACT instead of it.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "ISO_YEAR(CREATED)\n" +
                "\n" +
                "System Functions\n" +
                "ABORT_SESSION\n" +
                "ABORT_SESSION(sessionInt)\n" +
                "Cancels the currently executing statement of another session. Closes the session and releases the allocated resources. Returns true if the session was closed, false if no session with the given id was found.\n" +
                "\n" +
                "If a client was connected while its session was aborted it will see an error.\n" +
                "\n" +
                "Admin rights are required to execute this command.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL ABORT_SESSION(3)\n" +
                "\n" +
                "ARRAY_GET\n" +
                "ARRAY_GET(arrayExpression, indexExpression)\n" +
                "Returns element at the specified 1-based index from an array.\n" +
                "\n" +
                "This function is deprecated, use [array element reference](https://www.h2database.com/html/grammar.html#array_element_reference) instead of it.\n" +
                "\n" +
                "Returns NULL if array or index is NULL.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL ARRAY_GET(ARRAY['Hello', 'World'], 2)\n" +
                "\n" +
                "CARDINALITY\n" +
                "{ CARDINALITY | { ARRAY_LENGTH } } (arrayExpression)\n" +
                "Returns the length of an array. Returns NULL if the specified array is NULL.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL CARDINALITY(ARRAY['Hello', 'World'])\n" +
                "\n" +
                "ARRAY_CONTAINS\n" +
                "ARRAY_CONTAINS(arrayExpression, value)\n" +
                "Returns a boolean TRUE if the array contains the value or FALSE if it does not contain it. Returns NULL if the specified array is NULL.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL ARRAY_CONTAINS(ARRAY['Hello', 'World'], 'Hello')\n" +
                "\n" +
                "ARRAY_CAT\n" +
                "ARRAY_CAT(arrayExpression, arrayExpression)\n" +
                "Returns the concatenation of two arrays.\n" +
                "\n" +
                "This function is deprecated, use || instead of it.\n" +
                "\n" +
                "Returns NULL if any parameter is NULL.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL ARRAY_CAT(ARRAY[1, 2], ARRAY[3, 4])\n" +
                "\n" +
                "ARRAY_APPEND\n" +
                "ARRAY_APPEND(arrayExpression, value)\n" +
                "Append an element to the end of an array.\n" +
                "\n" +
                "This function is deprecated, use || instead of it.\n" +
                "\n" +
                "Returns NULL if any parameter is NULL.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL ARRAY_APPEND(ARRAY[1, 2], 3)\n" +
                "\n" +
                "ARRAY_MAX_CARDINALITY\n" +
                "ARRAY_MAX_CARDINALITY(arrayExpression)\n" +
                "Returns the maximum allowed array cardinality (length) of the declared data type of argument.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "SELECT ARRAY_MAX_CARDINALITY(COL1) FROM TEST FETCH FIRST ROW ONLY;\n" +
                "\n" +
                "TRIM_ARRAY\n" +
                "TRIM_ARRAY(arrayExpression, int)\n" +
                "Removes the specified number of elements from the end of the array.\n" +
                "\n" +
                "Returns NULL if second parameter is NULL or if first parameter is NULL and second parameter is not negative. Throws exception if second parameter is negative or larger than number of elements in array. Otherwise returns the truncated array.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL TRIM_ARRAY(ARRAY[1, 2, 3, 4], 1)\n" +
                "\n" +
                "ARRAY_SLICE\n" +
                "ARRAY_SLICE(arrayExpression, lowerBoundInt, upperBoundInt)\n" +
                "Returns elements from the array as specified by the lower and upper bound parameters. Both parameters are inclusive and the first element has index 1, i.e. ARRAY_SLICE(a, 2, 2) has only the second element. Returns NULL if any parameter is NULL or if an index is out of bounds.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL ARRAY_SLICE(ARRAY[1, 2, 3, 4], 1, 3)\n" +
                "\n" +
                "AUTOCOMMIT\n" +
                "AUTOCOMMIT()\n" +
                "Returns true if auto commit is switched on for this session.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "AUTOCOMMIT()\n" +
                "\n" +
                "CANCEL_SESSION\n" +
                "CANCEL_SESSION(sessionInt)\n" +
                "Cancels the currently executing statement of another session. Returns true if the statement was canceled, false if the session is closed or no statement is currently executing.\n" +
                "\n" +
                "Admin rights are required to execute this command.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CANCEL_SESSION(3)\n" +
                "\n" +
                "CASEWHEN Function\n" +
                "CASEWHEN(boolean, aValue, bValue)\n" +
                "Returns 'aValue' if the boolean expression is true, otherwise 'bValue'.\n" +
                "\n" +
                "This function is deprecated, use CASE instead of it.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CASEWHEN(ID=1, 'A', 'B')\n" +
                "\n" +
                "COALESCE\n" +
                "{ COALESCE | { NVL } } (aValue, bValue [,...])\n" +
                "| IFNULL(aValue, bValue)\n" +
                "Returns the first value that is not null.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "COALESCE(A, B, C)\n" +
                "\n" +
                "CONVERT\n" +
                "CONVERT(value, dataTypeOrDomain)\n" +
                "Converts a value to another data type.\n" +
                "\n" +
                "This function is deprecated, use CAST instead of it.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CONVERT(NAME, INT)\n" +
                "\n" +
                "CURRVAL\n" +
                "CURRVAL( [ schemaNameString, ] sequenceString )\n" +
                "Returns the latest generated value of the sequence for the current session. Current value may only be requested after generation of the sequence value in the current session. This method exists only for compatibility, when it isn't required use CURRENT VALUE FOR sequenceName instead. If the schema name is not set, the current schema is used. When sequence is not found, the uppercase name is also checked. This method returns a long.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CURRVAL('TEST_SEQ')\n" +
                "\n" +
                "CSVWRITE\n" +
                "CSVWRITE ( fileNameString, queryString [, csvOptions [, lineSepString] ] )\n" +
                "Writes a CSV (comma separated values). The file is overwritten if it exists. If only a file name is specified, it will be written to the current working directory. For each parameter, NULL means the default value should be used. The default charset is the default value for this system, and the default field separator is a comma.\n" +
                "\n" +
                "The values are converted to text using the default string representation; if another conversion is required you need to change the select statement accordingly. The parameter nullString is used when writing NULL (by default nothing is written when NULL appears). The default line separator is the default value for this system (system property line.separator).\n" +
                "\n" +
                "The returned value is the number or rows written. Admin rights are required to execute this command.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL CSVWRITE('data/test.csv', 'SELECT * FROM TEST');\n" +
                "CALL CSVWRITE('data/test2.csv', 'SELECT * FROM TEST', 'charset=UTF-8 fieldSeparator=|');\n" +
                "-- Write a tab-separated file\n" +
                "CALL CSVWRITE('data/test.tsv', 'SELECT * FROM TEST', 'charset=UTF-8 fieldSeparator=' || CHAR(9));\n" +
                "\n" +
                "CURRENT_SCHEMA\n" +
                "CURRENT_SCHEMA | SCHEMA()\n" +
                "Returns the name of the default schema for this session.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL CURRENT_SCHEMA\n" +
                "\n" +
                "CURRENT_CATALOG\n" +
                "CURRENT_CATALOG | DATABASE()\n" +
                "Returns the name of the database.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL CURRENT_CATALOG\n" +
                "\n" +
                "DATABASE_PATH\n" +
                "DATABASE_PATH()\n" +
                "Returns the directory of the database files and the database name, if it is file based. Returns NULL otherwise.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL DATABASE_PATH();\n" +
                "\n" +
                "DATA_TYPE_SQL\n" +
                "DATA_TYPE_SQL\n" +
                "(objectSchemaString, objectNameString, objectTypeString, typeIdentifierString)\n" +
                "Returns SQL representation of data type of the specified constant, domain, table column, routine result or argument.\n" +
                "\n" +
                "For constants object type is 'CONSTANT' and type identifier is the value of INFORMATION_SCHEMA.CONSTANTS.DTD_IDENTIFIER.\n" +
                "\n" +
                "For domains object type is 'DOMAIN' and type identifier is the value of INFORMATION_SCHEMA.DOMAINS.DTD_IDENTIFIER.\n" +
                "\n" +
                "For columns object type is 'TABLE' and type identifier is the value of INFORMATION_SCHEMA.COLUMNS.DTD_IDENTIFIER.\n" +
                "\n" +
                "For routines object name is the value of INFORMATION_SCHEMA.ROUTINES.SPECIFIC_NAME, object type is 'ROUTINE', and type identifier is the value of INFORMATION_SCHEMA.ROUTINES.DTD_IDENTIFIER for data type of the result and the value of INFORMATION_SCHEMA.PARAMETERS.DTD_IDENTIFIER for data types of arguments. Aggregate functions aren't supported by this function, because their data type isn't statically known.\n" +
                "\n" +
                "This function returns NULL if any argument is NULL, object type is not valid, or object isn't found.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "DATA_TYPE_SQL('PUBLIC', 'C', 'CONSTANT', 'TYPE')\n" +
                "DATA_TYPE_SQL('PUBLIC', 'D', 'DOMAIN', 'TYPE')\n" +
                "DATA_TYPE_SQL('PUBLIC', 'T', 'TABLE', '1')\n" +
                "DATA_TYPE_SQL('PUBLIC', 'R_1', 'ROUTINE', 'RESULT')\n" +
                "DATA_TYPE_SQL('PUBLIC', 'R_1', 'ROUTINE', '1')\n" +
                "COALESCE(\n" +
                "    QUOTE_IDENT(DOMAIN_SCHEMA) || '.' || QUOTE_IDENT(DOMAIN_NAME),\n" +
                "    DATA_TYPE_SQL(TABLE_SCHEMA, TABLE_NAME, 'TABLE', DTD_IDENTIFIER))\n" +
                "\n" +
                "DB_OBJECT_ID\n" +
                "DB_OBJECT_ID({{'ROLE'|'SETTING'|'SCHEMA'|'USER'}, objectNameString\n" +
                "| {'CONSTANT'|'CONSTRAINT'|'DOMAIN'|'INDEX'|'ROUTINE'|'SEQUENCE'\n" +
                "    |'SYNONYM'|'TABLE'|'TRIGGER'}, schemaNameString, objectNameString })\n" +
                "Returns internal identifier of the specified database object as integer value or NULL if object doesn't exist.\n" +
                "\n" +
                "Admin rights are required to execute this function.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL DB_OBJECT_ID('ROLE', 'MANAGER');\n" +
                "CALL DB_OBJECT_ID('TABLE', 'PUBLIC', 'MY_TABLE');\n" +
                "\n" +
                "DB_OBJECT_SQL\n" +
                "DB_OBJECT_SQL({{'ROLE'|'SETTING'|'SCHEMA'|'USER'}, objectNameString\n" +
                "| {'CONSTANT'|'CONSTRAINT'|'DOMAIN'|'INDEX'|'ROUTINE'|'SEQUENCE'\n" +
                "    |'SYNONYM'|'TABLE'|'TRIGGER'}, schemaNameString, objectNameString })\n" +
                "Returns internal SQL definition of the specified database object or NULL if object doesn't exist or it is a system object without SQL definition.\n" +
                "\n" +
                "This function should not be used to analyze structure of the object by machine code. Internal SQL representation may contain undocumented non-standard clauses and may be different in different versions of H2. Objects are described in the INFORMATION_SCHEMA in machine-readable way.\n" +
                "\n" +
                "Admin rights are required to execute this function.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL DB_OBJECT_SQL('ROLE', 'MANAGER');\n" +
                "CALL DB_OBJECT_SQL('TABLE', 'PUBLIC', 'MY_TABLE');\n" +
                "\n" +
                "DECODE\n" +
                "DECODE(value, whenValue, thenValue [,...])\n" +
                "Returns the first matching value. NULL is considered to match NULL. If no match was found, then NULL or the last parameter (if the parameter count is even) is returned. This function is provided for Oracle compatibility, use CASE instead of it.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL DECODE(RAND()>0.5, 0, 'Red', 1, 'Black');\n" +
                "\n" +
                "DISK_SPACE_USED\n" +
                "DISK_SPACE_USED(tableNameString)\n" +
                "Returns the approximate amount of space used by the table specified. Does not currently take into account indexes or LOB's. This function may be expensive since it has to load every page in the table.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL DISK_SPACE_USED('my_table');\n" +
                "\n" +
                "SIGNAL\n" +
                "SIGNAL(sqlStateString, messageString)\n" +
                "Throw an SQLException with the passed SQLState and reason.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL SIGNAL('23505', 'Duplicate user ID: ' || user_id);\n" +
                "\n" +
                "ESTIMATED_ENVELOPE\n" +
                "ESTIMATED_ENVELOPE(tableNameString, columnNameString)\n" +
                "Returns the estimated minimum bounding box that encloses all specified GEOMETRY values. Only 2D coordinate plane is supported. NULL values are ignored. Column must have a spatial index. This function is fast, but estimation may include uncommitted data (including data from other transactions), may return approximate bounds, or be different with actual value due to other reasons. Use with caution. If estimation is not available this function returns NULL. For accurate and reliable result use ESTIMATE aggregate function instead.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL ESTIMATED_ENVELOPE('MY_TABLE', 'GEOMETRY_COLUMN');\n" +
                "\n" +
                "FILE_READ\n" +
                "FILE_READ(fileNameString [,encodingString])\n" +
                "Returns the contents of a file. If only one parameter is supplied, the data are returned as a BLOB. If two parameters are used, the data is returned as a CLOB (text). The second parameter is the character set to use, NULL meaning the default character set for this system.\n" +
                "\n" +
                "File names and URLs are supported. To read a stream from the classpath, use the prefix classpath:.\n" +
                "\n" +
                "Admin rights are required to execute this command.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "SELECT LENGTH(FILE_READ('~/.h2.server.properties')) LEN;\n" +
                "SELECT FILE_READ('http://localhost:8182/stylesheet.css', NULL) CSS;\n" +
                "\n" +
                "FILE_WRITE\n" +
                "FILE_WRITE(blobValue, fileNameString)\n" +
                "Write the supplied parameter into a file. Return the number of bytes written.\n" +
                "\n" +
                "Write access to folder, and admin rights are required to execute this command.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "SELECT FILE_WRITE('Hello world', '/tmp/hello.txt')) LEN;\n" +
                "\n" +
                "GREATEST\n" +
                "GREATEST(aValue, bValue [,...])\n" +
                "Returns the largest value that is not NULL, or NULL if all values are NULL.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL GREATEST(1, 2, 3);\n" +
                "\n" +
                "LEAST\n" +
                "LEAST(aValue, bValue [,...])\n" +
                "Returns the smallest value that is not NULL, or NULL if all values are NULL.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL LEAST(1, 2, 3);\n" +
                "\n" +
                "LOCK_MODE\n" +
                "LOCK_MODE()\n" +
                "Returns the current lock mode. See SET LOCK_MODE. This method returns an int.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL LOCK_MODE();\n" +
                "\n" +
                "LOCK_TIMEOUT\n" +
                "LOCK_TIMEOUT()\n" +
                "Returns the lock timeout of the current session (in milliseconds).\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "LOCK_TIMEOUT()\n" +
                "\n" +
                "MEMORY_FREE\n" +
                "MEMORY_FREE()\n" +
                "Returns the free memory in KB (where 1024 bytes is a KB). This method returns a long. The garbage is run before returning the value. Admin rights are required to execute this command.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "MEMORY_FREE()\n" +
                "\n" +
                "MEMORY_USED\n" +
                "MEMORY_USED()\n" +
                "Returns the used memory in KB (where 1024 bytes is a KB). This method returns a long. The garbage is run before returning the value. Admin rights are required to execute this command.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "MEMORY_USED()\n" +
                "\n" +
                "NEXTVAL\n" +
                "NEXTVAL ( [ schemaNameString, ] sequenceString )\n" +
                "Increments the sequence and returns its value. The current value of the sequence and the last identity in the current session are updated with the generated value. Used values are never re-used, even when the transaction is rolled back. This method exists only for compatibility, it's recommended to use the standard NEXT VALUE FOR sequenceName instead. If the schema name is not set, the current schema is used. When sequence is not found, the uppercase name is also checked. This method returns a long.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "NEXTVAL('TEST_SEQ')\n" +
                "\n" +
                "NULLIF\n" +
                "NULLIF(aValue, bValue)\n" +
                "Returns NULL if 'a' is equal to 'b', otherwise 'a'.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "NULLIF(A, B)\n" +
                "A / NULLIF(B, 0)\n" +
                "\n" +
                "NVL2\n" +
                "NVL2(testValue, aValue, bValue)\n" +
                "If the test value is null, then 'b' is returned. Otherwise, 'a' is returned. The data type of the returned value is the data type of 'a' if this is a text type.\n" +
                "\n" +
                "This function is provided for Oracle compatibility, use CASE or COALESCE instead of it.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "NVL2(X, 'not null', 'null')\n" +
                "\n" +
                "READONLY\n" +
                "READONLY()\n" +
                "Returns true if the database is read-only.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "READONLY()\n" +
                "\n" +
                "ROWNUM\n" +
                "ROWNUM()\n" +
                "Returns the number of the current row. This method returns a long value. It is supported for SELECT statements, as well as for DELETE and UPDATE. The first row has the row number 1, and is calculated before ordering and grouping the result set, but after evaluating index conditions (even when the index conditions are specified in an outer query). Use the ROW_NUMBER() OVER () function to get row numbers after grouping or in specified order.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "SELECT ROWNUM(), * FROM TEST;\n" +
                "SELECT ROWNUM(), * FROM (SELECT * FROM TEST ORDER BY NAME);\n" +
                "SELECT ID FROM (SELECT T.*, ROWNUM AS R FROM TEST T) WHERE R BETWEEN 2 AND 3;\n" +
                "\n" +
                "SESSION_ID\n" +
                "SESSION_ID()\n" +
                "Returns the unique session id number for the current database connection. This id stays the same while the connection is open. This method returns an int. The database engine may re-use a session id after the connection is closed.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL SESSION_ID()\n" +
                "\n" +
                "SET\n" +
                "SET(@variableName, value)\n" +
                "Updates a variable with the given value. The new value is returned. When used in a query, the value is updated in the order the rows are read. When used in a subquery, not all rows might be read depending on the query plan. This can be used to implement running totals / cumulative sums.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "SELECT X, SET(@I, COALESCE(@I, 0)+X) RUNNING_TOTAL FROM SYSTEM_RANGE(1, 10)\n" +
                "\n" +
                "TRANSACTION_ID\n" +
                "TRANSACTION_ID()\n" +
                "Returns the current transaction id for this session. This method returns NULL if there is no uncommitted change, or if the database is not persisted. Otherwise a value of the following form is returned: logFileId-position-sessionId. This method returns a string. The value is unique across database restarts (values are not re-used).\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL TRANSACTION_ID()\n" +
                "\n" +
                "TRUNCATE_VALUE\n" +
                "TRUNCATE_VALUE(value, precisionInt, forceBoolean)\n" +
                "Truncate a value to the required precision. If force flag is set to FALSE fixed precision values are not truncated. The method returns a value with the same data type as the first parameter.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CALL TRUNCATE_VALUE(X, 10, TRUE);\n" +
                "\n" +
                "CURRENT_PATH\n" +
                "CURRENT_PATH\n" +
                "Returns the comma-separated list of quoted schema names where user-defined functions are searched when they are referenced without the schema name.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CURRENT_PATH\n" +
                "\n" +
                "CURRENT_ROLE\n" +
                "CURRENT_ROLE\n" +
                "Returns the name of the PUBLIC role.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CURRENT_ROLE\n" +
                "\n" +
                "CURRENT_USER\n" +
                "CURRENT_USER | SESSION_USER | SYSTEM_USER | USER\n" +
                "Returns the name of the current user of this session.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "CURRENT_USER\n" +
                "\n" +
                "H2VERSION\n" +
                "H2VERSION()\n" +
                "Returns the H2 version as a String.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "H2VERSION()\n" +
                "\n" +
                "JSON Functions\n" +
                "JSON_OBJECT\n" +
                "JSON_OBJECT(\n" +
                "[{{[KEY] string VALUE expression} | {string : expression}} [,...] ]\n" +
                "[ { NULL | ABSENT } ON NULL ]\n" +
                "[ { WITH | WITHOUT } UNIQUE KEYS ]\n" +
                ")\n" +
                "Returns a JSON object constructed from the specified properties. If ABSENT ON NULL is specified properties with NULL value are not included in the object. If WITH UNIQUE KEYS is specified the constructed object is checked for uniqueness of keys, nested objects, if any, are checked too.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "JSON_OBJECT('id': 100, 'name': 'Joe', 'groups': '[2,5]' FORMAT JSON);\n" +
                "\n" +
                "JSON_ARRAY\n" +
                "JSON_ARRAY(\n" +
                "[expression [,...]]|{(query) [FORMAT JSON]}\n" +
                "[ { NULL | ABSENT } ON NULL ]\n" +
                ")\n" +
                "Returns a JSON array constructed from the specified values or from the specified single-column subquery. If NULL ON NULL is specified NULL values are included in the array.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "JSON_ARRAY(10, 15, 20);\n" +
                "JSON_ARRAY(JSON_DATA_A FORMAT JSON, JSON_DATA_B FORMAT JSON);\n" +
                "JSON_ARRAY((SELECT J FROM PROPS) FORMAT JSON);\n" +
                "\n" +
                "Table Functions\n" +
                "CSVREAD\n" +
                "CSVREAD(fileNameString [, columnsString [, csvOptions ] ] )\n" +
                "Returns the result set of reading the CSV (comma separated values) file. For each parameter, NULL means the default value should be used.\n" +
                "\n" +
                "If the column names are specified (a list of column names separated with the fieldSeparator), those are used, otherwise (or if they are set to NULL) the first line of the file is interpreted as the column names. In that case, column names that contain no special characters (only letters, '_', and digits; similar to the rule for Java identifiers) are processed is the same way as unquoted identifiers and therefore case of characters may be changed. Other column names are processed as quoted identifiers and case of characters is preserved. To preserve the case of column names unconditionally use caseSensitiveColumnNames option.\n" +
                "\n" +
                "The default charset is the default value for this system, and the default field separator is a comma. Missing unquoted values as well as data that matches nullString is parsed as NULL. All columns are of type VARCHAR.\n" +
                "\n" +
                "The BOM (the byte-order-mark) character 0xfeff at the beginning of the file is ignored.\n" +
                "\n" +
                "This function can be used like a table: SELECT * FROM CSVREAD(...).\n" +
                "\n" +
                "Instead of a file, a URL may be used, for example jar:file:///c:/temp/example.zip!/org/example/nested.csv. To read a stream from the classpath, use the prefix classpath:. To read from HTTP, use the prefix http: (as in a browser).\n" +
                "\n" +
                "For performance reason, CSVREAD should not be used inside a join. Instead, import the data first (possibly into a temporary table) and then use the table.\n" +
                "\n" +
                "Admin rights are required to execute this command.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "SELECT * FROM CSVREAD('test.csv');\n" +
                "-- Read a file containing the columns ID, NAME with\n" +
                "SELECT * FROM CSVREAD('test2.csv', 'ID|NAME', 'charset=UTF-8 fieldSeparator=|');\n" +
                "SELECT * FROM CSVREAD('data/test.csv', null, 'rowSeparator=;');\n" +
                "-- Read a tab-separated file\n" +
                "SELECT * FROM CSVREAD('data/test.tsv', null, 'rowSeparator=' || CHAR(9));\n" +
                "SELECT \"Last Name\" FROM CSVREAD('address.csv');\n" +
                "SELECT \"Last Name\" FROM CSVREAD('classpath:/org/acme/data/address.csv');\n" +
                "\n" +
                "LINK_SCHEMA\n" +
                "LINK_SCHEMA (targetSchemaString, driverString, urlString,\n" +
                "userString, passwordString, sourceSchemaString)\n" +
                "Creates table links for all tables in a schema. If tables with the same name already exist, they are dropped first. The target schema is created automatically if it does not yet exist. The driver name may be empty if the driver is already loaded. The list of tables linked is returned in the form of a result set. Admin rights are required to execute this command.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "SELECT * FROM LINK_SCHEMA('TEST2', '', 'jdbc:h2:./test2', 'sa', 'sa', 'PUBLIC');\n" +
                "\n" +
                "TABLE\n" +
                "{ TABLE | TABLE_DISTINCT }\n" +
                "( { name dataTypeOrDomain = {array|rowValueExpression} } [,...] )\n" +
                "Returns the result set. TABLE_DISTINCT removes duplicate rows.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "SELECT * FROM TABLE(V INT = ARRAY[1, 2]);\n" +
                "SELECT * FROM TABLE(ID INT=(1, 2), NAME VARCHAR=('Hello', 'World'));\n" +
                "\n" +
                "UNNEST\n" +
                "UNNEST(array, [,...]) [WITH ORDINALITY]\n" +
                "Returns the result set. Number of columns is equal to number of arguments, plus one additional column with row number if WITH ORDINALITY is specified. Number of rows is equal to length of longest specified array. If multiple arguments are specified and they have different length, cells with missing values will contain null values.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "SELECT * FROM UNNEST(ARRAY['a', 'b', 'c']);";

        String[] lines = content.split("\n");

        ArrayList<String> signatureFinal = new ArrayList<>();
        // 过滤3行以上的数据
        ArrayList<String> signatureGroup = new ArrayList<>();
        for (String line : lines) {
            if (line.isEmpty()) {
                // 如果当前列表》=3，那么函数签名有效，保存到最终列表
                if (signatureGroup.size() >= 3) {
                    signatureFinal.addAll(signatureGroup);
                }
                // 清空列表
                signatureGroup.clear();

            } else {
                // 如果不为空行，那么加入当前列表
                signatureGroup.add(line);
            }

        }

        signatureFinal.forEach(System.out::println);

    }

    @Test
    void ImpalaDBFunctionsPreProcessor() {
        String content ="BITAND(integer_type a, same_type b)\n" +
                "Purpose: Returns an integer value representing the bits that are set to 1 in both of the arguments. If the arguments are of different sizes, the smaller is promoted to the type of the larger.\n" +
                "Usage notes: The BITAND() function is equivalent to the & binary operator.\n" +
                "\n" +
                "Return type: Same as the input value\n" +
                "\n" +
                "Added in: Impala 2.3.0\n" +
                "\n" +
                "Examples:\n" +
                "\n" +
                "The following examples show the results of ANDing integer values. 255 contains all 1 bits in its lowermost 7 bits. 32767 contains all 1 bits in its lowermost 15 bits. You can use the BIN() function to check the binary representation of any integer value, although the result is always represented as a 64-bit value. If necessary, the smaller argument is promoted to the type of the larger one.\n" +
                "\n" +
                "select bitand(255, 32767); /* 0000000011111111 & 0111111111111111 */\n" +
                "+--------------------+\n" +
                "| bitand(255, 32767) |\n" +
                "+--------------------+\n" +
                "| 255                |\n" +
                "+--------------------+\n" +
                "\n" +
                "select bitand(32767, 1); /* 0111111111111111 & 0000000000000001 */\n" +
                "+------------------+\n" +
                "| bitand(32767, 1) |\n" +
                "+------------------+\n" +
                "| 1                |\n" +
                "+------------------+\n" +
                "\n" +
                "select bitand(32, 16); /* 00010000 & 00001000 */\n" +
                "+----------------+\n" +
                "| bitand(32, 16) |\n" +
                "+----------------+\n" +
                "| 0              |\n" +
                "+----------------+\n" +
                "\n" +
                "select bitand(12,5); /* 00001100 & 00000101 */\n" +
                "+---------------+\n" +
                "| bitand(12, 5) |\n" +
                "+---------------+\n" +
                "| 4             |\n" +
                "+---------------+\n" +
                "\n" +
                "select bitand(-1,15); /* 11111111 & 00001111 */\n" +
                "+----------------+\n" +
                "| bitand(-1, 15) |\n" +
                "+----------------+\n" +
                "| 15             |\n" +
                "+----------------+\n" +
                "BITNOT(integer_type a)\n" +
                "Purpose: Inverts all the bits of the input argument.\n" +
                "Usage notes: The BITNOT() function is equivalent to the ~ unary operator.\n" +
                "\n" +
                "Return type: Same as the input value\n" +
                "\n" +
                "Added in: Impala 2.3.0\n" +
                "\n" +
                "Examples:\n" +
                "\n" +
                "These examples illustrate what happens when you flip all the bits of an integer value. The sign always changes. The decimal representation is one different between the positive and negative values.\n" +
                "\n" +
                "select bitnot(127); /* 01111111 -> 10000000 */\n" +
                "+-------------+\n" +
                "| bitnot(127) |\n" +
                "+-------------+\n" +
                "| -128        |\n" +
                "+-------------+\n" +
                "\n" +
                "select bitnot(16); /* 00010000 -> 11101111 */\n" +
                "+------------+\n" +
                "| bitnot(16) |\n" +
                "+------------+\n" +
                "| -17        |\n" +
                "+------------+\n" +
                "\n" +
                "select bitnot(0); /* 00000000 -> 11111111 */\n" +
                "+-----------+\n" +
                "| bitnot(0) |\n" +
                "+-----------+\n" +
                "| -1        |\n" +
                "+-----------+\n" +
                "\n" +
                "select bitnot(-128); /* 10000000 -> 01111111 */\n" +
                "+--------------+\n" +
                "| bitnot(-128) |\n" +
                "+--------------+\n" +
                "| 127          |\n" +
                "+--------------+\n" +
                "BITOR(integer_type a, same_type b)\n" +
                "Purpose: Returns an integer value representing the bits that are set to 1 in either of the arguments. If the arguments are of different sizes, the smaller is promoted to the type of the larger.\n" +
                "Usage notes: The BITOR() function is equivalent to the | binary operator.\n" +
                "\n" +
                "Return type: Same as the input value\n" +
                "\n" +
                "Added in: Impala 2.3.0\n" +
                "\n" +
                "Examples:\n" +
                "\n" +
                "The following examples show the results of ORing integer values.\n" +
                "\n" +
                "select bitor(1,4); /* 00000001 | 00000100 */\n" +
                "+-------------+\n" +
                "| bitor(1, 4) |\n" +
                "+-------------+\n" +
                "| 5           |\n" +
                "+-------------+\n" +
                "\n" +
                "select bitor(16,48); /* 00001000 | 00011000 */\n" +
                "+---------------+\n" +
                "| bitor(16, 48) |\n" +
                "+---------------+\n" +
                "| 48            |\n" +
                "+---------------+\n" +
                "\n" +
                "select bitor(0,7); /* 00000000 | 00000111 */\n" +
                "+-------------+\n" +
                "| bitor(0, 7) |\n" +
                "+-------------+\n" +
                "| 7           |\n" +
                "+-------------+\n" +
                "BITXOR(integer_type a, same_type b)\n" +
                "Purpose: Returns an integer value representing the bits that are set to 1 in one but not both of the arguments. If the arguments are of different sizes, the smaller is promoted to the type of the larger.\n" +
                "Usage notes: The BITXOR() function is equivalent to the ^ binary operator.\n" +
                "\n" +
                "Return type: Same as the input value\n" +
                "\n" +
                "Added in: Impala 2.3.0\n" +
                "\n" +
                "Examples:\n" +
                "\n" +
                "The following examples show the results of XORing integer values. XORing a non-zero value with zero returns the non-zero value. XORing two identical values returns zero, because all the 1 bits from the first argument are also 1 bits in the second argument. XORing different non-zero values turns off some bits and leaves others turned on, based on whether the same bit is set in both arguments.\n" +
                "\n" +
                "select bitxor(0,15); /* 00000000 ^ 00001111 */\n" +
                "+---------------+\n" +
                "| bitxor(0, 15) |\n" +
                "+---------------+\n" +
                "| 15            |\n" +
                "+---------------+\n" +
                "\n" +
                "select bitxor(7,7); /* 00000111 ^ 00000111 */\n" +
                "+--------------+\n" +
                "| bitxor(7, 7) |\n" +
                "+--------------+\n" +
                "| 0            |\n" +
                "+--------------+\n" +
                "\n" +
                "select bitxor(8,4); /* 00001000 ^ 00000100 */\n" +
                "+--------------+\n" +
                "| bitxor(8, 4) |\n" +
                "+--------------+\n" +
                "| 12           |\n" +
                "+--------------+\n" +
                "\n" +
                "select bitxor(3,7); /* 00000011 ^ 00000111 */\n" +
                "+--------------+\n" +
                "| bitxor(3, 7) |\n" +
                "+--------------+\n" +
                "| 4            |\n" +
                "+--------------+\n" +
                "COUNTSET(integer_type a [, INT zero_or_one])\n" +
                "Purpose: By default, returns the number of 1 bits in the specified integer value. If the optional second argument is set to zero, it returns the number of 0 bits instead.\n" +
                "Usage notes:\n" +
                "\n" +
                "In discussions of information theory, this operation is referred to as the \"population count\" or \"popcount\".\n" +
                "\n" +
                "Return type: Same as the input value\n" +
                "\n" +
                "Added in: Impala 2.3.0\n" +
                "\n" +
                "Examples:\n" +
                "\n" +
                "The following examples show how to count the number of 1 bits in an integer value.\n" +
                "\n" +
                "select countset(1); /* 00000001 */\n" +
                "+-------------+\n" +
                "| countset(1) |\n" +
                "+-------------+\n" +
                "| 1           |\n" +
                "+-------------+\n" +
                "\n" +
                "select countset(3); /* 00000011 */\n" +
                "+-------------+\n" +
                "| countset(3) |\n" +
                "+-------------+\n" +
                "| 2           |\n" +
                "+-------------+\n" +
                "\n" +
                "select countset(16); /* 00010000 */\n" +
                "+--------------+\n" +
                "| countset(16) |\n" +
                "+--------------+\n" +
                "| 1            |\n" +
                "+--------------+\n" +
                "\n" +
                "select countset(17); /* 00010001 */\n" +
                "+--------------+\n" +
                "| countset(17) |\n" +
                "+--------------+\n" +
                "| 2            |\n" +
                "+--------------+\n" +
                "\n" +
                "select countset(7,1); /* 00000111 = 3 1 bits; the function counts 1 bits by default */\n" +
                "+----------------+\n" +
                "| countset(7, 1) |\n" +
                "+----------------+\n" +
                "| 3              |\n" +
                "+----------------+\n" +
                "\n" +
                "select countset(7,0); /* 00000111 = 5 0 bits; third argument can only be 0 or 1 */\n" +
                "+----------------+\n" +
                "| countset(7, 0) |\n" +
                "+----------------+\n" +
                "| 5              |\n" +
                "+----------------+\n" +
                "GETBIT(integer_type a, INT position)\n" +
                "Purpose: Returns a 0 or 1 representing the bit at a specified position. The positions are numbered right to left, starting at zero. The position argument cannot be negative.\n" +
                "Usage notes:\n" +
                "\n" +
                "When you use a literal input value, it is treated as an 8-bit, 16-bit, and so on value, the smallest type that is appropriate. The type of the input value limits the range of the positions. Cast the input value to the appropriate type if you need to ensure it is treated as a 64-bit, 32-bit, and so on value.\n" +
                "\n" +
                "Return type: Same as the input value\n" +
                "\n" +
                "Added in: Impala 2.3.0\n" +
                "\n" +
                "Examples:\n" +
                "\n" +
                "The following examples show how to test a specific bit within an integer value.\n" +
                "\n" +
                "select getbit(1,0); /* 00000001 */\n" +
                "+--------------+\n" +
                "| getbit(1, 0) |\n" +
                "+--------------+\n" +
                "| 1            |\n" +
                "+--------------+\n" +
                "\n" +
                "select getbit(16,1) /* 00010000 */\n" +
                "+---------------+\n" +
                "| getbit(16, 1) |\n" +
                "+---------------+\n" +
                "| 0             |\n" +
                "+---------------+\n" +
                "\n" +
                "select getbit(16,4) /* 00010000 */\n" +
                "+---------------+\n" +
                "| getbit(16, 4) |\n" +
                "+---------------+\n" +
                "| 1             |\n" +
                "+---------------+\n" +
                "\n" +
                "select getbit(16,5) /* 00010000 */\n" +
                "+---------------+\n" +
                "| getbit(16, 5) |\n" +
                "+---------------+\n" +
                "| 0             |\n" +
                "+---------------+\n" +
                "\n" +
                "select getbit(-1,3); /* 11111111 */\n" +
                "+---------------+\n" +
                "| getbit(-1, 3) |\n" +
                "+---------------+\n" +
                "| 1             |\n" +
                "+---------------+\n" +
                "\n" +
                "select getbit(-1,25); /* 11111111 */\n" +
                "ERROR: Invalid bit position: 25\n" +
                "\n" +
                "select getbit(cast(-1 as int),25); /* 11111111111111111111111111111111 */\n" +
                "+-----------------------------+\n" +
                "| getbit(cast(-1 as int), 25) |\n" +
                "+-----------------------------+\n" +
                "| 1                           |\n" +
                "+-----------------------------+\n" +
                "ROTATELEFT(integer_type a, INT positions)\n" +
                "Purpose: Rotates an integer value left by a specified number of bits. As the most significant bit is taken out of the original value, if it is a 1 bit, it is \"rotated\" back to the least significant bit. Therefore, the final value has the same number of 1 bits as the original value, just in different positions. In computer science terms, this operation is a \"circular shift\".\n" +
                "Usage notes:\n" +
                "\n" +
                "Specifying a second argument of zero leaves the original value unchanged. Rotating a -1 value by any number of positions still returns -1, because the original value has all 1 bits and all the 1 bits are preserved during rotation. Similarly, rotating a 0 value by any number of positions still returns 0. Rotating a value by the same number of bits as in the value returns the same value. Because this is a circular operation, the number of positions is not limited to the number of bits in the input value. For example, rotating an 8-bit value by 1, 9, 17, and so on positions returns an identical result in each case.\n" +
                "\n" +
                "Return type: Same as the input value\n" +
                "\n" +
                "Added in: Impala 2.3.0\n" +
                "\n" +
                "Examples:\n" +
                "\n" +
                "select rotateleft(1,4); /* 00000001 -> 00010000 */\n" +
                "+------------------+\n" +
                "| rotateleft(1, 4) |\n" +
                "+------------------+\n" +
                "| 16               |\n" +
                "+------------------+\n" +
                "\n" +
                "select rotateleft(-1,155); /* 11111111 -> 11111111 */\n" +
                "+---------------------+\n" +
                "| rotateleft(-1, 155) |\n" +
                "+---------------------+\n" +
                "| -1                  |\n" +
                "+---------------------+\n" +
                "\n" +
                "select rotateleft(-128,1); /* 10000000 -> 00000001 */\n" +
                "+---------------------+\n" +
                "| rotateleft(-128, 1) |\n" +
                "+---------------------+\n" +
                "| 1                   |\n" +
                "+---------------------+\n" +
                "\n" +
                "select rotateleft(-127,3); /* 10000001 -> 00001100 */\n" +
                "+---------------------+\n" +
                "| rotateleft(-127, 3) |\n" +
                "+---------------------+\n" +
                "| 12                  |\n" +
                "+---------------------+\n" +
                "\n" +
                "ROTATERIGHT(integer_type a, INT positions)\n" +
                "Purpose: Rotates an integer value right by a specified number of bits. As the least significant bit is taken out of the original value, if it is a 1 bit, it is \"rotated\" back to the most significant bit. Therefore, the final value has the same number of 1 bits as the original value, just in different positions. In computer science terms, this operation is a \"circular shift\".\n" +
                "Usage notes:\n" +
                "\n" +
                "Specifying a second argument of zero leaves the original value unchanged. Rotating a -1 value by any number of positions still returns -1, because the original value has all 1 bits and all the 1 bits are preserved during rotation. Similarly, rotating a 0 value by any number of positions still returns 0. Rotating a value by the same number of bits as in the value returns the same value. Because this is a circular operation, the number of positions is not limited to the number of bits in the input value. For example, rotating an 8-bit value by 1, 9, 17, and so on positions returns an identical result in each case.\n" +
                "\n" +
                "Return type: Same as the input value\n" +
                "\n" +
                "Added in: Impala 2.3.0\n" +
                "\n" +
                "Examples:\n" +
                "\n" +
                "select rotateright(16,4); /* 00010000 -> 00000001 */\n" +
                "+--------------------+\n" +
                "| rotateright(16, 4) |\n" +
                "+--------------------+\n" +
                "| 1                  |\n" +
                "+--------------------+\n" +
                "\n" +
                "select rotateright(-1,155); /* 11111111 -> 11111111 */\n" +
                "+----------------------+\n" +
                "| rotateright(-1, 155) |\n" +
                "+----------------------+\n" +
                "| -1                   |\n" +
                "+----------------------+\n" +
                "\n" +
                "select rotateright(-128,1); /* 10000000 -> 01000000 */\n" +
                "+----------------------+\n" +
                "| rotateright(-128, 1) |\n" +
                "+----------------------+\n" +
                "| 64                   |\n" +
                "+----------------------+\n" +
                "\n" +
                "select rotateright(-127,3); /* 10000001 -> 00110000 */\n" +
                "+----------------------+\n" +
                "| rotateright(-127, 3) |\n" +
                "+----------------------+\n" +
                "| 48                   |\n" +
                "+----------------------+\n" +
                "SETBIT(integer_type a, INT position [, INT zero_or_one])\n" +
                "Purpose: By default, changes a bit at a specified position to a 1, if it is not already. If the optional third argument is set to zero, the specified bit is set to 0 instead.\n" +
                "Usage notes:\n" +
                "\n" +
                "If the bit at the specified position was already 1 (by default) or 0 (with a third argument of zero), the return value is the same as the first argument. The positions are numbered right to left, starting at zero. (Therefore, the return value could be different from the first argument even if the position argument is zero.) The position argument cannot be negative.\n" +
                "When you use a literal input value, it is treated as an 8-bit, 16-bit, and so on value, the smallest type that is appropriate. The type of the input value limits the range of the positions. Cast the input value to the appropriate type if you need to ensure it is treated as a 64-bit, 32-bit, and so on value.\n" +
                "\n" +
                "Return type: Same as the input value\n" +
                "\n" +
                "Added in: Impala 2.3.0\n" +
                "\n" +
                "Examples:\n" +
                "\n" +
                "select setbit(0,0); /* 00000000 -> 00000001 */\n" +
                "+--------------+\n" +
                "| setbit(0, 0) |\n" +
                "+--------------+\n" +
                "| 1            |\n" +
                "+--------------+\n" +
                "\n" +
                "select setbit(0,3); /* 00000000 -> 00001000 */\n" +
                "+--------------+\n" +
                "| setbit(0, 3) |\n" +
                "+--------------+\n" +
                "| 8            |\n" +
                "+--------------+\n" +
                "\n" +
                "select setbit(7,3); /* 00000111 -> 00001111 */\n" +
                "+--------------+\n" +
                "| setbit(7, 3) |\n" +
                "+--------------+\n" +
                "| 15           |\n" +
                "+--------------+\n" +
                "\n" +
                "select setbit(15,3); /* 00001111 -> 00001111 */\n" +
                "+---------------+\n" +
                "| setbit(15, 3) |\n" +
                "+---------------+\n" +
                "| 15            |\n" +
                "+---------------+\n" +
                "\n" +
                "select setbit(0,32); /* By default, 0 is a TINYINT with only 8 bits. */\n" +
                "ERROR: Invalid bit position: 32\n" +
                "\n" +
                "select setbit(cast(0 as bigint),32); /* For BIGINT, the position can be 0..63. */\n" +
                "+-------------------------------+\n" +
                "| setbit(cast(0 as bigint), 32) |\n" +
                "+-------------------------------+\n" +
                "| 4294967296                    |\n" +
                "+-------------------------------+\n" +
                "\n" +
                "select setbit(7,3,1); /* 00000111 -> 00001111; setting to 1 is the default */\n" +
                "+-----------------+\n" +
                "| setbit(7, 3, 1) |\n" +
                "+-----------------+\n" +
                "| 15              |\n" +
                "+-----------------+\n" +
                "\n" +
                "select setbit(7,2,0); /* 00000111 -> 00000011; third argument of 0 clears instead of sets */\n" +
                "+-----------------+\n" +
                "| setbit(7, 2, 0) |\n" +
                "+-----------------+\n" +
                "| 3               |\n" +
                "+-----------------+\n" +
                "SHIFTLEFT(integer_type a, INT positions)\n" +
                "Purpose: Shifts an integer value left by a specified number of bits. As the most significant bit is taken out of the original value, it is discarded and the least significant bit becomes 0. In computer science terms, this operation is a \"logical shift\".\n" +
                "Usage notes:\n" +
                "\n" +
                "The final value has either the same number of 1 bits as the original value, or fewer. Shifting an 8-bit value by 8 positions, a 16-bit value by 16 positions, and so on produces a result of zero.\n" +
                "\n" +
                "Specifying a second argument of zero leaves the original value unchanged. Shifting any value by 0 returns the original value. Shifting any value by 1 is the same as multiplying it by 2, as long as the value is small enough; larger values eventually become negative when shifted, as the sign bit is set. Starting with the value 1 and shifting it left by N positions gives the same result as 2 to the Nth power, or POW(2,N).\n" +
                "\n" +
                "Return type: Same as the input value\n" +
                "\n" +
                "Added in: Impala 2.3.0\n" +
                "\n" +
                "Examples:\n" +
                "\n" +
                "select shiftleft(1,0); /* 00000001 -> 00000001 */\n" +
                "+-----------------+\n" +
                "| shiftleft(1, 0) |\n" +
                "+-----------------+\n" +
                "| 1               |\n" +
                "+-----------------+\n" +
                "\n" +
                "select shiftleft(1,3); /* 00000001 -> 00001000 */\n" +
                "+-----------------+\n" +
                "| shiftleft(1, 3) |\n" +
                "+-----------------+\n" +
                "| 8               |\n" +
                "+-----------------+\n" +
                "\n" +
                "select shiftleft(8,2); /* 00001000 -> 00100000 */\n" +
                "+-----------------+\n" +
                "| shiftleft(8, 2) |\n" +
                "+-----------------+\n" +
                "| 32              |\n" +
                "+-----------------+\n" +
                "\n" +
                "select shiftleft(127,1); /* 01111111 -> 11111110 */\n" +
                "+-------------------+\n" +
                "| shiftleft(127, 1) |\n" +
                "+-------------------+\n" +
                "| -2                |\n" +
                "+-------------------+\n" +
                "\n" +
                "select shiftleft(127,5); /* 01111111 -> 11100000 */\n" +
                "+-------------------+\n" +
                "| shiftleft(127, 5) |\n" +
                "+-------------------+\n" +
                "| -32               |\n" +
                "+-------------------+\n" +
                "\n" +
                "select shiftleft(-1,4); /* 11111111 -> 11110000 */\n" +
                "+------------------+\n" +
                "| shiftleft(-1, 4) |\n" +
                "+------------------+\n" +
                "| -16              |\n" +
                "+------------------+\n" +
                "SHIFTRIGHT(integer_type a, INT positions)\n" +
                "Purpose: Shifts an integer value right by a specified number of bits. As the least significant bit is taken out of the original value, it is discarded and the most significant bit becomes 0. In computer science terms, this operation is a \"logical shift\".\n" +
                "Usage notes:\n" +
                "\n" +
                "Therefore, the final value has either the same number of 1 bits as the original value, or fewer. Shifting an 8-bit value by 8 positions, a 16-bit value by 16 positions, and so on produces a result of zero.\n" +
                "\n" +
                "Specifying a second argument of zero leaves the original value unchanged. Shifting any value by 0 returns the original value. Shifting any positive value right by 1 is the same as dividing it by 2. Negative values become positive when shifted right.\n" +
                "\n" +
                "Return type: Same as the input value";

        int GROUP_COUNT = 3;

        ArrayList<String> newLines = new ArrayList<>();
        String[] lines = content.split("\n");

        // 计算对其函数名需要宽度
        String prevLine = "";
        String currLine = "";
        int NameLength = 0;
        for (String line : lines) {
            currLine = line;
            // 首先查找Purpose:
            if (currLine.contains("Purpose:")) {
                String name = prevLine;
                if (prevLine.contains("(")) {
                    name = prevLine.substring(0,prevLine.indexOf('('));
                }
                newLines.add(name);
                newLines.add(prevLine);
                newLines.add(currLine.replace("Purpose:",""));

                int length = name.length();
                if (length > NameLength) {
                    NameLength = length;
                }
            }
            prevLine = line;

        }

        NameLength += 8;    //2个TAB长度

        for (int i = 0; i < newLines.size(); i+=GROUP_COUNT) {
            String line1 = newLines.get(i);
            String line2 = newLines.get(i+1);
            String line3 = newLines.get(i+2);

            int spaceNeeded = NameLength - line1.length() ;
            spaceNeeded = Math.max(spaceNeeded, 0);
            String signature = line1 + " ".repeat(spaceNeeded) + ":" + line2 +" :" + line3;

            System.out.println(signature);
        }

    }

    @Test
    void H2DBFunctionsPostProcessor() {
        String content ="CSVREAD\n" +
                "CSVREAD(fileNameString [, columnsString [, csvOptions ] ] )\n" +
                "Returns the result set of reading the CSV (comma separated values) file. For each parameter, NULL means the default value should be used.\n" +
                "LINK_SCHEMA\n" +
                "LINK_SCHEMA (targetSchemaString, driverString, urlString,userString, passwordString, sourceSchemaString)\n" +
                "Creates table links for all tables in a schema. If tables with the same name already exist, they are dropped first. The target schema is created automatically if it does not yet exist. The driver name may be empty if the driver is already loaded. The list of tables linked is returned in the form of a result set. Admin rights are required to execute this command.\n" +
                "TABLE\n" +
                "{ TABLE | TABLE_DISTINCT }( { name dataTypeOrDomain = {array|rowValueExpression} } [,...] )\n" +
                "Returns the result set. TABLE_DISTINCT removes duplicate rows.\n" +
                "UNNEST\n" +
                "UNNEST(array, [,...]) [WITH ORDINALITY]\n" +
                "Returns the result set. Number of columns is equal to number of arguments, plus one additional column with row number if WITH ORDINALITY is specified. Number of rows is equal to length of longest specified array. If multiple arguments are specified and they have different length, cells with missing values will contain null values.\n" ;

        int GROUP_COUNT = 3;
        StringBuilder stringBuffer = new StringBuilder();

        ArrayList<String> newLines = new ArrayList<>();
        String[] lines = content.split("\n");

        // 计算对其函数名需要宽度
        int NameLength = 0;
        for (int i = 0; i < lines.length; i+=GROUP_COUNT) {
            String functionName = lines[i];
            newLines.add(functionName);
            newLines.add(lines[i+1]);
            newLines.add(lines[i+2]);
            int length = functionName.length();
            if (length > NameLength) {
                NameLength = length;
            }
        }
        NameLength += 8;    //2个TAB长度

        for (int i = 0; i < newLines.size(); i+=GROUP_COUNT) {
            String line1 = newLines.get(i);
            String line2 = newLines.get(i+1);
            String line3 = newLines.get(i+2);

            int spaceNeeded = NameLength - line1.length() ;
            spaceNeeded = Math.max(spaceNeeded, 0);
            String signature = line1 + " ".repeat(spaceNeeded) + ":" + line2 +" :" + line3;

            System.out.println(signature);
        }
    }

    @Test
    void DMDBFunctionsPostProcessor() {
        String content ="DECODE(exp, search1, result1, … searchn, resultn [,default])\t查表译码\n" +
                "ISDATE(exp)\t判断表达式是否为有效的日期\n" +
                "ISNUMERIC(exp)\t判断表达式是否为有效的数值\n" +
                "DM_HASH (exp)\t根据给定表达式生成 HASH 值\n" +
                "LNNVL(condition)\t根据表达式计算结果返回布尔值\n" +
                "LENGTHB(value)\t返回 value 的字节数\n" +
                "FIELD(value, e1, e2, e3, e4...en)\t返回 value 在列表 e1, e2, e3, e4...en 中的位置序号，不在输入列表时则返回 0\n" +
                "ORA_HASH(exp [,max_bucket [,seed_value]])\t为表达式 exp 生成 HASH 桶值\n";
        int GROUP_COUNT = 3;
        StringBuilder stringBuffer = new StringBuilder();

        ArrayList<String> newLines = new ArrayList<>();
        String[] lines = content.split("\n");

        // 计算对其函数名需要宽度
        int NameLength = 0;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String[] split = line.split("\\(");

            String functionName = split[0];
            int length = functionName.length();
            if (length > NameLength) {
                NameLength = length;
            }

            newLines.add(functionName);
            newLines.add(line);
        }

        NameLength += 8;    //2个TAB长度

        for (int i = 0; i < newLines.size(); i+=2) {
            String name = newLines.get(i);
            String line2 = newLines.get(i+1);

            int spaceNeeded = NameLength - name.length() ;
            spaceNeeded = Math.max(spaceNeeded, 0);
            String signature = name + " ".repeat(spaceNeeded) + ":" + line2;

            System.out.println(signature);
        }
    }

    @Test
    void KingBaseFunctionsProcessor() throws FileNotFoundException {

        String fileName = "C:\\文档\\总结\\数据库函数\\kingbase.txt";

        // 出现用法后等待功能的描述
        boolean bInScope = false;
        boolean bWaitDescription = false;
        ArrayList<String> newLines = new ArrayList<>();

        try (Scanner sc = new Scanner(new FileReader(fileName))) {
            while (sc.hasNextLine()) {  //按行读取字符串
                String line = sc.nextLine();
                // 搜索name
                Pattern p = Pattern.compile("^8\\.\\d*[1-9]\\."); //得到字符串中的数字
                Matcher m = p.matcher(line);
                if(m.find()){
                    String ss = m.group();
                    Pattern p2 = Pattern.compile("[A-Za-z]+"); //得到字符串中的数字
                    Matcher m2 = p2.matcher(line);
                    if (m2.find()) {
                        newLines.add(m2.group());
                        System.out.print(line);
                    }
                }

                if (line.endsWith("用法：")) {

                    assertFalse(bWaitDescription);

                    String usage = sc.nextLine();
                    newLines.add(usage);
                    bInScope = true;
                    bWaitDescription = true;
                    System.out.print(usage);
                    continue;
                }

                if (line.trim().endsWith("功能：") || line.endsWith("功能： ¶")) {

                    assertTrue(bInScope);

                    String description = sc.nextLine();
                    newLines.add(description);
                    System.out.println(description);
                    bInScope = false;
                    bWaitDescription = false;
                }
            }
        }

        int spaceNeeded = 8;    //2个TAB长度
        for (int i = 0; i < newLines.size(); i+=3) {
            String name = newLines.get(i);
            String usage = newLines.get(i+1);
            String descr = newLines.get(i+2);

//            int spaceNeeded = NameLength - name.length() ;
//            spaceNeeded = Math.max(spaceNeeded, 0);
            String signature = name + " ".repeat(spaceNeeded) + ":" + usage  + ":" + descr;

            System.out.println(signature);
        }

        // 计算对其函数名需要宽度
//        int NameLength = 0;
//        for (int i = 0; i < lines.length; i++) {
//            String line = lines[i];
//            String[] split = line.split("\\(");
//
//            String functionName = split[0];
//            int length = functionName.length();
//            if (length > NameLength) {
//                NameLength = length;
//            }
//
//            newLines.add(functionName);
//            newLines.add(line);
//        }
//
//        NameLength += 8;    //2个TAB长度
//
//        for (int i = 0; i < newLines.size(); i+=2) {
//            String name = newLines.get(i);
//            String line2 = newLines.get(i+1);
//
//            int spaceNeeded = NameLength - name.length() ;
//            spaceNeeded = Math.max(spaceNeeded, 0);
//            String signature = name + " ".repeat(spaceNeeded) + ":" + line2;
//
//            System.out.println(signature);
//        }
    }


    @Test
    void ClassifyKingBaseFunctions() {
        String nameString = "CURRENT_SETTING ;GET_DISK_INFO ;SYS_CANCEL_BACKEND ;SYS_COLUMN_SIZE ;SYS_DATABASE_SIZE ;SYS_INDEXES_SIZE ;SYS_LS_DIR ;SYS_LS_LOGDIR ;SYS_LS_TMPDIR ;SYS_LS_WALDIR ;SYS_READ_BINARY_FILE ;SYS_READ_FILE ;SYS_RELATION_FILENODE ;SYS_RELATION_FILEPATH ;SYS_RELATION_SIZE ;SYS_RELOAD_CONF ;SYS_SIZE_BYTES ;SYS_SIZE_PRETTY ;SYS_STAT_FILE ;SYS_TABLE_SIZE ;SYS_TABLESPACE_SIZE ;SYS_TERMINATE_BACKEND ;SYS_TOTAL_RELATION_SIZE";
        List<String> functions = new ArrayList<>();
        Utils.loadFromFile("META-INF/druid/parser/kingbase/builtin_functions", functions);

        String[] names = nameString.split(";");

        List<String> newFunctions = new ArrayList<>();
        for (String name: names) {
            boolean bFound = false;

            // 先找完全匹配的
            for (String function: functions) {
                String[] split = function.split(":");
                if (split[0].trim().equalsIgnoreCase(name.trim())) {
                    newFunctions.add(function);
                    bFound = true;
                }
            }

            if (!bFound) {
                // 再找部分匹配的
                for (String function: functions) {
                    if (function.startsWith(name.trim())) {
                        newFunctions.add(function);
                        bFound = true;
                    }
                }
            }

            if (!bFound) {
                System.out.println("missing: " + name);
            }
        }

        newFunctions.forEach(System.out::println);

    }

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    @Test
    void getDbKeywords() {
        String dbKeywords = DbUtils.getDbKeywordsAsString(DbType.hive);
        System.out.println(dbKeywords);
    }

}