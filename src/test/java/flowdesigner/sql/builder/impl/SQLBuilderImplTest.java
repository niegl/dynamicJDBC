package flowdesigner.sql.builder.impl;

import com.alibaba.druid.DbType;
import flowdesigner.sql.builder.SQLBuilderFactory;
import flowdesigner.sql.builder.SQLCreateTableBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

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