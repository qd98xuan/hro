


package com.linzen.message.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.base.controller.SuperController;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.service.BillRuleService;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.vo.DownloadVO;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.MsgCode;
import com.linzen.emnus.ModuleTypeEnum;
import com.linzen.exception.DataBaseException;
import com.linzen.message.entity.AccountConfigEntity;
import com.linzen.message.model.accountconfig.AccountConfigForm;
import com.linzen.message.model.accountconfig.AccountConfigInfoVO;
import com.linzen.message.model.accountconfig.AccountConfigListVO;
import com.linzen.message.model.accountconfig.AccountConfigPagination;
import com.linzen.message.model.message.EmailModel;
import com.linzen.message.service.AccountConfigService;
import com.linzen.message.service.SendConfigTemplateService;
import com.linzen.message.util.DingTalkUtil;
import com.linzen.message.util.EmailUtil;
import com.linzen.message.util.QyWebChatUtil;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.util.*;
import com.linzen.util.wxutil.HttpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;


/**
 * 账号配置功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息
 * @date 2023-04-01
 */
@Slf4j
@RestController
@Tag(name = "账号配置功能", description = "message")
@RequestMapping("/api/message/AccountConfig")
public class AccountConfigController extends SuperController<AccountConfigService, AccountConfigEntity> {

    @Autowired
    private FileExport fileExport;

    @Autowired
    private BillRuleService billRuleService;

    @Autowired
    private ConfigValueUtil configValueUtil;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountConfigService accountConfigService;

    @Autowired
    private DictionaryDataService dictionaryDataService;

    @Autowired
    private SendConfigTemplateService sendConfigTemplateService;
//NotifyTemplateEntity.java

    /**
     * 列表
     *
     * @param accountConfigPagination 账号配置分页模型
     * @return
     */
    @Operation(summary = "列表")
    @SaCheckPermission("msgCenter.accountConfig")
    @GetMapping
    public ServiceResult<PageListVO<AccountConfigListVO>> list(AccountConfigPagination accountConfigPagination) throws IOException {
        List<AccountConfigEntity> list = accountConfigService.getList(accountConfigPagination);
        List<DictionaryDataEntity> smsSendTypeList = dictionaryDataService.getListByTypeDataCode("smsSendType");
        List<DictionaryDataEntity> webHookList = dictionaryDataService.getListByTypeDataCode("msgWebHookSendType");
        //处理id字段转名称，若无需转或者为空可删除
        SysUserEntity userEntity;
        List<AccountConfigListVO> listVO = JsonUtil.createJsonToList(list, AccountConfigListVO.class);
        for (AccountConfigListVO accountConfigVO : listVO) {
            //渠道
            if (StringUtil.isNotEmpty(accountConfigVO.getChannel())) {
                smsSendTypeList.stream().filter(t -> accountConfigVO.getChannel().equals(t.getEnCode())).findFirst()
                        .ifPresent(dataTypeEntity -> accountConfigVO.setChannel(dataTypeEntity.getFullName()));
            }
            //webhook类型
            if (StringUtil.isNotEmpty(accountConfigVO.getWebhookType())) {
                webHookList.stream().filter(t -> accountConfigVO.getWebhookType().equals(t.getEnCode())).findFirst()
                        .ifPresent(dataTypeEntity -> accountConfigVO.setWebhookType(dataTypeEntity.getFullName()));
            }

            if (StringUtil.isNotEmpty(accountConfigVO.getCreatorUserId())) {
                userEntity = userService.getInfo(accountConfigVO.getCreatorUserId());
                if (userEntity != null) {
                    accountConfigVO.setCreatorUser(userEntity.getRealName() + "/" + userEntity.getAccount());
                }
            }
        }

        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = BeanUtil.toBean(accountConfigPagination, PaginationVO.class);
        vo.setPagination(page);
        return ServiceResult.success(vo);
    }

    /**
     * 创建
     *
     * @param accountConfigForm 新建账号配置模型
     * @return ignore
     */
    @Operation(summary = "新建")
    @Parameters({
            @Parameter(name = "accountConfigForm", description = "新建账号配置模型")
    })
    @SaCheckPermission("msgCenter.accountConfig")
    @PostMapping
    @Transactional
    public ServiceResult create(@RequestBody @Valid AccountConfigForm accountConfigForm) throws DataBaseException {
        boolean b = accountConfigService.checkForm(accountConfigForm, 0, accountConfigForm.getType(), "");
        if (b) {
            return ServiceResult.error("编码不能重复");
        }
        boolean c = accountConfigService.checkGzhId(accountConfigForm.getAppKey(), 0, "7", "");
        if ("7".equals(accountConfigForm.getType())) {
            if (c) {
                return ServiceResult.error("微信公众号原始id不能重复");
            }
        }
        String mainId = RandomUtil.uuId();
        UserInfo userInfo = userProvider.get();
        AccountConfigEntity entity = BeanUtil.toBean(accountConfigForm, AccountConfigEntity.class);
        entity.setCreatorTime(DateUtil.getNowDate());
        entity.setCreatorUserId(userInfo.getUserId());
        entity.setId(mainId);
        accountConfigService.save(entity);
        return ServiceResult.success("创建成功");
    }


