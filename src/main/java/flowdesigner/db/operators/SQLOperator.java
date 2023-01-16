package flowdesigner.db.operators;

import com.alibaba.druid.DbType;

import java.util.ArrayList;
import java.util.HashSet;

import static flowdesigner.db.operators.SQLFunctionCatalog.*;

@Deprecated
public enum SQLOperator {

    //region 关系运算
    BETWEEN("between", RelationalOperator,"? [NOT] BETWEEN b AND c"),
    IS("IS", RelationalOperator,"? IS"),
    IS_NOT("IS NOT", RelationalOperator, "? IS NOT"),
    IS_NULL("isnull", RelationalOperator, "? IS NULL"),
    IS_NOT_NULL("IS NOT NULL", RelationalOperator,"? IS NOT NULL"),
    LIKE("LIKE", RelationalOperator,"? LIKE pat"),
    NOT_LIKE("NOT LIKE", RelationalOperator,"? NOT LIKE pat"),
    LIKE1("LIKE", RelationalOperator,"? [NOT] LIKE y [ESCAPE 'z'] ", DbType.of(DbType.oracle)),
    R_LIKE("RLIKE", RelationalOperator,"? RLIKE pat"),
    NOT_R_LIKE("NOT RLIKE",RelationalOperator,"? NOT RLIKE pat"),
    //endregion
    //region 逻辑运算符
    BooleanAnd("AND", LogicalOperator,"? AND B"),
    BooleanXor("XOR",LogicalOperator,"? XOR B"),
    BooleanOr("OR", LogicalOperator,"? OR B"),
    EXISTS("EXISTS",LogicalOperator,"[NOT] EXISTS (subquery)"),
    EWAH_BITMAP_AND("ewah_bitmap_and", LogicalOperator,"EWAH_BITMAP_AND(b1, b2)"),
    EWAH_BITMAP_OR("ewah_bitmap_or", LogicalOperator,"EWAH_BITMAP_OR(b1, b2)"),
    IN("in", LogicalOperator,"? IN (val1,...)"),
    Not("!", LogicalOperator,"! ?"),
    NOT("NOT", LogicalOperator,"NOT ?"),
    NOT_IN("not in", LogicalOperator, "? NOT IN (value,...|subquery)"),
    PoundGt("#>", LogicalOperator),
    PoundGtGt("#>>", LogicalOperator),
    PG_And("&&", LogicalOperator),
    QuesQues("??", LogicalOperator),
    QuesBar("?|", LogicalOperator),
    QuesAmp("?&", LogicalOperator),
    SubGt("->", LogicalOperator),
    SubGtGt("->>", LogicalOperator),
    XOR("XOR", LogicalOperator, "xor(?, ?)"),
    //endregion
    //region 算术运算符
    Plus("+", ArithmeticOperator,"+?"),
    Negative("-", ArithmeticOperator,"-?"),
    ADD("+", ArithmeticOperator, "? + ?"),
    Subtract("-", ArithmeticOperator, "? - ?"),
    MULTIPLY("*", ArithmeticOperator, "? * ?"),
    Divide("/", ArithmeticOperator, "? / ?"),
    DIV("DIV", ArithmeticOperator, "? DIV ?"),
    MOD("MOD", ArithmeticOperator, "? MOD ?"),
    Modulus("%", ArithmeticOperator, "? % ?"),
    //endregion
    //region 字符串函数
    ARRAY_EXTRACT("array_extract", StringFunction,"array_extract(?,1)"),
    ARRAY_SLICE("array_slice",StringFunction,"array_slice(?, begin, end)"),
    ASCII("ascii", StringFunction,"ASCII(?)"),
    base64("base64",StringFunction,"base64(?)"),
    BIN("bin",StringFunction,"BIN(?)"),
    BIT_LENGTH("BIT_LENGTH",StringFunction,"BIT_LENGTH(?)"),
    BTRIM("BTRIM",StringFunction,"BTRIM(?)"),
    CHAR_STR("CHAR",StringFunction,"CHAR(N,... [USING charset_name])"),
    CHAR_LENGTH("CHAR_LENGTH",StringFunction,"CHAR_LENGTH(?)"),
    CHARACTER_LENGTH("character_length", StringFunction,"CHARACTER_LENGTH(?)"),
    CHR("CHR",StringFunction,"CHR(?)"),
    CHR1("CHR",StringFunction,"CHR(? [USING NCHAR_CS])", DbType.of(DbType.oracle)),
    CHARINDEX("CHARINDEX",StringFunction,"CHARINDEX( substring ,   string ,  start )" ),
    CONCAT("concat", StringFunction,"CONCAT(str1,str2,...)"),
    CONCATA("CONCATA",StringFunction,"CONCATA(?,?)"),
    CONCAT_WS("concat_ws", StringFunction,"CONCAT_WS(separator,str1,str2,...)"),
    CONTEXT_NGRAMS("context_ngrams", StringFunction,"CONTEXT_NGRAMS(array<array<string>>, array<string>, int K, int pf)"),
    CONTAINS("contains",StringFunction,"contains(?, search_string)"),

    DATALENGTH("DATALENGTH",StringFunction,"DATALENGTH(?)"),
    DIFFERENCE("DIFFERENCE",StringFunction,"DIFFERENCE(?,?)"),
    ELT("elt", StringFunction,"ELT(N,str1,str2,str3,...)"),
    EXPORT_SET("export_set",StringFunction,"EXPORT_SET(bits,on,off[,separator[,number_of_bits]])"),

    FORMAT_STR("FORMAT",StringFunction,"FORMAT( value, format)"),
    FIELD("field", StringFunction,"FIELD(str,str1,str2,str3,...)"),
    FIND_IN_SET("find_in_set", StringFunction,"FIND_IN_SET(str,strlist)"),
    FORMAT_NUMBER("format_number", StringFunction,"format_number(number, d)"),
    FROM_BASE64("from_base64",StringFunction,"from_base64(?)"),

    IN_FILE("in_file", StringFunction,"in_file(?, filename)"),
    INSTR("instr", StringFunction,"INSTR(str,substr)"),
    INSTR1("instr", StringFunction,"INSTR(str,substr[,position [,occurrence]])", DbType.of(DbType.oracle)),
    INSTRB("INSTRB", StringFunction,"INSTRB(str,substr[,position [,occurrence]])", DbType.of(DbType.oracle)),
    INSTRC("INSTRC", StringFunction,"INSTRC(str,substr[,position [,occurrence]])", DbType.of(DbType.oracle)),
    INSTR2("INSTR2", StringFunction,"INSTR2(str,substr[,position [,occurrence]])", DbType.of(DbType.oracle)),
    INSTR4("INSTR4", StringFunction,"INSTR4(str,substr[,position [,occurrence]])", DbType.of(DbType.oracle)),
    INSERT("INSERT",StringFunction,"INSERT(?,POS,SIZE,replaceExpr)"),
    INITCAP("initcap", StringFunction,"initcap(?)"),

    LEVENSHTEIN("levenshtein", StringFunction,"levenshtein(string A, string B)"),
    LCASE("lcase", StringFunction,"LCASE(?)"),
    LEFT("LEFT",StringFunction,"LEFT(?,len)"),
    LENGTH("length", StringFunction,"LENGTH(?)"),
    LENGTHB("LENGTHB", StringFunction,"LENGTHB(?)"),
    LENGTHC("LENGTHC", StringFunction,"LENGTHC(?)"),
    LENGTH2("LENGTH2", StringFunction,"LENGTH2(?)"),
    LENGTH4("LENGTH4", StringFunction,"LENGTH4(?)"),
    LEN("LEN",StringFunction,"LEN(?)"),
    LOAD_FILE("load_file",StringFunction,"LOAD_FILE(file_name)"),
    LOCATE("locate", StringFunction,"LOCATE(substr,str[,pos])"),
    LOWER("lower", StringFunction,"LOWER(?)"),
    LPAD("lpad", StringFunction,"LPAD(?,len,padstr)"),
    LPAD1("LPAD", StringFunction,"LPAD(?,len[,padstr])", DbType.of(DbType.oracle)),
    LTRIM("ltrim", StringFunction,"LTRIM(?)"),
    LTRIM1("ltrim", StringFunction,"LTRIM(?[,set])", DbType.of(DbType.oracle)),

    MAKE_SET("make_set", StringFunction,"MAKE_SET(bits,str1,str2,...)"),
    MID("mid", StringFunction,"MID(?,pos,len)"),

    NGRAMS("ngrams", StringFunction,"ngrams(array<array<string>>, int N, int K, int pf)"),
    NCHAR("NCHAR",StringFunction,"NCHAR(n)"),
    NLS_INITCAP("NLS_INITCAP",StringFunction,"NLS_INITCAP(?[,nlsparam])"),
    NLS_LOWER("NLS_LOWER",StringFunction,"NLS_LOWER(?[,nlsparam])"),
    NLSSORT("NLSSORT",StringFunction,"NLSSORT(?[,nlsparam])"),

    OCTET_LENGTH("octet_length", StringFunction, "OCTET_LENGTH(?)"),
    OVERLAY("OVERLAY",StringFunction,"overlay(? placing string from 0)"),
    ORD("ord","ORD(?)"),

    POSITION("POSITION",StringFunction,"position(substring in str)"),
    PARSE_URL("parse_url", StringFunction,"parse_url(urlString, partToExtract [, keyToExtract])"),
    PRINTF("printf", StringFunction,"printf(format, ... args)"),
    POSSTR("POSSTR", StringFunction,"POSSTR(?,EXP2)"),
    PATINDEX("PATINDEX",StringFunction,"PATINDEX('%pattern%', ?)"),

    QUOTE("quote", StringFunction, "QUOTE(?)"),
    QUOTENAME("QUOTENAME",StringFunction,"QUOTENAME( ?, quote_char )"),

    REPEAT("repeat", StringFunction, "REPEAT(?,count)"),
    REPLACE("replace", StringFunction, "REPLACE(str,from_str,to_str)"),
    REPLACE1("replace", StringFunction, "REPLACE(str,search_string [,replacement_string])", DbType.of(DbType.oracle)),
    REVERSE("reverse", StringFunction, "REVERSE(?)"),
    RIGHT("RIGHT",StringFunction,"RIGHT(?,2)"),
    RPAD("rpad", StringFunction, "RPAD(?,len,padstr)"),
    RPAD1("rpad", StringFunction, "RPAD(?,len[,padstr])", DbType.of(DbType.oracle)),
    REPLICATE("REPLICATE",StringFunction,"REPLICATE( ?, 2 )"),
    RTRIM("rtrim", StringFunction, "RTRIM(?)"),
    RTRIM1("rtrim", StringFunction, "RTRIM(?[,set])", DbType.of(DbType.oracle)),
    SENTENCES("sentences", StringFunction,"sentences(string str, string lang, string locale)"),
    SOUNDEX("soundex", StringFunction,"SOUNDEX(?)"),
    SPACE("space", StringFunction, "SPACE(N)"),
    SUBSTR("substr", StringFunction,"SUBSTR(?,pos[,len])"),
    SUBSTRB("SUBSTRB", StringFunction,"SUBSTRB(?,pos[,len])"),
    SUBSTRC("SUBSTRC", StringFunction,"SUBSTRC(?,pos[,len])"),
    SUBSTR2("SUBSTR2", StringFunction,"SUBSTR2(?,pos[,len])"),
    SUBSTR4("SUBSTR4", StringFunction,"SUBSTR4(?,pos[,len])"),
    SUBSTRING("substring", StringFunction, "SUBSTRING(?,pos[,len])"),
    SUBSTRING_INDEX("substring_index", StringFunction,"SUBSTRING_INDEX(?,delim,count)"),
    SPLIT("split", StringFunction,"split(string str, string pat)"),
    SPLIT_PART("SPLIT_PART",StringFunction,"split_part(?, delimiter, 1)"),
    STRPOS("STRPOS",StringFunction,"strpos(string, substring)"),
    STR_TO_MAP("str_to_map", StringFunction,"str_to_map(?[, delimiter1, delimiter2])"),
    STRIP("STRIP",StringFunction,"STRIP(?)"),
    STR("STR",StringFunction,"STR(?)"),
    STUFF("STUFF",StringFunction,"STUFF( ? , start, length, new_string)"),

    TRANSLATE("translate", StringFunction,"translate(input, from, to)"),
    TRANSLATE_USING("TRANSLATE ... USING", StringFunction,"TRANSLATE(input USING cs)"),
    TRIM("trim", StringFunction,"TRIM(?),"),
    TRIM_LEADING("TRIM LEADING", StringFunction,"TRIM( LEADING [trim_charater] FROM ?),"),
    TRIM_TRAILING("TRIM TRAILING", StringFunction,"TRIM( TRAILING [trim_charater] FROM ?),"),
    TRIM_BOTH("TRIM BOTH", StringFunction,"TRIM( BOTH [trim_charater] FROM ?),"),
    TRIM_CHARACTER("TRIM CHARACTER", StringFunction,"TRIM( CHARACTER FROM ?),"),

