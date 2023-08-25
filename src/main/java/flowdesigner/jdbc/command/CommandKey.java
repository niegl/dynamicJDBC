package flowdesigner.jdbc.command;

public enum CommandKey {
    /**
     * 批量操作
     */
    /**
     * 获取数据库数据类型
     */
    CMD_DBReverseGetTypeInfo,
    /**
     * 获取数据库名清单
     */
    CMD_DBReverseGetSchemas,
    /**
     * 获取数据库中的所有表名清单
     */
    CMD_DBReverseGetAllTablesList,
    /**
     * 获取数据表结构，包含字段
     */
    CMD_DBReverseGetTableDDL,

    CMD_ParseDDLToTableImpl,
    CMD_DBReverseGetFKColumnFieldImpl,
    CMD_DBReverseGetFKReferenceImpl,
    CMD_DBReverseGetFunctionsImpl
}
