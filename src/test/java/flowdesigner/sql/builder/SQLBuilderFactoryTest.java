package flowdesigner.sql.builder;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import flowdesigner.sql.builder.impl.SQLAlterDatabaseBuilderImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import sqlTest.SQLTest;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SQLBuilderFactoryTest {

    @ParameterizedTest
    @MethodSource()
    void createSQLDropDatabaseBuilder(DbType dbType, String expected) throws SQLSyntaxErrorException {
        SQLDropDatabaseBuilder builder = SQLBuilderFactory.createSQLDropDatabaseBuilder(dbType);
        builder.dropDatabaseOrSchema("myschema");
        builder.setCascade(true);
//        builder.setIfExists(true);
        builder.setPhysical(true);
        builder.setRestrict(true);
        builder.setServer("server");

        SQLUtils.parseStatements(builder.toString(), dbType);

        String sqlString = SQLUtils.toSQLString(builder.getSQLStatement(), dbType);
//        assertEquals(expected,sqlString);

    }

    static Stream<Arguments> createSQLDropDatabaseBuilder() {
        ArrayList<Arguments> arguments = new ArrayList<>();
        for (DbType dbType : DbType.values()) {
            String syntax =  switch (dbType) {
                case oscar,postgresql -> "DROP SCHEMA myschema";
                case oracle, oceanbase_oracle -> "drop user myschema";
                default -> "DROP DATABASE myschema";
            };
            arguments.add(Arguments.of(dbType, syntax));
        }

        return arguments.stream();
    }

}