    UCASE("ucase", StringFunction, "UCASE(?)"),
    UNICODE("UNICODE",StringFunction,"UNICODE(?)"),
    UNBASE64("unbase64", StringFunction,"UNBASE64(?)"),
    UPPER("upper", StringFunction, "UPPER(?)"),

    WEIGHT_STRING("weight_string",StringFunction,"WEIGHT_STRING(? [AS {CHAR|BINARY}(N)] [flags])"),

    //endregion
    //region 连接运算符
    Concat("||", ConcatenationOperator,"? || ?"),
    //endregion
    //region 数学函数
    ABS("abs", MathematicalFunction, "ABS(?)"),
    ACOS("acos", MathematicalFunction, "ABS(?)"),
    ASIN("ASIN", MathematicalFunction,"ASIN(?)"),
    ATAN("atan", MathematicalFunction,"ASIN(?)"),
    ATAN2("ATAN2",MathematicalFunction,"ATAN2(?, ?)"),
    ABSVAL("ABSVAL",MathematicalFunction,"ABSVAL(?)"),
    BROUND("bround",MathematicalFunction,"BROUND(?)"),
    CEIL("ceil",MathematicalFunction,"CEIL(?)"),
    CEILING("ceiling",MathematicalFunction,"CEILING(?)"),
    CONV("conv",MathematicalFunction,"CONV(?,from_base,to_base)"),
    COS("cos",MathematicalFunction,"COS(?)"),
    COSH("COSH",MathematicalFunction,"COSH(?)"),
    COT("COT",MathematicalFunction,"COT(?)"),
    CRC32("crc32",MathematicalFunction,"CRC32(?)"),
    CBRT("cbrt",MathematicalFunction,"CBRT(?)"),
    DEGREES("degrees",MathematicalFunction,"DEGREES(?)"),
    DIGITS("DIGITS",MathematicalFunction,"DIGITS(?)"),
    EVEN("EVEN",MathematicalFunction,"EVEN(?)"),
    EXP("exp",MathematicalFunction,"EXP(?)"),
    E("e",MathematicalFunction,"E(?)"),
    FLOOR("floor",MathematicalFunction,"FLOOR(?)"),
    FORMAT("FORMAT",MathematicalFunction,"FORMAT( ?, D)"),
    FACTORIAL("factorial",MathematicalFunction,"FACTORIAL(?)"),
    GAMMA("GAMMA",MathematicalFunction,"GAMMA(?)"),
    LN("ln",MathematicalFunction,"LN(?)"),
    LOG10("log10",MathematicalFunction,"LOG10(?)"),
    LOG2("log2",MathematicalFunction,"LOG2(?)"),
    LOG("log",MathematicalFunction,"LOG(?)"),
    LGAMMA("LGAMMA",MathematicalFunction,"LGAMMA(?)"),
    MOD_M("MOD", MathematicalFunction, "MOD(N,M)"),
    MULTIPLY_ALT("MULTIPLY_ALT",MathematicalFunction,"MULTIPLY_ALT(?,?)"),
    NANVL("NANVL",MathematicalFunction,"NANVL(?,n)"),
    NEXTAFTER("NEXTAFTER",MathematicalFunction,"NEXTAFTER(?, y)"),
    NEGATIVE("negative",MathematicalFunction,"NEGATIVE(?)"),
    PI("pi",MathematicalFunction,"PI()"),
    POW("pow",MathematicalFunction,"POW(?,n)"),
    POWER("POWER",MathematicalFunction,"POWER(?,n)"),
    PMOD("pmod",MathematicalFunction,"PMOD(a,b)"),
    POSITIVE("positive",MathematicalFunction,"POSITIVE(?)"),
    RADIANS("RADIANS",MathematicalFunction,"RADIANS(?)"),
    RANDOM("RANDOM",MathematicalFunction,"RANDOM(seed)"),
    RAND("rand",MathematicalFunction,"RAND(seed)"),
    REMAINDER("REMAINDER",MathematicalFunction,"REMAINDER(?,n)"),
    ROUND("round",MathematicalFunction,"ROUND(?)"),
    SIGN("sign",MathematicalFunction,"SIGN(?)"),
    SIN("sin",MathematicalFunction,"SIN(?)"),
    SQRT("sqrt",MathematicalFunction,"SQRT(?)"),
    SINH("SINH",MathematicalFunction,"SINH(?)"),
    SQUARE("SQUARE",MathematicalFunction,"SQUARE(?)"),
    SHIFTLEFT("shiftleft",MathematicalFunction,"SHIFTLEFT(?,n)"),
    SHIFTRIGHT("shiftright",MathematicalFunction,"SHIFTRIGHT(?,n)"),
    SHIFTRIGHTUNSIGNED("shiftrightunsigned",MathematicalFunction,"SHIFTRIGHTUNSIGNED(?,n)"),
    SETSEED("SETSEED",MathematicalFunction,"SETSEED(?)"),
    TAN("tan",MathematicalFunction,"TAN(?)"),
    TANH("tanh(?)",MathematicalFunction,"TANH(?)"),
    TRUNC_NUMBER("TRUNC",MathematicalFunction,"TRUNC(?, n)"),
    TRUNCATE("trunc",MathematicalFunction,"TRUNCATE(?,D)"),
    WIDTH_BUCKET("width_bucket",MathematicalFunction,"WIDTH_BUCKET(?,min_value,max_value,num_buckets)"),
    //endregion
    //region 日期函数
    ADDDATE("ADDDATE",DatetimeFunctions,"ADDDATE(?,days)"),
    ADDTIME("ADDTIME",DatetimeFunctions,"ADDTIME(expr1,expr2)"),
    ADD_MONTHS("add_months",DatetimeFunctions,"ADD_MONTHS(month,days)"),
    ADD_MONTHS_HIVE("add_months",DatetimeFunctions,"add_months(?, num_months, output_date_format)",DbType.of(DbType.hive)),

    CONVERT_TZ("convert_tz",DatetimeFunctions,"CONVERT_TZ(?,from_tz,to_tz)"),
    CURDATE("curdate",DatetimeFunctions,"CURDATE()"),
    CURRENT_DATE("current_date",DatetimeFunctions,"CURRENT_DATE()"),
    CURRENT_TIME("current_time",DatetimeFunctions,"CURRENT_TIME()"),
    CURRENT_TIMESTAMP("current_timestamp",DatetimeFunctions,"CURRENT_TIMESTAMP()"),
    CURRENT_TIMESTAMP1("current_timestamp",DatetimeFunctions,"CURRENT_TIMESTAMP(precision)", DbType.of(DbType.oracle)),
    CURTIME("curtime",DatetimeFunctions,"CURTIME()"),

    DATE("date", DatetimeFunctions,"DATE(?)"),
    DATEDIFF("datediff",DatetimeFunctions,"DATEDIFF(expr1,expr2)"),
    DATE_ADD("date_add",DatetimeFunctions,"DATE_ADD(date,INTERVAL expr unit)"),
    DATE_ADD_HIVE("date_add",DatetimeFunctions,"DATE_ADD(startdate, days)",DbType.of(DbType.hive)),
    DATE_FORMAT("date_format",DatetimeFunctions,"DATE_FORMAT(?,format)"),
    DATE_SUB("date_sub",DatetimeFunctions,"DATE_SUB(date,INTERVAL expr unit)"),
    DATE_SUB_HIVE("date_sub",DatetimeFunctions,"DATE_SUB(startdate, days)",DbType.of(DbType.hive)),
    DAY("day",DatetimeFunctions,"DAY(?)"),
    DAYNAME("DAYNAME",DatetimeFunctions,"DAYNAME(?)"),
    DATENAME("DATENAME",DatetimeFunctions,"DATENAME(year, ?)"),
    DAYOFMONTH("dayofmonth",DatetimeFunctions,"DAYOFMONTH(?)"),
    DAYOFWEEK("DAYOFWEEK", DatetimeFunctions,"DAYOFWEEK(?)"),
    DAYOFYEAR("dayofyear",DatetimeFunctions,"DAYOFYEAR(?)"),
    DATEPART("",DatetimeFunctions,"DATEPART(yyyy,?)"),
    DATEFROMPARTS("DATEFROMPARTS",DatetimeFunctions,"DATEFROMPARTS( year ,  month ,  day )"),
    DAYS("DAYS",DatetimeFunctions,"DAYS(?)"),
    DATEADD("DATEADD",DatetimeFunctions,"DATEADD(day,2,?)"),
    DBTIMEZONE("DBTIMEZONE",DatetimeFunctions,"DBTIMEZONE()"),

    EXTRACT("extract",DatetimeFunctions,"EXTRACT(unit FROM ?)"),

    FLOOR_YEAR("floor_year", DatetimeFunctions,"floor_year(?)"),
    FLOOR_MONTH("floor_monthDatetimeFunctions", DatetimeFunctions,"floor_month(?)"),
    FLOOR_HOUR("floor_hour", DatetimeFunctions,"floor_hour(?)"),
    FLOOR_WEEK("floor_week", DatetimeFunctions, "floor_week(?)"),
    FLOOR_DAY("floor_day", DatetimeFunctions,"floor_day(?)"),
    FLOOR_QUARTER("floor_quarter", DatetimeFunctions,"floor_quarter(?)"),
    FLOOR_MINUTE("floor_minute", DatetimeFunctions,"floor_minute(?)"),
    FLOOR_SECOND("floor_second", DatetimeFunctions,"floor_second(?)"),
    FROM_DAYS("from_days", DatetimeFunctions,"from_days(N)"),
    FROM_TZ("FROM_TZ", DatetimeFunctions,"FROM_TZ(timestamp_value, time_zone_value)"),
    FROM_UNIXTIME("from_unixtime", DatetimeFunctions,"FROM_UNIXTIME(?[,format])"),
    FROM_UTC_TIMESTAMP("from_utc_timestamp", DatetimeFunctions,"FROM_UTC_TIMESTAMP(?, timeZone)"),
    GET_FORMAT("get_format", DatetimeFunctions,"GET_FORMAT(?, {'EUR'|'USA'|'JIS'|'ISO'|'INTERNAL'})"),
    GETDATE("GETDATE", DatetimeFunctions,"GETDATE()"),
    GETUTCDATE("GETUTCDATE", DatetimeFunctions,"GETUTCDATE()"),

    HOUR("hour", DatetimeFunctions,"HOUR(?)"),

    INTERNAL_INTERVAL("internal_interval", DatetimeFunctions,"internal_interval(intervalType,intervalArg)"),

    JULIAN_DAY("JULIAN_DAY", DatetimeFunctions,"JULIAN_DAY(?)"),

    LAST_DAY("last_day", DatetimeFunctions, "LAST_DAY(?)"),
    LOCALTIME("LOCALTIME", DatetimeFunctions, "LOCALTIME()"),
    LOCALTIMESTAMP("LOCALTIMESTAMP", DatetimeFunctions, "LOCALTIMESTAMP()"),
    LOCALTIMESTAMP1("LOCALTIMESTAMP", DatetimeFunctions, "LOCALTIMESTAMP(timestamp_precision)", DbType.of(DbType.oracle)),

    MAKEDATE("MAKEDATE", DatetimeFunctions,"MAKEDATE(year, day-of-year)"),
    MAKETIME("MAKETIME",DatetimeFunctions,"MAKETIME(hour, minute, second)"),
    MICROSECOND("MICROSECOND",DatetimeFunctions,"MICROSECOND(?)"),
    MINUTE("minute",DatetimeFunctions,"MINUTE(?)"),
    MONTH("month",DatetimeFunctions,"MONTH(?)"),
    MONTHNAME("MONTHNAME",DatetimeFunctions,"MONTHNAME(?)"),
    MONTHS_BETWEEN("months_between", DatetimeFunctions,"MONTHS_BETWEEN(DATE1,DATE2)"),
    MIDNIGHT_SECONDS("MIDNIGHT_SECONDS",DatetimeFunctions,"MIDNIGHT_SECONDS(?)"),

    NOW("now",DatetimeFunctions,"NOW()"),
    NEW_TIME("NEW_TIME", DatetimeFunctions,"NEW_TIME(?, timezone1, timezone2)"),
    NEXT_DAY("next_day", DatetimeFunctions,"NEXT_DAY(?, day_of_week)"),
    NUMTODSINTERVAL("NUMTODSINTERVAL", DatetimeFunctions,"NUMTODSINTERVAL(?, interval_unit)"),
    NUMTOYMINTERVAL("NUMTOYMINTERVAL", DatetimeFunctions,"NUMTOYMINTERVAL(?, interval_unit)"),
    ORA_DST_AFFECTED("ORA_DST_AFFECTED", DatetimeFunctions,"ORA_DST_AFFECTED(?)"),
    ORA_DST_CONVERT("ORA_DST_CONVERT", DatetimeFunctions,"ORA_DST_CONVERT(?[, integer[,integer]])"),
    ORA_DST_ERROR("ORA_DST_ERROR", DatetimeFunctions,"ORA_DST_ERROR(?)"),

