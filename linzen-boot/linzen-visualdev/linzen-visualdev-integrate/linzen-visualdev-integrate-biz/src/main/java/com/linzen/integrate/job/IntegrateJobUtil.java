package com.linzen.integrate.job;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.linzen.base.UserInfo;
import com.linzen.integrate.model.nodeJson.IntegrateModel;
import com.linzen.util.JsonUtil;
import com.linzen.util.RedisUtil;
import com.linzen.util.StringUtil;
import lombok.extern.slf4j.Slf4j;


/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
public class IntegrateJobUtil {
    /**
     * 缓存key
     */
    public static final String WORKTIMEOUT_REDIS_KEY = "idgenerator_IntegrateList";

    /**
     * 缓存key
     */
    public static final String IDGENERATOR_REDIS_KEY = "idgenerator_IntegrateModel";

    /**
     * 缓存key
     */
    public static final String IDGENERATORID_REDIS_KEY = "idgenerator_id";

    public static IntegrateModel getModel(IntegrateModel model, RedisUtil redisUtil) {
        String id = model.getId();
        String hashValues = redisUtil.getHashValues(IDGENERATOR_REDIS_KEY, id);
        IntegrateModel integrateModel = StringUtil.isNotEmpty(hashValues) ? JsonUtil.createJsonToBean(hashValues, IntegrateModel.class) : null;
        return integrateModel;
    }

    public static void insertModel(IntegrateModel model, RedisUtil redisUtil) {
        String integrateId = model.getId();
        redisUtil.insertHash(IDGENERATOR_REDIS_KEY, integrateId, JsonUtil.createObjectToString(model));
        insertTenant(model, redisUtil);
    }

    public static void removeModel(IntegrateModel model, RedisUtil redisUtil) {
        redisUtil.removeHash(IDGENERATOR_REDIS_KEY, model.getId());
    }

    public static void insertTenant(IntegrateModel model, RedisUtil redisUtil) {
        String tenantId = StringUtil.isNotEmpty(model.getUserInfo().getTenantId()) ? model.getUserInfo().getTenantId() : "linzen";
        UserInfo userInfo = model.getUserInfo();
        redisUtil.insertHash(WORKTIMEOUT_REDIS_KEY, tenantId, JsonUtil.createObjectToString(userInfo));
    }

    public static boolean getIntegrate(IntegrateModel model, RedisUtil redisUtil){
        String value = redisUtil.getHashValues(IDGENERATORID_REDIS_KEY, model.getId());
        return ObjectUtil.isNotEmpty(value);
    }

    public static void insertIntegrate(IntegrateModel model, RedisUtil redisUtil){
        redisUtil.insertHash(IDGENERATORID_REDIS_KEY, model.getId(), model.getId());
    }

    public static void removeIntegrate(IntegrateModel model, RedisUtil redisUtil){
        redisUtil.removeHash(IDGENERATORID_REDIS_KEY, model.getId());
    }

}
