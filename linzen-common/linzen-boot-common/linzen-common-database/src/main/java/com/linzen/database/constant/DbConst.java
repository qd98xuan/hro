package com.linzen.database.constant;

/**
 * 数据库相关静态常量
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class DbConst {

    public static final String LINZEN_ALLDATA_TEXT = "全部数据";
    public static final String LINZEN_ALLDATA = "linzen_alldata";

    /**
     * jdbc工具类返回数据类型
     */
    public static final String TABLE_FIELD_MOD = "tableFieldMod";
    public static final String MAP_MOD = "mapMods";
    public static final String INCLUDE_FIELD_MOD = "includeFieldMods";
    public static final String CUSTOM_MOD = "customMods";
    public static final String PAGE_MOD = "pageMod";

    /**
     * url连接
     */
    public static final String HOST = "{host}";
    public static final String PORT = "{port}";
    public static final String DB_NAME = "{dbname}";
    public static final String DB_SCHEMA = "{schema}";


    /**
     * 默认表（不可删）
     *
     */
    public static final String BYO_TABLE =
            "sys_advanced_query_scheme,base_app_data,base_authorize,base_bill_rule,base_columns_purview,base_common_fields," +
                    "base_common_words,base_data_interface,base_data_interface_log,base_data_interface_oauth,base_data_interface_variate," +
                    "base_db_link,base_dictionary_data,base_dictionary_type,base_file,base_group,base_im_content,base_im_reply," +
                    "base_integrate,base_integrate_node,base_integrate_queue,base_integrate_task,base_message,sys_module," +
                    "sys_module_authorize,sys_module_button,sys_module_column,sys_module_form,sys_module_link," +
                    "sys_module_scheme," +
                    "msg_account_config," +
                    "base_msg_monitor," +
                    "base_msg_send," +
                    "base_msg_send_template," +
                    "base_msg_short_link," +
                    "base_msg_sms_field," +
                    "base_msg_template," +
                    "base_msg_template_param," +
                    "base_msg_wechat_user," +
                    "base_notice," +
                    "sys_organize,base_organize_administrator,base_organize_relation,base_permission_group," +
                    "base_portal,base_portal_data,base_portal_manage,sys_position,base_print_log,base_print_template," +
                    "base_province,base_province_atlas,sys_role,base_schedule,base_schedule_log,base_schedule_user,base_sign_img," +
                    "base_socials_users,base_syn_third_info,sys_config,base_sys_log,sys_system,base_time_task," +
                    "base_time_task_log,sys_user,base_user_device,sys_user_old_password,sys_user_relation,base_visual_dev," +
                    "base_visual_dev_copy1,base_visual_filter,base_visual_link,base_visual_release,base_visual_release_copy1," +
                    "blade_visual,blade_visual_category,blade_visual_component,blade_visual_config,blade_visual_db,blade_visual_map," +
                    "blade_visual_record,ct293714178055641925,ct294012196562705414,ct294022926200511493,ct294031961024928774," +
                    "ct294031961024928775,ct294037561750363142,ct294037561750363143,ct294099893327272966,ct294376098702073542," +
                    "ct294382866144468678,ct395899726056134917,ct459734202267475653,ct469496947162544645,ct469499339585157637," +
                    "data_report,ext_big_data,ext_customer,ext_document,ext_document_share,ext_email_config,ext_email_receive," +
                    "ext_email_send,ext_employee,ext_order,ext_order_2,ext_order_entry,ext_order_entry_2,ext_order_receivable," +
                    "ext_order_receivable_2,ext_product,ext_product_classify,ext_product_entry,ext_product_goods,ext_project_gantt," +
                    "ext_table_example,ext_worklog,ext_worklog_share,flow_candidates,flow_comment,flow_delegate,flow_form,flow_form_copy1," +
                    "flow_form_relation,flow_launch_user,flow_reject_data,flow_task,flow_task_circulate,flow_task_node,flow_task_operator," +
                    "flow_task_operator_record,flow_task_operator_user,flow_template,flow_template_json,flow_visible,mt293714178051447621," +
                    "mt293732572561709893,mt293737725377420101,mt293740367726025541,mt293756710756064069,mt294012196562705413," +
                    "mt294022926196317189,mt294027801932110853,mt294031961024928773,mt294037561750363141,mt294090217118276613," +
                    "mt294099237023526085,mt294099893327272965,mt294104577349819397,mt294376098702073541,mt294382866144468677," +
                    "mt382811547782648325,mt385453315686752389,mt395179351563312389,mt395899725691230469,mt395964134161651973," +
                    "mt400655094485373381,mt420589912199274117,mt456990191458975685,mt469496947087047173,mt469499339534825989," +
                    "mt469502670336489989,mt470888132380721605,mt470913062111543749,mt470929966582727173,report_charts,report_department," +
                    "report_user,test_carapplication,test_cost,test_customer,test_details,test_position,test_projects,test_purchase," +
                    "test_purchase_form,test_purchase_form_order,test_purchaseorder,test_receivable,test_reimbursement,test_subpackage," +
                    "test_vehicleinfo,test_warehousing,test_warehousingorder,undo_log,wform_applybanquet,wform_applydelivergoods," +
                    "wform_applydelivergoodsentry,wform_applymeeting,wform_archivalborrow,wform_articleswarehous,wform_batchpack," +
                    "wform_batchtable,wform_conbilling,wform_contractapproval,wform_contractapprovalsheet,wform_debitbill," +
                    "wform_documentapproval,wform_documentsigning,wform_expenseexpenditure,wform_finishedproduct,wform_finishedproductentry," +
                    "wform_incomerecognition,wform_leaveapply,wform_letterservice,wform_materialrequisition,wform_materialrequisitionentry," +
                    "wform_monthlyreport,wform_officesupplies,wform_outboundorder,wform_outboundorderentry,wform_outgoingapply," +
                    "wform_paydistribution,wform_paymentapply,wform_postbatchtab,wform_procurementmaterial,wform_procurementmaterialentry," +
                    "wform_purchaselist,wform_purchaselistentry,wform_quotationapproval,wform_receiptprocessing,wform_receiptsign," +
                    "wform_rewardpunishment,wform_salesorder,wform_salesorderentry,wform_salessupport,wform_staffovertime," +
                    "wform_supplementcard,wform_travelapply,wform_travelreimbursement,wform_vehicleapply,wform_violationhandling," +
                    "wform_warehousereceipt,wform_warehousereceiptentry,wform_workcontactsheet,wform_zjf_wikxqi";
}