    PERIOD_ADD("PERIOD_ADD",DatetimeFunctions,"PERIOD_ADD(period, number)"),
    PERIOD_DIFF("PERIOD_DIFF",DatetimeFunctions,"PERIOD_DIFF(period, number)"),

    QUARTER("quarter", DatetimeFunctions, "QUARTER(?)"),

    ROUND_DT("ROUND", DatetimeFunctions, "ROUND(?[,fmt])"),

    SECOND("second",DatetimeFunctions,"SECOND(?)"),
    SEC_TO_TIME("SEC_TO_TIME",DatetimeFunctions,"SEC_TO_TIME(?)"),
    SESSIONTIMEZONE("SESSIONTIMEZONE",DatetimeFunctions,"SESSIONTIMEZONE"),
    STR_TO_DATE("STR_TO_DATE",DatetimeFunctions,"STR_TO_DATE(?,format)"),

    SUBDATE("SUBDATE",DatetimeFunctions,"SUBDATE(?,n)"),
    SUBTIME("SUBTIME",DatetimeFunctions,"SUBTIME(?,n)"),
    SUBTIME_MYSQL("SUBTIME",DatetimeFunctions,"SUBTIME(expr1,expr2)", DbType.of(DbType.mysql)),
    SYSDATE("SYSDATE",DatetimeFunctions,"SYSDATE()"),
    SYSDATE1("SYSDATE",DatetimeFunctions,"SYSDATE", DbType.of(DbType.oracle)),
    SYSDATETIME("SYSDATETIME",DatetimeFunctions,"SYSDATETIME()"),
    SYS_EXTRACT_UTC("SYS_EXTRACT_UTC",DatetimeFunctions,"SYS_EXTRACT_UTC(datetime_with_time_zone)"),

    SYSTIMESTAMP("SYSTIMESTAMP",DatetimeFunctions,"SYSTIMESTAMP()"),
    SYSTIMESTAMP1("SYSTIMESTAMP",DatetimeFunctions,"SYSTIMESTAMP", DbType.of(DbType.oracle)),

    TIME("TIME",DatetimeFunctions,"TIME(?)"),
    TIMEDIFF("TIMEDIFF",DatetimeFunctions,"TIMEDIFF(?,?)"),
    TIMESTAMP("TIMESTAMP",DatetimeFunctions,"TIMESTAMP(?)"),
    TIMESTAMP_ISO("TIMESTAMP_ISO",DatetimeFunctions,"TIMESTAMP_ISO(?)"),
    TIMESTAMPADD("timestampadd",DatetimeFunctions,"TIMESTAMPADD(unit,interval,?)"),
    TIMESTAMPDIFF("TIMESTAMPDIFF",DatetimeFunctions,"TIMESTAMPDIFF(?,?)"),
    TIMESTAMPDIFF_MYSQL("TIMESTAMPDIFF",DatetimeFunctions,"TIMESTAMPDIFF(unit,?,?)", DbType.of(DbType.mysql)),
    TIMESTAMP_FORMAT("TIMESTAMP_FORMAT",DatetimeFunctions,"TIMESTAMP_FORMAT(?,'yyyy-mm-dd')"),
    TIME_TO_SEC("TIME_TO_SEC",DatetimeFunctions,"TIME_TO_SEC(?)"),
    TO_DAYS("TO_DAYS",DatetimeFunctions,"TO_DAYS(?)"),
    TO_DSINTERVAL("TO_DSINTERVAL",DatetimeFunctions,"TO_DSINTERVAL('sql_format'|'ds_iso_format')"),
    TO_SECONDS("to_seconds",DatetimeFunctions,"to_seconds(?)"),
    TO_TIMESTAMP("TO_TIMESTAMP", DatetimeFunctions,"TO_TIMESTAMP(?)"),
    TO_TIMESTAMP1("TO_TIMESTAMP", DatetimeFunctions,"TO_TIMESTAMP(?[,fmt[, 'nlsparam']])", DbType.of(DbType.oracle)),
    TO_TIMESTAMP_TZ("TO_TIMESTAMP_TZ", DatetimeFunctions,"TO_TIMESTAMP_TZ(?[,fmt[, 'nlsparam']])", DbType.of(DbType.oracle)),
    TO_YMINTERVAL("TO_YMINTERVAL", DatetimeFunctions,"TO_YMINTERVAL(?)"), // 简化语法
    TRUNC("TRUNC",DatetimeFunctions,"TRUNC(?, unit)"),
    TRUNC1("TRUNC",DatetimeFunctions,"TRUNC(?[, fmt])", DbType.of(DbType.oracle)),
    TIME_FORMAT("TIME_FORMAT",DatetimeFunctions,"TIME_FORMAT(?,format)"),
    TZ_OFFSET("TZ_OFFSET",DatetimeFunctions,"TZ_OFFSET(SESSIONTIMEZONE|DBTIMEZONE|time_zone_name)"), //简化：'+|- hh:mi'
    TO_UNIX_TIMESTAMP("to_unix_timestamp",DatetimeFunctions,"to_unix_timestamp(?[, pattern])"),
    TO_DATE("to_date",DatetimeFunctions,"TO_DATE(?)"),
    TO_UTC_TIMESTAMP("to_utc_timestamp",DatetimeFunctions,"TO_UTC_TIMESTAMP(?)"),
    TO_UTC_TIMESTAMP_HIVE("to_utc_timestamp",DatetimeFunctions,"TO_UTC_TIMESTAMP(?,timezone)",DbType.of(DbType.hive)),

    UNIX_TIMESTAMP("unix_timestamp",DatetimeFunctions,"UNIX_TIMESTAMP([date])"),
    UNIX_TIMESTAMP_HIVE("unix_timestamp",DatetimeFunctions,"UNIX_TIMESTAMP([date][,pattern])",DbType.of(DbType.hive)),
    UTC_DATE("utc_date", DatetimeFunctions,"UTC_DATE()"),
    UTC_TIME("utc_time",DatetimeFunctions,"utc_time()"),
    UTC_TIMESTAMP("utc_timestamp",DatetimeFunctions,"UTC_TIMESTAMP()"),

    WEEK("WEEK",DatetimeFunctions,"WEEK(?)"),
    WEEKDAY("WEEKDAY",DatetimeFunctions,"WEEKDAY(?)"),
    WEEKOFYEAR("weekofyear", DatetimeFunctions,"WEEKOFYEAR(?)"),
    WEEK_ISO("WEEK_ISO",DatetimeFunctions,"WEEK_ISO(?)"),

    YEAR("year",DatetimeFunctions,"YEAR(?)"),
    YEARWEEK("YEARWEEK",DatetimeFunctions,"YEARWEEK(?)"),
    //endregion
    //region 比较运算符/函数
    ANY("ANY",ComparisonOperators,"ANY"),
    ALL("ALL",ComparisonOperators,"ALL"),
    SOME("SOME",ComparisonOperators,"SOME"),
    Equality("=", ComparisonOperators,"? = expr"),
    GreaterThan(">", ComparisonOperators,"? > expr"),
    GreaterThanOrEqual(">=", ComparisonOperators,"? >= expr"),
    LessThan("<", ComparisonOperators,"? < expr"),
    LessThanOrEqual("<=", ComparisonOperators,"? <= expr"),
    LessThanOrEqualOrGreaterThan("<=>", ComparisonOperators),
    LessThanOrGreater("<>", ComparisonOperators,"? <> expr"),
    NotEqual("!=", ComparisonOperators,"? != expr"),
    NotEqual1("<>", ComparisonOperators,"? <> expr", DbType.of(DbType.oracle)),
    NotEqual2("^=", ComparisonOperators, "? ^= expr", DbType.of(DbType.oracle)),
    NotLessThan("!<", ComparisonOperators),
    NotGreaterThan("!>", ComparisonOperators),
    SoundsLike("SOUNDS LIKE",ComparisonOperators,"expr1 SOUNDS LIKE expr2"),

    interval("interval",ComparisonOperators, "interval()"),

    GREATEST("greatest", ComparisonOperators,"GREATEST(x1, x2, ...)"),
    LEAST("least", ComparisonOperators,"least(x1, x2, ...)"),
    STRCMP("strcmp",ComparisonOperators,"STRCMP(expr1,expr2)"),
    IsDistinctFrom("IS DISTINCT FROM",ComparisonOperators),
    IsNotDistinctFrom("IS NOT DISTINCT FROM", ComparisonOperators),
    //endregion
    //region 复杂类型构造
    MAP("map", ComplexTypeConstructor,"MAP(key1, value1, key2, value2, ...)"),
    STRUCT("struct",ComplexTypeConstructor,"(val1, val2, val3, ...)"),
    NAMED_STRUCT("named_struct",ComplexTypeConstructor,"NAMED_STRUCT(name1, val1, name2, val2, ...)"),
    ARRAY("array",ComplexTypeConstructor,"ARRAY(val1, val2, ...)"),
    CREATE_UNION("create_union",ComplexTypeConstructor,"CREATE_UNION(tag, val1, val2, ...)"),
    //endregion
    //region HierarchicalQueryOperators
    Prior("PRIOR", HierarchicalQuery,"PRIOR ?"),
    ConnectByRoot("CONNECT_BY_ROOT", HierarchicalQuery,"CONNECT_BY_ROOT ?"),
    SYS_CONNECT_BY_PATH("SYS_CONNECT_BY_PATH",CollectionFunction,"SYS_CONNECT_BY_PATH(?, char)"),

    //endregion
    //region 集合操作符
    INTERSECT("INTERSECT", CollectionOperators, "statement1 INTERSECT statement2"),
    MINUS("MINUS", CollectionOperators, "statement1 MINUS statement2"),
    MULTISET_EXCEPT("MULTISET EXCEPT", CollectionOperators, "MULTISET EXCEPT [ALL|DISTINCT]"),
    MULTISET_INTERSECT("MULTISET INTERSECT", CollectionOperators, "MULTISET INTERSECT [ALL|DISTINCT]"),
    MULTISET_UNION("MULTISET UNION", CollectionOperators, "MULTISET UNION [ALL|DISTINCT]"),

    UNION("UNION", CollectionOperators, "statement1 UNION statement2"),
    UNION_ALL("UNION ALL", CollectionOperators, "statement1 UNION ALL statement2"),
    Array_Contains("@>", CollectionOperators),
    Array_ContainedBy("<@", CollectionOperators),
    //endregion
    //region 集合函数
    CARDINALITY("CARDINALITY",CollectionFunction, "CARDINALITY(nested_table)"),
    COLLECT("COLLECT",CollectionFunction, "COLLECT([DISTINCT|UNIQUE]? ORDER BY expr)"),
    NLS_CHARSET_DECL_LEN("NLS_CHARSET_DECL_LEN",CollectionFunction, "NLS_CHARSET_DECL_LEN(byte_count, char_set_id)"),
    NLS_CHARSET_ID("NLS_CHARSET_ID",CollectionFunction, "NLS_CHARSET_ID(haracter_set_name)"),
    NLS_CHARSET_NAME("NLS_CHARSET_NAME",CollectionFunction, "NLS_CHARSET_NAME(number)"),
    POWERMULTISET("POWERMULTISET",CollectionFunction, "POWERMULTISET(expr)"),
    POWERMULTISET_BY_CARDINALITY("POWERMULTISET_BY_CARDINALITY",CollectionFunction, "POWERMULTISET_BY_CARDINALITY(expr,cardinality)"),

    SET("SET",CollectionFunction, "SET(nested_table)"),
    st_intersects("st_intersects", CollectionFunction,"st_intersects(A, B)"),
    SIZE("size",CollectionFunction,"SIZE(?)"),
    MAP_KEYS("map_keys",CollectionFunction,"MAP_KEYS(?)"),
    MAP_VALUES("map_values",CollectionFunction,"MAP_VALUES(?)"),
    ARRAY_CONTAINS("array_contains",CollectionFunction,"ARRAY_CONTAINS(?)"),
    SORT_ARRAY("sort_array",CollectionFunction,"SORT_ARRAY(?)"),
    INDEX("index",CollectionFunction,"INDEX(a, n)"),
    GET_SPLITS("get_splits",CollectionFunction,"get_splits(string,int)"),
    //endregion
    //region XML Functions
    EQUALS_PATH("EQUALS_PATH", XMLFunctions,"EQUALS_PATH(?, path_string[,correlation_integer])"),
    UNDER_PATH("UNDER_PATH", XMLFunctions,"UNDER_PATH(?, path_string[,levels],path_string[,correlation_integer])"),

