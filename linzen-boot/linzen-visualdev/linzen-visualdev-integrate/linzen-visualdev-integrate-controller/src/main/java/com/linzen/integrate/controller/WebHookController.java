package com.linzen.integrate.controller;

import com.linzen.base.ServiceResult;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.MsgCode;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.exception.WorkFlowException;
import com.linzen.integrate.entity.IntegrateEntity;
import com.linzen.integrate.model.integrate.WebHookInfoVo;
import com.linzen.integrate.service.IntegrateService;
import com.linzen.integrate.util.IntegrateUtil;
import com.linzen.util.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Tag(name = "webhook触发", description = "WebHook")
@RestController
@RequestMapping("/api/visualdev/Hooks")
public class WebHookController {

    @Autowired
    private IntegrateService integrateService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private IntegrateUtil integrateUtil;
    @Autowired
    private UserProvider userProvider;

    private static final String WEBHOOK_RED_KEY = "webhookencode";

    private static long DEFAULT_CACHE_TIME = 60 * 5;

    @Operation(summary = "数据接收接口")
    @Parameters({
            @Parameter(name = "id", description = "base64转码id", required = true),
            @Parameter(name = "tenantId", description = "租户id", required = false)
    })
    @PostMapping("/{id}")
    @NoDataSourceBind
    public ServiceResult webhookTrigger(@PathVariable("id") String id,
                                       @RequestParam(value = "tenantId", required = false) String tenantId,
                                       @RequestBody Map<String, Object> body) throws WorkFlowException {
        String idReal = new String(Base64.decodeBase64(id.getBytes(StandardCharsets.UTF_8)));
        if (configValueUtil.isMultiTenancy()) {
            // 判断是不是从外面直接请求
            if (StringUtil.isNotEmpty(tenantId)) {
                //切换成租户库
                try {
                    TenantDataSourceUtil.switchTenant(tenantId);
                } catch (Exception e) {
                    return ServiceResult.error(MsgCode.LOG105.get());
                }
            }
        }
        integrateUtil.integrate(idReal, tenantId, body);
        return ServiceResult.success();
    }

    @Operation(summary = "获取webhookUrl")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @GetMapping("/getUrl")
    public ServiceResult getWebhookUrl(@RequestParam("id") String id) {
        String enCodeBase64 = new String(Base64.encodeBase64(id.getBytes(StandardCharsets.UTF_8)));
        String randomStr = UUID.randomUUID().toString().substring(0, 5);
        WebHookInfoVo vo = new WebHookInfoVo();
        vo.setEnCodeStr(enCodeBase64);
        vo.setRandomStr(randomStr);
        vo.setWebhookUrl("/api/visualdev/Hooks/" + enCodeBase64);
        vo.setRequestUrl("/api/visualdev/Hooks/" + enCodeBase64 + "/params/" + randomStr);
        return ServiceResult.success(vo);
    }

    @Operation(summary = "通过get接口获取参数")
    @Parameters({
            @Parameter(name = "id", description = "base64转码id", required = true),
            @Parameter(name = "randomStr", description = "获取webhookUrl提供的随机字符", required = true)
    })
    @GetMapping("/{id}/params/{randomStr}")
    @NoDataSourceBind
    public ServiceResult getWebhookParams(@PathVariable("id") String id,
                                         @PathVariable("randomStr") String randomStr) throws WorkFlowException {
        insertRedis(id, randomStr, new HashMap<>());
        return ServiceResult.success();
    }

    @Operation(summary = "通过post接口获取参数")
    @Parameters({
            @Parameter(name = "id", description = "base64转码id", required = true),
            @Parameter(name = "randomStr", description = "获取webhookUrl提供的随机字符", required = true)
    })
    @PostMapping("/{id}/params/{randomStr}")
    @NoDataSourceBind
    public ServiceResult postWebhookParams(@PathVariable("id") String id,
                                          @PathVariable("randomStr") String randomStr,
                                          @RequestBody Map<String, Object> obj) throws WorkFlowException {
        insertRedis(id, randomStr, new HashMap<>(obj));
        return ServiceResult.success();
    }

    /**
     * 助手id查询信息，写入缓存
     *
     * @param id
     * @param randomStr
     * @param resultMap
     * @throws WorkFlowException
     */
    private void insertRedis(String id, String randomStr, Map<String, Object> resultMap) throws WorkFlowException {
        String idReal = new String(Base64.decodeBase64(id.getBytes(StandardCharsets.UTF_8)));
        String key1 = WEBHOOK_RED_KEY + "_" + idReal + "_" + randomStr;
        if (!redisUtil.exists(key1)) {
            throw new WorkFlowException("路径错误");
        }
        String tenantId = redisUtil.getString(key1).toString();

        if (configValueUtil.isMultiTenancy()) {
            // 判断是不是从外面直接请求
            if (StringUtil.isNotEmpty(tenantId)) {
                //切换成租户库
                try {
                    TenantDataSourceUtil.switchTenant(tenantId);
                } catch (Exception e) {
                    throw new WorkFlowException(MsgCode.LOG105.get());
                }
            }
        }
        IntegrateEntity entity = integrateService.getInfo(idReal);
        if (Objects.equals(entity.getEnabledMark(), 0)) {
            throw new WorkFlowException("集成助手被禁用");
        }
        Map<String, Object> parameterMap = new HashMap<>(ServletUtil.getRequest().getParameterMap());
        for (String key : parameterMap.keySet()) {
            String[] parameterValues = ServletUtil.getRequest().getParameterValues(key);
            if (parameterValues.length == 1) {
                parameterMap.put(key, parameterValues[0]);
            } else {
                parameterMap.put(key, parameterValues);
            }
        }
        resultMap.putAll(parameterMap);
        if (resultMap.keySet().size() > 0) {
            redisUtil.insert(WEBHOOK_RED_KEY + "_" + randomStr, resultMap, DEFAULT_CACHE_TIME);
            redisUtil.remove(key1);
        }
    }

    @Operation(summary = "请求参数添加触发接口")
    @Parameters({
            @Parameter(name = "id", description = "base64转码id", required = true),
            @Parameter(name = "randomStr", description = "获取webhookUrl提供的随机字符", required = true)
    })
    @GetMapping("/{id}/start/{randomStr}")
    public ServiceResult start(@PathVariable("id") String id,
                              @PathVariable("randomStr") String randomStr) {
        redisUtil.remove(WEBHOOK_RED_KEY + "_" + randomStr);
        redisUtil.insert(WEBHOOK_RED_KEY + "_" + id + "_" + randomStr, userProvider.get().getTenantId(), DEFAULT_CACHE_TIME);
        return ServiceResult.success();
    }

    @Operation(summary = "获取缓存的接口参数")
    @Parameters({
            @Parameter(name = "randomStr", description = "获取webhookUrl提供的随机字符", required = true)
    })
    @GetMapping("/getParams/{randomStr}")
    public ServiceResult getRedisParams(@PathVariable("randomStr") String randomStr) {
        Map<String, Object> mapRedis = new HashMap<>();
        String key = WEBHOOK_RED_KEY + "_" + randomStr;
        if (redisUtil.exists(key)) {
            mapRedis = redisUtil.getMap(key);
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (String redisKey : mapRedis.keySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", redisKey);
            map.put("fullName", mapRedis.get(redisKey));
            list.add(map);
        }
        return ServiceResult.success(list);
    }
}
