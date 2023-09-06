package flowdesigner.jdbc.command;

import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.fastjson2.JSON;
import flowdesigner.jdbc.command.impl.*;
import flowdesigner.util.raw.kit.StringKit;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static flowdesigner.jdbc.command.CommandKey.*;

/**
 * 后期尝试对commandManager进行修改，使其能够直接获取结果类型，不需要进行object转换。
 */
@Slf4j
public class CommandManager {
    /** 命令注册表*/
    private static final Map<CommandKey, Class<?>> commandRegister = new HashMap<CommandKey, Class<?>>() {{
        put(CMD_DBReverseGetTypeInfo, DBReverseGetTypeInfoImpl.class);                  //逆向解析，获取数据库数据类型清单
        put(CMD_DBReverseGetSchemas, DBReverseGetSchemasImpl.class);                    //逆向解析，获取数据库清单
        put(CMD_DBReverseGetAllTablesList, DBReverseGetAllTableListImpl.class);         //逆向解析，获取表清单功能. 支持表和视图
        put(CMD_DBReverseGetTableDDL, DBReverseGetTableDDLImpl.class);                  //逆向解析，获取指定数据表DDL
        put(CMD_DBReverseGetFunctionsImpl, DBReverseGetFunctionsImpl.class);            //
        put(CMD_ParseDDLToTableImpl, DBParseDDLToTableImpl.class);                      //逆向解析，获取指定数据表DDL
        put(CMD_DBReverseGetPrimaryKeys, DBReverseGetPrimaryKeyImpl.class);
        put(CMD_DBReverseGetFKColumnFieldImpl, DBReverseGetFKInfoImpl.class);    //
        put(CMD_DBReverseGetFKReferenceImpl, DBReverseGetFKReferenceImpl.class);        //
    }};

    /**
     * 为了配合c++端调用，返回json数据格式。
     * @param connection
     * @param cmdText
     * @param params
     * @return String json
     */
    public static String exeCommandJson(Connection connection, CommandKey cmdText, Map<String, String> params) {
        ExecResult result = exeCommand(connection, cmdText, params);
        return JSON.toJSONString(result);
    }

    /***
     * 客户端调用命令的接口
     * @param connection jdbc连接
     * @param commandKey 命令文本，需要对应命令注册表中的命令
     * @param params 命令参数，需要和具体命令相符合
     * @return
     */
    public static ExecResult exeCommand(Connection connection, CommandKey commandKey, Map<String, String> params) {
        ExecResult ret = new ExecResult(ExecResult.FAILED, "未知异常");
        try {
            if (connection == null) {
                if (!(commandKey.equals(CMD_DBReverseGetTypeInfo) || commandKey.equals(CMD_ParseDDLToTableImpl))) {
                    ret.setBody("[parameter] connection should not be null.");
                    return ret;
                }
            }
            if (commandKey == null || params == null) {
                ret.setBody("[parameter] commandKey or params should not be null.");
                return ret;
            }

            Class<?> cmdClass = commandRegister.get(commandKey);
            if (cmdClass == null) {
                ret.setBody("Command [" + commandKey + "] Not Supported.");
                return ret;
            }

            // 输出执行的命令信息
            log.info("----------------------->>>[执行命令]<<<---------------------------");
            log.info(commandKey + " " + StringKit.join(params, " "));
            log.info("-----------------------------------------------------------------");

            // 执行命令
            Command<?> cmd = (Command<?>) cmdClass.getDeclaredConstructor().newInstance();
            ret = (ExecResult) cmd.exec(connection, params);
            return ret;

        } catch (InstantiationException | IllegalAccessException | SQLException e) {
            ret.setBody("执行命令异常!\n" + e.getMessage());
        } catch (ParserException e) {
            ret.setBody("parse exception:\n" + e.getMessage());
        } catch (Exception e) {
            ret.setBody("执行命令异常!\n" + e.getMessage());
        }

        return ret;
    }
}