    APPENDCHILDXML       ("APPENDCHILDXML",XMLFunctions,"APPENDCHILDXML(?, XPath_string,value_expr[, namespace_string])"),
    DELETEXML            ("DELETEXML",XMLFunctions,"DELETEXML(?, XPath_string[, namespace_string]))"),
    DEPTH                ("DEPTH",XMLFunctions,"DEPTH(correlation_integer)"),
    EXISTSNODE           ("EXISTSNODE",XMLFunctions,"EXISTSNODE(?, XPath_string[, namespace_string]))"),
    EXTRACT_XML        ("EXTRACT_XML",XMLFunctions,"EXTRACT_XML(?, XPath_string[, namespace_string]))"),
    EXTRACTVALUE("extractvalue", XMLFunctions,"ExtractValue(xml_frag, xpath_expr)"),
    EXTRACTVALUE1         ("EXTRACTVALUE",XMLFunctions,"EXTRACTVALUE(?, XPath_string[, namespace_string]))", DbType.of(DbType.oracle)),
    INSERTCHILDXML       ("INSERTCHILDXML",XMLFunctions,"INSERTCHILDXML(?, XPath_string,child_expr,value_expr[, namespace_string])"),
    INSERTCHILDXMLAFTER  ("INSERTCHILDXMLAFTER",XMLFunctions,"INSERTCHILDXMLAFTER(?, XPath_string,child_expr,value_expr[, namespace_string])"),
    INSERTCHILDXMLBEFORE ("INSERTCHILDXMLBEFORE",XMLFunctions,"INSERTCHILDXMLBEFORE(?, XPath_string,child_expr,value_expr[, namespace_string])"),
    INSERTXMLAFTER       ("INSERTXMLAFTER",XMLFunctions,"INSERTXMLAFTER(?, XPath_string,value_expr[, namespace_string])"),
    INSERTXMLBEFORE      ("INSERTXMLBEFORE",XMLFunctions,"INSERTXMLBEFORE(?, XPath_string,value_expr[, namespace_string])"),
    PATH                 ("PATH",XMLFunctions,"PATH(correlation_integer)"),
    SYS_DBURIGEN         ("SYS_DBURIGEN",XMLFunctions,"SYS_DBURIGEN(? [rowid],...)"),
    SYS_XMLAGG           ("SYS_XMLAGG",XMLFunctions,"SYS_XMLAGG(expr[,fmt])"),
    SYS_XMLGEN           ("SYS_XMLGEN",XMLFunctions,"SYS_XMLGEN(expr[,fmt])"),
    UPDATEXML("updatexml", XMLFunctions,"UpdateXML(xml_target, xpath_expr, new_xml)"),
    UPDATEXML1            ("UPDATEXML",XMLFunctions,"UPDATEXML(?, XPath_string,value_expr[,XPath_string2,value_expr2,...][, namespace_string])", DbType.of(DbType.oracle)),
    XMLAGG               ("XMLAGG",XMLFunctions,"XMLAGG(?[, ORDER BY ?])"),
    XMLCAST              ("XMLCAST",XMLFunctions,"XMLCAST(? AS datatype)"),
    XMLCDATA             ("XMLCDATA",XMLFunctions,"XMLCDATA(?)"),
    XMLCOLATTVAL         ("XMLCOLATTVAL",XMLFunctions,"XMLCOLATTVAL(value_expr [AS [c_alias|EVALNAME value_expr]],...)"),
    XMLCOMMENT           ("XMLCOMMENT",XMLFunctions,"XMLCOMMENT(value_expr)"),
    XMLCONCAT            ("XMLCONCAT",XMLFunctions,"XMLCONCAT(XMLType_instance,...)"),
    XMLDIFF              ("XMLDIFF",XMLFunctions,"XMLDIFF(XMLType_document,XMLType_document[,integer,string])"),
    XMLELEMENT           ("XMLELEMENT",XMLFunctions,"XMLELEMENT(...)"), //太复杂，简化
    XMLEXISTS            ("XMLEXISTS",XMLFunctions,"XMLEXISTS(XQuery_string[,XML_passing_clause])"),
    XMLFOREST            ("XMLFOREST",XMLFunctions,"XMLFOREST(? [AS [c_alias|EVALNAME value_expr]],...)"),
    XMLISVALID           ("XMLISVALID",XMLFunctions,"XMLISVALID(?[,xmlSchema_URL[,element]])"),
    XMLPARSE             ("XMLPARSE",XMLFunctions,"XMLPARSE([DOCUMENT|CONTENT] ? [WELLFORMED])"),
    XMLPATCH             ("XMLPATCH",XMLFunctions,"XMLPATCH(?,XMLType_document)"),
    XMLPI                ("XMLPI",XMLFunctions,"XMLPI(([NAME] identifier)|(EVALNAME value_expr) [,value_expr])"),
    XMLQUERY             ("XMLQUERY",XMLFunctions,"XMLQUERY(XQuery_string[,XML_passing_clause] RETURNING CONTENT [NULL ON EMPTY])"),
    XMLROOT              ("XMLROOT",XMLFunctions,"XMLROOT(value_expr,VERSION value_expr|NO VALUE [,STANDALONE YES|NO|(NO VALUE)])"),
    XMLSEQUENCE          ("XMLSEQUENCE",XMLFunctions,"XMLSEQUENCE(XMLType_instance|(sys_refcursor_instance[,fmt]))"),
    XMLSERIALIZE         ("XMLSERIALIZE",XMLFunctions,"XMLSERIALIZE(?)"), //简化
    XMLTABLE             ("XMLTABLE",XMLFunctions,"XMLTABLE([XMLNamespace_clause,]XQuery_string XMLTABLE_options)"),
    XMLTRANSFORM         ("XMLTRANSFORM",XMLFunctions,"XMLTRANSFORM(?,XMLType_instance|string)"),
    //endregion
    //region XPATH
    XPATH("xpath", XPathFunctions,"xpath(xml, xpath)"),
    XPATH_STRING("xpath_string", XPathFunctions,"xpath_string(xml, xpath)"),
    XPATH_FLOAT("xpath_float", XPathFunctions,"xpath_float(xml, xpath)"),
    XPATH_NUMBER("xpath_number", XPathFunctions,"xpath_number(xml, xpath)"),
    XPATH_INT("xpath_int", XPathFunctions,"xpath_int(xml, xpath)"),
    XPATH_BOOLEAN("xpath_boolean", XPathFunctions,"xpath_boolean(xml, xpath)"),
    XPATH_DOUBLE("xpath_double", XPathFunctions,"xpath_double(xml, xpath)"),
    XPATH_LONG("xpath_long", XPathFunctions,"xpath_long(xml, xpath)"),
    XPATH_SHORT("xpath_short", XPathFunctions,"xpath_short(xml, xpath)"),
    //endregion
    //region JSON
    GET_JSON_OBJECT("get_json_object", JSONFunctions,"get_json_object(json_string, path)"),
    JSON_EXTRACT_ALIAS("->", JSONFunctions,"column->path"),
    JSON_EXTRACT_ALIAS_1("->>", JSONFunctions,"column->>path"),
    JSON_ARRAY("json_array",JSONFunctions,"JSON_ARRAY([val, ...])"),
    JSON_ARRAY_APPEND("json_array_append",JSONFunctions,"JSON_ARRAY_APPEND(?, path, val[, path, val] ...)"),
    JSON_ARRAY_INSERT("json_array_insert",JSONFunctions,"JSON_ARRAY_INSERT(?, path, val[, path, val] ...)"),
    JSON_CONTAINS("json_contains",JSONFunctions,"JSON_CONTAINS(?, candidate[, path])"),
    JSONContains("?", JSONFunctions),
    JSON_CONTAINS_PATH("json_contains_path",JSONFunctions,"JSON_CONTAINS_PATH(?, one_or_all, path[, path] ...)"),
    JSON_DEPTH("json_depth",JSONFunctions,"json_depth(?)"),
    JSON_EXTRACT("json_extract",JSONFunctions,"JSON_EXTRACT(?, path[, path] ...)"),
    JSON_INSERT("json_insert",JSONFunctions,"JSON_INSERT(?, path, val[, path, val] ...)"),
    JSON_KEYS("json_keys",JSONFunctions,"JSON_KEYS(?[, path])"),
    JSON_LENGTH("json_length",JSONFunctions,"JSON_LENGTH(?[, path])"),
    JSON_MERGE("json_merge",JSONFunctions,"JSON_MERGE(?, ...)"),
    JSON_MERGE_PATCH("json_merge_patch",JSONFunctions,"JSON_MERGE_PATCH(?, ...)"),
    JSON_MERGE_PRESERVE("json_merge_preserve",JSONFunctions,"JSON_MERGE_PRESERVE(json_doc, ...)"),
    JSON_OBJECT("json_object",JSONFunctions,"JSON_OBJECT([key, val[, key, val] ...])"),
    JSON_OVERLAPS("json_objectagg",JSONFunctions,"JSON_OVERLAPS(json_doc1, json_doc2)"),
    JSON_PRETTY("json_pretty",JSONFunctions,"json_pretty(?)"),
    JSON_QUOTE("json_quote",JSONFunctions,"json_quote(?)"),
    JSON_QUERY("JSON_QUERY",JSONFunctions,"JSON_QUERY(? [FORMAT JSON],JSON_path_expression [JSON_query_returning_clause][JSON_query_wrapper_clause][JSON_query_on_error_clause])"),
    JSON_REMOVE("json_remove",JSONFunctions,"JSON_REMOVE(?, path ...)"),
    JSON_REPLACE("json_replace",JSONFunctions,"JSON_REPLACE(?, path, val[, path, val] ...)"),
    JSON_SCHEMA_VALID("json_schema_valid",JSONFunctions,"JSON_SCHEMA_VALID(schema,document)"),
    JSON_SCHEMA_VALIDATION_REPORT("json_schema_validation_report",JSONFunctions,"JSON_SCHEMA_VALIDATION_REPORT(schema,document)"),
    JSON_SEARCH("json_search",JSONFunctions,"JSON_SEARCH(?, one_or_all, search_str[, escape_char[, path] ...])"),
    JSON_SET("json_set",JSONFunctions,"JSON_SET(?, path, val[, path, val] ...)"),

    JSON_STORAGE_FREE("json_storage_free",JSONFunctions,"json_storage_free(?)"),
    JSON_STORAGE_SIZE("json_storage_size",JSONFunctions,"json_storage_size(?)"),
    JSON_TABLE("json_table",JSONFunctions,"JSON_TABLE(?, path COLUMNS (column_list) [AS] alias)"),
    JSON_TABLE1("JSON_TABLE",JSONFunctions,"JSON_TABLE(? [FORMAT JSON],JSON_path_expression [JSON_query_on_error_clause] JSON_columns_clause)", DbType.of(DbType.oracle)),
    JSON_TYPE("json_type",JSONFunctions,"JSON_TYPE(?)"),
    JSON_UNQUOTE("json_unquote",JSONFunctions,"JSON_UNQUOTE(?)"),
    JSON_VALID("json_valid",JSONFunctions,"JSON_VALID(?)"),
    JSON_VALUE("json_value",JSONFunctions,"JSON_VALUE(json_doc, path)"),
    JSON_VALUE1("json_value",JSONFunctions,"JSON_VALUE(? [FORMAT JSON],JSON_path_expression [JSON_query_returning_clause][JSON_query_on_error_clause])", DbType.of(DbType.oracle)),
    //endregion
    //region Bit Functions and Operators
    BitwiseAnd("&", BitOperators, "? & expr"),
    BITWISE_XOR("^", BitOperators,"? ^ expr"),
    BitwiseOr("|", BitOperators, "? | expr"),
    Bitwise_INVERSION("~", BitOperators, "? ~ expr"),
    BITWISE_XOR_EQ("^=", BitOperators, "? ^= expr"),
    BitwiseNot("!", BitOperators, "!?"),
    BIT_COUNT("BIT_COUNT",BitFunctions,"BIT_COUNT(?)"),
    BITAND("BITAND",BitFunctions,"BITAND(?,?)", DbType.of(DbType.oracle)),
    EWAH_BITMAP("ewah_bitmap",BitFunctions,"ewah_bitmap(expr)"),
    EWAH_BITMAP_EMPTY("ewah_bitmap_empty",BitFunctions,"ewah_bitmap_empty(expr)"),
    LEFT_SHIFT("<<", BitOperators,"? << N"),
    RIGHT_SHIFT(">>", BitOperators, "? >> N"),
    //    BIT_AND("BIT_AND",BitFunctions,"BIT_AND(?)"),
//    BIT_OR("BIT_OR",BitFunctions,"BIT_OR(?)"),
//    BIT_XOR("BIT_XOR",BitFunctions,"BIT_XOR(?)"),
    //endregion
    //region 大对象
    BFILENAME("BFILENAME", LargeObjectFunctions,"BFILENAME('directory','filename')"),
    EMPTY_BLOB("EMPTY_BLOB", LargeObjectFunctions,"EMPTY_BLOB()"),
    EMPTY_CLOB("EMPTY_CLOB", LargeObjectFunctions,"EMPTY_CLOB()"),

