package com.linzen.permission.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.linzen.annotation.UserPermission;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.base.controller.SuperController;
import com.linzen.config.ConfigValueUtil;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.exception.LoginException;
import com.linzen.permission.entity.SocialsUserEntity;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.model.socails.SocialsUserInfo;
import com.linzen.permission.model.socails.SocialsUserModel;
import com.linzen.permission.model.socails.SocialsUserVo;
import com.linzen.permission.service.SocialsUserService;
import com.linzen.permission.service.UserService;
import com.linzen.permission.util.socials.AuthCallbackNew;
import com.linzen.permission.util.socials.AuthSocialsUtil;
import com.linzen.permission.util.socials.SocialsAuthEnum;
import com.linzen.permission.util.socials.SocialsConfig;
import com.linzen.util.*;
import com.linzen.util.wxutil.HttpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.enums.AuthResponseStatus;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 单点登录
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "第三方登录和绑定", description = "Socials")
@RestController
@RequestMapping("/api/permission/socials")
@Slf4j
public class SocialsUserController extends SuperController<SocialsUserService, SocialsUserEntity> {
    @Autowired
    private SocialsUserService socialsUserService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private AuthSocialsUtil authSocialsUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private SocialsConfig socialsConfig;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取用户列表
     *
     * @param
     * @return ignore
     */
    @Operation(summary = "获取用户授权列表")
    @Parameters({
            @Parameter(name = "userId", description = "用户id")
    })
    @GetMapping
    public ServiceResult<List<SocialsUserVo>> getList(@RequestParam(value = "userId", required = false) String userId) {
        if (StringUtil.isEmpty(userId)) {
            userId = userProvider.get().getUserId();
        }
        List<Map<String, Object>> platformInfos = SocialsAuthEnum.getPlatformInfos();
        String s = JSONArray.toJSONString(platformInfos);
        List<SocialsUserVo> socialsUserVos = JsonUtil.createJsonToList(s, SocialsUserVo.class);
        List<SocialsConfig.Config> config = socialsConfig.getConfig();
        List<SocialsUserVo> res = new ArrayList<>();
        if (config == null) {
            return ServiceResult.error("第三方登录未配置！");
        }
        config.stream().forEach(item -> {
            socialsUserVos.stream().forEach(item2 -> {
                if (item2.getEnname().toLowerCase().equals(item.getProvider())) {
                    res.add(item2);
                }
            });
        });
        //查询绑定信息
        List<SocialsUserEntity> listByUserId = socialsUserService.getListByUserId(userId);
        List<SocialsUserModel> listModel = JsonUtil.createJsonToList(listByUserId, SocialsUserModel.class);
        res.stream().forEach(item -> {
            listModel.stream().forEach(item2 -> {
                if (item.getEnname().equals(item2.getSocialType())) item.setEntity(item2);
            });
        });
        return ServiceResult.success(res);
    }

    /**
     * 绑定：重定向第三方登录页面
     *
     * @return ignore
     */
    @Operation(summary = "重定向第三方登录页面")
    @Parameters({
            @Parameter(name = "source", description = "地址", required = true)
    })
    @GetMapping("/render/{source}")
    @ResponseBody
    public ServiceResult render(@PathVariable String source) {
        AuthRequest authRequest = authSocialsUtil.getAuthRequest(source, userProvider.get().getUserId(), false, null, userProvider.get().getTenantId());
        String authorizeUrl = authRequest.authorize(AuthStateUtils.createState());
        return ServiceResult.success(authorizeUrl);
    }


