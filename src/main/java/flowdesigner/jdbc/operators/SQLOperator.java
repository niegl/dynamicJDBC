package flowdesigner.jdbc.operators;

public enum SQLOperator {

    //region 一元操作符
    Plus("+"),
    Negative("-"),
    Not("!"),
    Compl("~"),
    Prior("PRIOR"),
    ConnectByRoot("CONNECT BY"),
    BINARY("BINARY"),
    RAW("RAW"),
    NOT("NOT"),
    // Number of points in path or polygon
    Pound("#"),
    //endregion
    //region 二元操作符
    Union("UNION", 0),
    COLLATE("COLLATE", 20),
    BitwiseXor("^", 50),
    BitwiseXorEQ("^=", 110),
    Multiply("*", 60),
    Divide("/", 60),
    DIV("DIV", 60),
    Modulus("%", 60),
    MOD("MOD", 60),
    ADD("+", 70),
    Subtract("-", 70),
    SubGt("->", 20),
    SubGtGt("->>", 20),
    PoundGt("#>", 20),
    PoundGtGt("#>>", 20),
    QuesQues("??", 20),
    QuesBar("?|", 20),
    QuesAmp("?&", 20),
    LeftShift("<<", 80),
    RightShift(">>", 80),
    BitwiseAnd("&", 90),
    BitwiseOr("|", 100),
    GreaterThan(">", 110),
    GreaterThanOrEqual(">=", 110),
    Is("IS", 110),
    LessThan("<", 110),
    LessThanOrEqual("<=", 110),
    LessThanOrEqualOrGreaterThan("<=>", 110),
    LessThanOrGreater("<>", 110),
    IsDistinctFrom("IS DISTINCT FROM", 110),
    IsNotDistinctFrom("IS NOT DISTINCT FROM", 110),
    Like("LIKE", 110),
    SoudsLike("SOUNDS LIKE", 110),
    NotLike("NOT LIKE", 110),
    ILike("ILIKE", 110),
    NotILike("NOT ILIKE", 110),
    AT_AT("@@", 110),
    SIMILAR_TO("SIMILAR TO", 110),
    POSIX_Regular_Match("~", 110),
    POSIX_Regular_Match_Insensitive("~*", 110),
    POSIX_Regular_Not_Match("!~", 110),
    POSIX_Regular_Not_Match_POSIX_Regular_Match_Insensitive("!~*", 110),
    Array_Contains("@>", 110),
    Array_ContainedBy("<@", 110),
    SAME_AS("~=", 110),
    JSONContains("?", 110),
    RLike("RLIKE", 110),
    NotRLike("NOT RLIKE", 110),
    NotEqual("!=", 110),
    NotLessThan("!<", 110),
    NotGreaterThan("!>", 110),
    IsNot("IS NOT", 110),
    Escape("ESCAPE", 110),
    RegExp("REGEXP", 110),
    NotRegExp("NOT REGEXP", 110),
    Equality("=", 110),
    BitwiseNot("!", 130),
    Concat("||", 140),
    BooleanAnd("AND", 140),
    BooleanXor("XOR", 150),
    BooleanOr("OR", 160),
    Assignment(":=", 169),
    PG_And("&&", 140),
    PG_ST_DISTANCE("<->", 20),
    //endregion

    //region ComplexTypeConstructor
    Map("map"),
    Struct("struct"),
    Named_struct("named_struct"),
    Array("array"),
    Create_union("create_union"),
    //endregion
    //region MathematicalFunction
    round("round"),
    trunc("trunc","trunc(?)"),
    bround("bround"),
    FLOOR("floor"),
    CEIL("ceil"),
    ceiling("ceiling"),
    rand("rand"),
    EXP("exp"),
    LN("ln"),
    log10("log10"),
    log2("log2"),
    LOG("log"),
    pow("pow"),
    SQRT("sqrt"),
    bin("bin"),
    hex("hex"),
    unhex("unhex"),
    conv("conv"),
    ABS("abs"),
    pmod("pmod"),
    SIN("sin"),
    asin("asin"),
    COS("cos"),
    COT("COT","COT(?)"),
    ACOS("acos"),
    TAN("tan"),
    atan("atan"),
    ATAN2("ATAN2","atan2(?, ?)"),
    DEGREES("degrees"),
    RADIANS("radians"),
    RANDOM("RANDOM","RANDOM()"),
    positive("positive"),
    negative("negative"),
    SIGN("sign"),
    e("e"),
    PI("pi"),
    POWER("POWER","POWER(?,n)"),
    factorial("factorial"),
    CBRT("cbrt"),
    shiftleft("shiftleft"),
    shiftright("shiftright"),
    shiftrightunsigned("shiftrightunsigned"),
    greatest("greatest"),
    least("least"),
    width_bucket("width_bucket"),
    //endregion