    //endregion
    //region 对象引用函数
    DEREF("DEREF",ObjectReferenceFunctions,"DEREF(?)"),
    MAKE_REF("MAKE_REF",ObjectReferenceFunctions,"MAKE_REF(?[,key...])"),
    REF("REF",ObjectReferenceFunctions,"REF(?)"),
    REFTOHEX("REFTOHEX",ObjectReferenceFunctions,"REFTOHEX(?)"),
    VALUE("VALUE",ObjectReferenceFunctions,"VALUE(?)"),
    //endregion
    //region 赋值操作
    Assignment(":=", AssignmentOperators),
    //endregion
    //region 条件操作
    IS_NAN("IS NAN",ConditionalOperators,"? IS [NOT] NAN"),
    IS_INFINITE("IS [NOT] INFINITE",ConditionalOperators,"? IS [NOT] INFINITE"),
    IS_PRESENT("IS PRESENT",ConditionalOperators,"? IS PRESENT"),
    IS_A_SET("IS [NOT] A SET",ConditionalOperators,"? IS [NOT] A SET"),
    IS_EMPTY("IS [NOT] EMPTY",ConditionalOperators,"? IS [NOT] EMPTY"),
    IS_JSON("IS [NOT] JSON",ConditionalOperators,"? IS [NOT] JSON [FORMAT JSON] [STRICT|LAX] [WITH|WITHOUT UNIQUE KEYS]", DbType.of(DbType.oracle)),
    IS_OF_TYPE_CONDITION("IS [NOT] OF TYPE",ConditionalOperators,"? IS [NOT] OF TYPE", DbType.of(DbType.oracle)),

    MEMBEROF("member of",ConditionalOperators,"? MEMBER OF(json_array)"),
    MEMBER("member",ConditionalOperators,"? [NOT] MEMBER [OF] nested_table"),
    SUBMULTISET("SUBMULTISET",ConditionalOperators,"nested_table1 [NOT] SUBMULTISET [OF] nested_table2 "),
    //endregion
    //region 条件函数
    ISDATE("ISDATE",ConditionalFunction,"ISDATE(?)"),
    ISNULL("isnull", ConditionalFunction, "ISNULL(?)"),
    ISNOTNULL("isnull", ConditionalFunction, "ISNOTNULL(?)"),
    ISFINITE("ISFINITE"),
    ISINF("ISINF"),
    ISNAN("ISNAN"),
    ISNUMERIC("ISNUMERIC",ConditionalFunction,"ISNUMERIC(?)"),
    ASSERT_TRUE("assert_true",ConditionalFunction,"assert_true(boolean condition)"),
    JSON_EXIST("JSON_EXISTS",ConditionalFunction,"JSON_EXISTS(?[, FORMAT JSON], JSON_path_expression [ERROR|TRUE|FALSE ON ERROR]", DbType.of(DbType.oracle)),
    JSON_TEXTCONTAINS("JSON_TEXTCONTAINS",ConditionalFunction,"JSON_TEXTCONTAINS(?, JSON_path_expression, string", DbType.of(DbType.oracle)),

    //region NULL相关函数
    COALESCE("COALESCE", NULLRelatedFunctions, "COALESCE(T v1, T v2, ...)"),
    LNNVL("LNNVL", NULLRelatedFunctions, "LNNVL(condition)"),
    NANVL1("NANVL", NULLRelatedFunctions, "NANVL(n2,n1)"),
    NULLIF("nullif", NULLRelatedFunctions,"nullif( a, b )"),
    NVL("nvl", NULLRelatedFunctions, "NVL(T value, T default_value)"),
    NVL2("nvl2", NULLRelatedFunctions, "NVL2(expr1, expr2, expr3)"),
    //endregion

    //endregion
    //region 流程控制
    IF("if", FlowControlFunctions,"if(boolean testCondition, T valueTrue, T valueFalseOrNull)"),
    IIF("IIF",FlowControlFunctions,"IIF( condition, value_if_true, value_if_false)"),
    CASE_VALUE("CASE value", FlowControlFunctions,"CASE ? WHEN compare_value THEN result [WHEN compare_value THEN result ...] [ELSE result] END"),
    CASE_CONDITION("CASE WHEN condition", FlowControlFunctions,"CASE WHEN condition THEN result [WHEN condition THEN result ...] [ELSE result] END"),
    IFNULL("IFNULL", FlowControlFunctions,"IFNULL(v1,v2)"),
    //endregion
    //region 正则表达式
    REGEXP("REGEXP", RegularExpressions,"? REGEXP pat"),
    NOT_REGEXP("NOT REGEXP", RegularExpressions,"? NOT REGEXP pat"),
    REGEXP_EXTRACT("regexp_extract", RegularExpressions,"regexp_extract(string subject, string pattern, int index)"),
    REGEXP_INSTR("REGEXP_INSTR", RegularExpressions, "REGEXP_INSTR(?, pat[, pos[, occurrence[, return_option[, match_type]]]])", DbType.of(DbType.mysql)),
    REGEXP_INSTR1("REGEXP_INSTR", RegularExpressions, "REGEXP_INSTR(?, pat[, pos[, occurrence[, return_option[, match_param [,subexpr]]]]])", DbType.of(DbType.oracle)),
    REGEXP_LIKE("regexp_like", RegularExpressions,"REGEXP_LIKE(expr, pat[, match_type])"),
    REGEXP_REPLACE("regexp_replace", RegularExpressions,"regexp_replace(string INITIAL_STRING, string PATTERN, string REPLACEMENT)"),
    REGEXP_REPLACE1("regexp_replace", RegularExpressions,"REGEXP_REPLACE(?, pat, repl[, pos[, occurrence[, match_type]]])", DbType.of(DbType.mysql)),
    REGEXP_REPLACE2("regexp_replace", RegularExpressions,"REGEXP_REPLACE(?, pat, [repl[, pos[, occurrence[, match_type]]]])", DbType.of(DbType.oracle)),

    REGEXP_SUBSTR("regexp_substr",RegularExpressions,"REGEXP_SUBSTR(?, pat[, pos[, occurrence[, match_type]]])"),
    REGEXP_SUBSTR1("regexp_substr",RegularExpressions,"REGEXP_SUBSTR(?, pat[, pos[, occurrence[, match_type[,subexpr]]]])", DbType.of(DbType.oracle)),
    //endregion
    //region 类型转换函数
    AS_BINARY("AS BINARY",ConversionFunctions, "? AS BINARY"),
    ASCIISTR("ASCIISTR", ConversionFunctions, "ASCIISTR(?)"),
    BINARY("BINARY", ConversionFunctions, "BINARY ?"),
    BINARY_HIVE("BINARY", ConversionFunctions, "BINARY(?)",DbType.of(DbType.hive)),
    BIN_TO_NUM("BIN_TO_NUM", ConversionFunctions, "BIN_TO_NUM(?[,...])"),
    CAST("cast",ConversionFunctions, "CAST(? AS type)"),
    CAST_MULTISET("CAST ... MULTISET",ConversionFunctions, "CAST(MULTISET(subquery) AS type)"),
    COMPOSE("COMPOSE",ConversionFunctions,"COMPOSE(?)"),
    CHARTOROWID("CHARTOROWID",ConversionFunctions,"CHARTOROWID(?)"),
    CONVERT_USING("CONVERT USING",ConversionFunctions,"CONVERT(? USING transcoding_name)"),
    CONVERT("CONVERT",ConversionFunctions,"CONVERT(?,type)"),
    CONVERT1("CONVERT",ConversionFunctions,"CONVERT(? ,dest_char_set [,source_char_set ])", DbType.of(DbType.oracle)),

    DECOMPOSE("DECOMPOSE",ConversionFunctions,"DECOMPOSE(? ['CANONICAL'|'COMPATIBILITY '])"),
    HEX("hex",ConversionFunctions,"HEX(?)"),
    HEXTORAW("HEXTORAW",ConversionFunctions,"HEXTORAW(?)"),
    RAWTOHEX("RAWTOHEX", ConversionFunctions, "RAWTOHEX(?)"),
    RAWTONHEX("RAWTONHEX", ConversionFunctions, "RAWTONHEX(?)"),
    ROWIDTOCHAR("ROWIDTOCHAR", ConversionFunctions, "ROWIDTOCHAR(?)"),
    ROWIDTONCHAR("ROWIDTONCHAR", ConversionFunctions, "ROWIDTONCHAR(?)"),
    SCN_TO_TIMESTAMP("SCN_TO_TIMESTAMP", ConversionFunctions, "SCN_TO_TIMESTAMP(?)"),
    TIMESTAMP_TO_SCN("TIMESTAMP_TO_SCN", ConversionFunctions, "TIMESTAMP_TO_SCN(?)"),
    TO_BINARY_DOUBLE("TO_BINARY_DOUBLE", ConversionFunctions, "TO_BINARY_DOUBLE((?[,fmt[, 'nlsparam']]))"),
    TO_BINARY_FLOAT("TO_BINARY_FLOAT", ConversionFunctions, "TO_BINARY_FLOAT((?[,fmt[, 'nlsparam']]))"),
    TO_BLOB("TO_BLOB", ConversionFunctions, "TO_BLOB((?)"),
    TO_CLOB("TO_CLOB", ConversionFunctions, "TO_CLOB((?)"),
    TO_DATE1("to_date",ConversionFunctions,"TO_DATE(?[,fmt[, 'nlsparam']])"),
    TO_LOB("TO_LOB",ConversionFunctions,"TO_LOB(?)"),
    TO_MULTI_BYTE("TO_MULTI_BYTE",ConversionFunctions,"TO_MULTI_BYTE(?)"),
    TO_NCHAR("TO_NCHAR ",ConversionFunctions,"TO_NCHAR(?)"),
    TO_NCHAR_DATETIME("TO_NCHAR(datetime)",ConversionFunctions,"TO_NCHAR(?[,fmt[, 'nlsparam']])"),
    TO_NCHAR_NUMBER("TO_NCHAR(number)",ConversionFunctions,"TO_NCHAR(?[,fmt[, 'nlsparam']])"),
    TO_NCLOB("TO_NCLOB ",ConversionFunctions,"TO_NCLOB(?)"),
    TO_NUMBER("TO_NUMBER", ConversionFunctions,"TO_NUMBER(?)"),
    TO_NUMBER1("TO_NUMBER", ConversionFunctions,"TO_NUMBER(?[,fmt[, 'nlsparam']])", DbType.of(DbType.oracle)),
    TO_SINGLE_BYTE("TO_SINGLE_BYTE", ConversionFunctions,"TO_SINGLE_BYTE(?)"),
    TREAT("TREAT", ConversionFunctions,"TREAT(? AS [REF] [schema.] type)"),
    USING_BINARY("USING BINARY",ConversionFunctions, "? USING BINARY"),
    UNISTR("UNISTR",ConversionFunctions, "UNISTR(?)"),


    OCT("oct",ConversionFunctions,"OCT(?)"),

