package flowdesigner.sql.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import flowdesigner.sql.builder.SQLBuilderFactory;
import flowdesigner.sql.builder.SQLDropTableBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import sqlTest.SQLTest;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SQLDropTableBuilderImplTest {
    SQLDropTableBuilder dropTableBuilder;

    @ParameterizedTest
    @MethodSource()
    void dropTable(DbType dbType, String expected) throws SQLSyntaxErrorException {
        dropTableBuilder = SQLBuilderFactory.createDropTableBuilder(dbType);
        dropTableBuilder.dropTable("db_name");
        dropTableBuilder.setIfExists(true);

        SQLStatement statement = SQLTest.parser(dropTableBuilder.toString(), dbType);
//        System.out.println(statement.getClass());
        Assertions.assertEquals(expected, dropTableBuilder.toString());
    }

    static Stream<Arguments> dropTable() {
        ArrayList<Arguments> arguments = new ArrayList<>();
        for (DbType dbType : DbType.values()) {
            String syntax =  switch (dbType) {
                default -> "DROP TABLE IF EXISTS db_name";
            };
            arguments.add(Arguments.of(dbType, syntax));
        }

        return arguments.stream();
    }
}