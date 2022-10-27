package flowdesigner.jdbc.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import flowdesigner.jdbc.builder.SQLBuilder;
import flowdesigner.jdbc.builder.SQLBuilderFactory;
import flowdesigner.jdbc.builder.SQLCreateTableBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SQLBuilderImplTest {


    @ParameterizedTest
    @MethodSource("addCheckConstraint")
    void addBeforeComment(DbType dbType, String expected) {
        SQLCreateTableBuilder createTableBuilder = SQLBuilderFactory.createCreateTableBuilder(dbType);
        createTableBuilder.setName("tableName");
        createTableBuilder.addBeforeComment("comment");

        System.out.println(createTableBuilder.toString());
    }

    @Test
    void addAfterComment() {
    }

    static Stream<Arguments> addCheckConstraint() {
        return Stream.of(Arguments.arguments(DbType.mysql, ""),
                Arguments.arguments(DbType.hive, ""),
                Arguments.arguments(DbType.oracle, "")
        );
    }
}