    //region CollectionFunction
    size("size"),
    map_keys("map_keys"),
    map_values("map_values"),
    array_contains("array_contains"),
    sort_array("sort_array"),
    //endregion

    //region Type Conversion Functions
    binary("binary"),
    cast("cast"),
    asciistr("asciistr"),
    bin_to_num("bin_to_num"),
    CONVERT("convert","convert(?,d_chset)"),
    TO_CHAR("to_char", "to_char(?)"),
    TO_NUMBER("to_number","to_number(?)"),
    TO_TIMESTAMP("TO_TIMESTAMP","TO_TIMESTAMP(?)"),
    //endregion

    //region Date Functions
    from_unixtime("from_unixtime"),
    unix_timestamp("unix_timestamp"),
    TO_DATE("to_date"),
    year("year"),
    quarter("quarter"),
    month("month"),
    day("day"),
    dayofmonth("dayofmonth"),
    DAYOFWEEK("DAYOFWEEK", "DAYOFWEEK(?)"),
    hour("hour"),
    minute("minute"),
    second("second"),
    weekofyear("weekofyear"),
    extract("extract"),
    datediff("datediff"),
    date_add("date_add"),
    date_sub("date_sub"),
    from_utc_timestamp("from_utc_timestamp"),
    to_utc_timestamp("to_utc_timestamp"),
    current_date("current_date"),
    current_timestamp("current_timestamp"),
    add_months("add_months"),
    last_day("last_day"),
    next_day("next_day"),
    TRUNC("TRUNC","TRUNC(?)"),
    months_between("months_between"),
    date_format("date_format"),
    SYSDATE("SYSDATE","SYSDATE"),
    SYSTIMESTAMP("SYSTIMESTAMP","SYSTIMESTAMP"),
    DBTIMEZONE("DBTIMEZONE","DBTIMEZONE"),
    ROUND("ROUND","ROUND(?)"),
    //endregion

    //region Conditional Functions
    isnull("isnull"),
    isnotnull("isnotnull"),
    nvl("nvl"),
    assert_true("assert_true"),
    IF("if"),
    COALESCE("COALESCE"),
    nullif("nullif"),
    CASE("case"),
    //endregion

    //region 聚合函数
    AVG("AVG"), COUNT("COUNT"), MAX("MAX"), MIN("MIN"), STDDEV("STDDEV"), SUM("SUM","SUM([distinct] ?)"),
    variance("variance"),
    var_pop("var_pop"),
    var_samp("var_samp"),
    stddev_pop("stddev_pop"),
    stddev_samp("stddev_samp"),
    collect_set("collect_set"),
    collect_list("collect_list"),
    // 两个参数
    covar_pop("covar_pop"),
    covar_samp("covar_samp"),
    corr("corr"),
    // 一个参数+ 一个小数
    percentile("percentile"),
    percentile_approx("percentile_approx"),
    // 一个参数+一个int
    histogram_numeric("histogram_numeric"),
    ntile("ntile"),
    // 全匹配模式
    regr_avgx("regr_avgx"),
    regr_avgy("regr_avgy"),
    regr_count("regr_count"),
    regr_intercept("regr_intercept"),
    regr_r2("regr_r2"),
    regr_slope("regr_slope"),
    regr_sxx("regr_sxx"),
    regr_sxy("regr_sxy"),
    regr_syy("regr_syy"),
    //endregion

    //region String Functions
    // 常量
    space("space"),
    // 一个参数
    BIT_LENGTH("BIT_LENGTH","BIT_LENGTH(?)"),
    CHAR_LENGTH("CHAR_LENGTH","CHAR_LENGTH(?)"),
    ASCII("ascii"),
    BTRIM("BTRIM","BTRIM(?)"),
    CHR("CHR","CHR(?)"),
    base64("base64"),
    character_length("character_length"),
    LENGTH("length"),
    LOWER("lower"),
    LCASE("lcase"),
    UPPER("upper"),
    ucase("ucase"),
    ltrim("ltrim"),
    rtrim("rtrim"),
    trim("trim"),
    OCTET_LENGTH("octet_length"),
    OVERLAY("OVERLAY","overlay(? placing string from 0)"),
    POSITION("POSITION","position(substring in ?)"),
    quote("quote"),
    reverse("reverse"),
    sentences("sentences"),
    unbase64("unbase64"),
    INITCAP("initcap"),
    soundex("soundex"),
    // 两个参数
    concat("concat"),
    CONCATA("CONCATA","CONCATA(?,?)"),
    levenshtein("levenshtein"),
    // 参数 + 常量
    substr("substr"),
    SUBSTRING("substring"),
    decode("decode"),
    elt("elt"),
    encode("encode"),
    field("field"),
    find_in_set("find_in_set"),
    format_number("format_number"),
    in_file("in_file"),
    instr("instr"),
    locate("locate"),
    lpad("lpad"),
    rpad("rpad"),
    parse_url("parse_url"),
    printf("printf"),
    regexp_extract("regexp_extract"),
    regexp_replace("regexp_replace"),
    repeat("repeat"),
    replace("replace"),
    split("split"),
    SPLIT_PART("SPLIT_PART","split_part(?, delimiter, 1)"),
    STRPOS("STRPOS","strpos(string, substring)"),
    TO_ASCII("TO_ASCII","TO_ASCII(?)"),
    TO_HEX("TO_HEX","TO_HEX(?)"),
    str_to_map("str_to_map"),
    substring_index("substring_index"),
    TRANSLATE("translate"),

