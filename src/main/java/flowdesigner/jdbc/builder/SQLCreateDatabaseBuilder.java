package flowdesigner.jdbc.builder;

import java.util.List;
import java.util.Map;

public interface SQLCreateDatabaseBuilder {
    SQLCreateDatabaseBuilder setName(String db_name);

    SQLCreateDatabaseBuilder setIfNotExists(boolean ifNotExists);

    SQLCreateDatabaseBuilder createDatabase(boolean ifNotExists, String db_name, List<String> create_option_key, List<String> create_option_value);

    SQLCreateDatabaseBuilder createDatabase(boolean ifNotExists, String db_name, Map<String, String> create_option);
}
