package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.EmailConfigEntity;
import com.linzen.base.entity.SysConfigEntity;
import com.linzen.base.model.systemconfig.EmailTestForm;
import com.linzen.base.model.systemconfig.SysConfigModel;
import com.linzen.base.service.SysconfigService;
import com.linzen.constant.MsgCode;
import com.linzen.message.entity.QyWebChatModel;
import com.linzen.message.model.message.DingTalkModel;
import com.linzen.message.util.DingTalkUtil;
import com.linzen.message.util.QyWebChatUtil;
import com.linzen.permission.model.user.form.UserUpdateAdminForm;
import com.linzen.permission.model.user.vo.UserAdminVO;
import com.linzen.permission.service.UserService;
import com.linzen.util.JsonUtil;
import com.linzen.util.RandomUtil;
import com.linzen.util.wxutil.HttpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * 系统配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "系统配置", description = "SysConfig")
@RestController
@RequestMapping("/api/system/SysConfig")
public class SysConfigController extends SuperController<SysconfigService, SysConfigEntity> {

    @Autowired
    private SysconfigService sysconfigService;

    @Autowired
    private UserService userService;

    /**
     * 列表
     *
     * @return ignore
     */
    @Operation(summary = "列表")
    @GetMapping
    public ServiceResult<SysConfigModel> list() {
        List<SysConfigEntity> list = sysconfigService.getList("SysConfig");
        HashMap<String, String> map = new HashMap<>(16);
        for (SysConfigEntity sys : list) {
            map.put(sys.getFkey(), sys.getValue());
        }
        SysConfigModel sysConfigModel = BeanUtil.toBean(map, SysConfigModel.class);
        return ServiceResult.success(sysConfigModel);
    }