    TO_CHAR("to_char", ConversionFunctions, "to_char(?)"),
    TO_CHAR1("TO_CHAR",ConversionFunctions,"TO_CHAR (?[,fmt[, 'nlsparam']])"),
    TO_BASE_64("to_base64",ConversionFunctions, "to_base64(?)"),
    TO_ASCII("TO_ASCII",ConversionFunctions,"TO_ASCII(?)"),
    TO_HEX("TO_HEX",ConversionFunctions,"TO_HEX(?)"),
    SMALLINT("SMALLINT", ConversionFunctions,"SMALLINT(?)"),
    INTEGER("INTEGER", ConversionFunctions,"INTEGER(?)"),
    BIGINT("BIGINT", ConversionFunctions,"BIGINT(?)"),
    DECIMAL("DECIMAL", ConversionFunctions,"DECIMAL(?)"),
    REAL("REAL", ConversionFunctions,"REAL(?)"),
    DOUBLE("DOUBLE", ConversionFunctions,"DOUBLE(?)"),
    FLOAT("FLOAT", ConversionFunctions,"FLOAT(?)"),
    CHAR("CHAR", ConversionFunctions,"CHAR(?)"),
    VARCHAR("VARCHAR", ConversionFunctions,"VARCHAR(?)"),
    VARCHAR_FORMAT_BIT("VARCHAR_FORMAT_BIT",ConversionFunctions, "VARCHAR_FORMAT_BIT(?)"),
    VARCHAR_FORMAT("VARCHAR_FORMAT", ConversionFunctions,"VARCHAR_FORMAT(?,'YYYY-MM-DD HH24:MI:SS')"),
    VARCHAR_BIT_FORMAT("VARCHAR_BIT_FORMAT", ConversionFunctions,"VARCHAR_BIT_FORMAT(?)"),
    LONG_VARCHAR("LONG_VARCHAR", ConversionFunctions,"LONG_VARCHAR(?)"),
    CLOB("CLOB", ConversionFunctions,"CLOB(?)"),
    GRAPHIC("GRAPHIC", ConversionFunctions,"GRAPHIC(?)"),
    VARGRAPHIC("VARGRAPHIC", ConversionFunctions,"VARGRAPHIC(?)"),
    LONG_VARGRAPHIC("LONG_VARGRAPHIC", ConversionFunctions,"LONG_VARGRAPHIC(?)"),
    DBCLOB("DBCLOB", ConversionFunctions,"DBCLOB(?)"),
    BLOB("BLOB", ConversionFunctions,"BLOB(?)"),
    DATE_CAST("DATE", ConversionFunctions,"DATE(?)"),
    UNHEX("unhex",ConversionFunctions,"UNHEX(?)"),
    //endregion
    //region 加密解密
    AES_ENCRYPT("aes_encrypt", ENCRYPT_DECRYPT,"AES_ENCRYPT(?,key_str[,init_vector][,kdf_name][,salt][,info | iterations])",DbType.of(DbType.mysql)),
    AES_ENCRYPT1("aes_encrypt", ENCRYPT_DECRYPT,"AES_ENCRYPT(?,key_str)"),
    AES_DECRYPT("aes_decrypt",ENCRYPT_DECRYPT,"AES_DECRYPT(?,key_str[,init_vector][,kdf_name][,salt][,info | iterations])",DbType.of(DbType.mysql)),
    AES_DECRYPT1("AES_DECRYPT", ENCRYPT_DECRYPT,"AES_DECRYPT(?,key_str)"),
    DES_DECRYPT("DES_DECRYPT",ENCRYPT_DECRYPT,"DES_DECRYPT(?)"),
    DES_ENCRYPT("DES_ENCRYPT",ENCRYPT_DECRYPT,"DES_ENCRYPT(?)"),
    MD5("md5",ENCRYPT_DECRYPT,"MD5(?)"),
    RANDOM_BYTES("random_bytes",ENCRYPT_DECRYPT,"RANDOM_BYTES(len)"),
    sha("sha",ENCRYPT_DECRYPT,"SHA(?)"),
    sha1("sha1",ENCRYPT_DECRYPT,"SHA1(?)"),
    sha2("sha2",ENCRYPT_DECRYPT,"SHA2(?)"),
    STATEMENT_DIGEST("statement_digest",ENCRYPT_DECRYPT,"STATEMENT_DIGEST(statement)"),
    statement_digest_text("statement_digest_text",ENCRYPT_DECRYPT,"STATEMENT_DIGEST_TEXT(statement)"),
    validate_password_strength("validate_password_strength",ENCRYPT_DECRYPT,"validate_password_strength()"),

    ASYMMETRIC_DECRYPT("asymmetric_decrypt",ENCRYPT_DECRYPT,"asymmetric_decrypt(algorithm, ?, priv_key_str)"),
    ASYMMETRIC_ENCRYPT("asymmetric_encrypt",ENCRYPT_DECRYPT,"asymmetric_encrypt(algorithm, ?, pub_key_str)"),
    ASYMMETRIC_DERIVE("asymmetric_derive",ENCRYPT_DECRYPT,"asymmetric_derive(pub_key_str, priv_key_str)"),
    ASYMMETRIC_SIGN("asymmetric_sign",ENCRYPT_DECRYPT,"asymmetric_sign(algorithm, ?, priv_key_str, digest_type)"),
    ASYMMETRIC_VERIFY("asymmetric_verify",ENCRYPT_DECRYPT,"asymmetric_verify(algorithm, ?, sig_str, pub_key_str, digest_type)"),
    CREATE_ASYMMETRIC_PRIV_KEY("create_asymmetric_priv_key",ENCRYPT_DECRYPT,"create_asymmetric_priv_key(algorithm, {key_len|dh_secret})"),
    CREATE_ASYMMETRIC_PUB_KEY("create_asymmetric_pub_key",ENCRYPT_DECRYPT,"create_asymmetric_pub_key(algorithm, priv_key_str)"),
    CREATE_DH_PARAMETERS("create_dh_parameters",ENCRYPT_DECRYPT,"create_dh_parameters(key_len)"),
    CREATE_DIGEST("create_digest",ENCRYPT_DECRYPT,"create_digest(digest_type, ?)"),
    ENCRYPT("ENCRYPT",ENCRYPT_DECRYPT,"ENCRYPT(?)"),
    DECRYPT_BIN("DECRYPT_BIN",ENCRYPT_DECRYPT,"DECRYPT_BIN(?)"),
    DECRYPT_CHARs("DECRYPT_CHARs",ENCRYPT_DECRYPT,"DECRYPT_CHARs(?)"),
    OLD_PASSWORD("OLD_PASSWORD",ENCRYPT_DECRYPT,"OLD_PASSWORD(?)"),
    PASSWORD("PASSWORD",ENCRYPT_DECRYPT,"PASSWORD(?)"),
    //endregion
    //region 编解码
    DECODE("decode", EncodingAndDecodingFunctions,"DECODE(?)"),
    DECODE1("decode", EncodingAndDecodingFunctions,"DECODE(binary bin, string charset)", DbType.of(DbType.hive)),
    DECODE2("decode", EncodingAndDecodingFunctions,"DECODE(?, {search,result...}[,default])", DbType.of(DbType.oracle)),
    DUMP("DUMP", EncodingAndDecodingFunctions,"DUMP(?[,return_fmt[,start_position[,length]]])", DbType.of(DbType.oracle)),
    ENCODE("encode", EncodingAndDecodingFunctions,"ENCODE(?)"),
    ENCODE1("encode", EncodingAndDecodingFunctions,"ENCODE(string src, string charset)", DbType.of(DbType.hive)),
    ORA_HASH("ORA_HASH", EncodingAndDecodingFunctions,"ORA_HASH(?[,max_bucket[,seed_value]])", DbType.of(DbType.oracle)),
    STANDARD_HASH("STANDARD_HASH", EncodingAndDecodingFunctions,"STANDARD_HASH(?[,'method'])", DbType.of(DbType.oracle)),
    VSIZE("VSIZE", EncodingAndDecodingFunctions,"VSIZE(?)", DbType.of(DbType.oracle)),
    //endregion
    //region 数据掩码/脱敏函数
    MASK("mask",DataMaskingFunction,"mask(?[, upper[, lower[, number]]])"),
    MASK_FIRST_N("mask_first_n",DataMaskingFunction,"mask_first_n(?[, n])"),
    MASK_LAST_N("mask_last_n",DataMaskingFunction,"mask_last_n(str[, n])"),
    MASK_SHOW_FIRST_N("mask_show_first_n",DataMaskingFunction,"mask_show_first_n(str[, n])"),
    MASK_SHOW_LAST_N("mask_show_last_n",DataMaskingFunction,"mask_show_last_n(str[, n])"),
    MASK_HASH("mask_hash",DataMaskingFunction,"mask_hash(?)"),
    //endregion
    //region 压缩解压缩
    uncompress("uncompress",CompressionFunctions,"uncompress(?)"),
    compress("compress",CompressionFunctions,"compress(?)"),
    uncompressed_length("uncompressed_length",CompressionFunctions,"uncompressed_length()"),
    //endregion
    //region Locking Fucntions
    GET_LOCK("get_lock", LockFunctions,"GET_LOCK(?,timeout)"),
    IS_FREE_LOCK("is_free_lock", LockFunctions,"IS_FREE_LOCK(?)"),
    IS_USED_LOCK("IS_USED_LOCK", LockFunctions,"IS_USED_LOCK(?)"),
    RELEASE_LOCK("RELEASE_LOCK", LockFunctions,"RELEASE_LOCK(?)"),
    RELEASE_ALL_LOCKS("release_all_locks", LockFunctions,"RELEASE_ALL_LOCKS()"),
    //endregion
    //region 信息函数
    BENCHMARK     ("BENCHMARK",InformationFunctions,"BENCHMARK(count,?)"),
    BUILDVERSION("buildversion",InformationFunctions,"BUILDVERSION()"),
    CHARSET       ("CHARSET",InformationFunctions,"CHARSET(?)"),
    COERCIBILITY  ("COERCIBILITY",InformationFunctions,"COERCIBILITY(?)"),
    COLLATION     ("COLLATION",InformationFunctions,"COLLATION(?)"),
    CON_DBID_TO_ID("CON_DBID_TO_ID",InformationFunctions,"CON_DBID_TO_ID(container_dbid)"),
    CON_GUID_TO_ID("CON_GUID_TO_ID",InformationFunctions,"CON_GUID_TO_ID(container_dbid)"),
    CON_UID_TO_ID("CON_UID_TO_ID",InformationFunctions,"CON_UID_TO_ID(container_dbid)"),
    CON_NAME_TO_ID("CON_NAME_TO_ID",InformationFunctions,"CON_NAME_TO_ID(container_dbid)"),
    CONNECTION_ID ("CONNECTION_ID",InformationFunctions,"CONNECTION_ID()"),
    CURRENT_DATABASE("current_database",InformationFunctions,"CURRENT_DATABASE()"),
    CURRENT_USER  ("CURRENT_USER",InformationFunctions,"CURRENT_USER()"),
    DATABASE      ("DATABASE",InformationFunctions,"DATABASE()"),
    FOUND_ROWS    ("FOUND_ROWS",InformationFunctions,"FOUND_ROWS ()"),
    ICU_VERSION("icu_version",InformationFunctions,"ICU_VERSION()"),
    LAST_INSERT_ID("LAST_INSERT_ID",InformationFunctions,"LAST_INSERT_ID()"),
    LOGGED_IN_USER("logged_in_user",InformationFunctions,"logged_in_user()"),
    ORA_INVOKING_USER("ORA_INVOKING_USER",InformationFunctions,"ORA_INVOKING_USER"),
    ORA_INVOKING_USERID("ORA_INVOKING_USERID",InformationFunctions,"ORA_INVOKING_USERID"),
    ROLES_GRAPHML("roles_graphml",InformationFunctions,"ROLES_GRAPHML()"),
    ROW_COUNT     ("ROW_COUNT",InformationFunctions,"ROW_COUNT()"),
    SCHEMA        ("SCHEMA",InformationFunctions,"SCHEMA()"),
    SESSION_USER  ("SESSION_USER",InformationFunctions,"SESSION_USER ()"),
    SYS_CONTEXT   ("SYS_CONTEXT",InformationFunctions,"SYS_CONTEXT('namespace', 'parameter'[,length])"),
    SYS_GUID   ("SYS_GUID",InformationFunctions,"SYS_GUID()"),

    SYSTEM_USER   ("SYSTEM_USER",InformationFunctions,"SYSTEM_USER()"),
    SYS_TYPEID   ("SYS_TYPEID",InformationFunctions,"SYS_TYPEID(object_type_value)"),
    UID          ("UID",InformationFunctions,"UID"),

