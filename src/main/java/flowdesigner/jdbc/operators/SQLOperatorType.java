package flowdesigner.jdbc.operators;

public enum SQLOperatorType {
    //Built-in Operators
    RelationalOperator,ArithmeticOperator,LogicalOperator, // SQLBinaryOperator
    ComplexTypeConstructor,
    //Built-in Functions
    MathematicalFunction,
    CollectionFunction,
    TypeConversionFunction,
    DateFunction,
    ConditionalFunction,
    StringFunction,
    DataMaskingFunction,
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
