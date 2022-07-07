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
    Mod("MOD", 60),
    Add("+", 70),
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
    bround("bround"),
    floor("floor"),
    ceil("ceil"),
    ceiling("ceiling"),
    rand("rand"),
    exp("exp"),
    ln("ln"),
    log10("log10"),
    log2("log2"),
    log("log"),
    pow("pow"),
    sqrt("sqrt"),
    bin("bin"),
    hex("hex"),
    unhex("unhex"),
    conv("conv"),
    abs("abs"),
    pmod("pmod"),
    sin("sin"),
    asin("asin"),
    cos("cos"),
    acos("acos"),
    tan("tan"),
    atan("atan"),
    degrees("degrees"),
    radians("radians"),
    positive("positive"),
    negative("negative"),
    sign("sign"),
    e("e"),
    pi("pi"),
    factorial("factorial"),
    cbrt("cbrt"),
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
    //endregion

    //region Date Functions
    from_unixtime("from_unixtime"),
    unix_timestamp("unix_timestamp"),
    to_date("to_date"),
    year("year"),
    quarter("quarter"),
    month("month"),
    day("day"),
    dayofmonth("dayofmonth"),
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
    trunc("trunc"),
    months_between("months_between"),
    date_format("date_format"),
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
    AVG("AVG"), COUNT("COUNT"), MAX("MAX"), MIN("MIN"), STDDEV("STDDEV"), SUM("SUM"),
    //endregion


    //region String Functions
    substr("substr"),
    substring("substring");
    //endregion


    SQLOperator(String name) {
        this(name, 999);
    }

    SQLOperator(String name, int priority) {
        this.name = name;
        this.nameLCase = name.toLowerCase();
        this.priority = priority;
    }

    public final String name;
    public final String nameLCase;
    public final int priority;

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
            case Add:
            case Subtract:
            case Multiply:
            case Divide:
            case DIV:
            case Modulus:
            case Mod:
                return true;
            default:
                return false;
        }
    }

    public boolean isStringFunction() {
        switch(this) {
            case substr:
            case substring:
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
            case        binary:
                return true;
            default:
                return false;
        }
    }
    public boolean isDateFunction() {
        switch(this) {
            case from_unixtime:
            case         unix_timestamp:
            case         to_date:
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
            case        trunc:
            case        months_between:
            case        date_format:
                return true;
            default:
                return false;
        }
    }
    public boolean isMathematicalFunction() {
        switch(this) {
            case bround:
            case floor:
            case        ceil:
            case        ceiling:
            case        rand:
            case        exp:
            case        ln:
            case        log10:
            case        log2:
            case        log:
            case        pow:
            case        sqrt:
            case       bin:
            case       hex:
            case       unhex:
            case        conv:
            case        abs:
            case        pmod:
            case        sin:
            case       asin:
            case        cos:
            case        acos:
            case       tan:
            case       atan:
            case       degrees:
            case      radians:
            case       positive:
            case       negative:
            case        sign:
            case        e:
            case        pi:
            case        factorial:
            case        cbrt:
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


}
