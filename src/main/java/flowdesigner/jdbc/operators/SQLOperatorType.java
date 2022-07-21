package flowdesigner.jdbc.operators;

public enum SQLOperatorType {
    //Built-in Operators
    RelationalOperator,ArithmeticOperator,LogicalOperator, // SQLBinaryOperator
    ComplexTypeConstructor,
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
    MiscFunctions,
    //Built-in Aggregate Functions (UDAF)
    UDAF,
    //Built-in Table-Generating Functions (UDTF)
    UDTF,
    GROUPingAndSORTing,
    UtilityFunction,
    UDF,
    Others,

    BinaryOperator,
    UnaryOperator
}
