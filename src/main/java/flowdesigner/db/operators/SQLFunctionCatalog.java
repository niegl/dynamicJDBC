package flowdesigner.db.operators;

@Deprecated
public enum SQLFunctionCatalog {
    //Built-in Operators
    BuiltIn,
    /**
     * 一元操作符
     */
    UnaryOperator,
    /**
     * SQLBinaryOperator=二位操作符
     */
    BinaryOperator,
    /**
     * 关系运算符
     */
    RelationalOperator, ComparisonOperators,
    ArithmeticOperator,
    LogicalOperator,
    AssignmentOperators,
    /**
     * 连接运算符
     */
    ConcatenationOperator,

    /**
     * 字符串/文本函数
     */
    StringFunction,
    CharacterSetFunctions,

    /**
     * 数学函数或数值函数
     */
    MathematicalFunction,
    NumericFunctions,   // 数值函数：接收数字输入并返回数字值，包括超越函数，指的是变量指的是变量之间的关系不能用有限的加减乘除来表示，一般来表示三角函数COS,ACOS,ASIN...

    /**
     * 日期、时间函数
     */
    DateFunction,
    TimeFunction,
    TimeStampFunction,
    DatetimeFunctions,

    IntervalFunction,

    /**
     * Bit Functions and Operators
     */
    BitOperators,
    BitFunctions,

    /**
     * XML
     */
    XMLFunctions,
    XPathFunctions,
    /**
     * JSON
     */
    JSONFunctions,

    /**
     * 大对象函数
     */
    LargeObjectFunctions,

    /**
     * 复杂对象构造
     */
    ComplexTypeConstructor,

    /**
     * 集合运算符
     */
    CollectionOperators,
    /**
     * 集合函数
     */
    CollectionFunction,
    /**
     * 多重集运算符
     */
    MultisetOperators,


    /**
     * 转换函数
     */
    TypeConversionFunction,
    ConversionFunctions,
    ConversionOperators,

    /**
     * 一般比较函数
     */
    GeneralComparisonFunctions,

    /**
     * 正则表达式
     */
    RegularExpressions,

    /**
     * 核对运算符（排序函数？）
     */
    COLLATEOperator,

    /**
     * 对象引用函数
     */
    ObjectReferenceFunctions,

    /**
     * 层次查询操作符
     */
    HierarchicalQuery,
    /**
     * 层次函数
     */
    HierarchicalFunctions,

    //Built-in Functions
    /**
     * 模式匹配函数
     */
    PatternMatchingFunction,

    /**
     * NULL相关函数
     */
    NULLRelatedFunctions,
    /**
     * 条件函数
     */
    ConditionalFunction,
    ConditionalOperators,
    /**
     * 流程控制函数
     */
    FlowControlFunctions,

    /**
     * 分析函数（窗口函数）
     */
    WindowFunction,

    /**
     * MySQL的performance schema 用于监控MySQL server在一个较低级别的运行过程中的资源消耗、资源等待等情况。
     */
    PerformanceSchemaFunctions,

    /**
     * OLAP函数
     */
    OLAPFunctions,

    /**
     * 数据桶功能.由于数据库本身已经是可扩展的，所以一切成为了可能。就是说，现在可以为自定义的业务对象和富对象来定制数据库管理系统的索引和查询优化机制。可以提供自定义的索引和查询优化实现方法，来改进原有的服务实现。你需要用扩展接口在服务器上注册这些实现。注册后，会让服务器在处理特定流程时，使用你的实现来代替原有本身的实现。
     */
    DataCartridgeFunctions,

    /**
     * 数据格式化
     */
    DataFormatFunction,

    /**
     * 数据掩码、加密解密、编码解码
     */
    DataMaskingFunction,
    ENCRYPT_DECRYPT,
    EncodingAndDecodingFunctions,

    /**
     * 压缩
     */
    CompressionFunctions,
    /**
     * 聚合函数
     */
    AggregateFunctions,

    //Built-in Table-Generating Functions (UDTF)
    TableGeneratingFunctions,

    /**
     * 用户自定义函数
     */
    UDF,

    /**
     * 数据挖掘功能
     */
    DataMiningFunctions,
    /**
     * 模型函数(机器学习函数)
     */
    ModelFunctions,

    /**
     * 锁功能
     */
    LockFunctions,
    /**
     * 信息函数, 如CONNECTION_ID()，DATABASE()，LAST_INSERT_ID()...
     */
    InformationFunctions,

    GlobalTransactionIdentifiers,

    /**
     * 杂项功能
     */
    MiscellaneousFunctions,

    /**
     * 以下待删除
     */
    GROUPingAndSORTing,
    UtilityFunction

}
