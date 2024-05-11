package com.linzen.database.sql.util;

/**
 * 类功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class SqlFrameFastUtil {

    /**
     * 增填权限
     */
    public final static String INSERT_AUTHORIZE = "INSERT INTO base_authorize (F_ID, f_item_type, f_item_id, f_object_type, f_object_id, f_sort_code, f_creator_time, f_creator_user_id %COLUMN_KEY% ) VALUES  (?,?,?,?,?,?,?,? %COLUMN_PLACEHOLDER%)";
    public final static String INSERT_AUTHORIZE2 = "INSERT INTO base_authorize (F_ID, f_item_type, f_item_id, f_object_type, f_object_id, f_sort_code, f_creator_time, f_creator_user_id %COLUMN_KEY% ) VALUES  (?,?,?,?,?,?,TO_DATE(?,'yyyy-mm-dd hh24:mi:ss'),? %COLUMN_PLACEHOLDER%)";
    public final static String AUTHOR_DEL = "DELETE FROM base_authorize WHERE (f_object_id in( '{authorizeIds}') AND f_item_type <> 'portalManage')";

}
