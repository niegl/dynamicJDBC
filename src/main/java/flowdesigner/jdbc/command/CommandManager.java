package flowdesigner.jdbc.command;

import com.google.gson.Gson;
import flowdesigner.jdbc.command.impl.*;
import flowdesigner.jdbc.util.raw.kit.StringKit;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static flowdesigner.jdbc.command.CommandKey.*;

/**
 * 后期尝试对commandManager进行修改，使其能够直接获取结果类型，不需要进行object转换。
 */
public class CommandManager {

    /** 使用java自带的log工具 */
    private static final Logger logger = Logger.getLogger("CommandManager");
    /** 命令注册表*/
    private static final Map<CommandKey, Class<?>> commandRegister = new HashMap<CommandKey, Class<?>>() {{
        put(CMD_DBReverseGetTypeInfo, DBReverseGetTypeInfoImpl.class);          //逆向解析，获取数据表清单
        put(CMD_DBReverseGetSchemas, DBReverseGetSchemasImpl.class);            //逆向解析，获取数据表清单
        put(CMD_DBReverseGetAllTablesList, DBReverseGetAllTablesListImpl.class);  //逆向解析，获取数据表清单
        put(CMD_DBReverseGetTableDDL, DBReverseGetTableDDLImpl.class);            //逆向解析，获取指定数据表DDL
        put(CMD_ParseDDLToTableImpl, DBParseDDLImpl.class);                     //逆向解析，获取指定数据表DDL
        put(CMD_DBExecuteCommandImpl, DBExecuteCommandImpl.class);              //正向执行，获取SQL语句执行结果
        put(CMD_DBExecuteUpdateCommandImpl, DBExecuteUpdateCommandImpl.class);
        put(CMD_DBReverseGetFKColumnFieldImpl, DBReverseGetFKColumnFieldImpl.class);            //正向执行，获取SQL语句执行结果
        put(CMD_DBReverseGetFKReferenceImpl, DBReverseGetFKReferenceImpl.class);            //正向执行，获取SQL语句执行结果

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
        Gson gson = new Gson();
        return gson.toJson(result);
    }
    /***
     * 客户端调用命令的接口
     * @param connection jdbc连接
     * @param cmdText 命令文本，需要对应命令注册表中的命令
     * @param params 命令参数，需要和具体命令相符合
     * @return
     */
    public static ExecResult exeCommand(Connection connection, CommandKey cmdText, Map<String, String> params) {
        ExecResult ret = new ExecResult(ExecResult.FAILED, "未知异常");
        try {
            if (connection == null)
                if (!(cmdText.equals(CMD_DBReverseGetTypeInfo)
                        || cmdText.equals(CMD_ParseDDLToTableImpl)))
                    return ret;

            Class<?> cmdClass = commandRegister.get(cmdText);
            if (cmdClass == null) {
                String errMsg = "Command [" + cmdText + "] Not Supported.";
                throw new RuntimeException(errMsg);
            }

            // 输出执行的命令信息
            logger.info("------------------------------------>>>[执行命令]<<<-------------------------------------");
            logger.info(cmdText + " " + StringKit.join(params, " "));
            logger.info("----------------------------------------------------------------------------------------");

            // 执行命令
            Command<?> cmd = (Command<?>) cmdClass.getDeclaredConstructor().newInstance();
            ret = (ExecResult) cmd.exec(connection, params);
            return ret;

        } catch (InstantiationException | IllegalAccessException | SQLException e) {
            logger.log(Level.SEVERE,"执行命令异常", e);
            ret.setBody("执行命令异常!\n" + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE,"未知的错误", e);
            ret.setBody("执行命令异常!\n" + e.getMessage());
        }

        return ret;
    }
}