    // 全模式
    context_ngrams("context_ngrams"),
    concat_ws("concat_ws"),
    get_json_object("get_json_object"),
    ngrams("ngrams"),
    //endregion

    //region Data Masking Functions
    mask("mask"),
    mask_first_n("mask_first_n"),
    mask_last_n("mask_last_n"),
    mask_show_first_n("mask_show_first_n"),
    mask_show_last_n("mask_show_last_n"),
    mask_hash("mask_hash"),
    //endregion

    //region Misc. Functions
    // 无参
    current_user("current_user"),
    logged_in_user("logged_in_user"),
    current_database("current_database"),
    version("version"),
    surrogate_key("surrogate_key"),
    // 一个参数
    hash("hash"),
    MD5("md5"),
    sha1("sha1"),
    sha("sha"),
    crc32("crc32"),
    // 一个参数+ int
    sha2("sha2"),
    // 一个参数+ string
    aes_encrypt("aes_encrypt"),
    aes_decrypt("aes_decrypt"),
    // 函数
    java_method("java_method"),
    reflect("reflect"),
    //endregion

    explode("explode"),
    posexplode("posexplode"),
    inline("inline"),
    stack("stack"),
    json_tuple("json_tuple"),
    parse_url_tuple("parse_url_tuple")
    ;

    SQLOperator(String name) {
        this(name, 999);
    }
    SQLOperator(String name, String usage) {
        this(name, 999, usage);
    }
    SQLOperator(String name, int priority) {
        this(name, priority, "");
    }
    SQLOperator(String name, int priority, String usage) {
        this.name = name;
        this.nameLCase = name.toLowerCase();
        this.priority = priority;
        this.usage = usage;
    }

    public final String name;
    public final String nameLCase;
    public final int priority;
    /**
     * 简化签名的获取，不在使用标准的SQL语法
     */
    public final String usage;

    public boolean isRelational() {

        switch(this) {
            case Equality:
            case Like:
            case SoudsLike:
            case NotEqual:
            case GreaterThan:
            case GreaterThanOrEqual:
            case LessThan:
            case LessThanOrEqual:
            case LessThanOrGreater:
            case NotLike:
            case NotLessThan:
            case NotGreaterThan:
            case RLike:
            case NotRLike:
            case RegExp:
            case NotRegExp:
            case Is:
            case IsNot:
                return true;
            default:
                return false;
        }
    }

    public boolean isLogical() {
        return this == BooleanAnd || this == BooleanOr || this == BooleanXor;
    }

    public boolean isArithmetic() {
        switch(this) {
            case ADD:
            case Subtract:
            case Multiply:
            case Divide:
            case DIV:
            case Modulus:
            case MOD:
                return true;
            default:
                return false;
        }
    }
    public boolean isDataMaskingFunctions() {
        switch(this) {
            case mask:
            case mask_first_n:
            case mask_last_n:
            case mask_show_first_n:
            case mask_show_last_n:
            case mask_hash:
                return true;
            default:
                return false;
        }
    }

    public boolean isMiscFunctions() {
        switch(this) {
            case current_user:
            case logged_in_user:
            case current_database:
            case version:
            case surrogate_key:
            // 一个参数
            case hash:
            case MD5:
            case sha1:
            case sha:
            case crc32:
            // 一个参数+ int
            case sha2:
            // 一个参数+ string
            case aes_encrypt:
            case aes_decrypt:
            // 函数
            case java_method:
            case reflect:
                return true;
            default:
                return false;
        }
    }