    /**
     * 保存设置
     *
     * @param sysConfigModel 系统配置模型
     * @return ignore
     */
    @Operation(summary = "更新系统配置")
    @Parameter(name = "sysConfigModel", description = "系统模型", required = true)
    @SaCheckPermission("system.sysConfig")
    @PutMapping
    public ServiceResult save(@RequestBody SysConfigModel sysConfigModel) {
        if (Objects.nonNull(sysConfigModel.getVerificationCodeNumber())) {
            if (sysConfigModel.getVerificationCodeNumber() > 6) {
                return ServiceResult.error("验证码位数不能大于6");
            }
            if (sysConfigModel.getVerificationCodeNumber() < 3) {
                return ServiceResult.error("验证码位数不能小于3");
            }
        }
        List<SysConfigEntity> entitys = new ArrayList<>();
        Map<String, Object> map = JsonUtil.entityToMap(sysConfigModel);
        map.put("isLog", "1");
        map.put("sysTheme", "1");
        map.put("pageSize", "30");
        map.put("lastLoginTime", 1);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            SysConfigEntity entity = new SysConfigEntity();
            entity.setId(RandomUtil.uuId());
            entity.setFkey(entry.getKey());
            entity.setValue(String.valueOf(entry.getValue()));
            entitys.add(entity);
        }
        sysconfigService.save(entitys);
        return ServiceResult.success(MsgCode.SU005.get());
    }

    /**
     * 邮箱账户密码验证
     *
     * @param emailTestForm 邮箱测试模型
     * @return ignore
     */
    @Operation(summary = "邮箱连接测试")
    @Parameter(name = "emailTestForm", description = "邮箱测试模型", required = true)
    @SaCheckPermission("system.sysConfig")
    @PostMapping("/Email/Test")
    public ServiceResult checkLogin(@RequestBody EmailTestForm emailTestForm) {
        EmailConfigEntity entity = BeanUtil.toBean(emailTestForm, EmailConfigEntity.class);
        entity.setEmailSsl(Integer.valueOf(emailTestForm.getSsl()));
        String result = sysconfigService.checkLogin(entity);
        if ("true".equals(result)) {
            return ServiceResult.success(MsgCode.SU017.get());
        } else {
            return ServiceResult.error(result);
        }
    }


    //=====================================测试企业微信、钉钉的连接=====================================

    /**
     * 测试企业微信配置的连接功能
     *
     * @param type           0-发送消息,1-同步组织
     * @param qyWebChatModel 企业微信模型
     * @return ignore
     */
    @Operation(summary = "测试企业微信配置的连接")
    @Parameters({
            @Parameter(name = "type", description = "0-发送消息,1-同步组织", required = true),
            @Parameter(name = "qyWebChatModel", description = "企业微信模型", required = true)
    })
    @SaCheckPermission("system.sysConfig")
    @PostMapping("{type}/testQyWebChatConnect")
    public ServiceResult testQyWebChatConnect(@PathVariable("type") String type, @RequestBody @Valid QyWebChatModel qyWebChatModel) {
        JSONObject retMsg = new JSONObject();
        // 测试发送消息、组织同步的连接
        String corpId = qyWebChatModel.getQyhCorpId();
        String agentSecret = qyWebChatModel.getQyhAgentSecret();
        String corpSecret = qyWebChatModel.getQyhCorpSecret();
        // 测试发送消息的连接
        if ("0".equals(type)) {
            retMsg = QyWebChatUtil.getAccessToken(corpId, agentSecret);
            if (HttpUtil.isWxError(retMsg)) {
                return ServiceResult.error("测试发送消息的连接失败：" + retMsg.getString("errmsg"));
            }
            return ServiceResult.success("测试发送消息连接成功");
        } else if ("1".equals(type)) {
            retMsg = QyWebChatUtil.getAccessToken(corpId, corpSecret);
            if (HttpUtil.isWxError(retMsg)) {
                return ServiceResult.error("测试组织同步的连接失败：" + retMsg.getString("errmsg"));
            }
            return ServiceResult.success("测试组织同步连接成功");
        }
        return ServiceResult.error("测试连接类型错误");
    }

    /**
     * 测试钉钉配置的连接功能
     *
     * @param dingTalkModel 钉钉模板
     * @return ignore
     */
    @Operation(summary = "测试钉钉配置的连接")
    @Parameters({
            @Parameter(name = "dingTalkModel", description = "钉钉模型", required = true)
    })
    @SaCheckPermission("system.sysConfig")
    @PostMapping("/testDingTalkConnect")
    public ServiceResult testDingTalkConnect(@RequestBody @Valid DingTalkModel dingTalkModel) {
        JSONObject retMsg = new JSONObject();
        // 测试钉钉配置的连接
        String appKey = dingTalkModel.getDingSynAppKey();
        String appSecret = dingTalkModel.getDingSynAppSecret();
        ///
//        String agentId = dingTalkModel.getDingAgentId();
        // 测试钉钉的连接
        retMsg = DingTalkUtil.getAccessToken(appKey, appSecret);
        if (!retMsg.getBoolean("code")) {
            return ServiceResult.error("测试钉钉连接失败：" + retMsg.getString("error"));
        }

        return ServiceResult.success("测试钉钉连接成功");
    }

    /**
     * 获取管理员集合
     *
     * @return
     */
    @Operation(summary = "获取管理员集合")
    @SaCheckPermission("system.sysConfig")
    @GetMapping("/getAdminList")
    public ServiceResult<List<UserAdminVO>> getAdminList(){
        List<UserAdminVO> admins = JsonUtil.createJsonToList(userService.getAdminList(), UserAdminVO.class);
        return ServiceResult.success(admins);
    }

    /**
     * 获取管理员集合
     *
     * @param userUpAdminForm 超级管理员设置表单参数
     * @return
     */
    @Operation(summary = "获取管理员集合")
    @Parameters({
            @Parameter(name = "userUpAdminForm", description = "超级管理员设置表单参数", required = true)
    })
    @SaCheckPermission("system.sysConfig")
    @PutMapping("/setAdminList")
    public ServiceResult<String> setAdminList(@RequestBody UserUpdateAdminForm userUpAdminForm){
        userService.setAdminListByIds(userUpAdminForm.getAdminIds());
        return ServiceResult.success(MsgCode.SU004.get());
    }

}
