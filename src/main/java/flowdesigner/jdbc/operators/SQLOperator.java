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
    XOR("XOR", "xor(?, ?)"),
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
    EWAH_BITMAP_AND("ewah_bitmap_and","ewah_bitmap_and(b1, b2)"),
    EWAH_BITMAP_OR("ewah_bitmap_or","ewah_bitmap_or(b1, b2)"),
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
    SoudsLike("SOUNDS LIKE",110,"? SOUNDS LIKE expr2"),
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
    BETWEEN("between", "? [NOT] BETWEEN b AND c"),
    IN("in", "? IN (val1, val2, ...)"),
    NOT_IN("not in", "? NOT IN (value,...)"),
    //endregion

    //region Built-In Function and Operator
    MBRTOUCHES("mbrtouches","MBRTouches(g1, g2)"),
    FORMAT_BYTES("format_bytes","FORMAT_BYTES(count)"),
    ST_SRID("st_srid","st_srid(?)"),
    IS_IPV_4_COMPAT("is_ipv4_compat","is_ipv4_compat(?)"),
    NORMALIZE_STATEMENT("normalize_statement","normalize_statement(?)"),
    MBRCOVERS("mbrcovers","MBRCovers(g1, g2)"),
    READ_FIREWALL_USERS("read_firewall_users","read_firewall_users(user, mode)"),
    ST_UNION("st_union","ST_Union(g1, g2)"),

    st_pointatdistance("st_pointatdistance","st_pointatdistance()"),
    st_mpointfromtext("st_mpointfromtext","st_mpointfromtext()"),
    st_collect("st_collect","st_collect()"),
    inet6_aton("inet6_aton","inet6_aton()"),
    st_isclosed("st_isclosed","st_isclosed()"),


    st_intersects("st_intersects","st_intersects()"),
    mbrcoveredby("mbrcoveredby","mbrcoveredby()"),
    st_numgeometries("st_numgeometries","st_numgeometries()"),
    st_geomcollfromwkb("st_geomcollfromwkb","st_geomcollfromwkb()"),
    st_asbinary("st_asbinary","st_asbinary()"),
    st_pointfromgeohash("st_pointfromgeohash","st_pointfromgeohash()"),
    json_pretty("json_pretty","json_pretty()"),
    master_pos_wait("master_pos_wait","master_pos_wait()"),
    st_endpoint("st_endpoint","st_endpoint()"),
    st_symdifference("st_symdifference","st_symdifference()"),
    multipoint("multipoint","multipoint()"),
    st_envelope("st_envelope","st_envelope()"),
    st_touches("st_touches","st_touches()"),
    regexp_substr("regexp_substr","regexp_substr()"),
    mbrcontains("mbrcontains","mbrcontains()"),
    mbrwithin("mbrwithin","mbrwithin()"),
    internal_check_time("internal_check_time","internal_check_time()"),
    st_isvalid("st_isvalid","st_isvalid()"),
    st_longfromgeohash("st_longfromgeohash","st_longfromgeohash()"),
    st_difference("st_difference","st_difference()"),
    current_role("current_role","current_role()"),
    ps_current_thread_id("ps_current_thread_id","ps_current_thread_id()"),
    make_set("make_set","make_set()"),
    json_schema_validation_report("json_schema_validation_report","json_schema_validation_report()"),
    timestampadd("timestampadd","timestampadd()"),
    st_issimple("st_issimple","st_issimple()"),
    from_base64("from_base64","from_base64()"),
    bin_to_uuid("bin_to_uuid","bin_to_uuid()"),
    set_firewall_mode("set_firewall_mode","set_firewall_mode(user, mode)"),
    oct("oct","oct()"),
    json_merge("json_merge","json_merge()"),
    export_set("export_set","export_set()"),
    multipolygon("multipolygon","multipolygon()"),
    st_linefromtext("st_linefromtext","st_linefromtext()"),
    internal_data_free("internal_data_free","internal_data_free()"),
    internal_max_data_length("internal_max_data_length","internal_max_data_length()"),
    interval("interval","interval()"),
    ISNOTNULL("IS NOT NULL","? IS NOT NULL"),
    charset("charset","charset()"),
    can_access_user("can_access_user","can_access_user()"),
    json_array_insert("json_array_insert","json_array_insert()"),
    st_makeenvelope("st_makeenvelope","st_makeenvelope()"),
    st_transform("st_transform","st_transform()"),
    get_format("get_format","get_format()"),
    uncompressed_length("uncompressed_length","uncompressed_length()"),
    utc_timestamp("utc_timestamp","utc_timestamp()"),
    st_buffer_strategy("st_buffer_strategy","st_buffer_strategy()"),
    st_intersection("st_intersection","st_intersection()"),
    st_area("st_area","st_area()"),
    polygon("polygon","polygon()"),
    read_firewall_whitelist("read_firewall_whitelist","read_firewall_whitelist(user, rule)"),

    st_validate("st_validate","st_validate()"),
    json_length("json_length","json_length()"),
    st_linefromwkb("st_linefromwkb","st_linefromwkb()"),
    internal_table_rows("internal_table_rows","internal_table_rows()"),
    row_count("row_count","row_count()"),
    uuid_to_bin("uuid_to_bin","uuid_to_bin()"),
    mbrdisjoint("mbrdisjoint","mbrdisjoint()"),
    json_keys("json_keys","json_keys()"),
    st_interiorringn("st_interiorringn","st_interiorringn()"),
    st_contains("st_contains","st_contains()"),

    is_uuid("is_uuid","is_uuid()"),
    internal_index_length("internal_index_length","internal_index_length()"),
    validate_password_strength("validate_password_strength","validate_password_strength()"),

    st_astext("st_astext","st_astext()"),
    json_storage_free("json_storage_free","json_storage_free()"),
    is_ipv4_mapped("is_ipv4_mapped","is_ipv4_mapped()"),
    icu_version("icu_version","icu_version()"),
    st_overlaps("st_overlaps","st_overlaps()"),
    mbrequals("mbrequals","mbrequals()"),
    st_asgeojson("st_asgeojson","st_asgeojson()"),
    st_hausdorffdistance("st_hausdorffdistance","st_hausdorffdistance()"),
    internal_is_enabled_role("internal_is_enabled_role","internal_is_enabled_role()"),
    utc_date("utc_date","utc_date()"),
    internal_dd_char_length("internal_dd_char_length","internal_dd_char_length()"),
    st_numpoints("st_numpoints","st_numpoints()"),
    mbroverlaps("mbroverlaps","mbroverlaps()"),
    st_polyfromtext("st_polyfromtext","st_polyfromtext()"),
    st_pointfromwkb("st_pointfromwkb","st_pointfromwkb()"),
    st_geomfromtext("st_geomfromtext","st_geomfromtext()"),
    internal_checksum("internal_checksum","internal_checksum()"),
    json_array("json_array","json_array()"),
    curtime("curtime","curtime()"),
    st_mpointfromwkb("st_mpointfromwkb","st_mpointfromwkb()"),
    st_mpolyfromtext("st_mpolyfromtext","st_mpolyfromtext()"),
    st_x("st_x","st_x()"),
    st_y("st_y","st_y()"),
    dayofyear("dayofyear","dayofyear()"),
    json_depth("json_depth","json_depth()"),
    st_geometrytype("st_geometrytype","st_geometrytype()"),
    roles_graphml("roles_graphml","roles_graphml()"),
    mid("mid","mid()"),
    st_polyfromwkb("st_polyfromwkb","st_polyfromwkb()"),
    st_distance_sphere("st_distance_sphere","st_distance_sphere()"),
    DEFAULT("default","default()"),
    name_const("name_const","name_const()"),
    st_swapxy("st_swapxy","st_swapxy()"),
    current_time("current_time","current_time()"),
    json_remove("json_remove","json_remove()"),
    json_extract("json_extract","json_extract()"),
    st_startpoint("st_startpoint","st_startpoint()"),
    internal_auto_increment("internal_auto_increment","internal_auto_increment()"),
    regexp_instr("regexp_instr","regexp_instr()"),
    st_isempty("st_isempty","st_isempty()"),

    linestring("linestring","linestring()"),
    st_lineinterpolatepoints("st_lineinterpolatepoints","st_lineinterpolatepoints()"),
    between_and("between ... and ...","? BETWEEN min AND max"),
    NOT_BETWEEN_AND("not between ... and ...","? NOT BETWEEN min AND max"),
    st_distance("st_distance","st_distance()"),
    st_longitude("st_longitude","st_longitude()"),
    coercibility("coercibility","coercibility()"),
    json_valid("json_valid","json_valid()"),
    can_access_table("can_access_table","can_access_table()"),
    ISNULL("isnull","isnull(?),"),
    ps_thread_id("ps_thread_id","ps_thread_id()"),
    source_pos_wait("source_pos_wait","source_pos_wait()"),
    internal_data_length("internal_data_length","internal_data_length()"),
    st_geomfromgeojson("st_geomfromgeojson","st_geomfromgeojson()"),
    json_merge_patch("json_merge_patch","json_merge_patch()"),
    st_length("st_length","st_length()"),
    json_set("json_set","json_set()"),

    st_disjoint("st_disjoint","st_disjoint()"),
    st_frechetdistance("st_frechetdistance","st_frechetdistance()"),
    weight_string("weight_string","weight_string()"),
    curdate("curdate","curdate()"),
    json_contains("json_contains","json_contains()"),
    internal_keys_disabled("internal_keys_disabled","internal_keys_disabled()"),
    json_table("json_table","json_table()"),
    st_exteriorring("st_exteriorring","st_exteriorring()"),
    now("now","now()"),
    internal_get_username("internal_get_username","internal_get_username()"),

    st_pointfromtext("st_pointfromtext","st_pointfromtext()"),
    ord("ord","ord()"),
    internal_index_column_cardinality("internal_index_column_cardinality","internal_index_column_cardinality()"),
    memberof("member of","? MEMBER OF(json_array)"),
    gtid_subset("gtid_subset","gtid_subset()"),
    gtid_subtract("gtid_subtract","gtid_subtract()"),
    internal_get_view_warning_or_error("internal_get_view_warning_or_error","internal_get_view_warning_or_error()"),
    json_object("json_object","json_object()"),
    get_dd_column_privileges("get_dd_column_privileges","get_dd_column_privileges()"),
    st_convexhull("st_convexhull","st_convexhull()"),
    from_days("from_days","from_days()"),
    json_replace("json_replace","json_replace()"),
    json_insert("json_insert","json_insert()"),
    internal_avg_row_length("internal_avg_row_length","internal_avg_row_length()"),
    any_value("any_value","any_value()"),
    st_latitude("st_latitude","st_latitude()"),
    inet_aton("inet_aton","inet_aton()"),
    compress("compress","compress()"),
    json_quote("json_quote","json_quote()"),
    multilinestring("multilinestring","multilinestring()"),
    benchmark("benchmark","benchmark()"),
    get_dd_index_sub_part_length("get_dd_index_sub_part_length","get_dd_index_sub_part_length()"),
    geometrycollection("geometrycollection","geometrycollection()"),
    can_access_database("can_access_database","can_access_database()"),
    st_centroid("st_centroid","st_centroid()"),
    json_type("json_type","json_type()"),
    st_lineinterpolatepoint("st_lineinterpolatepoint","st_lineinterpolatepoint()"),
    st_within("st_within","st_within()"),
    geomcollection("geomcollection","geomcollection()"),
    inet6_ntoa("inet6_ntoa","inet6_ntoa()"),
    json_storage_size("json_storage_size","json_storage_size()"),
    json_search("json_search","json_search()"),
    statement_digest_text("statement_digest_text","statement_digest_text()"),
    mbrintersects("mbrintersects","mbrintersects()"),
    st_dimension("st_dimension","st_dimension()"),
    st_crosses("st_crosses","st_crosses()"),
    json_arrayagg("json_arrayagg","json_arrayagg()"),
    st_geomcollfromtext("st_geomcollfromtext","st_geomcollfromtext()"),
    st_mlinefromtext("st_mlinefromtext","st_mlinefromtext()"),
    format_pico_time("format_pico_time","format_pico_time()"),
    st_mpolyfromwkb("st_mpolyfromwkb","st_mpolyfromwkb()"),
    st_geometryn("st_geometryn","st_geometryn()"),
    st_simplify("st_simplify","st_simplify()"),
    can_access_column("can_access_column","can_access_column()"),
    st_geomfromwkb("st_geomfromwkb","st_geomfromwkb()"),
    json_contains_path("json_contains_path","json_contains_path()"),

    notin("notin","notin()"),
    nth_value("nth_value","nth_value()"),
    collation("collation","collation()"),
    st_numinteriorring("st_numinteriorring","st_numinteriorring()"),
    random_bytes("random_bytes","random_bytes()"),
    strcmp("strcmp","strcmp()"),
    st_equals("st_equals","st_equals()"),
    wait_until_sql_thread_after_gtids("wait_until_sql_thread_after_gtids","wait_until_sql_thread_after_gtids()"),
    regexp_like("regexp_like","regexp_like()"),
    st_latfromgeohash("st_latfromgeohash","st_latfromgeohash()"),
    found_rows("found_rows","found_rows()"),
    wait_for_executed_gtid_set("wait_for_executed_gtid_set","wait_for_executed_gtid_set()"),
    uncompress("uncompress","uncompress()"),
    MYSQL_FIREWALL_FLUSH_STATUS("mysql_firewall_flush_status","mysql_firewall_flush_status()"),
    json_objectagg("json_objectagg","json_objectagg()"),
    internal_is_mandatory_role("internal_is_mandatory_role","internal_is_mandatory_role()"),
    values("values","values()"),
    is_ipv6("is_ipv6","is_ipv6()"),
    is_ipv4("is_ipv4","is_ipv4()"),
    point("point","point()"),
    json_array_append("json_array_append","json_array_append()"),
    st_buffer("st_buffer","st_buffer()"),
    statement_digest("statement_digest","statement_digest()"),
    st_geohash("st_geohash","st_geohash()"),
    st_pointn("st_pointn","st_pointn()"),

    get_dd_create_options("get_dd_create_options","get_dd_create_options()"),
    match("match","match()"),
    inet_ntoa("inet_ntoa","inet_ntoa()"),

    json_merge_preserve("json_merge_preserve","json_merge_preserve()"),
    load_file("load_file","load_file()"),
    st_mlinefromwkb("st_mlinefromwkb","st_mlinefromwkb()"),
    //endregion

    //region ComplexTypeConstructor
    Map("map"),
    Struct("struct"),
    Named_struct("named_struct"),
    Array("array"),
    Create_union("create_union"),
    //endregion
    //region MathematicalFunction
    STD("std","std(?)"),
    trunc("trunc","trunc(?)"),
    bround("bround"),
    FLOOR("floor"),
    CEIL("ceil"),
    CEILING("ceiling"),
    rand("rand"),
    EXP("exp"),
    LN("ln"),
    LOG10("log10"),
    LOG2("log2"),
    LOG("log"),
    POW("pow"),
    SQRT("sqrt"),
    bin("bin"),
    hex("hex"),
    unhex("unhex"),
    conv("conv"),
    ABS("abs"),
    ABSVAL("ABSVAL","ABSVAL(?)"),
    pmod("pmod"),
    SIN("sin"),
    asin("asin(?)"),
    COSH("COSH","COSH(?)"),
    COS("cos"),
    COT("COT","COT(?)"),
    SINH("SINH","SINH(?)"),
    DIGITS("DIGITS","DIGITS(?)"),
    MULTIPLY_ALT("MULTIPLY_ALT","MULTIPLY_ALT(?,?)"),
    ACOS("acos"),
    ASIN("ASIN","ASIN(?)"),
    TAN("tan"),
    TANH("tanh(?)"),
    ATAN("atan"),
    ATAN2("ATAN2","atan2(?, ?)"),
    BIT_COUNT("BIT_COUNT","BIT_COUNT(?)"),
    EVEN("EVEN","EVEN(?)"),
    GAMMA("GAMMA","GAMMA(?)"),

    DEGREES("degrees"),
    RADIANS("radians"),
    SQUARE("SQUARE","SQUARE(?)"),
    RANDOM("RANDOM","RANDOM()"),
    positive("positive"),
    negative("negative"),
    SIGN("sign"),
    e("e"),
    PI("pi"),
    POWER("POWER","POWER(?,n)"),
    FACTORIAL("factorial"),
    CBRT("cbrt"),
    shiftleft("shiftleft"),
    shiftright("shiftright"),
    shiftrightunsigned("shiftrightunsigned"),
    GREATEST("greatest"),
    LEAST("least","least(x1, x2, ...)"),
    LGAMMA("LGAMMA","lgamma(?)"),
    NEXTAFTER("NEXTAFTER","nextafter(?, y)"),
    SETSEED("SETSEED","SETSEED(?)"),
    width_bucket("width_bucket"),

    //endregion

    //region CollectionFunction
    size("size"),
    map_keys("map_keys"),
    map_values("map_values"),
    array_contains("array_contains"),
    sort_array("sort_array"),
    INDEX("index","index(a, n)"),
    GET_SPLITS("get_splits","get_splits(string,int)"),
    //endregion

    //region Type Conversion Functions
    binary("binary"),
    USING_BINARY("USING BINARY","? USING BINARY"),
    AS_BINARY("AS BINARY","? AS BINARY"),
    cast("cast"),
    asciistr("asciistr"),
    bin_to_num("bin_to_num"),
    CONVERT("convert"),
    convert_tz("convert_tz","convert_tz()"),
    TO_CHAR("to_char", "to_char(?)"),
    TO_NUMBER("to_number","to_number(?)"),
    TO_TIMESTAMP("TO_TIMESTAMP","TO_TIMESTAMP(?)"),
    TO_BASE_64("to_base64","to_base64(?)"),
    SMALLINT("SMALLINT","SMALLINT(?)"),
    INTEGER("INTEGER","INTEGER(?)"),
    BIGINT("BIGINT","BIGINT(?)"),
    DECIMAL("DECIMAL","DECIMAL(?)"),
    REAL("REAL","REAL(?)"),
    DOUBLE("DOUBLE","DOUBLE(?)"),
    FLOAT("FLOAT","FLOAT(?)"),
    CHAR("CHAR","CHAR(?)"),
    VARCHAR("VARCHAR","VARCHAR(?)"),
    VARCHAR_FORMAT_BIT("VARCHAR_FORMAT_BIT","VARCHAR_FORMAT_BIT(?)"),
    VARCHAR_FORMAT("VARCHAR_FORMAT","VARCHAR_FORMAT(?,'YYYY-MM-DD HH24:MI:SS')"),
    VARCHAR_BIT_FORMAT("VARCHAR_BIT_FORMAT","VARCHAR_BIT_FORMAT(?)"),
    LONG_VARCHAR("LONG_VARCHAR","LONG_VARCHAR(?)"),
    CLOB("CLOB","CLOB(?)"),
    GRAPHIC("GRAPHIC","GRAPHIC(?)"),
    VARGRAPHIC("VARGRAPHIC","VARGRAPHIC(?)"),
    LONG_VARGRAPHIC("LONG_VARGRAPHIC","LONG_VARGRAPHIC(?)"),
    DBCLOB("DBCLOB","DBCLOB(?)"),
    BLOB("BLOB","BLOB(?)"),
    DATE("DATE","DATE(?)"),
    TIME("TIME","TIME(?)"),
    TIME_FORMAT("TIME_FORMAT","TIME_FORMAT(?,format)"),
    TIME_TO_SEC("TIME_TO_SEC","TIME_TO_SEC(?)"),

    //endregion

    //region Date Functions
    INTERNAL_INTERVAL("internal_interval","internal_interval(intervalType,intervalArg)"),
    TIMEDIFF("TIMEDIFF","TIMEDIFF(?,?)"),
    TIMESTAMP("TIMESTAMP","TIMESTAMP(?)"),
    from_unixtime("from_unixtime"),
    unix_timestamp("unix_timestamp"),
    UTC_TIME("utc_time","utc_time()"),
    TO_UNIX_TIMESTAMP("to_unix_timestamp","to_unix_timestamp(?[, pattern])"),
    TO_DATE("to_date"),
    TO_SECONDS("to_seconds","to_seconds(?)"),
    YEAR("year"),
    FLOOR_YEAR("floor_year","floor_year(?)"),
    FLOOR_MONTH("floor_month","floor_month(?)"),
    FLOOR_HOUR("floor_hour","floor_hour(?)"),
    FLOOR_WEEK("floor_week","floor_week(?)"),
    FLOOR_DAY("floor_day","floor_day(?)"),
    FLOOR_QUARTER("floor_quarter","floor_quarter(?)"),
    FLOOR_MINUTE("floor_minute","floor_minute(?)"),
    FLOOR_SECOND("floor_second","floor_second(?)"),
    YEARWEEK("YEARWEEK","YEARWEEK(?)"),
    quarter("quarter"),
    MONTH("month"),
    DAY("day"),
    hour("hour"),
    minute("minute"),
    second("second"),
    SEC_TO_TIME("SEC_TO_TIME"),
    STR_TO_DATE("STR_TO_DATE"),
    SLEEP("sleep","SLEEP(duration)"),
    MICROSECOND("MICROSECOND"),
    MONTHNAME("MONTHNAME","MONTHNAME(?)"),
    PERIOD_ADD("PERIOD_ADD","PERIOD_ADD(period, number)"),
    PERIOD_DIFF("PERIOD_DIFF","PERIOD_DIFF(period, number)"),

    DAYNAME("DAYNAME","DAYNAME(?)"),
    WEEK("WEEK","WEEK(?)"),
    WEEKDAY("WEEKDAY","WEEKDAY(?)"),
    WEEK_ISO("WEEK_ISO","WEEK_ISO(?)"),
    TIMESTAMP_ISO("TIMESTAMP_ISO","TIMESTAMP_ISO(?)"),
    WEEKOFYEAR("weekofyear"),
    dayofmonth("dayofmonth"),
    DAYOFWEEK("DAYOFWEEK", "DAYOFWEEK(?)"),
    SYSDATETIME("SYSDATETIME","SYSDATETIME()"),
    extract("extract"),
    DATEDIFF("datediff"),
    TIMESTAMPDIFF("TIMESTAMPDIFF","TIMESTAMPDIFF(?,?)"),
    TO_DAYS("TO_DAYS","TO_DAYS(?)"),
    DATEFROMPARTS("DATEFROMPARTS","DATEFROMPARTS( year ,  month ,  day )"),
    TIMESTAMP_FORMAT("TIMESTAMP_FORMAT","TIMESTAMP_FORMAT(?,'yyyy-mm-dd')"),
    DATENAME("DATENAME","DATENAME(year, ?)"),
    DATEPART("","DATEPART(yyyy,?)"),
    date_add("date_add"),
    date_sub("date_sub"),
    SUBDATE("SUBDATE","SUBDATE(?,n)"),
    SUBTIME("SUBTIME","SUBTIME(?,n)"),
    ADDDATE("ADDDATE","ADDDATE(?,n)"),
    ADDTIME("ADDTIME","ADDDATE(?,n)"),
    DAYS("DAYS","DAYS(?)"),
    JULIAN_DAY("JULIAN_DAY","JULIAN_DAY(?)"),
    MIDNIGHT_SECONDS("MIDNIGHT_SECONDS","MIDNIGHT_SECONDS(?)"),
    from_utc_timestamp("from_utc_timestamp"),
    to_utc_timestamp("to_utc_timestamp"),
    current_date("current_date"),
    CURRENT_TIMESTAMP("current_timestamp"),
    DATEADD("DATEADD","DATEADD(day,2,?)"),
    add_months("add_months"),
    last_day("last_day"),
    LOCALTIME("LOCALTIME","LOCALTIME()"),
    LOCALTIMESTAMP("LOCALTIMESTAMP","LOCALTIMESTAMP()"),
    MAKEDATE("MAKEDATE","MAKEDATE(year, day-of-year)"),
    MAKETIME("MAKETIME","MAKETIME(hour, minute, second)"),
    next_day("next_day"),
    GETDATE("GETDATE","GETDATE()"),
    GETUTCDATE("GETUTCDATE","GETUTCDATE()"),
    ISDATE("ISDATE","ISDATE(?)"),
    TRUNC("TRUNC","TRUNC(?)"),
    TRUNCATE("TRUNCATE"),
    months_between("months_between"),
    date_format("date_format"),
    SYSDATE("SYSDATE","SYSDATE()"),
    SYSTIMESTAMP("SYSTIMESTAMP","SYSTIMESTAMP()"),
    DBTIMEZONE("DBTIMEZONE","DBTIMEZONE()"),
    ROUND("ROUND","ROUND(?)"),

    //endregion

    //region Conditional Functions
    ISFINITE("ISFINITE"),
    ISINF("ISINF"),
    ISNAN("ISNAN"),
    ISNUMERIC("ISNUMERIC","ISNUMERIC(?)"),
    nvl("nvl"),
    assert_true("assert_true"),
    COALESCE("COALESCE"),
    IF("if"),
    NULLIF("nullif"),
    IFNULL("IFNULL","IFNULL(v1,v2)"),
    CASE("case"),
    //endregion



    //region 聚合函数
    AVG("AVG"),
    COUNT("COUNT"),
    COUNT_DISTINCT("count(distinct)","count(distinct)(?)"),
    MAX("MAX"), MIN("MIN"), STDDEV("STDDEV"), SUM("SUM","SUM([distinct] ?)"),
    RANK("rank","RANK() over(partition by ?  order by ? desc)"),
    DENSE_RANK("dense_rank","dense_rank() over(partition by ?  order by ? desc)"),
    ROW_NUMBER("row_number","row_number() over(partition by ?  order by ? desc)"),
    PERCENT_RANK("percent_rank","percent_rank() over(partition by ?  order by ? desc)"),
    CUME_DIST("cume_dist","cume_dist() over(partition by ?  order by ? desc)"),
    LAST_VALUE("last_value","last_value() over(partition by ?  order by ? desc)"),
    FIRST_VALUE("first_value","first_value() over(partition by ?  order by ? desc)"),
    LEAD("lead","lead(?) over(partition by ?  order by ? desc)"),
    LAG("lag","lag(?) over(partition by ?  order by ? desc)"),
    GROUP_CONCAT("group_concat","group_concat(?)"),
    stddev_pop("stddev_pop"),
    stddev_samp("stddev_samp"),
    variance("variance"),
    VAR_POP("var_pop"),
    var_samp("var_samp"),
    BIT_OR("bit_or","bit_or(?)"),
    BIT_AND("bit_and","bit_and(?)"),
    BIT_XOR("bit_xor","bit_xor(?)"),
    collect_set("collect_set"),
    collect_list("collect_list"),
    COMPUTE_STATS("compute_stats","compute_stats(?)"),
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
    VALUE("VALUE","VALUE(EXPRESSION1,EXPRESSION2)"), //db2
    //endregion

    //region String Functions
    // 常量
    ARRAY_EXTRACT("array_extract","array_extract(?,1)"),
    ARRAY_SLICE("array_slice","array_slice(?, begin, end)"),
    CONTAINS("contains","contains(?, search_string)"),
    SPACE("space"),
    // 一个参数
    BIT_LENGTH("BIT_LENGTH","BIT_LENGTH(?)"),
    CHAR_LENGTH("CHAR_LENGTH","CHAR_LENGTH(?)"),
    ASCII("ascii"),
    STRIP("STRIP","STRIP(?)"),
    UNICODE("UNICODE","UNICODE(?)"),
    BTRIM("BTRIM","BTRIM(?)"),
    CHR("CHR","CHR(?)"),
    STR("STR","STR(?)"),
    STUFF("STUFF","STUFF( ? , start, length, new_string)"),
    CHARINDEX("CHARINDEX","CHARINDEX( substring ,   string ,  start )" ),
    DATALENGTH("DATALENGTH","DATALENGTH(?)"),
    DIFFERENCE("DIFFERENCE","DIFFERENCE(?,?)"),
    FORMAT("FORMAT","FORMAT( value, format)"),
    LEFT("LEFT","LEFT( ?,2)"),
    RIGHT("RIGHT","RIGHT(?,2)"),
    base64("base64","base64(?)"),
    character_length("character_length"),
    LENGTH("length"),
    LEN("LEN","LEN(?)"),
    NCHAR("NCHAR","NCHAR(2)"),
    PATINDEX("PATINDEX","PATINDEX('%pattern%', ?)"),
    QUOTENAME("QUOTENAME","QUOTENAME( ?, quote_char )"),
    REPLICATE("REPLICATE","REPLICATE( ?, 2 )"),
    LOWER("lower"),
    LCASE("lcase"),
    UPPER("upper"),
    ucase("ucase"),
    ltrim("ltrim"),
    RTRIM("rtrim"),
    trim("trim"),
    OCTET_LENGTH("octet_length"),
    OVERLAY("OVERLAY","overlay(? placing string from 0)"),
    POSITION("POSITION","position(substring in ?)"),
    quote("quote"),
    REVERSE("reverse"),
    sentences("sentences"),
    unbase64("unbase64"),
    INITCAP("initcap"),
    SOUNDEX("soundex"),
    // 两个参数
    concat("concat"),
    CONCATA("CONCATA","CONCATA(?,?)"),
    levenshtein("levenshtein"),
    // 参数 + 常量
    SUBSTR("substr"),
    SUBSTRING("substring"),
    decode("decode"),
    elt("elt"),
    encode("encode"),
    field("field"),
    find_in_set("find_in_set"),
    format_number("format_number"),
    in_file("in_file"),
    instr("instr"),
    LOCATE("locate"),
    lpad("lpad"),
    rpad("rpad"),
    parse_url("parse_url"),
    printf("printf"),
    regexp_extract("regexp_extract"),
    regexp_replace("regexp_replace"),
    REPEAT("repeat"),
    REPLACE("replace"),
    split("split"),
    SPLIT_PART("SPLIT_PART","split_part(?, delimiter, 1)"),
    STRPOS("STRPOS","strpos(string, substring)"),
    TO_ASCII("TO_ASCII","TO_ASCII(?)"),
    TO_HEX("TO_HEX","TO_HEX(?)"),
    str_to_map("str_to_map"),
    substring_index("substring_index"),
    TRANSLATE("translate"),
    INSERT("INSERT","INSERT(?,POS,SIZE,replaceExpr)"),
    POSSTR("POSSTR","POSSTR(?,EXP2)"),
    // 全模式
    context_ngrams("context_ngrams"),
    CONCAT_WS("concat_ws"),
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
    IIF("IIF","IIF( condition, value_if_true, value_if_false)"),
    logged_in_user("logged_in_user"),
    current_database("current_database"),

    version("version"),
    surrogate_key("surrogate_key"),
    // 一个参数
    hash("hash"),
    MD5("md5"),
    UUID("uuid","uuid()"),
    UUID_SHORT("uuid_short","uuid_short()"),
    sha1("sha1"),
    sha("sha"),
    crc32("crc32"),
    // 一个参数+ int
    sha2("sha2"),
    // 一个参数+ string

    GETHINT("GETHINT"),
    GENERATE_UNIQUE("GENERATE_UNIQUE","GENERATE_UNIQUE()"),
    // 函数
    java_method("java_method"),
    reflect("reflect"),
    REFLECT_2("reflect2","reflect2(?,method[,arg1[,arg2..]])"),
    GROUPING("grouping","grouping(a, b)"),
    EWAH_BITMAP("ewah_bitmap","ewah_bitmap(expr)"),
    EWAH_BITMAP_EMPTY("ewah_bitmap_empty","ewah_bitmap_empty(expr)"),
    //endregion

    //region InformationFunctions
    BENCHMARK     ("BENCHMARK","BENCHMARK(count,?)"),
    CHARSET       ("CHARSET","CHARSET(?)"),
    COERCIBILITY  ("COERCIBILITY","COERCIBILITY(?)"),
    COLLATION     ("COLLATION","COLLATION(?)"),
    CONNECTION_ID ("CONNECTION_ID","CONNECTION_ID()"),
    CURRENT_USER  ("CURRENT_USER","CURRENT_USER()"),
    DATABASE      ("DATABASE","DATABASE()"),
    FOUND_ROWS    ("FOUND_ROWS","FOUND_ROWS ()"),
    LAST_INSERT_ID("LAST_INSERT_ID","LAST_INSERT_ID()"),
    ROW_COUNT     ("ROW_COUNT","ROW_COUNT()"),
    SCHEMA        ("SCHEMA","SCHEMA()"),
    SESSION_USER  ("SESSION_USER","SESSION_USER ()"),
    SYSTEM_USER   ("SYSTEM_USER","SYSTEM_USER()"),
    USER          ("USER","USER()"),
    VERSION       ("VERSION","VERSION()"),
    //endregion

    //region ENCRYPTdecrypt
    aes_encrypt("aes_encrypt","aes_encrypt(?)"),
    aes_decrypt("aes_decrypt","aes_decrypt(?)"),
    DES_DECRYPT("DES_DECRYPT","DES_DECRYPT(?)"),
    DES_ENCRYPT("DES_ENCRYPT","DES_ENCRYPT(?)"),
    ASYMMETRIC_DECRYPT("asymmetric_decrypt","asymmetric_decrypt(algorithm, ?, priv_key_str)"),
    ASYMMETRIC_ENCRYPT("asymmetric_encrypt","asymmetric_encrypt(algorithm, ?, pub_key_str)"),
    ASYMMETRIC_DERIVE("asymmetric_derive","asymmetric_derive(pub_key_str, priv_key_str)"),
    ASYMMETRIC_SIGN("asymmetric_sign","asymmetric_sign(algorithm, ?, priv_key_str, digest_type)"),
    ASYMMETRIC_VERIFY("asymmetric_verify","asymmetric_verify(algorithm, ?, sig_str, pub_key_str, digest_type)"),
    CREATE_ASYMMETRIC_PRIV_KEY("create_asymmetric_priv_key","create_asymmetric_priv_key(algorithm, {key_len|dh_secret})"),
    CREATE_ASYMMETRIC_PUB_KEY("create_asymmetric_pub_key","create_asymmetric_pub_key(algorithm, priv_key_str)"),
    CREATE_DH_PARAMETERS("create_dh_parameters","create_dh_parameters(key_len)"),
    CREATE_DIGEST("create_digest","create_digest(digest_type, ?)"),
    ENCRYPT("ENCRYPT","ENCRYPT(?)"),
    DECRYPT_BIN("DECRYPT_BIN","DECRYPT_BIN(?)"),
    DECRYPT_CHARs("DECRYPT_CHARs","DECRYPT_CHARs(?)"),
    OLD_PASSWORD("OLD_PASSWORD","OLD_PASSWORD(?)"),
    PASSWORD("PASSWORD","PASSWORD(?)"),
    //endregion

    //region Locking Fucntions
    GET_LOCK("get_lock","GET_LOCK(?,timeout)"),
    IS_FREE_LOCK("is_free_lock","IS_FREE_LOCK(?)"),
    IS_USED_LOCK("IS_USED_LOCK","IS_USED_LOCK(?)"),
    RELEASE_LOCK("RELEASE_LOCK","RELEASE_LOCK(?)"),
    RELEASE_ALL_LOCKS("release_all_locks","release_all_locks()"),
    //endregion

    explode("explode"),
    posexplode("posexplode"),
    inline("inline"),
    stack("stack"),
    json_tuple("json_tuple"),
    JSON_VALUE("json_value","JSON_VALUE(json_doc, path)"),
    JSON_SCHEMA_VALID("json_schema_valid","JSON_SCHEMA_VALID(schema,document)"),
    JSON_OVERLAPS("json_overlaps","JSON_OVERLAPS(?, ?)"),
    JSON_UNQUOTE("json_unquote","JSON_UNQUOTE(?)"),
    parse_url_tuple("parse_url_tuple"),


    //region XPATH
    EXTRACTVALUE("extractvalue","ExtractValue(xml_frag, xpath_expr)"),
    UPDATEXML("updatexml","UpdateXML(xml_target, xpath_expr, new_xml)"),
    XPATH_STRING("xpath_string","xpath_string(xml, xpath)"),
    XPATH_FLOAT("xpath_float","xpath_float(xml, xpath)"),
    XPATH_NUMBER("xpath_number","xpath_number(xml, xpath)"),
    XPATH_INT("xpath_int","xpath_int(xml, xpath)"),
    XPATH_BOOLEAN("xpath_boolean","xpath_boolean(xml, xpath)"),
    XPATH_DOUBLE("xpath_double","xpath_double(xml, xpath)"),
    XPATH_LONG("xpath_long","xpath_long(xml, xpath)"),
    XPATH_SHORT("xpath_short","xpath_short(xml, xpath)"),
    XPATH("xpath","xpath(xml, xpath)")

    //endregion
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
            case BETWEEN:
            case IN:
            case NOT_IN:
                return true;
            default:
                return false;
        }
    }

    public boolean isLogical() {
        return this == BooleanAnd || this == BooleanOr || this == BooleanXor || this == EWAH_BITMAP_AND
                || this == BitwiseAnd || this == EWAH_BITMAP_OR;
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

    public boolean isBuildIn() {
        switch(this) {
            case MBRTOUCHES                          :
            case FORMAT_BYTES:
            case COUNT_DISTINCT:
            case ST_SRID:
            case IS_IPV_4_COMPAT:
            case JSON_VALUE:
            case JSON_SCHEMA_VALID:
            case NORMALIZE_STATEMENT:
            case UUID_SHORT:
            case JSON_OVERLAPS:
            case MBRCOVERS:
            case READ_FIREWALL_USERS:
            case ST_UNION:
            case JSON_UNQUOTE:
            case st_pointatdistance                  :
            case st_mpointfromtext                   :
            case st_collect                          :
            case inet6_aton                          :
            case st_isclosed                         :
            case st_intersects                       :
            case mbrcoveredby                        :
            case st_numgeometries                    :
            case st_geomcollfromwkb                  :
            case st_asbinary                         :
            case st_pointfromgeohash                 :
            case json_pretty                         :
            case master_pos_wait                     :
            case st_endpoint                         :
            case st_symdifference                    :
            case multipoint                          :
            case st_envelope                         :
            case st_touches                          :
            case regexp_substr                       :
            case mbrcontains                         :
            case mbrwithin                           :
            case internal_check_time                 :
            case st_isvalid                          :
            case st_longfromgeohash                  :
            case st_difference                       :
            case current_role                        :
            case ps_current_thread_id                :
            case make_set                            :
            case json_schema_validation_report       :
            case timestampadd                        :
            case st_issimple                         :
            case from_base64                         :
            case bin_to_uuid                         :
            case set_firewall_mode                   :
            case oct                                 :
            case json_merge                          :
            case export_set                          :
            case multipolygon                        :
            case st_linefromtext                     :
            case internal_data_free                  :
            case internal_max_data_length            :
            case interval                            :
            case charset                             :
            case can_access_user                     :
            case json_array_insert                   :
            case st_makeenvelope                     :
            case convert_tz                          :
            case st_transform                        :
            case get_format                          :
            case uncompressed_length                 :
            case utc_timestamp                       :
            case st_buffer_strategy                  :
            case st_intersection                     :
            case st_area                             :
            case polygon                             :
            case read_firewall_whitelist             :
            case st_validate                         :
            case json_length                         :
            case st_linefromwkb                      :
            case internal_table_rows                 :
            case row_count                           :
            case uuid_to_bin                         :
            case mbrdisjoint                         :
            case json_keys                           :
            case st_interiorringn                    :
            case st_contains                         :
            case is_uuid                             :
            case internal_index_length               :
            case validate_password_strength          :
            case st_astext                           :
            case json_storage_free                   :
            case is_ipv4_mapped                      :
            case icu_version                         :
            case st_overlaps                         :
            case mbrequals                           :
            case st_asgeojson                        :
            case st_hausdorffdistance                :
            case internal_is_enabled_role            :
            case utc_date                            :
            case internal_dd_char_length             :
            case st_numpoints                        :
            case mbroverlaps                         :
            case st_polyfromtext                     :
            case st_pointfromwkb                     :
            case st_geomfromtext                     :
            case internal_checksum                   :
            case json_array                          :
            case curtime                             :
            case st_mpointfromwkb                    :
            case st_mpolyfromtext                    :
            case st_x                                :
            case st_y                                :
            case dayofyear                           :
            case json_depth                          :
            case st_geometrytype                     :
            case roles_graphml                       :
            case mid                                 :
            case st_polyfromwkb                      :
            case st_distance_sphere                  :
            case DEFAULT:
            case name_const                          :
            case st_swapxy                           :
            case current_time                        :
            case UTC_TIME:
            case json_remove                         :
            case json_extract                        :
            case st_startpoint                       :
            case internal_auto_increment             :
            case regexp_instr                        :
            case st_isempty                          :
            case linestring                          :
            case st_lineinterpolatepoints            :
            case st_distance                         :
            case st_longitude                        :
            case coercibility                        :
            case json_valid                          :
            case can_access_table                    :
            case ps_thread_id                        :
            case source_pos_wait                     :
            case internal_data_length                :
            case st_geomfromgeojson                  :
            case json_merge_patch                    :
            case st_length                           :
            case json_set                            :
            case ASYMMETRIC_DECRYPT:
            case st_disjoint                         :
            case st_frechetdistance                  :
            case weight_string                       :
            case curdate                             :
            case json_contains                       :
            case internal_keys_disabled              :
            case json_table                          :
            case st_exteriorring                     :
            case now                                 :
            case internal_get_username               :
            case st_pointfromtext                    :
            case ord                                 :
            case internal_index_column_cardinality   :
            case memberof                           :
            case gtid_subset                         :
            case gtid_subtract                       :
            case internal_get_view_warning_or_error  :
            case json_object                         :
            case get_dd_column_privileges            :
            case st_convexhull                       :
            case from_days                           :
            case json_replace                        :
            case json_insert                         :
            case internal_avg_row_length             :
            case any_value                           :
            case st_latitude                         :
            case inet_aton                           :
            case compress                            :
            case json_quote                          :
            case multilinestring                     :
            case benchmark                           :
            case get_dd_index_sub_part_length        :
            case geometrycollection                  :
            case can_access_database                 :
            case st_centroid                         :
            case json_type                           :
            case st_lineinterpolatepoint             :
            case st_within                           :
            case geomcollection                      :
            case inet6_ntoa                          :
            case json_storage_size                   :
            case json_search                         :
            case statement_digest_text               :
            case mbrintersects                       :
            case st_dimension                        :
            case st_crosses                          :
            case json_arrayagg                       :
            case st_geomcollfromtext                 :
            case st_mlinefromtext                    :
            case format_pico_time                    :
            case st_mpolyfromwkb                     :
            case st_geometryn                        :
            case st_simplify                         :
            case can_access_column                   :
            case st_geomfromwkb                      :
            case json_contains_path                  :
            case nth_value                           :
            case collation                           :
            case st_numinteriorring                  :
            case random_bytes                        :
            case strcmp                              :
            case st_equals                           :
            case wait_until_sql_thread_after_gtids   :
            case regexp_like                         :
            case st_latfromgeohash                   :
            case found_rows                          :
            case wait_for_executed_gtid_set          :
            case uncompress                          :
            case MYSQL_FIREWALL_FLUSH_STATUS:
            case json_objectagg                      :
            case internal_is_mandatory_role          :
            case values                              :
            case is_ipv6                             :
            case is_ipv4                             :
            case point                               :
            case json_array_append                   :
            case st_buffer                           :
            case statement_digest                    :
            case st_geohash                          :
            case st_pointn                           :
            case get_dd_create_options               :
            case match                               :
            case inet_ntoa                           :
            case json_merge_preserve                 :
            case load_file                           :
            case st_mlinefromwkb                     :
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

    public boolean isLockingFunctions() {
        switch(this) {
            case GET_LOCK:
            case IS_USED_LOCK:
            case RELEASE_LOCK:
            case IS_FREE_LOCK:
            case RELEASE_ALL_LOCKS:
                return true;
            default:
                return false;
        }
    }
    public boolean isMiscFunctions() {
        switch(this) {
            case logged_in_user:
            case current_database:
            case version:
            case surrogate_key:
                // 一个参数
            case hash:
            case MD5:
            case UUID:
            case UUID_SHORT:
            case sha1:
            case sha:
            case crc32:
                // 一个参数+ int
            case sha2:
                // 一个参数+ string
                // 函数
            case java_method:
            case reflect:
            case REFLECT_2:
            case GROUPING:
            case EWAH_BITMAP:
            case EWAH_BITMAP_EMPTY:
                return true;
            default:
                return false;
        }
    }
    public boolean isInformationFunctions() {
        switch(this) {
            case BENCHMARK     :
            case CHARSET       :
            case COERCIBILITY  :
            case COLLATION     :
            case CONNECTION_ID :
            case CURRENT_USER  :
            case DATABASE      :
            case FOUND_ROWS    :
            case LAST_INSERT_ID:
            case ROW_COUNT     :
            case SCHEMA        :
            case SESSION_USER  :
            case SYSTEM_USER   :
            case USER          :
            case VERSION       :
                return true;
            default:
                return false;
        }
    }

    public boolean isStringFunction() {
        switch(this) {
            case ARRAY_EXTRACT:
            case ARRAY_SLICE:
            case CONTAINS:
            case SPACE:
            case ASCII:
            case UNICODE:
            case BTRIM:
            case BIT_LENGTH:
            case CHAR_LENGTH:
            case DATALENGTH:
            case DIFFERENCE:
            case FORMAT:
            case LEFT:
            case RIGHT:
            case CHR:
            case CHAR:
            case STR:
            case STUFF:
            case CHARINDEX:
            case base64:
            case character_length:
            case LENGTH:
            case LEN:
            case NCHAR:
            case PATINDEX:
            case QUOTENAME:
            case REPLICATE:
            case LOWER:
            case LCASE:
            case UPPER:
            case ucase:
            case ltrim:
            case RTRIM:
            case trim:
            case OCTET_LENGTH:
            case OVERLAY:
            case POSITION:
            case quote:
            case REVERSE:
            case sentences:
            case unbase64:
            case INITCAP:
            case SOUNDEX:
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
            case LOCATE:
            case lpad:
            case rpad:
            case parse_url:
            case printf:
            case regexp_extract:
            case regexp_replace:
            case REPEAT:
            case REPLACE:
            case split:
            case SPLIT_PART:
            case STRPOS:
            case TO_ASCII:
            case TO_HEX:
            case str_to_map:
            case SUBSTR:
            case SUBSTRING:
            case substring_index:
            case TRANSLATE:
            case context_ngrams:
            case CONCAT_WS:
            case get_json_object:
            case ngrams:
            case POSSTR:
            case INSERT:
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
            case INDEX:
            case GET_SPLITS:
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
            case TO_BASE_64:
            case SMALLINT:
            case INTEGER:
            case BIGINT:
            case DECIMAL:
            case REAL:
            case DOUBLE:
            case FLOAT:
            case CHAR:
            case VARCHAR:
            case VARCHAR_FORMAT_BIT:
            case VARCHAR_FORMAT:
            case VARCHAR_BIT_FORMAT:
            case LONG_VARCHAR:
            case CLOB:
            case GRAPHIC:
            case VARGRAPHIC:
            case LONG_VARGRAPHIC:
            case DBCLOB:
            case BLOB:
            case DATE:
            case TIME:
            case TIME_FORMAT:
            case TIME_TO_SEC:
            case TIMEDIFF:
            case INTERNAL_INTERVAL:
            case TIMESTAMP:
                return true;
            default:
                return false;
        }
    }
    public boolean isDateFunction() {
        switch(this) {
            case from_unixtime:
            case         unix_timestamp:
            case TO_UNIX_TIMESTAMP:
            case TO_DATE:
            case TO_SECONDS:
            case YEAR:
            case FLOOR_YEAR:
            case FLOOR_MONTH:
            case FLOOR_HOUR:
            case FLOOR_WEEK:
            case FLOOR_DAY:
            case FLOOR_QUARTER:
            case FLOOR_SECOND:
            case YEARWEEK:
            case        quarter:
            case MONTH:
            case SYSDATETIME:
            case DAY:
            case        dayofmonth:
            case TO_DAYS:
            case        hour:
            case       minute:
            case        second:
            case SEC_TO_TIME:
            case STR_TO_DATE:
            case SLEEP:
            case MICROSECOND:
            case MONTHNAME:
            case PERIOD_ADD:
            case PERIOD_DIFF:
            case DAYNAME:
            case WEEK:
            case WEEKDAY:
            case WEEK_ISO:
            case TIMESTAMP_ISO:
            case DAYS:
            case JULIAN_DAY:
            case MIDNIGHT_SECONDS:
            case WEEKOFYEAR:
            case         extract:
            case DATEDIFF:
            case TIMESTAMPDIFF:
            case DATEFROMPARTS:
            case TIMESTAMP_FORMAT:
            case DATENAME:
            case DATEPART:
            case        date_add:
            case        date_sub:
            case SUBDATE:
            case SUBTIME:
            case ADDDATE:
            case ADDTIME:
            case        from_utc_timestamp:
            case        to_utc_timestamp:
            case        current_date:
            case CURRENT_TIMESTAMP:
            case DATEADD:
            case       add_months:
            case         last_day:
            case        next_day:
            case GETDATE:
            case GETUTCDATE:
            case ISDATE:
            case        TRUNC:
            case TRUNCATE:
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
            case ABS      :
            case ACOS     :
            case ASIN     :
            case ATAN2    :
            case BIT_COUNT:
            case CBRT     :
            case CEIL     :
            case CEILING  :
            case CHR      :
            case COS      :
            case COT      :
            case DEGREES  :
            case EVEN     :
            case FACTORIAL:
            case FLOOR    :
            case GAMMA    :
            case GREATEST :
            case LEAST    :
            case LGAMMA   :
            case NEXTAFTER:
            case SETSEED  :
            case LN       :
            case LOG      :
            case LOG2     :
            case LOG10    :

            case PI       :
            case POW      :
            case POWER    :
            case RADIANS  :
            case RANDOM   :
            case ROUND    :

            case SIN      :
            case SIGN     :
            case SQRT     :
            case XOR      :
            case TAN      :
            case bround:
            case        rand:
            case EXP:
            case       bin:
            case       hex:
            case       unhex:
            case        conv:
            case ABSVAL:
            case        pmod:
            case       asin:
            case ATAN:
            case SQUARE:
            case       positive:
            case       negative:
            case trunc:
            case STD:
            case        e:
            case       shiftleft:
            case       shiftright:
            case        shiftrightunsigned:
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
            case RANK:
            case DENSE_RANK:
            case ROW_NUMBER:
            case PERCENT_RANK:
            case CUME_DIST:
            case LAST_VALUE:
            case FIRST_VALUE:
            case LEAD:
            case LAG:
            case GROUP_CONCAT:
            case variance:
            case VAR_POP:
            case var_samp:
            case BIT_OR:
            case BIT_AND:
            case BIT_XOR:
            case collect_set:
            case collect_list:
            case COMPUTE_STATS:
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
            case VALUE:
                return true;
            default:
                return false;
        }
    }
    public boolean isConditionalFunction() {
        switch (this) {
            case ISNULL:
            case ISFINITE:
            case ISINF    :
            case ISNAN    :
            case ISNUMERIC:
            case ISNOTNULL:
            case  nvl:
            case  assert_true:
            case  IF:
            case  COALESCE:
            case NULLIF:
            case IFNULL:
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

    public boolean isXPathUDF() {
        switch (this) {
            case XPATH_STRING:
            case XPATH_FLOAT:
            case XPATH_NUMBER:
            case XPATH_INT:
            case XPATH_BOOLEAN:
            case XPATH_DOUBLE:
            case XPATH_LONG:
            case XPATH_SHORT:
            case XPATH:
            case EXTRACTVALUE:
            case UPDATEXML:
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

    public boolean isENCRYPT_DECRYPT() {
        switch (this) {
            case aes_encrypt:
            case aes_decrypt:
            case ASYMMETRIC_DECRYPT:
            case ASYMMETRIC_ENCRYPT:
            case ASYMMETRIC_DERIVE:
            case ASYMMETRIC_SIGN:
            case ASYMMETRIC_VERIFY:
            case CREATE_ASYMMETRIC_PRIV_KEY:
            case CREATE_ASYMMETRIC_PUB_KEY:
            case CREATE_DH_PARAMETERS:
            case CREATE_DIGEST:
            case ENCRYPT:
            case DECRYPT_BIN:
            case DECRYPT_CHARs:
            case DES_DECRYPT:
            case DES_ENCRYPT:
            case OLD_PASSWORD:
            case PASSWORD:
                return true;
            default:
                return false;
        }
    }
}
