package com.linzen.util;

import com.linzen.model.tenant.TenantVO;

/**
 * 租户线程缓存工具类
 * @author FHNP
 * @user N
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class TenantHolder {

    private TenantHolder() {
    }

    private static final ThreadLocal<TenantVO> TENANT_CACHE = new ThreadLocal<>();

    public static void setLocalTenantCache(TenantVO tenantInfo){
        TENANT_CACHE.set(tenantInfo);
    }

    public static TenantVO getLocalTenantCache(){
        return TENANT_CACHE.get();
    }

    public static void clearLocalTenantCache(){
        TENANT_CACHE.remove();
    }

    public static String getDatasourceId() {
        return getLocalTenantCache() == null ? null: getLocalTenantCache().getEnCode();
    }
    /**
     * 取得当前数据源名称
     */
    public static String getDatasourceName() {
        return getLocalTenantCache() == null ? null: getLocalTenantCache().getDbName();
    }

    public static boolean isSchema(){
        return getLocalTenantCache() != null && getLocalTenantCache().isSchema();
    }

    public static boolean isColumn(){
        return getLocalTenantCache() != null && getLocalTenantCache().isColumn();
    }

    public static boolean isRemote(){
        return getLocalTenantCache() != null && getLocalTenantCache().isRemote();
    }


}
