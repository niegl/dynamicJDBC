package flowdesigner.jdbc.dialect.impl;

import flowdesigner.jdbc.dialect.DBDialect;

import java.sql.Connection;
import java.sql.SQLException;

public class DBDialectImpala extends DBDialect {
    @Override
    public String getCatalogPattern(Connection conn, String catalogPattern) throws SQLException {
        return "Impala";
    }
}
