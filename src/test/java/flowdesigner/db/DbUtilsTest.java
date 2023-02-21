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
                "WKT Definition\n" +
                "Well-Known Text for exchanging geometry data in ASCII form.\n" +
                "AsText\n" +
                "Synonym for ST_AsText.\n" +
                "AsWKT\n" +
                "Synonym for ST_AsText.\n" +
                "GeomCollFromText\n" +
                "Synonym for ST_GeomCollFromText.\n" +
                "GeometryCollectionFromText\n" +
                "Synonym for ST_GeomCollFromText.\n" +
                "GeometryFromText\n" +
                "Synonym for ST_GeomFromText.\n" +
                "GeomFromText\n" +
                "Synonym for ST_GeomFromText.\n" +
                "LineFromText\n" +
                "Synonym for ST_LineFromText.\n" +
                "LineStringFromText\n" +
                "Synonym for ST_LineFromText.\n" +
                "MLineFromText\n" +
                "Constructs MULTILINESTRING using its WKT representation and SRID.\n" +
                "MPointFromText\n" +
                "Constructs a MULTIPOINT value using its WKT and SRID.\n" +
                "MPolyFromText\n" +
                "Constructs a MULTIPOLYGON value.\n" +
                "MultiLineStringFromText\n" +
                "Synonym for MLineFromText.\n" +
                "MultiPointFromText\n" +
                "Synonym for MPointFromText.\n" +
                "MultiPolygonFromText\n" +
                "Synonym for MPolyFromText.\n" +
                "PointFromText\n" +
                "Synonym for ST_PointFromText.\n" +
                "PolyFromText\n" +
                "Synonym for ST_PolyFromText.\n" +
                "PolygonFromText\n" +
                "Synonym for ST_PolyFromText.\n" +
                "ST_AsText\n" +
                "Converts a value to its WKT-Definition.\n" +
                "ST_ASWKT\n" +
                "Synonym for ST_ASTEXT().\n" +
                "ST_GeomCollFromText\n" +
                "Constructs a GEOMETRYCOLLECTION value.\n" +
                "ST_GeometryCollectionFromText\n" +
                "Synonym for ST_GeomCollFromText.\n" +
                "ST_GeometryFromText\n" +
                "Synonym for ST_GeomFromText.\n" +
                "ST_GeomFromText\n" +
                "Constructs a geometry value using its WKT and SRID.\n" +
                "ST_LineFromText\n" +
                "Creates a linestring value.\n" +
                "ST_LineStringFromText\n" +
                "Synonym for ST_LineFromText.\n" +
                "ST_PointFromText\n" +
                "Constructs a POINT value.\n" +
                "ST_PolyFromText\n" +
                "Constructs a POLYGON value.\n" +
                "ST_PolygonFromText\n" +
                "Synonym for ST_PolyFromText.";
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