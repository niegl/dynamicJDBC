package flowdesigner.jdbc.operators;

public enum SQLOperatorType {
    //Built-in Operators
    RelationalOperator,ArithmeticOperator,LogicalOperator, // SQLBinaryOperator
    ComplexTypeConstructor,
    BuiltIn,
    //Built-in Functions
    MathematicalFunction,
    PatternMatchingFunction,
    CollectionFunction,
    TypeConversionFunction,
    DateFunction,
    TimeStampFunction,
    TimeFunction,
    IntervalFunction,
    DataFormatFunction,
    ConditionalFunction,
    StringFunction,
    DataMaskingFunction,
    LockFunctions,
    InformationFunctions,
    MiscFunctions,
    //Built-in Aggregate Functions (UDAF)
    UDAF,
    ENCRYPT_DECRYPT,
    XPathUDF,
    //Built-in Table-Generating Functions (UDTF)
    UDTF,
    GROUPingAndSORTing,
    UtilityFunction,
    UDF,
    Others,

    BinaryOperator,
    UnaryOperator
}