    /**
     * 设置租户库
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    private boolean setTenantData(String tenantId, UserInfo userInfo) {
        try{
            TenantDataSourceUtil.switchTenant(tenantId);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * 解绑
     *
     * @param userId 用户id
     * @param id 主键
     * @return ignore
     */
    @UserPermission
    @Operation(summary = "解绑")
    @Parameters({
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "id", description = "主键", required = true)
    })
    @DeleteMapping("/{id}")
    public ServiceResult deleteSocials(@RequestParam(value = "userId",required = false)String userId,@PathVariable("id") String id) {
        SocialsUserEntity byId = socialsUserService.getById(id);
        UserInfo userInfo = userProvider.get();
        boolean b = socialsUserService.removeById(id);
        if (b) {
            //多租户开启-解除绑定
            if (configValueUtil.isMultiTenancy()) {
                String param = "?userId=" + byId.getUserId() + "&tenantId=" + userInfo.getTenantId() + "&socialsType=" + byId.getSocialType();
                JSONObject object = HttpUtil.httpRequest(configValueUtil.getMultiTenancyUrl() + "socials" + param, "DELETE", null);
                if (object == null || "500".equals(object.get("code").toString()) || "400".equals(object.getString("code"))) {
                    return ServiceResult.error("多租户解绑失败");
                }
            }
            return ServiceResult.success("解绑成功");
        }
        return ServiceResult.error("解绑失败");
    }


    @GetMapping("/list")
    @NoDataSourceBind
    public List<SocialsUserVo> getLoginList(@RequestParam("ticket") String ticket) {
        if (!socialsConfig.isSocialsEnabled()) return null;
        List<Map<String, Object>> platformInfos = SocialsAuthEnum.getPlatformInfos();
        String s = JSONArray.toJSONString(platformInfos);
        List<SocialsUserVo> socialsUserVos = JsonUtil.createJsonToList(s, SocialsUserVo.class);
        List<SocialsConfig.Config> config = socialsConfig.getConfig();
        List<SocialsUserVo> res = new ArrayList<>();
        config.stream().forEach(item -> {
            socialsUserVos.stream().forEach(item2 -> {
                if (item2.getEnname().toLowerCase().equals(item.getProvider())) {
                    AuthRequest authRequest = authSocialsUtil.getAuthRequest(item2.getEnname(), null, true, ticket, null);
                    String authorizeUrl = authRequest.authorize(AuthStateUtils.createState());
                    item2.setRenderUrl(authorizeUrl);
                    res.add(item2);
                }
            });
        });
        return res;
    }

    @GetMapping("/getSocialsUserInfo")
    @NoDataSourceBind
    public SocialsUserInfo getSocialsUserInfo(@RequestParam("source") String source, @RequestParam("code") String code,
                                              @RequestParam(value = "state", required = false) String state) throws LoginException {
        //获取第三方请求
        AuthCallbackNew callback = setAuthCallback(code, state);
        AuthRequest authRequest = authSocialsUtil.getAuthRequest(source, null, false, null, null);
        AuthResponse<AuthUser> res = authRequest.login(callback);
        if(AuthResponseStatus.FAILURE.getCode()==res.getCode()){
            throw new LoginException("连接失败！");
        }else if(AuthResponseStatus.SUCCESS.getCode()!=res.getCode()){
            throw new LoginException("授权失败:"+res.getMsg());
        }
        //登录用户第三方id
        String uuid = getSocialUuid(res);
        String socialName=StringUtil.isNotEmpty(res.getData().getUsername())?res.getData().getUsername():res.getData().getNickname();
        SocialsUserInfo socialsUserInfo = getUserInfo(source,  uuid, socialName);
        return socialsUserInfo;
    }

    /**
     * 获取用户绑定信息列表
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    @NoDataSourceBind
    public SocialsUserInfo getUserInfo(String source, String uuid, String socialName) throws LoginException {
        SocialsUserInfo socialsUserInfo=new SocialsUserInfo();
        UserInfo userInfo=new UserInfo();
        //查询租户绑定
        if ("wechat_applets".equals(source)) {
            source = "wechat_open";
        }
        if (configValueUtil.isMultiTenancy()) {
            JSONObject object = HttpUtil.httpRequest(configValueUtil.getMultiTenancyUrl() + "socials/list?socialsId=" + uuid, "GET", null);
            if (object == null || "500".equals(object.get("code").toString()) || "400".equals(object.getString("code"))) {
                throw new LoginException("租户绑定信息查询错误！");
            }
            if ("200".equals(object.get("code").toString())) {
                JSONArray data = JSONArray.parseArray(object.get("data").toString());
                int size = data.size();
                System.out.println(size);
                if (data == null || data.size() == 0) {
                    socialsUserInfo.setSocialUnionid(uuid);
                    socialsUserInfo.setSocialName(socialName);
                    return socialsUserInfo;
                } else if (data.size() == 1) {
                    //租户开启时-切换租户库
                    JSONObject oneUser = (JSONObject) data.get(0);
                    setTenantData(oneUser.get("tenantId").toString(), userInfo);
                    List<SocialsUserEntity> list = socialsUserService.getUserIfnoBySocialIdAndType(uuid, source);
                    if (CollectionUtil.isEmpty(list)) {
                        throw new LoginException("第三方未绑定账号！");
                    }
                    SysUserEntity infoById = userService.getInfo(list.get(0).getUserId());
                    userInfo = BeanUtil.toBean(infoById, UserInfo.class);
                    userInfo.setUserId(infoById.getId());
                    userInfo.setUserAccount(oneUser.get("tenantId").toString() + "@" + infoById.getAccount());
                    socialsUserInfo.setTenantUserInfo(data);
                    socialsUserInfo.setUserInfo(userInfo);
                } else {
                    socialsUserInfo.setTenantUserInfo(data);
                }
            }
        } else {//非多租户
            //查询绑定
            List<SocialsUserEntity> list = socialsUserService.getUserIfnoBySocialIdAndType(uuid, source);
            if (CollectionUtil.isNotEmpty(list)) {
                SysUserEntity infoById = userService.getInfo(list.get(0).getUserId());
                userInfo = BeanUtil.toBean(infoById, UserInfo.class);
                userInfo.setUserId(infoById.getId());
                userInfo.setUserAccount(infoById.getAccount());
                socialsUserInfo.setUserInfo(userInfo);
            } else {
                socialsUserInfo.setSocialUnionid(uuid);
                socialsUserInfo.setSocialName(socialName);
            }
        }
        return socialsUserInfo;
    }

    /**
     * 绑定
     *
     * @return ignore
     */
    @GetMapping("/callback")
    @ResponseBody
    @NoDataSourceBind
    public JSONObject binding(@RequestParam("source") String source,
                              @RequestParam(value = "userId", required = false) String userId,
                              @RequestParam(value = "tenantId", required = false) String tenantId,
                              @RequestParam(value = "code", required = false) String code,
                              @RequestParam(value = "state", required = false) String state) {
        log.info("进入callback：" + source + " callback params：");
        //获取第三方请求
        AuthCallbackNew callback = setAuthCallback(code, state);
        //租户开启时-切换租户库
        if (configValueUtil.isMultiTenancy()) {
            boolean b = setTenantData(tenantId, new UserInfo());
            if (!b) {
                return resultJson(201, "查询租户信息错误！");
            }

        }
        //获取第三方请求
        AuthRequest authRequest = authSocialsUtil.getAuthRequest(source, userId, false, null, null);
        AuthResponse<AuthUser> res = authRequest.login(callback);
        log.info(JSONObject.toJSONString(res));
        if (res.ok()) {
            String uuid = getSocialUuid(res);
            List<SocialsUserEntity> userIfnoBySocialIdAndType = socialsUserService.getUserIfnoBySocialIdAndType(uuid, source);
            if (CollectionUtil.isNotEmpty(userIfnoBySocialIdAndType)) {
                SysUserEntity info = userService.getInfo(userIfnoBySocialIdAndType.get(0).getUserId());
                return resultJson(201, "当前账户已被" + info.getRealName() + "/" + info.getAccount() + "绑定，不能重复绑定");
            }
            SocialsUserEntity socialsUserEntity = new SocialsUserEntity();
            socialsUserEntity.setUserId(userId);
            socialsUserEntity.setSocialType(source);
            socialsUserEntity.setSocialName(res.getData().getUsername());
            socialsUserEntity.setSocialId(uuid);
            socialsUserEntity.setCreatorTime(new Date());
            boolean save = socialsUserService.save(socialsUserEntity);

            //租户开启时-添加租户库绑定数据
            if (configValueUtil.isMultiTenancy() && save) {
                JSONObject params = JSONObject.parseObject(JSONObject.toJSONString(socialsUserEntity));
                SysUserEntity info = userService.getInfo(userId);
                params.put("tenantId", tenantId);
                params.put("account", info.getAccount());
                params.put("accountName", info.getRealName() + "/" + info.getAccount());
                JSONObject object = HttpUtil.httpRequest(configValueUtil.getMultiTenancyUrl() + "socials", "POST", params.toJSONString());
                if (object == null || "500".equals(object.get("code").toString()) || "400".equals(object.getString("code"))) {
                    return resultJson(201, "用户租户绑定错误!");
                }
            }
            return resultJson(200, "绑定成功!");

        }
        return resultJson(201, "第三方回调失败！");
    }

    /**
     * 设置第三方code state参数
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    private AuthCallbackNew setAuthCallback(String code, String state) {
        AuthCallbackNew callback = new AuthCallbackNew();
        callback.setAuthCode(code);
        callback.setAuth_code(code);
        callback.setAuthorization_code(code);
        callback.setCode(code);
        callback.setState(state);
        return callback;
    }

    /**
     * 返回json
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    private JSONObject resultJson(int code, String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("message", message);
        return jsonObject;
    }
    private String getSocialUuid(AuthResponse<AuthUser> res) {
        String uuid = res.getData().getUuid();
        if (res.getData().getToken() != null && StringUtil.isNotEmpty(res.getData().getToken().getUnionId())) {
            uuid = res.getData().getToken().getUnionId();
        }
        return uuid;
    }

    /**
     * 绑定
     *
     * @return ignore
     */
    @GetMapping("/loginbind")
    @ResponseBody
    @NoDataSourceBind
    public void loginAutoBinding(@RequestParam("socialType") String socialType,
                                 @RequestParam("socialUnionid") String socialUnionid,
                                 @RequestParam("socialName") String socialName,
                                 @RequestParam("userId") String userId,
                                 @RequestParam(value = "tenantId", required = false) String tenantId) {
        //查询租户绑定
        if ("wechat_applets".equals(socialType)) {
            socialType = "wechat_open";
        }
        //租户开启时-切换租户库
        if (configValueUtil.isMultiTenancy()) {
            setTenantData(tenantId, new UserInfo());
        }
        List<SocialsUserEntity> list = socialsUserService.getListByUserIdAndSource(userId, socialType);
        if(CollectionUtil.isNotEmpty(list)){//账号已绑定该第三方其他账号，则不绑定
            return;
        }
        SocialsUserEntity socialsUserEntity = new SocialsUserEntity();
        socialsUserEntity.setUserId(userId);
        socialsUserEntity.setSocialType(socialType);
        socialsUserEntity.setSocialName(socialName);
        socialsUserEntity.setSocialId(socialUnionid);
        socialsUserEntity.setCreatorTime(new Date());
        boolean save = socialsUserService.save(socialsUserEntity);
        //租户开启时-添加租户库绑定数据
        if (configValueUtil.isMultiTenancy() && save) {
            JSONObject params = JSONObject.parseObject(JSONObject.toJSONString(socialsUserEntity));
            SysUserEntity info = userService.getInfo(userId);
            params.put("tenantId", tenantId);
            params.put("account", info.getAccount());
            params.put("accountName", info.getRealName() + "/" + info.getAccount());
            JSONObject object = HttpUtil.httpRequest(configValueUtil.getMultiTenancyUrl() + "socials", "POST", params.toJSONString());

        }
    }
}
