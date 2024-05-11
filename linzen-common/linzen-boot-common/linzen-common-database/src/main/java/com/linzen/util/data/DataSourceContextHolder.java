package com.linzen.util.data;

import com.linzen.util.TenantHolder;

/**
 * 数据库上下文切换
 *
 * @see TenantHolder
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Deprecated
public class DataSourceContextHolder {

    private static final ThreadLocal<String> CONTEXT_DB_NAME_HOLDER = new ThreadLocal<>();

    private static final ThreadLocal<String> CONTEXT_DB_ID_HOLDER = new ThreadLocal<>();

    private static final ThreadLocal<Boolean> CONTEXT_ASSIGN_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前数据库
     */
    public static void setDatasource(String dbId,String dbName, boolean assign) {
        CONTEXT_DB_NAME_HOLDER.set(dbName);
        CONTEXT_DB_ID_HOLDER.set(dbId);
        CONTEXT_ASSIGN_HOLDER.set(assign);
    }

    /**
     * 取得当前数据源Id
     */
    public static String getDatasourceId() {
        String str = CONTEXT_DB_ID_HOLDER.get();
        return str;
    }
    /**
     * 取得当前数据源名称
     */
    public static String getDatasourceName() {
        String str = CONTEXT_DB_NAME_HOLDER.get();
        return str;
    }

    public static Boolean isAssignDataSource(){
        return Boolean.TRUE.equals(CONTEXT_ASSIGN_HOLDER.get());
    }

    /**
     * 清除上下文数据
     */
    public static void clearDatasourceType() {
        CONTEXT_DB_NAME_HOLDER.remove();
        CONTEXT_DB_ID_HOLDER.remove();
        CONTEXT_ASSIGN_HOLDER.remove();
    }
}
