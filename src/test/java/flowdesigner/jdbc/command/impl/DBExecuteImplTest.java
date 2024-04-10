package flowdesigner.jdbc.command.impl;

import com.alibaba.druid.DbType;
import com.alibaba.fastjson2.JSON;
import flowdesigner.jdbc.command.ExecResult;
import flowdesigner.jdbc.driver.DynamicDriver;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class DBExecuteImplTest {
    Connection connection;
    DynamicDriver dynamicDriver = null;

    Connection getConnection(DbType dbType) {



        switch (dbType) {
            case hive -> {
                dynamicDriver = new DynamicDriver("C:\\test");
                Properties properties = new Properties();
                properties.setProperty("driverClassName","org.apache.hive.jdbc.HiveDriver");
                properties.setProperty("url","jdbc:hive2://172.30.224.36:10000");
                properties.setProperty("username","admin");
                properties.setProperty("password","admin");
                dynamicDriver.set_propertyInfo(properties);
            }
            case mysql -> {
                dynamicDriver = new DynamicDriver("C:\\Users\\nieguangling\\AppData\\Roaming\\DBeaverData\\drivers\\maven\\maven-central\\mysql");
                Properties properties = new Properties();
                properties.setProperty("driverClassName","com.mysql.cj.jdbc.Driver");
                properties.setProperty("url","jdbc:mysql://localhost:3306");
                properties.setProperty("username","root");
                properties.setProperty("password","123456");
                dynamicDriver.set_propertyInfo(properties);
            }
        }

        try {
            connection = dynamicDriver.getConnection();
        } catch (SQLException e) {
            System.out.println(dynamicDriver.get_errMessage());
            e.printStackTrace();
        }
        assertNotNull(connection);

        return connection;
    }

    @Test
    void execSelect() throws SQLException {
        exec(DbType.hive,"-- ----------------------------------------------------------------------------\n" +
                "-- 子 系 统:\n" +
                "-- 功能描述:OD线路清分\n" +
                "-- 版    本:20210401\n" +
                "-- 源    表: CUT_PI_EXIT_S(O,E)_LINEDESC,CUT_PI_EXIT_S(O,E)_ACCLIST,cut_pi_exit_cms_filtered(_O,_E)\n" +
                "--          ,T99_SRC_STATION_RELA_HIS,T99_SRC_PROD_RELA_CD,T99_SRC_LINE_RELA_HIS,T99_PROD_SPLIT_CD,T50_PRODUCT\n" +
                "-- 目 标 表: T80_OD_LINE\n" +
                "-- 加工方式: 数据表\n" +
                "-- 执行频率: 每天\n" +
                "-- 修改记录:\n" +
                "--      Created by wujunjun at 2013-4-08 16:46:37  v1.0\n" +
                "--      modify by wujunjun at 2013-4-16 16:46:37 合并10号线一二期数据 v1.1\n" +
                "--      合并10号线处理说明：由于ACC系统对于十号线,四号线有两套线路编号，因此在清分时，会产生线路为90、10的数据，并且认为是两条线路，线路顺序号不同，仓库需要对这样的数据进行修正，\n" +
                "--      合并规则：十号线一二期合并，四号线与大兴线新增四号大兴线\n" +
                "--      modify by lixuan at 2013/7/29 15:08:38   v1.2 补全1天前、七天前的数据, 修改OD_ID编码规则\n" +
                "--      modify by lixuan at 2013/8/15 15:32:27   V1.3 修改行车方向代码取值规则\n" +
                "--      Modify history list:Modify by  yangming  at 2018-03-25 v1.4(增加票种过滤机制)\n" +
                "--      Modify by  zhangxu  at 2020-10-29 v1.9(首都机场线计次票小票种)\n" +
                "--     20191112 Lerry Modify history list:Created by Lerry\n" +
                "--     20210108 Ryh Modify history list:Created by Ryh\n" +
                "--     20210401 Lerry Modified inequation processing method\n" +
                "-- ----------------------------------------------------------------------------\n" +
                "set hive.exec.dynamic.partition.mode=nonstrict;\n" +
                "set spark.sql.shuffle.partitions = 200 ;\n" +
                "\n" +
                "CREATE database if not exists UDMS_TEMP;\n" +
                "drop table if exists UDMS_TEMP.VT_T80_OD_LINE_temp;\n" +
                "CREATE  TABLE UDMS_TEMP.VT_T80_OD_LINE_temp  like ${bmnc_pddl}.T80_OD_LINE ;\n" +
                "\n" +
                "drop table if exists UDMS_TEMP.VT_T80_OD_LINE_temp2 ;\n" +
                "CREATE  TABLE UDMS_TEMP.VT_T80_OD_LINE_temp2\n" +
                "(\n" +
                "      OD_ID STRING ,\n" +
                "      Line_Seq_Num INT ,\n" +
                "      Line_ID STRING  ,\n" +
                "      Curline_Trip_Drct_Cd STRING ,\n" +
                "      In_Curline_Station_Id STRING,\n" +
                "      Out_Curline_Station_Id STRING ,\n" +
                "      In_Curline_On_Time TIMESTAMP  ,\n" +
                "      In_Curline_On_Dt STRING  ,\n" +
                "      In_Curline_On_Tm STRING  ,\n" +
                "      Out_Curline_Off_Time TIMESTAMP  ,\n" +
                "      Out_Curline_Off_Dt STRING  ,\n" +
                "      Out_Curline_Off_Tm STRING   ,\n" +
                "      OD_Curline_Run_Distance DECIMAL(18,2)  ,\n" +
                "      OD_Curline_Run_Duration INT  ,\n" +
                "      Trafin_Curline_Wait_Duration INT  ,\n" +
                "      Wait_Duration INT  ,\n" +
                "      Trafin_Curline_Stay_Cnt INT ,\n" +
                "      Trafin_Curline_Walk_Duration INT  ,\n" +
                "      Pass_Curline_Station_Cnt INT  ,\n" +
                "      Prod_ID STRING ,\n" +
                "      Clr_Amt DECIMAL(18,2)  ,\n" +
                "      CCH_Irgul_List STRING ,\n" +
                "      UD_Data_Proc_Ind INT  ,\n" +
                "      Load_File_Type_Cd STRING ,\n" +
                "      Data_Dt STRING  ,\n" +
                "      MOT_ENTRY_INDUSTRYCODE STRING,\n" +
                "      PRODUCT_ACTION_SEQUENCE_NUMBER STRING\n" +
                " )stored as orc;\n" +
                "\n" +
                "--每日正常数据\n" +
                "INSERT INTO UDMS_TEMP.VT_T80_OD_LINE_temp2\n" +
                "SELECT\n" +
                "\tCONCAT(COALESCE(CONCAT('S01','02',date_format(COALESCE(A1.TXN_DATE_TIME,CAST('0001-01-01 00:00:00'AS TIMESTAMP)) ,'yyyyMMddHHmmss')\n" +
                "\t\t,lpad(CAST(CAST(A1.DEVICE_ID AS bigint) AS string),10,'0')\n" +
                "\t\t,lpad(CAST(CAST(A1.UDSN AS bigint) AS string),10,'0')\n" +
                "\t    ,date_format(cast('${HIVECONFTXDATE}' as string),'yyyyMMdd'),lpad(CAST(CAST(A1.REC_SEQ AS bigint) AS string),10,'0'),''))\n" +
                "\t\t,'01')--OD编号   modify by lixuan at 20130730\n" +
                "\t,CAST (A.Line_Seq_Num AS INTEGER)\n" +
                "    ,case when( A4.Line_ID IS NULL or A4.Line_ID= '')\n" +
                "\t THEN  COALESCE(A41.Line_ID ,'00')\n" +
                "\t ELSE A4.Line_ID\n" +
                "\t END\n" +
                "\t,CASE WHEN Curline_Trip_Drct_Cd = '1' THEN '01'\n" +
                "\t\t\t\tWHEN Curline_Trip_Drct_Cd = '2' THEN '02'\n" +
                "\t\t\t\tWHEN Curline_Trip_Drct_Cd = '0' THEN '03'\n" +
                "\t\t\t\tELSE COALESCE(TRIM(Curline_Trip_Drct_Cd),'')\n" +
                "\t END -- modify by lixuan at 20130815\n" +
                "    ,case when( A2.Station_ID IS NULL or  A2.Station_ID= '')\n" +
                "\t THEN  COALESCE(A21.Station_ID ,'0000')\n" +
                "\t ELSE A2.Station_ID\n" +
                "\t END\n" +
                "    ,case when( A3.Station_ID IS NULL or  A3.Station_ID= '')\n" +
                "\t THEN  COALESCE(A31.Station_ID ,'0000')\n" +
                "\t ELSE A3.Station_ID\n" +
                "\t END\n" +
                "    ,concat((CASE WHEN (In_Curline_On_Time  IS NULL  or  In_Curline_On_Time= '') THEN  '0001-01-01'\n" +
                "     \t\t      ELSE concat(substr(In_Curline_On_Time,0,4),'-',substr(In_Curline_On_Time,5,2),'-',substr(In_Curline_On_Time,7,2))\n" +
                "              END)\n" +
                "            ,' '\n" +
                "            ,(CASE WHEN (In_Curline_On_Time  IS NULL  or  In_Curline_On_Time = '') THEN  '00:00:00'\n" +
                " \t\t\t\t ELSE CONCAT(substr(In_Curline_On_Time,9,2)  , ':' , substr(In_Curline_On_Time,11,2)  , ':'  ,substr(In_Curline_On_Time,13,2) )\n" +
                "  \t\t\t  END)\n" +
                "  \t\t\t )\n" +
                "    ,CASE WHEN   (In_Curline_On_Time  IS NULL  or  In_Curline_On_Time = '')  THEN   '0001-01-01' ELSE concat(substr(In_Curline_On_Time,0,4),'-',substr(In_Curline_On_Time,5,2),'-',substr(In_Curline_On_Time,7,2))  END\n" +
                "\t,CASE WHEN   (In_Curline_On_Time  IS NULL  or  In_Curline_On_Time = '')  THEN   '00:00:00' ELSE CONCAT(substr(In_Curline_On_Time,9,2)  , ':' , substr(In_Curline_On_Time,11,2)  , ':'  ,substr(In_Curline_On_Time,13,2))   END\n" +
                "\t,concat( (CASE WHEN (Out_Curline_Off_Time  IS NULL  or  Out_Curline_Off_Time = '') THEN  '0001-01-01'\n" +
                "\t\t\t       ELSE concat(substr(Out_Curline_Off_Time,0,4),'-',substr(Out_Curline_Off_Time,5,2),'-',substr(Out_Curline_Off_Time,7,2))\n" +
                " \t\t\t   END )\n" +
                " \t\t\t ,' '\n" +
                " \t\t\t ,(CASE WHEN (Out_Curline_Off_Time  IS NULL  or  Out_Curline_Off_Time = '') THEN   '00:00:00'\n" +
                " \t\t\t\t    ELSE CONCAT(substr(Out_Curline_Off_Time,9,2)  , ':' , substr(Out_Curline_Off_Time,11,2)  , ':'  ,substr(Out_Curline_Off_Time,13,2))\n" +
                " \t\t\t   END)\n" +
                " \t\t\t)\n" +
                "\t,CASE WHEN   (Out_Curline_Off_Time  IS NULL  or  Out_Curline_Off_Time = '')  THEN   '0001-01-01' ELSE concat(substr(Out_Curline_Off_Time,0,4),'-',substr(Out_Curline_Off_Time,5,2),'-',substr(Out_Curline_Off_Time,7,2)) END\n" +
                "\t,CASE WHEN   (Out_Curline_Off_Time  IS NULL  or  Out_Curline_Off_Time = '')  THEN   '00:00:00'    ELSE CONCAT(substr(Out_Curline_Off_Time,9,2)  , ':' , substr(Out_Curline_Off_Time,11,2)  , ':'  ,substr(Out_Curline_Off_Time,13,2))   END\n" +
                "\t,COALESCE(OD_Curline_Run_Distance,0)\n" +
                "\t,COALESCE(OD_Curline_Run_Duration,0)\n" +
                "\t,COALESCE(Trafin_Curline_Wait_Duration,0)\n" +
                "\t,COALESCE(Wait_Duration,0)\n" +
                "\t,COALESCE(Trafin_Curline_Stay_Cnt,0)\n" +
                "\t,COALESCE(Trafin_Curline_Walk_Duration,0)\n" +
                "\t,COALESCE(Pass_Curline_Station_Cnt,0)\n" +
                "\t,CASE WHEN floor(A1.PRODUCT_ISSUER_ID)=1 AND T4.Prod_ID IS NOT NULL THEN T4.Prod_ID\n" +
                "          WHEN floor(A1.PRODUCT_ISSUER_ID)=1 AND T4.Prod_ID IS NULL  THEN CONCAT('S01','01',TRIM(CAST(FLOOR(A1.PRODUCT_TYPE) AS STRING)) )\n" +
                "          WHEN floor(A1.PRODUCT_ISSUER_ID)=99 THEN  CONCAT('S01','02',TRIM(CAST(FLOOR(A1.PRODUCT_TYPE) AS STRING)))\n" +
                "\t      WHEN floor(A1.PRODUCT_ISSUER_ID)=9 THEN  CONCAT('S01','09',TRIM(CAST(FLOOR(A1.PRODUCT_TYPE) AS STRING)))\n" +
                "  \t      ELSE ''\n" +
                "  \t END  \tas PROD--产品编号\n" +
                "\t,COALESCE(A5.CLR_AMT,0)\n" +
                "\t,COALESCE(replace(A1.EXCEPTION_LIST,'null',''),'')\n" +
                "\t,COALESCE(A1.FLAG,0)\n" +
                "\t,'01' --每日正常文件\n" +
                "\t,CAST('${HIVECONFTXDATE}' AS string)\n" +
                "\t,TRIM(COALESCE(cast(A1.MOT_ENTRY_INDUSTRYCODE as string),''))\n" +
                "\t,TRIM(CAST(CAST(A1.PRODUCT_ACTION_SEQUENCE_NUMBER AS decimal) AS string))\n" +
                "FROM (SELECT * FROM ${BMNC_SDATA}.CUT_PI_EXIT_S_LINEDESC WHERE data_dt='${HIVECONFTXDATE}')  A\n" +
                "JOIN (SELECT * from ${BMNC_SDATA}.cut_pi_exit_cms_filtered WHERE data_dt='${HIVECONFTXDATE}') A1 ON  A.REC_SEQ = A1.REC_SEQ\n" +
                "LEFT JOIN ${BMNC_PCODEVW}.T99_SRC_PROD_RELA_CD T4\n" +
                "    ON  A1.PRODUCT_ISSUER_ID =T4.PRODUCT_ISSUER_ID\n" +
                "    AND A1.CARD_TYPE = T4.CARD_TYPE\n" +
                "    AND A1.PRODUCT_TYPE = T4.PRODUCT_TYPE\n" +
                "\n" +
                "LEFT JOIN (select Src_Station_Cd,Station_ID,Start_Tm, End_Tm from ${BMNC_PCODE}.T99_SRC_STATION_RELA_HIS   where Source_Sys_Cd = 'S01') A2\n" +
                "    ON  A.In_Curline_Station_Id = A2.Src_Station_Cd\n" +
                "\n" +
                "LEFT JOIN (select Src_Station_Cd,Station_ID,Start_Tm, End_Tm from ${BMNC_PCODE}.T99_SRC_STATION_RELA_HIS where Source_Sys_Cd = 'S01') A21\n" +
                "    ON  A.In_Curline_Station_Id = A21.Src_Station_Cd\n" +
                "\n" +
                "LEFT JOIN (select Src_Station_Cd,Station_ID,Start_Tm, End_Tm from ${BMNC_PCODE}.T99_SRC_STATION_RELA_HIS   where Source_Sys_Cd = 'S01') A3\n" +
                "    ON  A.Out_Curline_Station_Id = A3.Src_Station_Cd\n" +
                "\n" +
                "LEFT JOIN (select Src_Station_Cd,Station_ID,Start_Tm, End_Tm from ${BMNC_PCODE}.T99_SRC_STATION_RELA_HIS where Source_Sys_Cd = 'S01') A31\n" +
                "    ON  A.Out_Curline_Station_Id = A31.Src_Station_Cd\n" +
                "\n" +
                "LEFT JOIN (select Line_ID,Src_Line_ID,Start_Tm, End_Tm  from ${BMNC_PCODE}.T99_SRC_LINE_RELA_HIS where Source_Sys_Cd = 'S01') A4\n" +
                "    ON lpad(cast(cast(A.Line_ID AS bigint) as string),3,'0')= A4.Src_Line_ID\n" +
                "\n" +
                "LEFT JOIN (select Line_ID,Src_Line_ID,Start_Tm, End_Tm  from ${BMNC_PCODE}.T99_SRC_LINE_RELA_HIS where Source_Sys_Cd = 'S01') A41\n" +
                "    ON lpad(cast(cast(A.Line_ID AS bigint) as string),3,'0')= A41.Src_Line_ID\n" +
                "\n" +
                "LEFT JOIN (SELECT * FROM ${BMNC_SDATA}.CUT_PI_EXIT_S_ACCLIST  WHERE data_dt='${HIVECONFTXDATE}') A5\n" +
                "      ON A.REC_SEQ = A5.REC_SEQ AND A.inLine_NO = A5.inLine_NO\n" +
                "WHERE\n" +
                "      (CASE WHEN floor(A1.PRODUCT_ISSUER_ID)=1 AND T4.Prod_ID IS NOT NULL THEN T4.Prod_ID\n" +
                "       WHEN floor(A1.PRODUCT_ISSUER_ID)=1 AND T4.Prod_ID IS NULL   THEN CONCAT('S01','01',TRIM(CAST(A1.PRODUCT_TYPE AS STRING)) )\n" +
                "       WHEN floor(A1.PRODUCT_ISSUER_ID)=99 THEN  CONCAT('S01','02',TRIM(CAST(A1.PRODUCT_TYPE AS STRING)))\n" +
                "\t   WHEN floor(A1.PRODUCT_ISSUER_ID)=9 THEN  CONCAT('S01','09',TRIM(CAST(A1.PRODUCT_TYPE AS STRING)))\n" +
                "  \tELSE '' END)  IS NOT NULL\n" +
                "    and (CASE WHEN (A.In_Curline_On_Time IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8)  END)-date_format(T4.Effect_Dt,'yyyyMMdd') >=0\n" +
                "    and (CASE WHEN (A.In_Curline_On_Time IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8)  END)-date_format(T4.Invalid_Dt,'yyyyMMdd')< 0\t\n" +
                "    \n" +
                "\tand (CASE WHEN (A.In_Curline_On_Time IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8)  END)-date_format(A2.Start_Tm,'yyyyMMdd') >=0\n" +
                "    and (CASE WHEN (A.In_Curline_On_Time IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8)  END)-date_format(A2.End_Tm,'yyyyMMdd')< 0\t\n" +
                "    \n" +
                "    and unix_timestamp(A1.TXN_DATE_TIME)-unix_timestamp(A21.Start_Tm)>=0\n" +
                "    and unix_timestamp(A1.TXN_DATE_TIME)-unix_timestamp(A21.End_Tm)< 0\t\n" +
                "\t\n" +
                "\tAND (CASE WHEN (A.In_Curline_On_Time IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8)  END)-date_format(A3.Start_Tm,'yyyyMMdd') >=0\n" +
                "    and (CASE WHEN (A.In_Curline_On_Time IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8)  END)-date_format(A3.End_Tm,'yyyyMMdd')< 0\n" +
                "    \n" +
                "\tAND unix_timestamp(A1.TXN_DATE_TIME)-unix_timestamp(A31.Start_Tm)>=0\n" +
                "    and unix_timestamp(A1.TXN_DATE_TIME)-unix_timestamp(A31.End_Tm)< 0\t\n" +
                "    \n" +
                "\tand (CASE WHEN (A.In_Curline_On_Time  IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8) END)-date_format(A4.Start_Tm,'yyyyMMdd')>=0\n" +
                "    and (CASE WHEN (A.In_Curline_On_Time  IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8) END)-date_format(A4.End_Tm,'yyyyMMdd')< 0\t\n" +
                "    \n" +
                "\tAND unix_timestamp(A1.TXN_DATE_TIME)-unix_timestamp(A41.Start_Tm)>=0\n" +
                "    and unix_timestamp(A1.TXN_DATE_TIME)-unix_timestamp(A41.End_Tm)< 0\t\n" +
                "\t\n" +
                "\t;\n" +
                "\n" +
                "--补全前一天出站交易数据\n" +
                "INSERT INTO UDMS_TEMP.VT_T80_OD_LINE_temp2\n" +
                "SELECT\n" +
                "\tCONCAT(COALESCE(CONCAT('S01','02',date_format(COALESCE(A1.TXN_DATE_TIME,CAST('0001-01-01 00:00:00'AS TIMESTAMP)) ,'yyyyMMddHHmmss')\n" +
                "\t\t,lpad(CAST(CAST(COALESCE(A1.DEVICE_ID,0) AS bigint) AS string),10,'0')\n" +
                "\t\t,lpad(CAST(CAST(COALESCE(A1.UDSN,0) AS bigint) AS string),10,'0')\n" +
                "\t\t,date_format(cast('${HIVECONFTXDATE}' as string),'yyyyMMdd'),lpad(CAST(CAST(A1.REC_SEQ AS bigint) AS string),10,'0'),''))\n" +
                "\t\t,'02')\n" +
                "    ,CAST (A.Line_Seq_Num AS INTEGER)\n" +
                "    ,case when( A4.Line_ID IS NULL or A4.Line_ID= '')\n" +
                "\t      THEN  COALESCE(A41.Line_ID ,'00')\n" +
                "\t      ELSE A4.Line_ID\n" +
                "\t END\n" +
                "\t,CASE WHEN Curline_Trip_Drct_Cd = '1' THEN '01'\n" +
                "\t\t  WHEN Curline_Trip_Drct_Cd = '2' THEN '02'\n" +
                "\t\t  WHEN Curline_Trip_Drct_Cd = '0' THEN '03'\n" +
                "\t\t  ELSE COALESCE(TRIM(Curline_Trip_Drct_Cd),'')\n" +
                "\t END -- modify by lixuan at 20130815\n" +
                "\t,case when( A2.Station_ID IS NULL or  A2.Station_ID= '')\n" +
                "\t    THEN  COALESCE(A21.Station_ID ,'0000')\n" +
                "\t    ELSE A2.Station_ID\n" +
                "\t END\n" +
                "\t,case when( A3.Station_ID IS NULL or  A3.Station_ID= '')\n" +
                "\t    THEN  COALESCE(A31.Station_ID ,'0000')\n" +
                "\t    ELSE A3.Station_ID\n" +
                "\t END\n" +
                "\t,concat((CASE WHEN (In_Curline_On_Time IS NULL or In_Curline_On_Time= '') THEN  '0001-01-01'\n" +
                "     \t\t      ELSE concat(substr(In_Curline_On_Time,0,4),'-',substr(In_Curline_On_Time,5,2),'-',substr(In_Curline_On_Time,7,2))\n" +
                "             END)\n" +
                "            ,' '\n" +
                "            ,(CASE WHEN (In_Curline_On_Time IS NULL or In_Curline_On_Time = '') THEN  '00:00:00'\n" +
                " \t\t\t\t    ELSE CONCAT(substr(In_Curline_On_Time,9,2) , ':' , substr(In_Curline_On_Time,11,2) , ':' ,substr(In_Curline_On_Time,13,2) )\n" +
                "  \t\t\t  END)\n" +
                "  \t\t\t)\n" +
                "\t,CASE WHEN  (In_Curline_On_Time  IS NULL  or  In_Curline_On_Time = '') THEN  '0001-01-01' ELSE concat(substr(In_Curline_On_Time,0,4),'-',substr(In_Curline_On_Time,5,2),'-',substr(In_Curline_On_Time,7,2))  END\n" +
                "\t,CASE WHEN  (In_Curline_On_Time  IS NULL  or  In_Curline_On_Time = '') THEN  '00:00:00'   ELSE CONCAT(substr(In_Curline_On_Time,9,2)  , ':' , substr(In_Curline_On_Time,11,2)  , ':'  ,substr(In_Curline_On_Time,13,2))   END\n" +
                "\t,concat( (CASE WHEN (Out_Curline_Off_Time  IS NULL  or  Out_Curline_Off_Time = '') THEN  '0001-01-01'\n" +
                "\t\t\t\t   ELSE concat(substr(Out_Curline_Off_Time,0,4),'-',substr(Out_Curline_Off_Time,5,2),'-',substr(Out_Curline_Off_Time,7,2))\n" +
                " \t\t\t   END )\n" +
                " \t\t\t ,' '\n" +
                " \t\t\t ,(CASE WHEN (Out_Curline_Off_Time  IS NULL  or  Out_Curline_Off_Time = '') THEN '00:00:00'\n" +
                " \t\t\t\t\tELSE CONCAT(substr(Out_Curline_Off_Time,9,2) , ':' ,substr(Out_Curline_Off_Time,11,2) , ':' ,substr(Out_Curline_Off_Time,13,2))\n" +
                " \t\t\t   END)\n" +
                " \t\t\t)\n" +
                "\t,CASE WHEN  (Out_Curline_Off_Time  IS NULL  or  Out_Curline_Off_Time = '')  THEN  '0001-01-01' ELSE concat(substr(Out_Curline_Off_Time,0,4),'-',substr(Out_Curline_Off_Time,5,2),'-',substr(Out_Curline_Off_Time,7,2)) END\n" +
                "\t,CASE WHEN  (Out_Curline_Off_Time  IS NULL  or  Out_Curline_Off_Time = '')  THEN  '00:00:00'   ELSE CONCAT(substr(Out_Curline_Off_Time,9,2)  , ':' , substr(Out_Curline_Off_Time,11,2)  , ':'  ,substr(Out_Curline_Off_Time,13,2))   END\n" +
                "\t,COALESCE(OD_Curline_Run_Distance,0)\n" +
                "\t,COALESCE(OD_Curline_Run_Duration,0)\n" +
                "\t,COALESCE(Trafin_Curline_Wait_Duration,0)\n" +
                "\t,COALESCE(Wait_Duration,0)\n" +
                "\t,COALESCE(Trafin_Curline_Stay_Cnt,0)\n" +
                "\t,COALESCE(Trafin_Curline_Walk_Duration,0)\n" +
                "\t,COALESCE(Pass_Curline_Station_Cnt,0)\n" +
                "\t,CASE WHEN floor(A1.PRODUCT_ISSUER_ID)=1 AND T4.Prod_ID IS NOT NULL THEN T4.Prod_ID\n" +
                "          WHEN floor(A1.PRODUCT_ISSUER_ID)=1 AND T4.Prod_ID IS NULL   THEN CONCAT('S01','01',TRIM(CAST(FLOOR(A1.PRODUCT_TYPE) AS STRING)) )\n" +
                "          WHEN floor(A1.PRODUCT_ISSUER_ID)=99 THEN  CONCAT('S01','02',TRIM(CAST(FLOOR(A1.PRODUCT_TYPE) AS STRING)))\n" +
                "\t      WHEN floor(A1.PRODUCT_ISSUER_ID)=9 THEN  CONCAT('S01','09',TRIM(CAST(FLOOR(A1.PRODUCT_TYPE) AS STRING)))\n" +
                "  \t ELSE ''\n" +
                "  \t END  \tas PROD--产品编号\n" +
                "\t,COALESCE(A5.CLR_AMT,0)\n" +
                "\t,COALESCE(replace(A1.EXCEPTION_LIST,'null',''),'')\n" +
                "\t,COALESCE(A1.FLAG,0)\n" +
                "\t,'02' --前一天出站交易文件\n" +
                "\t,CAST('${HIVECONFTXDATE}' AS string)\n" +
                "\t,TRIM(COALESCE(cast(A1.MOT_ENTRY_INDUSTRYCODE as string),''))\n" +
                "\t,TRIM(CAST(CAST(A1.PRODUCT_ACTION_SEQUENCE_NUMBER AS decimal) AS string))\n" +
                "FROM (SELECT * FROM ${BMNC_SDATA}.CUT_PI_EXIT_O_LINEDESC WHERE data_dt='${HIVECONFTXDATE}')  A\n" +
                "JOIN (SELECT * from ${BMNC_SDATA}.cut_pi_exit_cms_o_filtered WHERE data_dt='${HIVECONFTXDATE}') A1 ON  A.REC_SEQ = A1.REC_SEQ\n" +
                "LEFT JOIN ${BMNC_PCODEVW}.T99_SRC_PROD_RELA_CD T4\n" +
                "    ON  A1.PRODUCT_ISSUER_ID  = T4.PRODUCT_ISSUER_ID\n" +
                "    AND A1.CARD_TYPE = T4.CARD_TYPE\n" +
                "    AND A1.PRODUCT_TYPE = T4.PRODUCT_TYPE\n" +
                "\n" +
                "LEFT JOIN (select Src_Station_Cd,Station_ID,Start_Tm, End_Tm from ${BMNC_PCODE}.T99_SRC_STATION_RELA_HIS   where Source_Sys_Cd = 'S01') A2\n" +
                "    ON  A.In_Curline_Station_Id = A2.Src_Station_Cd\n" +
                "\n" +
                "LEFT JOIN (select Src_Station_Cd,Station_ID,Start_Tm, End_Tm from ${BMNC_PCODE}.T99_SRC_STATION_RELA_HIS where Source_Sys_Cd = 'S01') A21\n" +
                "    ON  A.In_Curline_Station_Id = A21.Src_Station_Cd\n" +
                "\n" +
                "LEFT JOIN (select Src_Station_Cd,Station_ID,Start_Tm, End_Tm from ${BMNC_PCODE}.T99_SRC_STATION_RELA_HIS   where Source_Sys_Cd = 'S01') A3\n" +
                "    ON  A.Out_Curline_Station_Id = A3.Src_Station_Cd\n" +
                "\n" +
                "LEFT JOIN (select Src_Station_Cd,Station_ID,Start_Tm, End_Tm from ${BMNC_PCODE}.T99_SRC_STATION_RELA_HIS where Source_Sys_Cd = 'S01') A31\n" +
                "    ON  A.Out_Curline_Station_Id = A31.Src_Station_Cd\n" +
                "\n" +
                "LEFT JOIN (select Line_ID,Src_Line_ID,Start_Tm, End_Tm  from ${BMNC_PCODE}.T99_SRC_LINE_RELA_HIS where Source_Sys_Cd = 'S01') A4\n" +
                "    ON lpad(cast(cast(A.Line_ID AS bigint) as string),3,'0')= A4.Src_Line_ID\n" +
                "\n" +
                "LEFT JOIN (select Line_ID,Src_Line_ID,Start_Tm, End_Tm  from ${BMNC_PCODE}.T99_SRC_LINE_RELA_HIS where Source_Sys_Cd = 'S01') A41\n" +
                "    ON lpad(cast(cast(A.Line_ID AS bigint) as string),3,'0')= A41.Src_Line_ID\n" +
                "\n" +
                "LEFT JOIN (SELECT * from ${BMNC_SDATA}.CUT_PI_EXIT_O_ACCLIST  WHERE data_dt='${HIVECONFTXDATE}') A5 ON A.REC_SEQ = A5.REC_SEQ AND A.inLine_NO = A5.inLine_NO\n" +
                "WHERE\n" +
                "    (CASE WHEN floor(A1.PRODUCT_ISSUER_ID)=1 AND T4.Prod_ID IS NOT NULL THEN T4.Prod_ID\n" +
                "          WHEN floor(A1.PRODUCT_ISSUER_ID)=1 AND T4.Prod_ID IS NULL   THEN CONCAT('S01','01',TRIM(CAST(A1.PRODUCT_TYPE AS STRING)) )\n" +
                "          WHEN floor(A1.PRODUCT_ISSUER_ID)=99 THEN  CONCAT('S01','02',TRIM(CAST(A1.PRODUCT_TYPE AS STRING)))\n" +
                "\t      WHEN floor(A1.PRODUCT_ISSUER_ID)=9 THEN  CONCAT('S01','09',TRIM(CAST(A1.PRODUCT_TYPE AS STRING)))\n" +
                "  \t ELSE '' END)  IS NOT NULL\n" +
                "\t \n" +
                "      and (CASE WHEN (A.In_Curline_On_Time IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8)  END)-date_format(T4.Effect_Dt,'yyyyMMdd') >=0\n" +
                "    and (CASE WHEN (A.In_Curline_On_Time IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8)  END)-date_format(T4.Invalid_Dt,'yyyyMMdd')< 0\t \n" +
                "\t \n" +
                "    and (CASE WHEN (A.In_Curline_On_Time IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8)  END)-date_format(A2.Start_Tm,'yyyyMMdd') >=0\n" +
                "    and (CASE WHEN (A.In_Curline_On_Time IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8)  END)-date_format(A2.End_Tm,'yyyyMMdd')< 0\t \n" +
                "\t\n" +
                "    AND (CASE WHEN (A.In_Curline_On_Time IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8)  END)-date_format(A3.Start_Tm,'yyyyMMdd') >=0\n" +
                "    and (CASE WHEN (A.In_Curline_On_Time IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8)  END)-date_format(A3.End_Tm,'yyyyMMdd')< 0\t\n" +
                "    \n" +
                "\tand unix_timestamp(A1.TXN_DATE_TIME)-unix_timestamp(A21.Start_Tm)>=0\n" +
                "    and unix_timestamp(A1.TXN_DATE_TIME)-unix_timestamp(A21.End_Tm)< 0\t \n" +
                "    AND unix_timestamp(A1.TXN_DATE_TIME)-unix_timestamp(A31.Start_Tm)>=0\n" +
                "    and unix_timestamp(A1.TXN_DATE_TIME)-unix_timestamp(A31.End_Tm)< 0\t\n" +
                "    and (CASE WHEN (A.In_Curline_On_Time  IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8) END)-date_format(A4.Start_Tm,'yyyyMMdd')>=0\n" +
                "    and (CASE WHEN (A.In_Curline_On_Time  IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8) END)-date_format(A4.End_Tm,'yyyyMMdd')< 0\t\n" +
                "    AND unix_timestamp(A1.TXN_DATE_TIME)-unix_timestamp(A41.Start_Tm)>=0\n" +
                "    and unix_timestamp(A1.TXN_DATE_TIME)-unix_timestamp(A41.End_Tm)< 0\t\n" +
                "\t\n" +
                ";\n" +
                "\n" +
                "--补全七天前出站交易数据\n" +
                "INSERT INTO UDMS_TEMP.VT_T80_OD_LINE_temp2\n" +
                "SELECT\n" +
                "\tCONCAT(COALESCE(CONCAT('S01','02',date_format(COALESCE(A1.TXN_DATE_TIME,CAST('0001-01-01 00:00:00'AS TIMESTAMP)) ,'yyyyMMddHHmmss')\n" +
                "\t\t,lpad(CAST(CAST(COALESCE(A1.DEVICE_ID,0) AS bigint) AS string),10,'0')\n" +
                "\t\t,lpad(CAST(CAST(COALESCE(A1.UDSN,0) AS bigint) AS string),10,'0')\n" +
                "\t\t,date_format(cast('${HIVECONFTXDATE}' as string),'yyyyMMdd'),lpad(CAST(CAST(A1.REC_SEQ AS bigint) AS string),10,'0'),''))\n" +
                "\t\t,'03')--OD编号   modify by lixuan at 20130730\n" +
                "    ,CAST (A.Line_Seq_Num AS INTEGER)\n" +
                "    ,case when( A4.Line_ID IS NULL or A4.Line_ID= '')\n" +
                "\t      THEN  COALESCE(A41.Line_ID ,'00')\n" +
                "\t      ELSE A4.Line_ID\n" +
                "\t END\n" +
                "    ,CASE WHEN Curline_Trip_Drct_Cd = '1' THEN '01'\n" +
                "\t\t  WHEN Curline_Trip_Drct_Cd = '2' THEN '02'\n" +
                "\t\t  WHEN Curline_Trip_Drct_Cd = '0' THEN '03'\n" +
                "\t ELSE COALESCE(TRIM(Curline_Trip_Drct_Cd),'')\n" +
                "\t END -- modify by lixuan at 20130815\n" +
                "\t,case when( A2.Station_ID IS NULL or  A2.Station_ID= '') THEN  COALESCE(A21.Station_ID ,'0000')\n" +
                "\t ELSE A2.Station_ID\n" +
                "\t END\n" +
                "\t,case when( A3.Station_ID IS NULL or  A3.Station_ID= '')\n" +
                "\t      THEN  COALESCE(A31.Station_ID ,'0000')\n" +
                "\t ELSE A3.Station_ID\n" +
                "\t END\n" +
                "    ,concat((CASE WHEN (In_Curline_On_Time  IS NULL  or  In_Curline_On_Time= '') THEN  '0001-01-01'\n" +
                "     \t\t      ELSE concat(substr(In_Curline_On_Time,0,4),'-',substr(In_Curline_On_Time,5,2),'-',substr(In_Curline_On_Time,7,2))\n" +
                "             END)\n" +
                "           ,' '\n" +
                "           , (CASE WHEN (In_Curline_On_Time  IS NULL  or  In_Curline_On_Time = '') THEN  '00:00:00'\n" +
                " \t\t\t\t   ELSE CONCAT(substr(In_Curline_On_Time,9,2)  , ':' , substr(In_Curline_On_Time,11,2)  , ':'  ,substr(In_Curline_On_Time,13,2) )\n" +
                "  \t\t\t  END)\n" +
                "  \t\t\t)\n" +
                "\t,CASE WHEN  (In_Curline_On_Time  IS NULL  or  In_Curline_On_Time = '') THEN '0001-01-01' ELSE concat(substr(In_Curline_On_Time,0,4),'-',substr(In_Curline_On_Time,5,2),'-',substr(In_Curline_On_Time,7,2))  END\n" +
                "\t,CASE WHEN  (In_Curline_On_Time  IS NULL  or  In_Curline_On_Time = '') THEN '00:00:00'   ELSE CONCAT(substr(In_Curline_On_Time,9,2) , ':' ,substr(In_Curline_On_Time,11,2)  , ':'  ,substr(In_Curline_On_Time,13,2))   END\n" +
                "\t,concat( (CASE WHEN (Out_Curline_Off_Time  IS NULL  or  Out_Curline_Off_Time = '') THEN   '0001-01-01'\n" +
                "\t\t\t       ELSE concat(substr(Out_Curline_Off_Time,0,4),'-',substr(Out_Curline_Off_Time,5,2),'-',substr(Out_Curline_Off_Time,7,2))\n" +
                " \t\t\t  END )\n" +
                " \t\t\t ,' '\n" +
                " \t\t\t ,(CASE WHEN (Out_Curline_Off_Time  IS NULL or Out_Curline_Off_Time = '') THEN   '00:00:00'\n" +
                " \t\t\t\t    ELSE CONCAT(substr(Out_Curline_Off_Time,9,2) ,':', substr(Out_Curline_Off_Time,11,2) ,':',substr(Out_Curline_Off_Time,13,2))\n" +
                " \t\t\t   END)\n" +
                " \t\t\t)\n" +
                "\t,CASE WHEN (Out_Curline_Off_Time  IS NULL or Out_Curline_Off_Time = '')  THEN   '0001-01-01' ELSE concat(substr(Out_Curline_Off_Time,0,4),'-',substr(Out_Curline_Off_Time,5,2),'-',substr(Out_Curline_Off_Time,7,2)) END\n" +
                "\t,CASE WHEN (Out_Curline_Off_Time  IS NULL or Out_Curline_Off_Time = '')  THEN   '00:00:00'   ELSE CONCAT(substr(Out_Curline_Off_Time,9,2) , ':' ,substr(Out_Curline_Off_Time,11,2)  , ':'  ,substr(Out_Curline_Off_Time,13,2))   END\n" +
                "\t,COALESCE(OD_Curline_Run_Distance,0)\n" +
                "\t,COALESCE(OD_Curline_Run_Duration,0)\n" +
                "\t,COALESCE(Trafin_Curline_Wait_Duration,0)\n" +
                "\t,COALESCE(Wait_Duration,0)\n" +
                "\t,COALESCE(Trafin_Curline_Stay_Cnt,0)\n" +
                "\t,COALESCE(Trafin_Curline_Walk_Duration,0)\n" +
                "\t,COALESCE(Pass_Curline_Station_Cnt,0)\n" +
                "\t,CASE WHEN floor(A1.PRODUCT_ISSUER_ID)=1 AND T4.Prod_ID IS NOT NULL THEN T4.Prod_ID\n" +
                "          WHEN floor(A1.PRODUCT_ISSUER_ID)=1 AND T4.Prod_ID IS NULL  THEN CONCAT('S01','01',TRIM(CAST(FLOOR(A1.PRODUCT_TYPE) AS STRING)) )\n" +
                "          WHEN floor(A1.PRODUCT_ISSUER_ID)=99 THEN CONCAT('S01','02',TRIM(CAST(FLOOR(A1.PRODUCT_TYPE) AS STRING)))\n" +
                "\t      WHEN floor(A1.PRODUCT_ISSUER_ID)=9 THEN  CONCAT('S01','09',TRIM(CAST(FLOOR(A1.PRODUCT_TYPE) AS STRING)))\n" +
                "  \t ELSE ''\n" +
                "  \t END  as PROD--产品编号\n" +
                "\t,COALESCE(A5.CLR_AMT,0)\n" +
                "\t,COALESCE(replace(A1.EXCEPTION_LIST,'null',''),'')\n" +
                "\t,COALESCE(A1.FLAG,0)\n" +
                "\t,'03' --七天前出站交易文件\n" +
                "\t,CAST('${HIVECONFTXDATE}' AS string)\n" +
                "    ,TRIM(COALESCE(cast(A1.MOT_ENTRY_INDUSTRYCODE as string),''))\n" +
                "\t,TRIM(CAST(CAST(A1.PRODUCT_ACTION_SEQUENCE_NUMBER AS decimal) AS string))\n" +
                "FROM (SELECT * FROM ${BMNC_SDATA}.CUT_PI_EXIT_E_LINEDESC WHERE data_dt='${HIVECONFTXDATE}')  A\n" +
                "JOIN (SELECT * from  ${BMNC_SDATA}.cut_pi_exit_cms_e_filtered WHERE data_dt='${HIVECONFTXDATE}') A1 ON  A.REC_SEQ = A1.REC_SEQ\n" +
                "LEFT JOIN ${BMNC_PCODEVW}.T99_SRC_PROD_RELA_CD T4\n" +
                "    ON  A1.PRODUCT_ISSUER_ID = T4.PRODUCT_ISSUER_ID\n" +
                "    AND A1.CARD_TYPE = T4.CARD_TYPE\n" +
                "    AND A1.PRODUCT_TYPE = T4.PRODUCT_TYPE\n" +
                "\n" +
                "LEFT JOIN (select Src_Station_Cd,Station_ID,Start_Tm, End_Tm from ${BMNC_PCODE}.T99_SRC_STATION_RELA_HIS   where Source_Sys_Cd = 'S01') A2\n" +
                "    ON  A.In_Curline_Station_Id = A2.Src_Station_Cd\n" +
                "\n" +
                "LEFT JOIN (select Src_Station_Cd,Station_ID,Start_Tm, End_Tm from ${BMNC_PCODE}.T99_SRC_STATION_RELA_HIS where Source_Sys_Cd = 'S01') A21\n" +
                "    ON  A.In_Curline_Station_Id = A21.Src_Station_Cd\n" +
                "\n" +
                "LEFT JOIN (select Src_Station_Cd,Station_ID,Start_Tm, End_Tm from ${BMNC_PCODE}.T99_SRC_STATION_RELA_HIS   where Source_Sys_Cd = 'S01') A3\n" +
                "    ON  A.Out_Curline_Station_Id = A3.Src_Station_Cd\n" +
                "\n" +
                "LEFT JOIN (select Src_Station_Cd,Station_ID,Start_Tm, End_Tm from ${BMNC_PCODE}.T99_SRC_STATION_RELA_HIS where Source_Sys_Cd = 'S01') A31\n" +
                "    ON  A.Out_Curline_Station_Id = A31.Src_Station_Cd\n" +
                "\n" +
                "LEFT JOIN (select Line_ID,Src_Line_ID,Start_Tm, End_Tm  from ${BMNC_PCODE}.T99_SRC_LINE_RELA_HIS where Source_Sys_Cd = 'S01') A4\n" +
                "    ON lpad(cast(cast(A.Line_ID AS bigint) as string),3,'0')= A4.Src_Line_ID\n" +
                "\n" +
                "LEFT JOIN (select Line_ID,Src_Line_ID,Start_Tm, End_Tm  from ${BMNC_PCODE}.T99_SRC_LINE_RELA_HIS where Source_Sys_Cd = 'S01') A41\n" +
                "    ON lpad(cast(cast(A.Line_ID AS bigint) as string),3,'0')= A41.Src_Line_ID\n" +
                "\n" +
                "LEFT JOIN (SELECT * FROM ${BMNC_SDATA}.CUT_PI_EXIT_E_ACCLIST  WHERE data_dt='${HIVECONFTXDATE}') A5 ON A.REC_SEQ = A5.REC_SEQ AND A.inLine_NO = A5.inLine_NO\n" +
                "WHERE\n" +
                " (CASE WHEN floor(A1.PRODUCT_ISSUER_ID)=1 AND T4.Prod_ID IS NOT NULL THEN T4.Prod_ID\n" +
                "WHEN floor(A1.PRODUCT_ISSUER_ID)=1 AND T4.Prod_ID IS NULL   THEN CONCAT('S01','01',TRIM(CAST(A1.PRODUCT_TYPE AS STRING)) )\t\t--S0101170002\n" +
                "      WHEN floor(A1.PRODUCT_ISSUER_ID)=99 THEN  CONCAT('S01','02',TRIM(CAST(A1.PRODUCT_TYPE AS STRING)))\n" +
                "\t  WHEN floor(A1.PRODUCT_ISSUER_ID)=9 THEN  CONCAT('S01','09',TRIM(CAST(A1.PRODUCT_TYPE AS STRING)))\n" +
                "  \tELSE '' END)  IS NOT NULL\n" +
                "      and (CASE WHEN (A.In_Curline_On_Time IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8)  END)-date_format(T4.Effect_Dt,'yyyyMMdd') >=0\n" +
                "    and (CASE WHEN (A.In_Curline_On_Time IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8)  END)-date_format(T4.Invalid_Dt,'yyyyMMdd')< 0\t\n" +
                "    and (CASE WHEN (A.In_Curline_On_Time IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8)  END)-date_format(A2.Start_Tm,'yyyyMMdd') >=0\n" +
                "    and (CASE WHEN (A.In_Curline_On_Time IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8)  END)-date_format(A2.End_Tm,'yyyyMMdd')< 0\t\n" +
                "    and unix_timestamp(A1.TXN_DATE_TIME)-unix_timestamp(A21.Start_Tm)>=0\n" +
                "    and unix_timestamp(A1.TXN_DATE_TIME)-unix_timestamp(A21.End_Tm)< 0\t\n" +
                "    AND (CASE WHEN (A.In_Curline_On_Time IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8)  END)-date_format(A3.Start_Tm,'yyyyMMdd') >=0\n" +
                "    and (CASE WHEN (A.In_Curline_On_Time IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8)  END)-date_format(A3.End_Tm,'yyyyMMdd')< 0\t\n" +
                "    AND unix_timestamp(A1.TXN_DATE_TIME)-unix_timestamp(A31.Start_Tm)>=0\n" +
                "    and unix_timestamp(A1.TXN_DATE_TIME)-unix_timestamp(A31.End_Tm)< 0\t\n" +
                "    and (CASE WHEN (A.In_Curline_On_Time  IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8) END)-date_format(A4.Start_Tm,'yyyyMMdd')>=0\n" +
                "    and (CASE WHEN (A.In_Curline_On_Time  IS NULL or A.In_Curline_On_Time = '') THEN '00010101' ELSE substr(A.In_Curline_On_Time,0,8) END)-date_format(A4.End_Tm,'yyyyMMdd')< 0\t\n" +
                "    AND unix_timestamp(A1.TXN_DATE_TIME)-unix_timestamp(A41.Start_Tm)>=0\n" +
                "    and unix_timestamp(A1.TXN_DATE_TIME)-unix_timestamp(A41.End_Tm)< 0\t\n" +
                "\t;\n" +
                "\n" +
                "\n" +
                "\n" +
                "INSERT INTO UDMS_TEMP.VT_T80_OD_LINE_temp\n" +
                "SELECT\n" +
                "   OD_ID                         --OD编号\n" +
                "  ,Line_Seq_Num                  --线路顺序号\n" +
                "  ,Line_ID                       --线路编号\n" +
                "  ,Curline_Trip_Drct_Cd          --本线行车方向代码\n" +
                "  ,In_Curline_Station_Id         --进入本线车站编号\n" +
                "  ,Out_Curline_Station_Id        --离开本线车站编号\n" +
                "  ,In_Curline_On_Time            --进入本线上车时间戳\n" +
                "  ,In_Curline_On_Dt              --进入本线上车日期\n" +
                "  ,In_Curline_On_Tm              --进入本线上车时间\n" +
                "  ,Out_Curline_Off_Time          --离开本线下车时间戳\n" +
                "  ,Out_Curline_Off_Dt            --离开本线下车日期\n" +
                "  ,Out_Curline_Off_Tm            --离开本线下车时间\n" +
                "  ,OD_Curline_Run_Distance       --OD对应本线列车运行距离\n" +
                "  ,OD_Curline_Run_Duration       --OD对应本线列车运行时长\n" +
                "  ,Trafin_Curline_Wait_Duration  --换入本线等候时长\n" +
                "  ,Wait_Duration                 --富余时长\n" +
                "  ,Trafin_Curline_Stay_Cnt       --换乘本线留乘次数\n" +
                "  ,Trafin_Curline_Walk_Duration  --换入本线走行时长\n" +
                "  ,Pass_Curline_Station_Cnt      --经由本线车站数\n" +
                "  ,COALESCE(B.SPLIT_PROD_ID ,C.SPLIT_PROD_ID,D.SPLIT_PROD_ID,A.PROD_ID)  --产品编号\n" +
                "  ,Clr_Amt                       --清分金额\n" +
                "  ,CCH_Irgul_List                --CCH异常列表\n" +
                "  ,UD_Data_Proc_Ind              --UD数据处理标志\n" +
                "  ,Load_File_Type_Cd             --加载文件类型代码\n" +
                "  ,Data_Dt                       --数据日期\n" +
                "FROM UDMS_TEMP.VT_T80_OD_LINE_temp2  A\n" +
                "LEFT JOIN ${BMNC_PCODE}.T99_PROD_SPLIT_CD   B\n" +
                "on A.MOT_ENTRY_INDUSTRYCODE = B.PORD_SPLIT_VALUE\n" +
                "and A.PROD_ID = B.PROD_ID\n" +
                "and B.PROD_SPLIT_TYPE = '3'\n" +
                "\n" +
                "LEFT JOIN ${BMNC_PCODE}.T99_PROD_SPLIT_CD   C\n" +
                "on A.PRODUCT_ACTION_SEQUENCE_NUMBER = C.PORD_SPLIT_VALUE\n" +
                "and A.PROD_ID = C.PROD_ID\n" +
                "and C.PROD_SPLIT_TYPE = '1'\n" +
                "\n" +
                "LEFT JOIN ${BMNC_PCODE}.T99_PROD_SPLIT_CD   D\n" +
                "on A.LINE_ID = D.PORD_SPLIT_VALUE\n" +
                "and A.PROD_ID = D.PROD_ID\n" +
                "and D.PROD_SPLIT_TYPE = '4'\n" +
                "\n" +
                "WHERE (\n" +
                "    (B.PROD_ID IS NOT NULL AND unix_timestamp(A.In_Curline_On_Time)-unix_timestamp(B.Effect_Dt) >= 0 AND unix_timestamp(A.In_Curline_On_Time)-unix_timestamp(B.Invalid_Dt) < 0)\n" +
                "    OR B.PROD_ID IS NULL\n" +
                ") \n" +
                "AND (\n" +
                "    (C.PROD_ID IS NOT NULL AND unix_timestamp(A.In_Curline_On_Time)-unix_timestamp(C.Effect_Dt) >= 0 AND unix_timestamp(A.In_Curline_On_Time)-unix_timestamp(C.Invalid_Dt) < 0)\n" +
                "    OR C.PROD_ID IS NULL\n" +
                ") \n" +
                "AND (\n" +
                "    (D.PROD_ID IS NOT NULL AND unix_timestamp(A.In_Curline_On_Time)-unix_timestamp(D.Effect_Dt) >= 0 AND unix_timestamp(A.In_Curline_On_Time)-unix_timestamp(D.Invalid_Dt) < 0)\n" +
                "    OR D.PROD_ID IS NULL\n" +
                ");\n" +
                "\n" +
                "drop table if exists UDMS_TEMP.t80_od_line_temp_vt;\n" +
                "create table UDMS_TEMP.t80_od_line_temp_vt like ${bmnc_pddl}.T80_OD_LINE;\n" +
                "\n" +
                "INSERT INTO UDMS_TEMP.t80_od_line_temp_vt\n" +
                "SELECT\n" +
                "   a.OD_ID                         --OD编号\n" +
                "  ,a.Line_Seq_Num                  --线路顺序号\n" +
                "  ,a.Line_ID                       --线路编号\n" +
                "  ,a.Curline_Trip_Drct_Cd          --本线行车方向代码\n" +
                "  ,a.In_Curline_Station_Id         --进入本线车站编号\n" +
                "  ,a.Out_Curline_Station_Id        --离开本线车站编号\n" +
                "  ,a.In_Curline_On_Time            --进入本线上车时间戳\n" +
                "  ,a.In_Curline_On_Dt              --进入本线上车日期\n" +
                "  ,a.In_Curline_On_Tm              --进入本线上车时间\n" +
                "  ,a.Out_Curline_Off_Time          --离开本线下车时间戳\n" +
                "  ,a.Out_Curline_Off_Dt            --离开本线下车日期\n" +
                "  ,a.Out_Curline_Off_Tm            --离开本线下车时间\n" +
                "  ,a.OD_Curline_Run_Distance       --OD对应本线列车运行距离\n" +
                "  ,a.OD_Curline_Run_Duration       --OD对应本线列车运行时长\n" +
                "  ,a.Trafin_Curline_Wait_Duration  --换入本线等候时长\n" +
                "  ,a.Wait_Duration                 --富余时长\n" +
                "  ,a.Trafin_Curline_Stay_Cnt       --换乘本线留乘次数\n" +
                "  ,a.Trafin_Curline_Walk_Duration  --换入本线走行时长\n" +
                "  ,a.Pass_Curline_Station_Cnt      --经由本线车站数\n" +
                "  ,case when X.prod_id is null and A.prod_id like 'S0109%' then 'S01099999' else a.prod_id end as prod_id\n" +
                "  ,a.Clr_Amt                       --清分金额\n" +
                "  ,a.CCH_Irgul_List                --CCH异常列表\n" +
                "  ,a.UD_Data_Proc_Ind              --UD数据处理标志\n" +
                "  ,a.Load_File_Type_Cd             --加载文件类型代码\n" +
                "  ,a.Data_Dt                       --数据日期\n" +
                "FROM UDMS_TEMP.VT_T80_OD_LINE_temp A\n" +
                "left JOIN ${BMNC_PDATA}.T50_PRODUCT X    --20210105   新增票种过滤机制\n" +
                "ON A.PROD_ID = X.PROD_ID\n" +
                "\n" +
                "where \n" +
                "(\n" +
                "    (X.PROD_ID IS NOT NULL AND unix_timestamp(A.In_Curline_On_Time)-unix_timestamp(X.Effect_Dt)>= 0\n" +
                "\t\tAND unix_timestamp(A.In_Curline_On_Time)-unix_timestamp(X.Invalid_Dt)< 0)\n" +
                "    OR X.PROD_ID IS NULL\n" +
                ")\n" +
                ";\n" +
                "\n" +
                "\n" +
                "INSERT OVERWRITE TABLE ${BMNC_PDATA}.T80_OD_LINE PARTITION(data_dt='${HIVECONFTXDATE}')\n" +
                "SELECT\n" +
                "\tA.OD_ID ,\n" +
                "\tA.Line_Seq_Num ,\n" +
                "\tA.Line_ID ,\n" +
                "\tA.Curline_Trip_Drct_Cd ,\n" +
                "\tA.In_Curline_Station_Id ,\n" +
                "\tA.Out_Curline_Station_Id ,\n" +
                "\tA.In_Curline_On_Time ,\n" +
                "\tA.In_Curline_On_Dt    ,\n" +
                "\tA.In_Curline_On_Tm ,\n" +
                "\tA.Out_Curline_Off_Time  ,\n" +
                "\tA.Out_Curline_Off_Dt  ,\n" +
                "\tA.Out_Curline_Off_Tm ,\n" +
                "\tA.OD_Curline_Run_Distance ,\n" +
                "\tA.OD_Curline_Run_Duration ,\n" +
                "\tA.Trafin_Curline_Wait_Duration ,\n" +
                "\tA.Wait_Duration ,\n" +
                "\tA.Trafin_Curline_Stay_Cnt ,\n" +
                "\tA.Trafin_Curline_Walk_Duration ,\n" +
                "\tA.Pass_Curline_Station_Cnt ,\n" +
                "\tA.Prod_ID ,\n" +
                "\tA.Clr_Amt ,\n" +
                "\tA.CCH_Irgul_List ,\n" +
                "\tA.UD_Data_Proc_Ind ,\n" +
                "\tA.Load_File_Type_Cd\n" +
                "FROM UDMS_TEMP.t80_od_line_temp_vt A,${BMNC_PDATA}.T50_PRODUCT X\n" +
                "where A.PROD_ID = X.PROD_ID\n" +
                "AND A.In_Curline_On_Time >= X.Effect_Dt\n" +
                "AND A.In_Curline_On_Time < X.Invalid_Dt\n" +
                "AND NOT (\n" +
                "\t\t(TRIM(A.PROD_ID) LIKE 'S010121%' OR TRIM(A.PROD_ID) = 'S010120') AND TRIM(A.LINE_ID) NOT IN ('88','98')\n" +
                "\t\t)\n" +
                ";\n" +
                "\n" +
                "\n" +
                "drop table if exists UDMS_TEMP.VT_T80_OD_LINE_temp;\n" +
                "drop table if exists UDMS_TEMP.VT_T80_OD_LINE_temp2 ;\n" +
                "drop table if exists UDMS_TEMP.t80_od_line_temp_vt;");
    }

    @Test
    void execInsert() throws SQLException {
        exec(DbType.hive,"INSERT INTO test.table1\n" +
                "(dimension_id, dimension_category, dimension_name, dimension_des, table_id, field_id, field_name, dimension_value_name, dimension_value_no)\n" +
                "VALUES('1', '1', '1', '1', '1', '1', '1', '1', '1');");
    }

    @Test
    void execCreate() throws SQLException {
        exec(DbType.hive,"CREATE TABLE `std_pdata.t70_exit`(\n" +
                "  `od_id` string COMMENT 'OD编号', \n" +
                "  `exit_dt` string COMMENT '出站日期', \n" +
                "  `exit_tm` string COMMENT '出站时间', \n" +
                "  `prod_id` string COMMENT '产品编号', \n" +
                "  `gate_id` string COMMENT '闸机编号', \n" +
                "  `exit_station_id` string COMMENT '出站车站编号', \n" +
                "  `exit_line_id` string COMMENT '出站线路编号'\n" +
                "--  `cch_irgul_list` string COMMENT 'CCH异常列表' \n" +
                "--  `data_dttm` timestamp COMMENT '数据时间戳', \n" +
                "--  `data_file_seq` string COMMENT '数据文件序号'\n" +
                ")\n" +
                "PARTITIONED BY ( \n" +
                "  `data_dt` string)\n" +
                "ROW FORMAT SERDE \n" +
                "  'org.apache.hadoop.hive.ql.io.orc.OrcSerde' \n" +
                "STORED AS INPUTFORMAT \n" +
                "  'org.apache.hadoop.hive.ql.io.orc.OrcInputFormat' \n" +
                "OUTPUTFORMAT \n" +
                "  'org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat'");
    }

    @Test
    void execShowDatabases() throws SQLException {
        exec(DbType.hive,"show databases;");
    }

    void exec(DbType dbType,String scripts) throws SQLException {
        getConnection(dbType);
        DBExecuteImpl dbExecute = new DBExecuteImpl();
        DBExecuteImpl.RunningStatus<Object> exec = dbExecute.exec(0,connection, scripts,200);
        String status = exec.getStatus();
        if (status.equals(ExecResult.SUCCESS)) {
            String s = JSON.toJSONString(exec);
            System.out.println(s);
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        dbExecute.release();
        dynamicDriver.close(connection);
    }

}