    USER          ("USER",InformationFunctions,"USER()"),
    USER1          ("USER",InformationFunctions,"USER", DbType.of(DbType.oracle)),
    USERENV          ("USERENV",InformationFunctions,"USERENV", DbType.of(DbType.oracle)),
    VERSION       ("VERSION",InformationFunctions,"VERSION('parameter')"),
    //endregion
    //region GlobalTransactionIdentifiers
    GTID_SUBSET("gtid_subset", GlobalTransactionIdentifiers,"GTID_SUBSET(set1,set2)"),
    GTID_SUBTRACT("gtid_subtract",GlobalTransactionIdentifiers,"GTID_SUBTRACT(set1,set2)"),
    WAIT_UNTIL_SQL_THREAD_AFTER_GTIDS("wait_until_sql_thread_after_gtids",GlobalTransactionIdentifiers,"WAIT_UNTIL_SQL_THREAD_AFTER_GTIDS(gtid_set[, timeout][,channel])"),
    WAIT_FOR_EXECUTED_GTID_SET("wait_for_executed_gtid_set",GlobalTransactionIdentifiers,"WAIT_FOR_EXECUTED_GTID_SET(gtid_set[, timeout])"),
    //endregion
    //region 聚合函数
    APPROX_COUNT_DISTINCT("APPROX_COUNT_DISTINCT",AggregateFunctions,"APPROX_COUNT_DISTINCT(?)"),
    AVG("AVG",AggregateFunctions,"AVG(?)"),
    AVG_DISTINCT("AVG(DISTINCT)",AggregateFunctions,"AVG(DISTINCT ?)"),
    BIT_AND("bit_and",AggregateFunctions,"bit_and(?)"),
    BIT_OR("bit_or",AggregateFunctions,"bit_or(?)"),
    BIT_XOR("bit_xor",AggregateFunctions,"bit_xor(?)"),
    COLLECT_LIST("collect_list",AggregateFunctions,"COLLECT_LIST(?)"),
    CORR("corr",AggregateFunctions,"corr(?, ?)"),
    CORR_K("CORR_K",AggregateFunctions,"CORR_K(?, ?[,COEFFICIENT|ONE_SIDED_SIG|ONE_SIDED_SIG_POS|ONE_SIDED_SIG_NEG|TWO_SIDED_SIG])"),
    CORR_S("CORR_S",AggregateFunctions,"CORR_S(?, ?[,COEFFICIENT|ONE_SIDED_SIG|ONE_SIDED_SIG_POS|ONE_SIDED_SIG_NEG|TWO_SIDED_SIG])"),
    COUNT("COUNT",AggregateFunctions,"COUNT(?)"),
    COUNT_DISTINCT("count(distinct)",AggregateFunctions,"COUNT(DISTINCT ?)"),
    COVAR_POP("covar_pop",AggregateFunctions,"covar_pop(?, ?)"),
    COVAR_SAMP("covar_samp",AggregateFunctions,"covar_samp(?, ?)"),
    GROUP_CONCAT("group_concat",AggregateFunctions,"GROUP_CONCAT(?)"),
    GROUP_ID("GROUP_ID",AggregateFunctions,"GROUP_ID()"),
    GROUPING("GROUPING",AggregateFunctions,"GROUPING(?)", DbType.of(DbType.oracle)),
    GROUPING1("grouping",AggregateFunctions,"GROUPING(? ...)"),
    GROUPING_ID("GROUPING_ID",AggregateFunctions,"GROUPING_ID(?,...)"),
    HISTOGRAM_NUMERIC("histogram_numeric",AggregateFunctions,"histogram_numeric(?, b)"),
    JSON_ARRAYAGG("json_arrayagg",AggregateFunctions,"JSON_ARRAYAGG(?)"),
    JSON_OBJECTAGG("json_objectagg",AggregateFunctions,"JSON_OBJECTAGG(key, value)"),
    MAX("MAX",AggregateFunctions,"MAX([DISTINCT] ?)"),
    MIN("MIN",AggregateFunctions,"MIN([DISTINCT] ?)"),
    PERCENTILE("percentile",AggregateFunctions,"percentile(BIGINT ?, p)"),
    PERCENTILE_APPROX("percentile_approx",AggregateFunctions,"percentile_approx(?, p [, B])"),
    REGR_AVGX("regr_avgx", AggregateFunctions,"REGR_AVGX(independent, dependent)"),
    REGR_AVGY("regr_avgy", AggregateFunctions,"REGR_AVGY(independent, dependent)"),
    REGR_COUNT("regr_count", AggregateFunctions,"REGR_COUNT(independent, dependent)"),
    REGR_INTERCEPT("regr_intercept", AggregateFunctions,"REGR_INTERCEPT(independent, dependent)"),
    REGR_R2("regr_r2", AggregateFunctions,"REGR_R2(independent, dependent)"),
    REGR_SLOPE("regr_slope", AggregateFunctions,"REGR_SLOPE(independent, dependent)"),
    REGR_SXX("regr_sxx", AggregateFunctions,"REGR_SXX(independent, dependent)"),
    REGR_SXY("regr_sxy", AggregateFunctions,"REGR_SXY(independent, dependent)"),
    REGR_SYY("regr_syy", AggregateFunctions,"REGR_SYY(independent, dependent)"),
    STD("std",AggregateFunctions,"STD(?)"),
    STDDEV("STDDEV",AggregateFunctions,"STDDEV(?)"),
    STDDEV_POP("stddev_pop",AggregateFunctions,"STDDEV_POP(?)"),
    STDDEV_SAMP("stddev_samp",AggregateFunctions,"STDDEV_SAMP(?)"),
    SUM("SUM",AggregateFunctions,"SUM([DISTINCT] ?)"),
    VAR_POP("var_pop",AggregateFunctions,"VAR_POP(?)"),
    VAR_SAMP("var_samp",AggregateFunctions,"VAR_SAMP(?)"),
    VARIANCE("variance",AggregateFunctions,"VARIANCE(?)"),

    COMPUTE_STATS("compute_stats",AggregateFunctions,"compute_stats(?)"),

    VALUE_AGG("VALUE",AggregateFunctions,"VALUE(EXPRESSION1,EXPRESSION2)",DbType.of(DbType.db2)), //db2
    //endregion
    //region 窗口函数
    CUME_DIST("cume_dist",WindowFunction,"cume_dist() over(partition by ?  order by ? desc)"),
    CUME_DIST1("CUME_DIST",WindowFunction,"CUME_DIST(expr,...) WITH GROUP (ORDER BY {expr [DESC|ASC] [NULLS [FIRST|LAST]],...})", DbType.of(DbType.oracle)),
    DENSE_RANK("dense_rank",WindowFunction,"dense_rank() over(partition by ?  order by ? desc)"),
    DENSE_RANK1("dense_rank",WindowFunction,"DENSE_RANK(expr,...) WITH GROUP (ORDER BY {expr [DESC|ASC] [NULLS [FIRST|LAST]],...})", DbType.of(DbType.oracle)),
    FIRST_VALUE("first_value",WindowFunction,"first_value(?) over(partition by ?  order by ? desc)"),
    LAG("lag",WindowFunction,"LAG(? [, N[, default]]) over(partition by ?  order by ? desc)"),
    LAST_VALUE("last_value",WindowFunction,"last_value(?) over(partition by ?  order by ? desc)"),
    LEAD("lead",WindowFunction,"LEAD(? [, N[, default]]) over(partition by ?  order by ? desc)"),
    NTH_VALUE("nth_value",WindowFunction,"NTH_VALUE(?, N) over_clause"),
    NTILE("ntile",WindowFunction,"NTILE(N) over_clause"),
    PERCENT_RANK("percent_rank",WindowFunction,"percent_rank() over(partition by ?  order by ? desc)"),
    RANK("rank",WindowFunction,"RANK() over(partition by ?  order by ? desc)"),
    ROW_NUMBER("row_number",WindowFunction,"row_number() over(partition by ?  order by ? desc)"),
    //endregion
    //region Table-Generating Functions (UDTF)
    EXPLODE("explode", TableGeneratingFunctions,"explode(?)"),
    POSEXPLODE("posexplode",TableGeneratingFunctions,"posexplode(?)"),
    INLINE("inline",TableGeneratingFunctions,"inline(?)"),
    STACK("stack",TableGeneratingFunctions,"stack(?)"),
    JSON_TUPLE("json_tuple",TableGeneratingFunctions,"json_tuple(?)"),
    PARSE_URL_TUPLE("parse_url_tuple",TableGeneratingFunctions,"PARSE_URL_TUPLE(?)"),
    //endregion
    //region PerformanceSchemaFunctions
    FORMAT_BYTES("format_bytes",PerformanceSchemaFunctions,"FORMAT_BYTES(count)"),
    FORMAT_PICO_TIME("format_pico_time",PerformanceSchemaFunctions,"FORMAT_PICO_TIME(time_val)"),
    PS_THREAD_ID("ps_thread_id",PerformanceSchemaFunctions,"PS_THREAD_ID(connection_id)"),
    PS_CURRENT_THREAD_ID("ps_current_thread_id",PerformanceSchemaFunctions,"ps_current_thread_id()"),
    //endregion
    //region 数据挖掘
    CLUSTER_DETAILS("CLUSTER_DETAILS",DataMiningFunctions),
    CLUSTER_DISTANCE("CLUSTER_DISTANCE",DataMiningFunctions),
    CLUSTER_ID("CLUSTER_ID",DataMiningFunctions),
    CLUSTER_PROBABILITY("CLUSTER_PROBABILITY",DataMiningFunctions),
    CLUSTER_SET("CLUSTER_SET",DataMiningFunctions),
    FEATURE_DETAILS("FEATURE_DETAILS",DataMiningFunctions),
    FEATURE_ID("FEATURE_ID",DataMiningFunctions),
    FEATURE_SET("FEATURE_SET",DataMiningFunctions),
    FEATURE_VALUE("FEATURE_VALUE",DataMiningFunctions),
    PREDICTION("PREDICTION",DataMiningFunctions),
    PREDICTION_BOUNDS("PREDICTION_BOUNDS",DataMiningFunctions),
    PREDICTION_COST("PREDICTION_COST",DataMiningFunctions),
    PREDICTION_DETAILS("PREDICTION_DETAILS",DataMiningFunctions),
    PREDICTION_PROBABILITY("PREDICTION_PROBABILITY",DataMiningFunctions),
    PREDICTION_SET("PREDICTION_SET",DataMiningFunctions),
    //endregion
    //region 模型函数
    CV("CV",ModelFunctions,"  MODEL\n" +
            "    PARTITION BY (?,...)\n" +
            "    DIMENSION BY (?,...)\n" +
            "    MEASURES (expr,...)\n" +
            "    IGNORE NAV\n" +
            "    UNIQUE DIMENSION\n" +
            "    RULES UPSERT SEQUENTIAL ORDER\n(expr with CV([dimension_column ]) \n)"),
    ITERATION_NUMBER("ITERATION_NUMBER",ModelFunctions,"  MODEL\n" +
            "    PARTITION BY (country)\n" +
            "    DIMENSION BY (prod, year)\n" +
            "    MEASURES (sale s)\n" +
            "    IGNORE NAV\n" +
            "    UNIQUE DIMENSION\n" +
            "    RULES UPSERT SEQUENTIAL ORDER ITERATE(2) \n(expr with ITERATION_NUMBER\n)"),
    PRESENTNNV("PRESENTNNV",ModelFunctions,"  MODEL\n" +
            "    PARTITION BY (country)\n" +
            "    DIMENSION BY (prod, year)\n" +
            "    MEASURES (sale s)\n" +
            "    IGNORE NAV\n" +
            "    UNIQUE DIMENSION\n" +
            "    RULES UPSERT SEQUENTIAL ORDER\n( expr with PRESENTNNV(cell_reference,expr1,expr2) \n)"),
    PRESENTV("PRESENTV",ModelFunctions,"  MODEL\n" +
            "    PARTITION BY (country)\n" +
            "    DIMENSION BY (prod, year)\n" +
            "    MEASURES (sale s)\n" +
            "    IGNORE NAV\n" +
            "    UNIQUE DIMENSION\n" +
            "    RULES UPSERT SEQUENTIAL ORDER\n(expr with PRESENTV(cell_reference,expr1,expr2)\n)"),
    PREVIOUS("PREVIOUS",ModelFunctions,"MODEL\n" +
            "    DIMENSION BY (dim_col)\n" +
            "    MEASURES (cur_val, 0 num_of_iterations)\n" +
            "    IGNORE NAV\n" +
            "    UNIQUE DIMENSION\n" +
            "    RULES ITERATE (1000) UNTIL (condition with PREVIOUS(cell_reference))\n" +
            "    (\n" +
            "      expr" +
            "    \n)"),
    //endregion
    //region 杂项
    ANY_VALUE("any_value", MiscellaneousFunctions, "any_value(?)"),
    BIN_TO_UUID("bin_to_uuid",MiscellaneousFunctions,"BIN_TO_UUID(?, swap_flag)"),
    DEFAULT("default",MiscellaneousFunctions,"DEFAULT(?)"),
    HASH("hash",MiscellaneousFunctions,"hash(?[, a2...])"),
    INET_ATON("inet_aton",MiscellaneousFunctions,"inet_aton(?)"),
    INET_NTOA("inet_ntoa",MiscellaneousFunctions,"inet_ntoa(?)"),
    INET6_NTOA("inet6_ntoa",MiscellaneousFunctions,"INET6_NTOA(?)"),
    IS_IPV4("is_ipv4",MiscellaneousFunctions,"is_ipv4(?)"),
    IS_IPV4_COMPAT("is_ipv4_compat",MiscellaneousFunctions,"is_ipv4_compat(?)"),
    IS_IPV4_MAPPED("is_ipv4_mapped",MiscellaneousFunctions,"is_ipv4_mapped(?)"),
    IS_IPV6("is_ipv6",MiscellaneousFunctions,"is_ipv6(?)"),
    IS_UUID("is_uuid",MiscellaneousFunctions,"is_uuid(?)"),
    JAVA_METHOD("java_method",MiscellaneousFunctions,"java_method(class, method[, arg1[, arg2..]])"),
    MASTER_POS_WAIT("master_pos_wait",MiscellaneousFunctions,"MASTER_POS_WAIT(log_name,log_pos[,timeout][,channel])"),
    NAME_CONST("name_const",MiscellaneousFunctions,"NAME_CONST(name,value)"),
    REFLECT("reflect",MiscellaneousFunctions,"reflect(class, method[, arg1[, arg2..]])"),
    REFLECT2("reflect2",MiscellaneousFunctions,"reflect2(?,method[,arg1[,arg2..]])"),
    SLEEP("sleep",MiscellaneousFunctions,"SLEEP(duration)"),
    SOURCE_POS_WAIT("source_pos_wait",MiscellaneousFunctions,"SOURCE_POS_WAIT(log_name,log_pos[,timeout][,channel])"),
    SURROGATE_KEY("surrogate_key",MiscellaneousFunctions,"surrogate_key([write_id_bits, task_id_bits])"),
    UUID("uuid",MiscellaneousFunctions,"uuid()"),
    UUID_SHORT("uuid_short",MiscellaneousFunctions,"uuid_short()"),
    UUID_TO_BIN("uuid_to_bin",MiscellaneousFunctions,"UUID_TO_BIN(?, swap_flag)"),
    VALUES("values",MiscellaneousFunctions,"values(?)"),
    //endregion


