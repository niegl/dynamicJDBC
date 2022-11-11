package flowdesigner.jdbc.command;

import com.alibaba.fastjson2.JSON;
import com.github.houbb.auto.log.annotation.AutoLog;
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
        put(CMD_DBReverseGetTypeInfo, DBReverseGetTypeInfoImpl.class);          //逆向解析，获取数据表清单
        put(CMD_DBReverseGetSchemas, DBReverseGetSchemasImpl.class);            //逆向解析，获取数据表清单
        put(CMD_DBReverseGetAllTablesList, DBReverseGetTablesImpl.class);       //逆向解析，获取数据表清单
        put(CMD_DBReverseGetTableDDL, DBReverseGetTablesDDLImpl.class);          //逆向解析，获取指定数据表DDL
        put(CMD_ParseDDLToTableImpl, DBParseDDLImpl.class);                     //逆向解析，获取指定数据表DDL
        put(CMD_DBExecuteCommandImpl, DBExecuteImpl.class);                             //正向执行，获取SQL语句执行结果
        put(CMD_DBReverseGetFKColumnFieldImpl, DBReverseGetFKColumnFieldImpl.class);    //正向执行，获取SQL语句执行结果
        put(CMD_DBReverseGetFKReferenceImpl, DBReverseGetFKReferenceImpl.class);        //正向执行，获取SQL语句执行结果
        put(CMD_DBReverseGetFunctionsImpl, DBReverseGetFunctionsImpl.class);            //正向执行，获取SQL语句执行结果
        //        put(CMD_DBExecuteUpdateCommandImpl, DBExecuteUpdateImpl.class);

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
    @AutoLog
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
        } catch (Exception e) {
            ret.setBody("执行命令异常!\n" + e.getMessage());
        }

        return ret;
    }
}
