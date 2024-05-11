package com.linzen.util;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaSessionCustomUtil;
import com.linzen.model.BaseSystemInfo;
import lombok.extern.slf4j.Slf4j;

import static com.linzen.consts.AuthConsts.DEFAULT_TENANT_ID;
import static com.linzen.consts.AuthConsts.TENANT_SESSION;


/**
 * @author FHNP
 * @copyright 领致信息技术有限公司
 */
@Slf4j
public class TenantProvider {


    private static final long tenantTimeout = 60 * 60 * 24 * 30L;

    /**
     * 获取租户Redis存储对象
     *
     * @param tenantId
     * @return
     */
    public static SaSession getTenantSession(String tenantId) {
        if (tenantId == null) {
            tenantId = DEFAULT_TENANT_ID;
        }
        SaSession saSession = SaSessionCustomUtil.getSessionById(TENANT_SESSION + tenantId);
        if (saSession != null && !saSession.get("init", false)) {
            saSession.set("init", true);
            saSession.updateTimeout(tenantTimeout);
        }
        return saSession;
    }

    /**
     * 存入租户缓存空间
     *
     * @param tenantId
     * @param key
     * @param value
     */
    public static void putTenantCache(String tenantId, String key, Object value) {
        SaSession saSession = getTenantSession(tenantId);
        if (saSession != null) {
            saSession.set(key, value).updateTimeout(tenantTimeout);
        }
    }

    /**
     * 获取租户缓存数据
     *
     * @param tenantId
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T getTenantCache(String tenantId, String key) {
        SaSession saSession = getTenantSession(tenantId);
        if (saSession != null) {
            return (T) saSession.get(key);
        }
        return null;
    }

    /**
     * 删除租户缓存数据
     *
     * @param tenantId
     * @param key
     */
    public static void delTenantCache(String tenantId, String key) {
        SaSession saSession = getTenantSession(tenantId);
        if (saSession != null) {
            saSession.delete(key);
        }
    }

    public static void renewTimeout(String tenantId, long timeout) {
        if (tenantId == null) {
            tenantId = DEFAULT_TENANT_ID;
        }
        SaSession saSession = getTenantSession(tenantId);
        if (saSession != null) {
            saSession.updateTimeout(timeout);
        }
    }


    private static ThreadLocal<BaseSystemInfo> systemInfoThreadLocal = new ThreadLocal<>();

    /**
     * 获取系统设置信息
     *
     * @return
     */
    public static BaseSystemInfo getBaseSystemInfo() {
        BaseSystemInfo systemInfo = systemInfoThreadLocal.get();
        return systemInfo;
    }


    public static void setBaseSystemInfo(BaseSystemInfo baseSystemInfo) {
        systemInfoThreadLocal.set(baseSystemInfo);
    }


    public static void clearBaseSystemIfo() {
        systemInfoThreadLocal.remove();
    }


}