    /**
     * 信息
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "信息")
    @GetMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("msgCenter.accountConfig")
    public ServiceResult<AccountConfigInfoVO> info(@PathVariable("id") String id) {
        AccountConfigEntity entity = accountConfigService.getInfo(id);
        AccountConfigInfoVO vo = BeanUtil.toBean(entity, AccountConfigInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 表单信息(详情页)
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "表单信息(详情页)")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("msgCenter.accountConfig")
    @GetMapping("/detail/{id}")
    public ServiceResult<AccountConfigInfoVO> detailInfo(@PathVariable("id") String id) {
        return info(id);
    }


    /**
     * 更新
     *
     * @param id                主键
     * @param accountConfigForm 修改账号配置模型
     * @return ignore
     */
    @Operation(summary = "更新")
    @PutMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "accountConfigForm", description = "修改账号配置模型", required = true)
    })
    @SaCheckPermission("msgCenter.accountConfig")
    @Transactional
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid AccountConfigForm accountConfigForm) throws DataBaseException {

        boolean b = accountConfigService.checkForm(accountConfigForm, 0, accountConfigForm.getType(), accountConfigForm.getId());
        if (b) {
            return ServiceResult.error("编码不能重复");
        }
        boolean c = accountConfigService.checkGzhId(accountConfigForm.getAppKey(), 0, "7", id);
        if ("7".equals(accountConfigForm.getType())) {
            if (c) {
                return ServiceResult.error("微信公众号原始id不能重复");
            }
        }
        //判断配置是否被引用
        if (Objects.equals(0, accountConfigForm.getEnabledMark())) {
            if (sendConfigTemplateService.isUsedAccount(accountConfigForm.getId())) {
                return ServiceResult.error("此记录与“消息发送配置”关联引用，不允许被禁用");
            }
        }
        UserInfo userInfo = userProvider.get();
        AccountConfigEntity entity = accountConfigService.getInfo(id);
        if (entity != null) {
            AccountConfigEntity subentity = BeanUtil.toBean(accountConfigForm, AccountConfigEntity.class);
            subentity.setCreatorTime(entity.getCreatorTime());
            subentity.setCreatorUserId(entity.getCreatorUserId());
            subentity.setUpdateTime(DateUtil.getNowDate());
            subentity.setUpdateUserId(userInfo.getUserId());
            boolean b1 = accountConfigService.updateById(subentity);
            if (!b1) {
                return ServiceResult.error("当前表单原数据已被调整，请重新进入该页面编辑并提交数据");
            }
            return ServiceResult.success("更新成功");
        } else {
            return ServiceResult.error("更新失败，数据不存在");
        }
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("msgCenter.accountConfig")
    @Transactional
    public ServiceResult delete(@PathVariable("id") String id) {
        AccountConfigEntity entity = accountConfigService.getInfo(id);
        if (entity != null) {
            //判断是否与消息发送配置关联
            //判断配置是否被引用
            if (sendConfigTemplateService.isUsedAccount(entity.getId())) {
                return ServiceResult.error("此记录与“消息发送配置”关联引用，不允许被删除");
            }

            accountConfigService.delete(entity);

        }
        return ServiceResult.success("删除成功");
    }


    /**
     * 开启或禁用
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "开启或禁用")
    @PostMapping("/unable/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("msgCenter.accountConfig")
    @Transactional
    public ServiceResult unable(@PathVariable("id") String id) {
        AccountConfigEntity entity = accountConfigService.getInfo(id);
        if (entity != null) {
            if ("1".equals(String.valueOf(entity.getEnabledMark()))) {
                entity.setEnabledMark(0);
                return ServiceResult.success("禁用成功");
            } else {
                //判断是否被引用

                entity.setEnabledMark(1);
                return ServiceResult.success("启用成功");
            }
        } else {
            return ServiceResult.error("操作失败，数据不存在");
        }
    }

    /**
     * 复制
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "复制")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("msgCenter.accountConfig")
    @PostMapping("/{id}/Actions/Copy")
    @Transactional
    public ServiceResult copy(@PathVariable("id") String id) {
        UserInfo userInfo = userProvider.get();
        AccountConfigEntity entity = accountConfigService.getInfo(id);
        if (entity != null) {
            entity.setEnabledMark(0);
            String copyNum = UUID.randomUUID().toString().substring(0, 5);
            entity.setFullName(entity.getFullName() + ".副本" + copyNum);
            entity.setEnCode(entity.getEnCode() + copyNum);
            entity.setCreatorTime(DateUtil.getNowDate());
            entity.setCreatorUserId(userInfo.getUserId());
            if ("7".equals(entity.getType())) {
                entity.setAppKey(entity.getAppKey() + "副本" + copyNum);
            }
            entity.setUpdateTime(null);
            entity.setUpdateUserId(null);
            entity.setId(RandomUtil.uuId());
            AccountConfigEntity copyEntity = BeanUtil.toBean(entity, AccountConfigEntity.class);
            if (copyEntity.getEnCode().length() > 50 || copyEntity.getFullName().length() > 50) {
                return ServiceResult.error("已到达该模板复制上限，请复制源模板");
            }
            accountConfigService.create(copyEntity);
            return ServiceResult.success("复制数据成功");
        } else {
            return ServiceResult.error("复制失败，数据不存在");
        }
    }


    /**
     * 导出账号配置
     *
     * @param id 账号配置id
     * @return ignore
     */
    @Operation(summary = "导出")
    @GetMapping("/{id}/Action/Export")
    public ServiceResult export(@PathVariable String id) {
        AccountConfigEntity entity = accountConfigService.getInfo(id);
        //导出文件
        DownloadVO downloadVO = fileExport.exportFile(entity, configValueUtil.getTemporaryFilePath(), entity.getFullName(), ModuleTypeEnum.ACCOUNT_CONFIG.getTableName());
        return ServiceResult.success(downloadVO);
    }

    /**
     * 导入账号配置
     *
     * @param multipartFile 备份json文件
     * @return 执行结果标识
     */
    @Operation(summary = "导入")
    @PostMapping(value = "/Action/Import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResult importData(@RequestPart("file") MultipartFile multipartFile) throws DataBaseException {
        UserInfo userInfo = userProvider.get();
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.ACCOUNT_CONFIG.getTableName())) {
            return ServiceResult.error(MsgCode.IMP002.get());
        }
        //获取文件内容
        String fileContent = FileUtil.getFileContent(multipartFile);
        AccountConfigEntity entity = JsonUtil.createJsonToBean(fileContent, AccountConfigEntity.class);
        entity.setCreatorUserId(userInfo.getUserId());
        entity.setCreatorTime(DateUtil.getNowDate());
        return accountConfigService.ImportData(entity);
    }

    /**
     * 测试发送邮件
     *
     * @param accountConfigForm 账号测试模型
     * @return
     */
    @Operation(summary = "测试发送邮箱")
    @Parameters({
            @Parameter(name = "accountConfigForm", description = "账号测试模型", required = true)
    })
    @SaCheckPermission("msgCenter.accountConfig")
    @PostMapping("/testSendMail")
    @Transactional
    public ServiceResult testSendMail(@RequestBody @Valid AccountConfigForm accountConfigForm) {
        List<String> toMails = accountConfigForm.getTestSendEmail();
        // 获取邮箱配置
        Map<String, String> objModel = new HashMap<>();
        objModel.put("emailSmtpHost", accountConfigForm.getSmtpServer());
        objModel.put("emailSmtpPort", accountConfigForm.getSmtpPort().toString());
        objModel.put("emailSenderName", accountConfigForm.getAddressorName());
        objModel.put("emailAccount", accountConfigForm.getSmtpUser());
        objModel.put("emailPassword", accountConfigForm.getSmtpPassword());
        objModel.put("emailSsl", accountConfigForm.getSslLink() == 1 ? "true" : "false");


        EmailModel emailModel = BeanUtil.toBean(objModel, EmailModel.class);
        StringBuilder toUserMail = new StringBuilder();
        String userEmailAll = "";
        String userEmail = "";
        String userName = "";

        // 相关参数验证
        if (StringUtil.isEmpty(emailModel.getEmailSmtpHost())) {
            return ServiceResult.error("发送失败，失败原因：SMTP服务为空");
        } else if (StringUtil.isEmpty(emailModel.getEmailSmtpPort())) {
            return ServiceResult.error("发送失败，失败原因：SMTP端口为空");
        } else if (StringUtil.isEmpty(emailModel.getEmailAccount())) {
            return ServiceResult.error("发送失败，失败原因：发件人邮箱为空");
        } else if (StringUtil.isEmpty(emailModel.getEmailPassword())) {
            return ServiceResult.error("发送失败，失败原因：发件人密码为空");
        } else if (toMails == null || toMails.size() < 1) {
            return ServiceResult.error("发送失败，失败原因：接收人为空");
        } else {
            // 设置邮件标题
            emailModel.setEmailTitle(accountConfigForm.getTestEmailTitle());
            // 设置邮件内容
            String content = accountConfigForm.getTestEmailContent();
            emailModel.setEmailContent(content);

            // 获取收件人的邮箱地址、创建消息用户实体
            for (String userId : toMails) {
                SysUserEntity userEntity = userService.getInfo(userId);
                if (userEntity != null) {
                    userEmail = StringUtil.isEmpty(userEntity.getEmail()) ? "" : userEntity.getEmail();
                    userName = userEntity.getRealName();
                }
                if (StringUtil.isNotBlank(userEmail) && !"null".equals(userEmail)) {
                    //校验用户邮箱格式
                    if (!isEmail(userEmail)) {
                        return ServiceResult.error("发送失败。失败原因：" + userName + "的邮箱账号格式有误！");
                    }
                    toUserMail = toUserMail.append(",").append(userName).append("<").append(userEmail).append(">");
                } else {
                    return ServiceResult.error("发送失败。失败原因：" + userName + "的邮箱账号为空！");
                }
            }
            // 处理接收人员的邮箱信息串并验证
            userEmailAll = toUserMail.toString();
            if (StringUtil.isNotEmpty(userEmailAll)) {
                userEmailAll = userEmailAll.substring(1);
            }
            if (StringUtil.isEmpty(userEmailAll)) {
                return ServiceResult.error("发送失败。失败原因：接收人对应的邮箱全部为空");
            } else {
                // 设置接收人员
                emailModel.setEmailToUsers(userEmailAll);
                // 发送邮件
                JSONObject retJson = EmailUtil.sendMail(emailModel);
                if (!retJson.getBoolean("code")) {
                    return ServiceResult.error("发送失败。失败原因：" + retJson.get("error"));
                }
            }
        }
        return ServiceResult.success("已发送");
    }

    /**
     * 测试企业微信配置的连接功能
     *
     * @param accountConfigForm 账号测试模型
     * @return ignore
     */
    @Operation(summary = "测试企业微信配置的连接")
    @Parameters({
            @Parameter(name = "accountConfigForm", description = "账号测试模型", required = true)
    })
    @SaCheckPermission("msgCenter.accountConfig")
    @PostMapping("/testQyWebChatConnect")
    public ServiceResult testQyWebChatConnect(@RequestBody @Valid AccountConfigForm accountConfigForm) {
        JSONObject retMsg;
        // 测试发送消息、组织同步的连接
        //企业微信企业id
        String corpId = accountConfigForm.getEnterpriseId();
        //企业微信应用secret
        String agentSecret = accountConfigForm.getAppSecret();
//        String corpSecret = testAccountConfigForm.getQyhCorpSecret();
        // 测试发送消息的连接
        retMsg = QyWebChatUtil.getAccessToken(corpId, agentSecret);
        if (HttpUtil.isWxError(retMsg)) {
            return ServiceResult.error("连接失败。失败原因：" + retMsg.getString("errmsg"));
        }
        return ServiceResult.success("连接成功");
    }

    /**
     * 测试钉钉配置的连接功能
     *
     * @param accountConfigForm 账号测试模型
     * @return ignore
     */
    @Operation(summary = "测试钉钉配置的连接")
    @Parameters({
            @Parameter(name = "accountConfigForm", description = "账号测试模型", required = true)
    })
    @SaCheckPermission("msgCenter.accountConfig")
    @PostMapping("/testDingTalkConnect")
    public ServiceResult testDingTalkConnect(@RequestBody @Valid AccountConfigForm accountConfigForm) {
        JSONObject retMsg;
        // 测试钉钉配置的连接
        String appKey = accountConfigForm.getAppId();
        String appSecret = accountConfigForm.getAppSecret();
        ///
//        String agentId = dingTalkModel.getDingAgentId();
        // 测试钉钉的连接
        retMsg = DingTalkUtil.getAccessToken(appKey, appSecret);
        if (!retMsg.getBoolean("code")) {
            return ServiceResult.error("连接失败。失败原因：" + retMsg.getString("error"));
        }

        return ServiceResult.success("连接成功");
    }

    public boolean isEmail(String email) {
        String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        Boolean b = email.matches(EMAIL_REGEX);
        return b;
    }
}