    //--------------------------explode----------
    Compl("~"),

    RAW("RAW"),
    // Number of points in path or polygon
    Pound("#"),
    //endregion
    //region 二元操作符

    COLLATE("COLLATE", BinaryOperator),

    ILike("ILIKE", BinaryOperator),
    NotILike("NOT ILIKE", BinaryOperator),
    AT_AT("@@", BinaryOperator),
    SIMILAR_TO("SIMILAR TO", BinaryOperator),
    POSIX_Regular_Match("~", BinaryOperator),
    POSIX_Regular_Match_Insensitive("~*", BinaryOperator),
    POSIX_Regular_Not_Match("!~", BinaryOperator),
    POSIX_Regular_Not_Match_POSIX_Regular_Match_Insensitive("!~*", BinaryOperator),

    SAME_AS("~=", BinaryOperator),


    Escape("ESCAPE", BinaryOperator),
    PG_ST_DISTANCE("<->", BinaryOperator),

    //endregion

    //region Built-In Function and Operator
    MBRTOUCHES("mbrtouches","MBRTouches(g1, g2)"),

    ST_SRID("st_srid","st_srid(?)"),

    NORMALIZE_STATEMENT("normalize_statement","normalize_statement(?)"),
    MBRCOVERS("mbrcovers","MBRCovers(g1, g2)"),
    READ_FIREWALL_USERS("read_firewall_users","read_firewall_users(user, mode)"),
    ST_UNION("st_union","ST_Union(g1, g2)"),

    st_pointatdistance("st_pointatdistance","st_pointatdistance()"),
    st_mpointfromtext("st_mpointfromtext","st_mpointfromtext()"),
    st_collect("st_collect","st_collect()"),
    inet6_aton("inet6_aton","inet6_aton()"),
    st_isclosed("st_isclosed","st_isclosed()"),



    mbrcoveredby("mbrcoveredby","mbrcoveredby()"),
    st_numgeometries("st_numgeometries","st_numgeometries()"),
    st_geomcollfromwkb("st_geomcollfromwkb","st_geomcollfromwkb()"),
    st_asbinary("st_asbinary","st_asbinary()"),
    st_pointfromgeohash("st_pointfromgeohash","st_pointfromgeohash()"),


    st_endpoint("st_endpoint","st_endpoint()"),
    st_symdifference("st_symdifference","st_symdifference()"),
    multipoint("multipoint","multipoint()"),
    st_envelope("st_envelope","st_envelope()"),
    st_touches("st_touches","st_touches()"),

    mbrcontains("mbrcontains","mbrcontains()"),
    mbrwithin("mbrwithin","mbrwithin()"),
    st_isvalid("st_isvalid","st_isvalid()"),
    st_longfromgeohash("st_longfromgeohash","st_longfromgeohash()"),
    st_difference("st_difference","st_difference()"),
    current_role("current_role","current_role()"),

    st_issimple("st_issimple","st_issimple()"),
    set_firewall_mode("set_firewall_mode","set_firewall_mode(user, mode)"),

    multipolygon("multipolygon","multipolygon()"),
    st_linefromtext("st_linefromtext","st_linefromtext()"),

    charset("charset","charset()"),

    st_makeenvelope("st_makeenvelope","st_makeenvelope()"),
    st_transform("st_transform","st_transform()"),

    st_buffer_strategy("st_buffer_strategy","st_buffer_strategy()"),
    st_intersection("st_intersection","st_intersection()"),
    st_area("st_area","st_area()"),
    polygon("polygon","polygon()"),
    read_firewall_whitelist("read_firewall_whitelist","read_firewall_whitelist(user, rule)"),

    st_validate("st_validate","st_validate()"),
    st_linefromwkb("st_linefromwkb","st_linefromwkb()"),
    row_count("row_count","row_count()"),

    mbrdisjoint("mbrdisjoint","mbrdisjoint()"),
    st_interiorringn("st_interiorringn","st_interiorringn()"),
    st_contains("st_contains","st_contains()"),

    st_astext("st_astext","st_astext()"),

    st_overlaps("st_overlaps","st_overlaps()"),
    mbrequals("mbrequals","mbrequals()"),
    st_asgeojson("st_asgeojson","st_asgeojson()"),
    st_hausdorffdistance("st_hausdorffdistance","st_hausdorffdistance()"),
    st_numpoints("st_numpoints","st_numpoints()"),
    mbroverlaps("mbroverlaps","mbroverlaps()"),
    st_polyfromtext("st_polyfromtext","st_polyfromtext()"),
    st_pointfromwkb("st_pointfromwkb","st_pointfromwkb()"),
    st_geomfromtext("st_geomfromtext","st_geomfromtext()"),
    st_mpointfromwkb("st_mpointfromwkb","st_mpointfromwkb()"),
    st_mpolyfromtext("st_mpolyfromtext","st_mpolyfromtext()"),
    st_x("st_x","st_x()"),
    st_y("st_y","st_y()"),
    st_geometrytype("st_geometrytype","st_geometrytype()"),

    st_polyfromwkb("st_polyfromwkb","st_polyfromwkb()"),
    st_distance_sphere("st_distance_sphere","st_distance_sphere()"),

    st_swapxy("st_swapxy","st_swapxy()"),


    st_startpoint("st_startpoint","st_startpoint()"),
    st_isempty("st_isempty","st_isempty()"),

    linestring("linestring","linestring()"),
    st_lineinterpolatepoints("st_lineinterpolatepoints","st_lineinterpolatepoints()"),
    between_and("between ... and ...","? BETWEEN min AND max"),
    NOT_BETWEEN_AND("not between ... and ...","? NOT BETWEEN min AND max"),
    st_distance("st_distance","st_distance()"),
    st_longitude("st_longitude","st_longitude()"),
    coercibility("coercibility","coercibility()"),

    st_geomfromgeojson("st_geomfromgeojson","st_geomfromgeojson()"),
    st_length("st_length","st_length()"),

    st_disjoint("st_disjoint","st_disjoint()"),
    st_frechetdistance("st_frechetdistance","st_frechetdistance()"),

    st_exteriorring("st_exteriorring","st_exteriorring()"),

    st_pointfromtext("st_pointfromtext","st_pointfromtext()"),

    st_convexhull("st_convexhull","st_convexhull()"),


    st_latitude("st_latitude","st_latitude()"),

    multilinestring("multilinestring","multilinestring()"),
    benchmark("benchmark","benchmark()"),
    geometrycollection("geometrycollection","geometrycollection()"),

    st_centroid("st_centroid","st_centroid()"),
    st_lineinterpolatepoint("st_lineinterpolatepoint","st_lineinterpolatepoint()"),
    st_within("st_within","st_within()"),
    geomcollection("geomcollection","geomcollection()"),

    mbrintersects("mbrintersects","mbrintersects()"),
    st_dimension("st_dimension","st_dimension()"),
    st_crosses("st_crosses","st_crosses()"),
    st_geomcollfromtext("st_geomcollfromtext","st_geomcollfromtext()"),
    st_mlinefromtext("st_mlinefromtext","st_mlinefromtext()"),

    st_mpolyfromwkb("st_mpolyfromwkb","st_mpolyfromwkb()"),
    st_geometryn("st_geometryn","st_geometryn()"),
    st_simplify("st_simplify","st_simplify()"),

    st_geomfromwkb("st_geomfromwkb","st_geomfromwkb()"),

    collation("collation","collation()"),
    st_numinteriorring("st_numinteriorring","st_numinteriorring()"),
    st_equals("st_equals","st_equals()"),

    st_latfromgeohash("st_latfromgeohash","st_latfromgeohash()"),
    found_rows("found_rows","found_rows()"),

    MYSQL_FIREWALL_FLUSH_STATUS("mysql_firewall_flush_status","mysql_firewall_flush_status()"),

    point("point","point()"),

    st_buffer("st_buffer","st_buffer()"),

    st_geohash("st_geohash","st_geohash()"),
    st_pointn("st_pointn","st_pointn()"),

    match("match","match()"),

    st_mlinefromwkb("st_mlinefromwkb","st_mlinefromwkb()"),
    //endregion


    GETHINT("GETHINT"),
    GENERATE_UNIQUE("GENERATE_UNIQUE","GENERATE_UNIQUE()"),
    // 函数
    /**
     * 守卫
     */
    DUMMY("DUMMY"),
    //endregion
    ;


    SQLOperator(String name) {
        this(name, MiscellaneousFunctions);
    }
    SQLOperator(String name, SQLFunctionCatalog catalog) {
        this(name, catalog, name +"()");
    }
    SQLOperator(String name, SQLFunctionCatalog catalog, String usage) {
        this(name, catalog, usage, DbType.of(DbType.other));
    }
    SQLOperator(String name, SQLFunctionCatalog catalog, String usage, long dbType) {
        this.name = name;
        this.nameLCase = name.toLowerCase();
        this.catalog = catalog;
        this.supportDbTypes = dbType;
        this.usage = usage;
    }
    SQLOperator(String name, String usage) {
        this(name, MiscellaneousFunctions, usage);
    }

    public final String name;
    public final String nameLCase;
    /**
     * 分类类型
     */
    public final SQLFunctionCatalog catalog;
    /**
     * 支持的数据库类型
     */
    public final long supportDbTypes;
    /**
     * 简化签名的获取，不在使用标准的SQL语法
     */
    public final String usage;

    public boolean isSupport(DbType dbType) {
        return (dbType.mask & supportDbTypes) != 0;
    }

    /**
     * 查找符合条件的签名
     * @param dbType 数据库类型
     * @param catalog 类别
     * @param name 函数名称
     * @return
     */
    public static SQLOperator of(DbType dbType, SQLFunctionCatalog catalog, String name) {
        SQLOperator sig = null;

        if (name == null || name.isEmpty()) {
            return null;
        }
        for (SQLOperator value : SQLOperator.values()) {
            if (value.catalog.equals(catalog) && value.name.equalsIgnoreCase(name)) {
                // 如果全部符合
                if (value.isSupport(dbType)) {
                    sig = value;
                    break;
                }
                // 如果类别和名称符合，检查数据库是否为other
                if (value.isSupport(DbType.other)) {
                    sig = value;
                }
            }
        }

        return sig;
    }

    public static ArrayList<SQLOperator> of(DbType dbType, String name) {
        ArrayList<SQLOperator> operators = new ArrayList<>();

        if (name == null || name.isEmpty()) {
            return operators;
        }

        HashSet<SQLFunctionCatalog> catalogs = getCatalog(dbType, name);
        for (SQLFunctionCatalog catalog: catalogs) {
            SQLOperator sig = null;
            for (SQLOperator value : SQLOperator.values()) {
                if (value.catalog.equals(catalog) && value.name.equalsIgnoreCase(name)) {
                    // 如果全部符合
                    if (value.isSupport(dbType)) {
                        sig = value;
                        break;
                    }
                    // 如果类别和名称符合，检查数据库是否为other
                    if (value.isSupport(DbType.other)) {
                        sig = value;
                    }
                }
            }
            if (sig != null) {
                operators.add(sig);
            }
        }

        return operators;
    }

    public static HashSet<SQLFunctionCatalog> getCatalog(DbType dbType, String name) {
        HashSet<SQLFunctionCatalog> catalogs = new HashSet<>();

        if (name == null || name.isEmpty()
                || dbType == null) {
            return null;
        }

        for (SQLOperator value : SQLOperator.values()) {
            if (value.name.equalsIgnoreCase(name) &&
                    (value.isSupport(dbType) || value.isSupport(DbType.other))) {
                catalogs.add(value.catalog);
            }
        }

        return catalogs;
    }

}
