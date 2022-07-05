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

    public static SQLOperator of(String name) {
        for (SQLOperator value : SQLOperator.values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }

        return null;
    }

}
