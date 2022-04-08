package flowdesigner.jdbc.command;

import com.alibaba.druid.util.JdbcUtils;
import com.google.gson.Gson;
import flowdesigner.jdbc.command.impl.DBReverseGetFKReferenceImpl;
import flowdesigner.jdbc.command.model.TableEntity;
import flowdesigner.jdbc.driver.DynamicDriver;
import flowdesigner.jdbc.command.model.FKColumnField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class CommandManagerTest {
    Connection connection = null;
    @BeforeEach
    void setUp() {
//        DynamicDriver dynamicDriver = new DynamicDriver("C:\\文档\\历史\\历史资料\\hive");
        DynamicDriver dynamicDriver = new DynamicDriver("C:\\Users\\nieguangling\\AppData\\Roaming\\DBeaverData\\drivers\\maven\\maven-central\\mysql");
        Properties properties = new Properties();
//        properties.setProperty("driverClassName","org.apache.hive.jdbc.HiveDriver");
//        properties.setProperty("url","jdbc:hive2://10.248.190.13:10000");
        properties.setProperty("driverClassName","com.mysql.cj.jdbc.Driver");
        properties.setProperty("url","jdbc:mysql://localhost:3306");
        properties.setProperty("username","root");
        properties.setProperty("password","123456");
        properties.setProperty("maxWait","3000");
        dynamicDriver.setM_propertyInfo(properties);

        try {
//            dynamicDriver.createDataSource();
            connection = dynamicDriver.getConnection();
        } catch (SQLException e) {
            System.out.println(dynamicDriver.get_errMessage());
            e.printStackTrace();
        }
        assertNotNull(connection);
    }

    @Test
    void testGetAllTables() throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        String catalog = connection.getCatalog();
        String schema = connection.getSchema();

        System.out.println(catalog + "," + schema);

        boolean supportsSchemasInTableDefinitions = connection.getMetaData().supportsSchemasInTableDefinitions();
        boolean supportsCatalogsInTableDefinitions = connection.getMetaData().supportsCatalogsInTableDefinitions();
        System.out.println(supportsCatalogsInTableDefinitions);
        System.out.println(supportsSchemasInTableDefinitions);
        ResultSet rs = meta.getTables(null, null, null, new String[]{"TABLE"});
        List<TableEntity> tableEntities = new ArrayList<TableEntity>();
        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME");
            String TABLE_CAT = rs.getString("TABLE_CAT");
            String TABLE_SCHEM = rs.getString("TABLE_SCHEM");

            System.out.println(tableName + "," + TABLE_CAT + "," + TABLE_SCHEM);

        }
    }

    @AfterEach
    void tearDown() {
        JdbcUtils.close(connection);
    }

    @Test
    void testExecuteUpdate() throws SQLException {
        Gson gson = new Gson();
        ExecResult cc = CommandManager.exeCommand(connection, CommandKey.CMD_DBExecuteUpdateCommandImpl,new HashMap<String,String>(){{
//            put("schemaPattern","test");
            put("SQL","ALTER TABLE test_db11.test_db111 ADD PRIMARY KEY(id);");
        }});
        String s = gson.toJson(cc);
        System.out.println(s);
    }

    @Test
    void testJDBCProperties() throws SQLException {
        Gson gson = new Gson();
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet clientInfoProperties = metaData.getClientInfoProperties();
        while (clientInfoProperties.next()) {
            String NAME = clientInfoProperties.getString("NAME");
            String DEFAULT_VALUE = clientInfoProperties.getString("DEFAULT_VALUE");
            String DESCRIPTION = clientInfoProperties.getString("DESCRIPTION");

            System.out.println(NAME.concat(",") + DEFAULT_VALUE + "," + DESCRIPTION);
        }
//        ResultSet attributes = metaData.getAttributes();
//        while (clientInfoProperties.next()) {
//            String NAME = clientInfoProperties.getString("NAME");
//            String DEFAULT_VALUE = clientInfoProperties.getString("DEFAULT_VALUE");
//            String DESCRIPTION = clientInfoProperties.getString("DESCRIPTION");
//
//            System.out.println(NAME.concat(",") + DEFAULT_VALUE + "," + DESCRIPTION);
//        }
    }

    @Test
    void exeCommand() {
        DynamicDriver dynamicDriver = new DynamicDriver("C:\\文档\\历史\\历史资料\\hive");

        Properties properties = new Properties();
        properties.setProperty("driverClassName","org.apache.hive.jdbc.HiveDriver");
        properties.setProperty("url","jdbc:hive2://10.248.190.13:10000");
        dynamicDriver.setM_propertyInfo(properties);
        Connection connection = null;
        try {
//            dynamicDriver.createDataSource();
            connection = dynamicDriver.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assertNotNull(connection);

    }


    @Test
    void testExeCommandGetSchemas() throws SQLException {
        Gson gson = new Gson();
        ExecResult cc = CommandManager.exeCommand(connection, CommandKey.CMD_DBReverseGetSchemas,new HashMap<String,String>());
        String s = gson.toJson(cc);
        System.out.println(s);

        DatabaseMetaData meta = connection.getMetaData();
        ResultSet catalog = meta.getCatalogs();
        while (catalog.next()) {
            System.out.println(catalog.getString("TABLE_CAT"));
        }

        System.out.println(meta.supportsSchemasInTableDefinitions());//-------
        System.out.println(meta.supportsSchemasInIndexDefinitions());
        System.out.println(meta.supportsSchemasInDataManipulation());
        System.out.println(meta.supportsSchemasInPrivilegeDefinitions());
        System.out.println(meta.supportsSchemasInProcedureCalls());

        ResultSet schemas = meta.getSchemas(null,null);
        while (schemas.next()) {
            String tableSchem = schemas.getString("TABLE_SCHEM");
            /**
             *  SQL Server系统保留表
             *  trace_xe_action_map,trace_xe_event_map
             */
            if (!tableSchem.equalsIgnoreCase("PDMAN_DB_VERSION")
                    && !tableSchem.equalsIgnoreCase("trace_xe_action_map")
                    && !tableSchem.equalsIgnoreCase("trace_xe_event_map")){
                System.out.println(tableSchem);
            }else{
                continue;
            }
        }

    }

    @Test
    void testExeCommandGetDDL() throws SQLException {
        Gson gson = new Gson();
        long start = Instant.now().toEpochMilli();
        ExecResult cc = CommandManager.exeCommand(connection, CommandKey.CMD_DBReverseGetTableDDL,new HashMap<String,String>(){{
            put("schemaPattern","std_pcode");
            put("tables","interface,t99_abn_trip_type_cd,t99_abnor_train_num_type_cd,t99_adjust_status,t99_alarm_level_cd,t99_alarm_status_cd,t99_alarm_type_cd,t99_amt_segment_cd,t99_app_scene_cd,t99_approval_status_cd,t99_area_belong_type_cd,t99_cala_stat_index_mapping,t99_card_discount_cd,t99_card_discount_cd_bak,t99_clr_method_cd,t99_config_point_rela_type_cd,t99_config_point_spcl_cd,t99_config_point_type_cd,t99_cont_info_type_cd,t99_contraband_cate_cd,t99_data_stat_std_cd,t99_date_attr_cd,t99_dectime_stat_index_cd_b,t99_dectm_calc_stat_index_cd,t99_del_reason_cd,t99_delay_later_type_cd,t99_delay_reason_cd,t99_device_spcl_cd,t99_device_status_cd,t99_device_type_cd,t99_direction_cd,t99_discount_type_cd,t99_ee_inst_status_cd,t99_emply_stat_cd,t99_enable_flag_cd,t99_engcspt_cd,t99_equip_cate_cd,t99_equip_cate_cd_b,t99_equip_pty_rela_type_cd,t99_error_device_catg_cd,t99_error_device_type_cd,t99_error_device_type_cdb,t99_error_duty_reason_cd,t99_event_cate_cd,t99_event_cate_cd_his,t99_event_cate_cd_his_y,t99_event_class_cd,t99_event_spcl_cd,t99_event_spcl_cd_his,t99_event_spcl_cd_his_y,t99_event_type_cd,t99_event_type_cd_his,t99_event_type_cd_his_y,t99_facility_type_cd,t99_fclty_device_class_cd,t99_feature_class_cd,t99_fee_data_src_cd,t99_final_discount_cd,t99_flow_limit_type_cd,t99_frequency_cd,t99_hang_cd,t99_hardware_type_cd,t99_holiday_cd,t99_househld_pty_cd,t99_index_id_acc_special,t99_indicator_type_cd,t99_infl_opr_policy_exe_type,t99_land_use_attr_cd,t99_later_segment_cd,t99_layout_cd,t99_line_attr_cd_his,t99_line_control_cd,t99_line_control_cd1231,t99_line_error_consq_cd,t99_line_merge_cd,t99_line_opr_type_cd_his,t99_line_party_rela_cd_his,t99_line_pty_spcl_cd,t99_line_stat_err_cate_cd,t99_line_type_cd,t99_line_type_cd_his,t99_load_file_type_cd,t99_lock_card_reason_cd,t99_lock_prod_reason_cd,t99_minutes_stat_index_rela,t99_move_time_type_cd,t99_net_info_type_cd_his,t99_net_stat_type_cd,t99_net_stat_type_cd1231,t99_net_status_cd_his,t99_net_status_cd_his_b,t99_net_type_cd,t99_network_rela_type_cd,t99_new_open_type,t99_note_cate_cd,t99_nstd_station_trans,t99_scene_attr_type_cd,t99_scene_attr_type_cd_his,t99_scene_attr_type_cd_his_y,t99_score_discount_cd,t99_score_discount_cd_bak,t99_seal_deal_type_cd,t99_source_event_cate_cd,t99_source_sys_cd,t99_source_sys_cd_b,t99_span_period_atrr_cd,t99_spec_opr_dt_type_cd,t99_spec_opr_dt_type_cd_his,t99_special_day_type_cd,t99_speciality_cd,t99_src_station_rela_his,t99_trip_direction_cd,t99_src_trip_direction_rela_his,t99_stat_index_cd,t99_stat_index_type_cd,t99_dectime_stat_index_cd,t99_src_prod_rela_cd,t99_train_parameter_his,t99_ride_km_segment_cd,t99_ride_mnt_segment_cd,t99_ride_scnt_segment_cd,t99_prod_split_cd,t99_stat_period_cd,t99_station_categ_cd,t99_stat_std_cd,dev_run_mode,t60_energy_stat_type_cd,dev_point_status_mode_rel,dev_alarm_type_info,dev_alarm_priority_info,dev_point_type_info,unit_info,dev_special_system_code,dev_type_info,dev_state_text_info,dev_device_info,additional_property_info,dev_additional_property,point_additional_property,dev_point_info,vl_type,t99_prod_cate_cd,t99_src_line_rela_his,pull_meta_info,pull_log");
        }});
        long end = Instant.now().toEpochMilli();
        String s = gson.toJson(cc);
        System.out.println(s);
        System.out.println(end - start);

        ResultSet tables = connection.getMetaData().getTables(null, null, "NewTable", new String[]{"TABLE"});
        while (tables.next()) {
            System.out.println(tables.getString("TABLE_NAME"));
        }
    }

    @Test
    void testExeCommandGetImportedKeys() throws SQLException {

        Gson gson = new Gson();
        ExecResult cc = CommandManager.exeCommand(connection, CommandKey.CMD_DBReverseGetFKColumnFieldImpl,new HashMap<String,String>(){{
            put("Table","NewTable");
        }});
        String s = gson.toJson(cc);
        System.out.println(s);
    }

    @Test
    void testGetImportedKeys() throws SQLException {

        DBReverseGetFKReferenceImpl getFKReference = new DBReverseGetFKReferenceImpl();
        ExecResult<List<FKColumnField>> execResult = getFKReference.exec(connection, new HashMap<String, String>() {{
            put("Table", "tb_emp3");
        }});

        System.out.println(execResult.getBody());

    }

    @Test
    void testExeCommandCrossReference() throws SQLException {

//        Gson gson = new Gson();
//        ExecResult cc = CommandManager.exeCommand(connection, CommandKey.CMD_DBReverseGetFKColumnFieldImpl,new HashMap<String,String>(){{
////            put("schemaPattern","test");
//            String foreignCatalog = params.get("foreignCatalog").toUpperCase();
//            String foreignSchema = params.get("foreignSchema").toUpperCase();
//            String foreignTable = params.get("foreignTable").toUpperCase();
//            put("foreignCatalog","NewTable");
//            put("foreignSchema","NewTable");
//            put("foreignTable","NewTable");
//        }});
//        String s = gson.toJson(cc);
        ResultSet rs = connection.getMetaData().getCrossReference(null, null, "tb_emp3",
                null,null,"NewTable");
        while (rs.next()) {
            FKColumnField fkColumnField = new FKColumnField();
            String PKTABLE_CAT = rs.getString("PKTABLE_CAT");
            String PKTABLE_SCHEM = rs.getString("PKTABLE_SCHEM");
            String PKTABLE_NAME = rs.getString("PKTABLE_NAME");
            String PKCOLUMN_NAME = rs.getString("PKCOLUMN_NAME");
            String FKCOLUMN_NAME = rs.getString("FKCOLUMN_NAME");
            String FK_NAME = rs.getString("FK_NAME");
            fkColumnField.setPKTABLE_CAT(PKTABLE_CAT);
            fkColumnField.setPKTABLE_SCHEM(PKTABLE_SCHEM);
            fkColumnField.setPKTABLE_NAME(PKTABLE_NAME);
            fkColumnField.setPKCOLUMN_NAME(PKCOLUMN_NAME);
            fkColumnField.setFKCOLUMN_NAME(FKCOLUMN_NAME);
            fkColumnField.setFK_NAME(FK_NAME);
            System.out.println(fkColumnField);
        }
    }
}