    public boolean isStringFunction() {
        switch(this) {
            case space:
            case ASCII:
            case BTRIM:
            case BIT_LENGTH:
            case CHAR_LENGTH:
            case CHR:
            case base64:
            case character_length:
            case LENGTH:
            case LOWER:
            case LCASE:
            case UPPER:
            case ucase:
            case ltrim:
            case rtrim:
            case trim:
            case OCTET_LENGTH:
            case OVERLAY:
            case POSITION:
            case quote:
            case reverse:
            case sentences:
            case unbase64:
            case INITCAP:
            case soundex:
            case concat:
            case CONCATA:
            case levenshtein:
            case decode:
            case elt:
            case encode:
            case field:
            case find_in_set:
            case format_number:
            case in_file:
            case instr:
            case locate:
            case lpad:
            case rpad:
            case parse_url:
            case printf:
            case regexp_extract:
            case regexp_replace:
            case repeat:
            case replace:
            case split:
            case SPLIT_PART:
            case STRPOS:
            case TO_ASCII:
            case TO_HEX:
            case str_to_map:
            case substr:
            case SUBSTRING:
            case substring_index:
            case TRANSLATE:
            case context_ngrams:
            case concat_ws:
            case get_json_object:
            case ngrams:
                return true;
            default:
                return false;
        }
    }
    public boolean isCollectionFunction() {
        switch(this) {
            case size:
            case        map_keys:
            case        map_values:
            case        array_contains:
            case         sort_array:
                return true;
            default:
                return false;
        }
    }
    public boolean isTypeConversionFunction() {
        switch(this) {
            case cast:
            case binary:
            case asciistr:
            case bin_to_num:
            case CONVERT:
            case TO_CHAR:
            case TO_NUMBER:
            case TO_TIMESTAMP:
                return true;
            default:
                return false;
        }
    }
    public boolean isDateFunction() {
        switch(this) {
            case from_unixtime:
            case         unix_timestamp:
            case TO_DATE:
            case         year:
            case        quarter:
            case        month:
            case         day:
            case        dayofmonth:
            case        hour:
            case       minute:
            case        second:
            case        weekofyear:
            case         extract:
            case        datediff:
            case        date_add:
            case        date_sub:
            case        from_utc_timestamp:
            case        to_utc_timestamp:
            case        current_date:
            case        current_timestamp:
            case       add_months:
            case         last_day:
            case        next_day:
            case        TRUNC:
            case        months_between:
            case        date_format:
            case SYSDATE:
            case SYSTIMESTAMP:
            case DBTIMEZONE:
            case ROUND:
                return true;
            default:
                return false;
        }
    }
    public boolean isMathematicalFunction() {
        switch(this) {
            case bround:
            case round:
            case FLOOR:
            case CEIL:
            case        ceiling:
            case        rand:
            case EXP:
            case LN:
            case        log10:
            case        log2:
            case LOG:
            case        pow:
            case SQRT:
            case       bin:
            case       hex:
            case       unhex:
            case        conv:
            case ABS:
            case        pmod:
            case SIN:
            case       asin:
            case COS:
            case COT:
            case ACOS:
            case TAN:
            case       atan:
            case       ATAN2:
            case DEGREES:
            case RADIANS:
            case RANDOM:
            case       positive:
            case       negative:
            case trunc:
            case SIGN:
            case        e:
            case PI:
            case POWER:
            case        factorial:
            case CBRT:
            case       shiftleft:
            case       shiftright:
            case        shiftrightunsigned:
            case      greatest:
            case      least:
            case       width_bucket:
                return true;
            default:
                return false;
        }
    }
    public boolean isAggregateFunction() {
        switch(this) {
            case AVG:
            case COUNT:
            case MAX:
            case MIN:
            case STDDEV:
            case SUM:
            case variance:
            case var_pop:
            case var_samp:
            case stddev_pop:
            case stddev_samp:
            case collect_set:
            case collect_list:
            // 两个参数
            case covar_pop:
            case covar_samp:
            case corr:
            // 一个参数+ 一个小数
            case percentile:
            case percentile_approx:
            // 一个参数+一个int
            case histogram_numeric:
            case ntile:
            // 全匹配模式
            case regr_avgx:
            case regr_avgy:
            case regr_count:
            case regr_intercept:
            case regr_r2:
            case regr_slope:
            case regr_sxx:
            case regr_sxy:
            case regr_syy:
                return true;
            default:
                return false;
        }
    }
    public boolean isConditionalFunction() {
        switch (this) {
            case  isnull:
            case  isnotnull:
            case  nvl:
            case  assert_true:
            case  IF:
            case  COALESCE:
            case  nullif:
            case CASE:
                return true;
            default:
                return false;
        }
    }

    public static SQLOperator of(String name) {
        for (SQLOperator value : SQLOperator.values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }

        return null;
    }


    public boolean isUDTF() {
        switch (this) {
            case explode:
            case posexplode:
            case inline:
            case stack:
            case json_tuple:
            case parse_url_tuple:
                return true;
            default:
                return false;
        }
    }

    public boolean isComplexTypeConstructor() {
        switch (this) {
            case Map:
            case Struct:
            case Array:
            case Named_struct:
            case Create_union:
                return true;
            default:
                return false;
        }
    }
}
